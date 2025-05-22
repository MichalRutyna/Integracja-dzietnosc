const jwt = require('jsonwebtoken');
const fs = require('fs');

// Read JWT secret from Docker secret
let JWT_SECRET;
try {
    JWT_SECRET = fs.readFileSync('/run/secrets/jwt_secret', 'utf8').trim();
    console.log('Successfully loaded JWT secret from Docker secret');
} catch (error) {
    console.error('Failed to read JWT secret from Docker secret, using unsafe key.',);
    JWT_SECRET = 'your_jwt_secret_here';
}

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