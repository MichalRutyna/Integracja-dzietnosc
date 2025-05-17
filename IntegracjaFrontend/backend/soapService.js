const soap = require('soap');
const { verifyToken } = require('./middleware/auth');

const mockData = [
    { Region: 'North', Year: 2020, Value: 150.5 },
    { Region: 'North', Year: 2021, Value: 165.2 },
    { Region: 'North', Year: 2022, Value: 180.7 },
    { Region: 'South', Year: 2020, Value: 120.3 },
    { Region: 'South', Year: 2021, Value: 125.8 },
    { Region: 'South', Year: 2022, Value: 140.2 },
    { Region: 'East', Year: 2020, Value: 200.1 },
    { Region: 'East', Year: 2021, Value: 210.5 },
    { Region: 'East', Year: 2022, Value: 225.8 }
];

const serviceObject = {
    DataService: {
        DataPort: {
            getRegionalData: function(args, callback, headers) {
                console.log('Received headers:', JSON.stringify(headers, null, 2));
                
                if (!headers || !headers['Security'] || !headers['Security']['BearerToken']) {
                    console.log('Missing security header or token');
                    callback({
                        Fault: {
                            Code: {
                                Value: 'soap:Client',
                                Subcode: { value: 'AuthenticationError' }
                            },
                            Reason: { Text: 'Missing authentication token' }
                        }
                    });
                    return;
                }

                const token = headers['Security']['BearerToken'];
                
                try {
                    const decoded = verifyToken(token);
                    if (!decoded) {
                        throw new Error('Invalid token');
                    }
                    
                    console.log('Authentication successful for user:', decoded.username);
                    
                    callback({
                        getRegionalDataResponse: {
                            result: mockData
                        }
                    });
                } catch (error) {
                    console.log('Token verification failed:', error.message);
                    callback({
                        Fault: {
                            Code: {
                                Value: 'soap:Client',
                                Subcode: { value: 'AuthenticationError' }
                            },
                            Reason: { Text: 'Invalid authentication token' }
                        }
                    });
                }
            }
        }
    }
};

// WSDL definition
const xml = require('fs').readFileSync('./service.wsdl', 'utf8');

// Create SOAP server
function createSoapServer(app) {
    const server = soap.listen(app, '/wsdl', serviceObject, xml);
    server.log = (type, data) => {
        console.log(`[SOAP ${type}]`, data);
    };
    console.log('SOAP Server initialized');
    return server;
}

module.exports = { createSoapServer }; 