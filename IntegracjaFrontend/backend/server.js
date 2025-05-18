const express = require('express');
const soap = require('soap');
const cors = require('cors');
const cookieParser = require('cookie-parser');
const { createSoapServer } = require('./soapService');
const { generateToken, restAuthMiddleware, cookieOptions } = require('./middleware/auth');
require('dotenv').config();

const app = express();
const port = process.env.PORT || 3001;
const soap_url = process.env.SOAP_URL || 'http://localhost:3001/wsdl?wsdl';

// Configure CORS with specific options
const corsOptions = {
    origin: 'http://localhost:3000', // your frontend URL
    methods: ['GET', 'POST'],
    allowedHeaders: ['Content-Type', 'Authorization'],
    credentials: true // needed for cookies
};

app.use(cors(corsOptions));
app.use(express.json());
app.use(cookieParser());

// test soap server
createSoapServer(app);

// Login endpoint to get JWT token
app.post('/api/login', (req, res) => {
    const { username, password } = req.body;
    
    // test
    if (username === 'admin' && password === 'password') {
        const token = generateToken({ id: 1, username });
        
        // respond with a cookie to set
        res.cookie('authToken', token, cookieOptions);
        
        res.json({ 
            status: 'success',
            message: 'Login successful'
        });
    } else {
        res.status(401).json({ 
            status: 'error',
            message: 'Authentication failed'
        });
    }
});

// Verification endpoint
app.get('/api/verify', restAuthMiddleware, (req, res) => {
    res.json({ status: 'success', message: 'Authenticated' });
});

// Protected SOAP client endpoint
app.get('/api/data', restAuthMiddleware, async (req, res) => {
    try {
        const client = await soap.createClientAsync(soap_url);
        const token = req.cookies?.authToken || 
                 (req.headers.authorization && req.headers.authorization.split(' ')[1]);

        const soapHeader = {
            "tns:Security": {
                "tns:BearerToken": token
            }
        };
        client.addSoapHeader(soapHeader);

        const args = {}; 
        const result = await client.getRegionalDataAsync(args);
        
        // transform into object
        // const transformedData = result[0].result.map(item => ({
        //     region: item.Region,
        //     year: parseInt(item.Year),
        //     value: parseFloat(item.Value)
        // }));
        // SOAP should respond with proper objects
        res.json(result[0].result);
    } catch (error) {
        console.error('SOAP request failed:', error);
        res.status(500).json({ 
            status: 'error',
            message: 'Failed to fetch data'  // Less detailed error message
        });
    }
});

app.listen(port, () => {
    console.log(`Server is running on port ${port}`);
}); 