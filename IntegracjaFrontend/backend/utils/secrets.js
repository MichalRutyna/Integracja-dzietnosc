const fs = require('fs');

async function readSecret(secretName) {
    try {
        return fs.readFileSync(`/run/secrets/${secretName}`, 'utf8').trim();
    } catch (error) {
        console.error(`Error reading secret ${secretName}:`, error);
        throw error;
    }
}

module.exports = { readSecret }; 