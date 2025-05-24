const jwt = require('jsonwebtoken');
const {readSecret} = require('../utils/secrets');

// Read JWT secret from Docker secret
const JWT_SECRET = readSecret('jwt_secret');

function generateToken(user) {
    return jwt.sign({ id: user.id, username: user.username }, JWT_SECRET, {
        expiresIn: '1h'
    });
}

function verifyToken(token) {
    try {
        return jwt.verify(token, JWT_SECRET);
    } catch (error) {
        return null;
    }
}

// securing REST
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

const cookieOptions = {
    httpOnly: true,
    secure: process.env.NODE_ENV === 'production', // true in production
    sameSite: 'strict',
    maxAge: 3600000 // 1 hour
};

module.exports = {
    generateToken,
    verifyToken,
    restAuthMiddleware,
    cookieOptions
}; 