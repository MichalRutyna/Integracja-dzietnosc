const express = require('express');
const soap = require('soap');
const axios = require('axios');

const { generateToken, restAuthMiddleware, cookieOptions } = require('./middleware/auth');
const { loginLimiter } = require('./middleware/rateLimiter');
const userService = require('./services/userService');
const db = require('./utils/database');

const router = express.Router()

const soap_url = process.env.SOAP_URL || 'http://localhost:8080/data-service?wsdl';
const download_url = process.env.INTERACTOR_URL || 'http://localhost:8090/api';

// Login endpoint
router.post('/api/login', loginLimiter, async (req, res) => {
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
            message: 'Login successful',
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
router.post('/api/register', loginLimiter, async (req, res) => {
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
router.get('/api/verify', restAuthMiddleware, (req, res) => {
    res.json({ status: 'success', message: 'Authenticated' });
});

// Get available datasets
router.get('/api/soap/datasets', restAuthMiddleware, async (req, res) => {
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

router.get('/api/soap/data', restAuthMiddleware, async (req, res) => {
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

// Logout endpoint
router.post('/api/logout', (req, res) => {
  try {
    // Clear the authToken cookie
    res.clearCookie('authToken', cookieOptions);
    
    res.json({ 
      status: 'success',
      message: 'Logout successful'
    });
  } catch (error) {
    console.error('Logout error:', error);
    res.status(500).json({
      status: 'error',
      message: 'An error occurred during logout'
    });
  }
});


// Get available datasets to download
router.get('/api/download/datasets', restAuthMiddleware, async (req, res) => {
    try {
        const response = await axios.get(download_url + '/datasets');
        
        if (response.data && response.status === 200) {
            res.json({ 
                status: 'success',
                datasets: response.data
            });
        } else {
            throw new Error('Request failed: ' +response.headers);
        }     
    } catch (error) {
        console.error('Download request failed:', error);
        res.status(500).json({ 
            status: 'error',
            message: 'Failed to fetch available datasets'
        });
    }
});

router.post('/api/download', restAuthMiddleware, async (req, res) => {
    try {
        // Get dataset from query parameter
        const dataset = req.query.dataset || '';

        const response = await axios.post(download_url + '/download', {}, {
            params: { dataset },
        });
        if (response.data && response.status === 202) {
            res.json({ 
                status: 'success',
                message: response.data.message,
                progress: response.data.progress,
                taskId: response.data.taskId
            });
        } else {
            throw new Error('Request failed: ' + response.data);
        }
    } catch (error) {
        console.error('Download request failed:', error);
        res.status(500).json({ 
            status: 'error',
            message: 'Failed to fetch data'
        });
    }
});

router.get('/api/download/status', restAuthMiddleware, async (req, res) => {
    try {
        const taskId = req.query.taskId || '';
        const response = await axios.get(download_url + '/status', {
            params: { taskId },
        });

        if (response.data && response.status === 200) {
            res.json({
                request_status: "success",
                message: response.data.status,
                progress: response.data.progress,
            });
        } else {
            throw new Error('Request failed: ' + response.data);
        }
    } catch (error) {
        console.error('Download status request failed:', error);
        res.status(500).json({ 
            status: 'error',
            message: 'Failed to fetch download status'
        });
    }
});

module.exports = router