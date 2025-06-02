package org.burza.soap_client;

import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPEnvelope;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPHeader;
import org.burza.auth.Token;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.soap.saaj.SaajSoapMessage;

import javax.xml.transform.TransformerException;
import java.io.IOException;

public class HeaderModifier implements WebServiceMessageCallback {
    private final String bearerToken;
    private static final String SECURITY_NS = "http://example.com/data-service";
    private static final String SECURITY_PREFIX = "data";

    public HeaderModifier() {
        this.bearerToken = Token.getToken("api_interactor");
    }

    @Override
    public void doWithMessage(WebServiceMessage message) throws IOException, TransformerException {
        try {
            SaajSoapMessage saajMessage = (SaajSoapMessage) message;
            SOAPEnvelope envelope = saajMessage.getSaajMessage().getSOAPPart().getEnvelope();
            SOAPHeader header = envelope.getHeader();
            if (header == null) {
                header = envelope.addHeader();
            }
            SOAPElement security = header.addChildElement("Security", SECURITY_PREFIX, SECURITY_NS);
            
            // Add BearerToken element
            SOAPElement bearerTokenElement = security.addChildElement("BearerToken", SECURITY_PREFIX);
            bearerTokenElement.addTextNode(bearerToken);
        } catch (Exception e) {
                System.err.println("error in header");
        }
    }
}