const fs = require('fs');

function readSecret(secretName) {
    try {
        if (fs.existsSync(`/run/secrets/${secretName}`)) {
            return fs.readFileSync(`/run/secrets/${secretName}`, 'utf8').trim();
        }
        else {
            console.log(`Reading secret ${secretName} from dev file...`);
            return fs.readFileSync(`../../secrets/${secretName}.txt`, 'utf8').trim();
        }
    } catch (error) {
        console.error(`Error reading secret ${secretName}`, error);
        throw error;
    }
}

module.exports = { readSecret }; 