const rateLimit = require('express-rate-limit');

const loginLimiter = rateLimit({
    windowMs: 2 * 60 * 1000, // 2 minutes
    max: 5, // 5 attempts
    message: {
        status: 'error',
        message: 'Too many login attempts. Please try again after 2 minutes.'
    },
    standardHeaders: true,
    legacyHeaders: false
});

module.exports = {
    loginLimiter
}; 