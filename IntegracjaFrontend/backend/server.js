const express = require('express');
const soap = require('soap');
const cors = require('cors');
const cookieParser = require('cookie-parser');
const { generateToken, restAuthMiddleware, cookieOptions } = require('./middleware/auth');
const { loginLimiter } = require('./middleware/rateLimiter');
const userService = require('./services/userService');
const db = require('./utils/database');

const app = express();
const port = process.env.PORT || 3001;
const soap_url = process.env.SOAP_URL || 'http://localhost:8080/data-service?wsdl';

// Configure CORS with specific options
const corsOptions = {
    origin: ['http://localhost:3000', 'http://localhost:3002'],
    methods: ['GET', 'POST'],
    allowedHeaders: ['Content-Type', 'Authorization'],
    credentials: true // needed for cookies
};

// Middleware
app.use(cors(corsOptions));
app.use(express.json());
app.use(cookieParser());

// Initialize database and start server
async function startServer() {
    try {
        // Initialize database collections
        await db.initializeDatabase();
        
        // Start the server
        app.listen(port, () => {
            console.log(`Server is running on port ${port}`);
        });
    } catch (error) {
        console.error('Failed to start server:', error);
        process.exit(1);
    }
}

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

        // Get database connection
        const database = await db.getDb();
        
        // Verify user
        const user = await userService.verifyUser(database, username, password);
        
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
app.post('/api/register', loginLimiter, async (req, res) => {
    try {
        const { username, password } = req.body;

        // Get database connection
        const database = await db.getDb();
        
        // Create user
        const user = await userService.createUser(database, username, password);
        
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

// Get available datasets
app.get('/api/datasets', restAuthMiddleware, async (req, res) => {
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
        const result = await client.getAvailableDatasetsAsync(args);
        
        res.json({ 
            status: 'success',
            datasets: result[0].datasets 
        });
    } catch (error) {
        console.error('SOAP request failed:', error);
        res.status(500).json({ 
            status: 'error',
            message: 'Failed to fetch available datasets'
        });
    }
});

// Protected SOAP client endpoint to get regional data
app.get('/api/data', restAuthMiddleware, async (req, res) => {
    try {
        const client = await soap.createClientAsync(soap_url);
        const token = req.cookies?.authToken || 
                     (req.headers.authorization && req.headers.authorization.split(' ')[1]);

        // Get dataset from query parameter
        const dataset = req.query.dataset || '';

        const soapHeader = {
            "tns:Security": {
                "tns:BearerToken": token
            }
        };
        client.addSoapHeader(soapHeader);

        const args = { 
            dataset: dataset
        }; 
        const result = await client.getRegionalDataAsync(args);
        
        res.json(result[0].result);
    } catch (error) {
        console.error('SOAP request failed:', error);
        res.status(500).json({ 
            status: 'error',
            message: 'Failed to fetch data'
        });
    }
});

// Graceful shutdown
process.on('SIGTERM', async () => {
    console.log('SIGTERM received. Closing database connection...');
    await db.close();
    process.exit(0);
});

process.on('SIGINT', async () => {
    console.log('SIGINT received. Closing database connection...');
    await db.close();
    process.exit(0);
});

// Start the server
startServer(); 