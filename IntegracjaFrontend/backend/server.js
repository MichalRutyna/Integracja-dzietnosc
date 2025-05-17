const express = require('express');
const soap = require('soap');
const cors = require('cors');
const { createSoapServer } = require('./soapService');
const { generateToken, restAuthMiddleware } = require('./middleware/auth');
require('dotenv').config();

const app = express();
const port = process.env.PORT || 3001;
const soap_url = process.env.SOAP_URL || 'http://localhost:3001/wsdl?wsdl';

app.use(cors());
app.use(express.json());

// test soap server
createSoapServer(app);

// Login endpoint to get JWT token
app.post('/api/login', (req, res) => {
    const { username, password } = req.body;
    
    // test
    if (username === 'admin' && password === 'password') {
        const token = generateToken({ id: 1, username });
        res.json({ token });
    } else {
        res.status(401).json({ error: 'Invalid credentials' });
    }
});

// Protected SOAP client endpoint
app.get('/api/data', restAuthMiddleware, async (req, res) => {
    try {
        const client = await soap.createClientAsync(soap_url);
        
        const soapHeader = {
            Security: {
                BearerToken: req.headers.authorization.split(' ')[1]
            }
        };
        client.addSoapHeader(soapHeader);

        const args = {}; 
        const result = await client.getRegionalDataAsync(args);
        
        // transform into object
        const transformedData = result[0].getRegionalDataResponse.result.map(item => ({
            region: item.Region,
            year: parseInt(item.Year),
            value: parseFloat(item.Value)
        }));
        
        res.json(transformedData);
    } catch (error) {
        console.error('SOAP request failed:', error);
        res.status(500).json({ error: 'Failed to fetch data from SOAP service' });
    }
});

app.listen(port, () => {
    console.log(`Server is running on port ${port}`);
}); 