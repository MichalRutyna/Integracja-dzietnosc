const { verifyToken } = require('../services/authService');

// Securing our REST API
function restAuthMiddleware(req, res, next) {
    const token = req.cookies?.authToken || 
                 (req.headers.authorization && req.headers.authorization.split(' ')[1]); // redundancy
                 
    if (!token) {
        return res.status(401).json({ 
            status: 'error',
            message: 'Authentication required'
        });
    }

    const decoded = verifyToken(token);
    if (!decoded) {
        return res.status(401).json({ 
            status: 'error',
            message: 'Authentication required'
        });
    }

    req.user = decoded;
    next();
}

module.exports = {
    restAuthMiddleware
}; 