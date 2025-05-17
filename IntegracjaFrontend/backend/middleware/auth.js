const jwt = require('jsonwebtoken');

const JWT_SECRET = process.env.JWT_SECRET || 'your-secret-key'; // In production, always use environment variable

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

function extractTokenFromSoapHeader(soapHeader) {
    if (!soapHeader || !soapHeader.Security || !soapHeader.Security.BearerToken) {
        return null;
    }
    return soapHeader.Security.BearerToken;
}

// Middleware for SOAP authentication
function soapAuthMiddleware(req, res, next) {
    const soapHeader = req.headers['soap-security-header'];
    if (!soapHeader) {
        return res.status(401).send('No SOAP security header found');
    }

    const token = extractTokenFromSoapHeader(JSON.parse(soapHeader));
    if (!token) {
        return res.status(401).send('No token found in SOAP header');
    }

    const decoded = verifyToken(token);
    if (!decoded) {
        return res.status(401).send('Invalid token');
    }

    req.user = decoded;
    next();
}

// REST API authentication middleware
function restAuthMiddleware(req, res, next) {
    const authHeader = req.headers.authorization;
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
        return res.status(401).json({ error: 'No token provided' });
    }

    const token = authHeader.split(' ')[1];
    const decoded = verifyToken(token);
    if (!decoded) {
        return res.status(401).json({ error: 'Invalid token' });
    }

    req.user = decoded;
    next();
}

module.exports = {
    generateToken,
    verifyToken,
    soapAuthMiddleware,
    restAuthMiddleware,
    extractTokenFromSoapHeader
}; 