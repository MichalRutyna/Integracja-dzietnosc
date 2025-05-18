const express = require('express');
const soap = require('soap');
const cors = require('cors');
const cookieParser = require('cookie-parser');
const { generateToken, restAuthMiddleware, cookieOptions } = require('./middleware/auth');
const { loginLimiter } = require('./middleware/rateLimiter');
const userService = require('./services/userService');

const app = express();
const port = process.env.PORT || 3001;
const soap_url = process.env.SOAP_URL || 'http://localhost:8080/data-service?wsdl';

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


// Login endpoint
app.post('/api/login', loginLimiter, async (req, res) => {
    try {
        const { username, password } = req.body;

        // Validate input
        if (!username || !password) {
            return res.status(400).json({
                status: 'error',
                message: 'Username and password are required'
            });
        }

        // Verify user
        const user = await userService.verifyUser(username, password);
        
        if (!user) {
            return res.status(401).json({
                status: 'error',
                message: 'Invalid credentials'
            });
        }

        // Generate token
        const token = generateToken(user);
        
        // respond with a cookie to set
        res.cookie('authToken', token, cookieOptions);
        
        res.json({ 
            status: 'success',
            message: 'Login successful'
        });
    } catch (error) {
        console.error('Login error:', error);
        res.status(500).json({
            status: 'error',
            message: 'An error occurred during login'
        });
    }
});

// Register endpoint
app.post('/api/register', async (req, res) => {
    try {
        const { username, password } = req.body;

        const user = await userService.createUser(username, password);
        
        res.status(201).json({
            status: 'success',
            message: 'User created successfully'
        });
    } catch (error) {
        if (error.message.includes('already exists')) {
            return res.status(409).json({
                status: 'error',
                message: error.message
            });
        }
        
        console.error('Registration error:', error);
        res.status(500).json({
            status: 'error',
            message: 'An error occurred during registration'
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