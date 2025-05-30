const express = require('express');

const { generateToken, cookieOptions, extractUserIdFromToken } = require('../services/authService');
const { restAuthMiddleware } = require('../middleware/auth');
const { loginLimiter } = require('../middleware/rateLimiter');
const userService = require('../services/userService');
const db = require('../utils/database');


const router = express.Router()

router.post('/login', loginLimiter, async (req, res) => {
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
router.post('/register', loginLimiter, async (req, res) => {
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

router.post('/logout', loginLimiter, (req, res) => {
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

  

// Verification endpoint
router.get('/verify', [restAuthMiddleware, loginLimiter], (req, res) => {
    res.json({ status: 'success', message: 'Authenticated' });
});

router.put('/user', restAuthMiddleware, async (req, res) => {
    try {
        const userId = extractUserIdFromToken(req.cookies.authToken);
        const updates = req.body;

        // Get database connection
        const database = await db.getDb();

        // Update user
        await userService.updateUser(database, userId, updates);

        res.json({
            status: 'success',
            message: 'User updated successfully'
        });
    } catch (error) {
        if (error.message.includes('not found')) {
            return res.status(404).json({
                status: 'error',
                message: error.message
            });
        }

        console.error('Update user error:', error);
        res.status(500).json({
            status: 'error',
            message: 'An error occurred while updating the user'
        });
    }
});

router.delete('/user', restAuthMiddleware, async (req, res) => {
    try {
        const userId = extractUserIdFromToken(req.cookies.authToken);

        // Get database connection
        const database = await db.getDb();

        // Delete user
        await userService.deleteUser(database, userId);

        res.json({
            status: 'success',
            message: 'User deleted successfully'
        });
    } catch (error) {
        if (error.message.includes('not found')) {
            return res.status(404).json({
                status: 'error',
                message: error.message
            });
        }

        console.error('Delete user error:', error);
        res.status(500).json({
            status: 'error',
            message: 'An error occurred while deleting the user'
        });
    }
});

module.exports = router;