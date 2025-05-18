package org.burza.soap_api;

import javax.xml.namespace.QName;
import jakarta.xml.soap.*;
import jakarta.xml.ws.handler.MessageContext;
import jakarta.xml.ws.handler.soap.SOAPHandler;
import jakarta.xml.ws.handler.soap.SOAPMessageContext;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SecurityHandler implements SOAPHandler<SOAPMessageContext> {
    private static final String NAMESPACE_URI = "http://example.com/data-service";

    @Override
    public Set<QName> getHeaders() {
        Set<QName> headers = new HashSet<>();
        headers.add(new QName(NAMESPACE_URI, "Security"));
        return headers;
    }

    @Override
    public boolean handleMessage(SOAPMessageContext soapMessageContext) {
        Boolean isOutbound = (Boolean) soapMessageContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        if (isOutbound) return true;
        try {
            SOAPMessage soapMsg = soapMessageContext.getMessage();
            SOAPPart sp = soapMsg.getSOAPPart();
            SOAPEnvelope se = sp.getEnvelope();
            SOAPHeader header = se.getHeader();
            if (header == null) {
                throw new RuntimeException("Missing SOAP header");
            }

            Iterator<?> it = header.getChildElements(new QName(NAMESPACE_URI, "Security"));
            if (!it.hasNext()) {
                throw new RuntimeException("Missing SecurityHeader element");
            }

            SOAPElement securityHeader = (SOAPElement) it.next();
            Iterator<?> tokenIter = securityHeader.getChildElements(new QName(NAMESPACE_URI, "BearerToken"));
            if (!tokenIter.hasNext()) {
                throw new RuntimeException("Missing BearerToken");
            }

            SOAPElement tokenElem = (SOAPElement) tokenIter.next();
            String token = tokenElem.getValue();

            if (!Auth.checkToken(token)) {
                throw new RuntimeException("Invalid token");
            }
        } catch (SOAPException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean handleFault(SOAPMessageContext soapMessageContext) {
        System.out.println("fault");
        return true;
    }

    @Override
    public void close(MessageContext messageContext) {
    }
}