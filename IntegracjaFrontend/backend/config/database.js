const { MongoClient } = require('mongodb');
require('dotenv').config();

const uri = `mongodb://${process.env.DB_USER || 'root'}:${process.env.DB_PASSWORD}@${process.env.DB_HOST || 'localhost'}:${process.env.DB_PORT || '27017'}/${process.env.DB_NAME || 'integration_db'}?authSource=admin`;

const client = new MongoClient(uri, {});

let db = null;

async function connect() {
    if (db) return db;
    try {
        await client.connect();
        db = client.db(process.env.DB_NAME || 'integration_db');
        console.log('Connected to MongoDB');
        return db;
    } catch (error) {
        console.error('MongoDB connection error:', error);
        throw error;
    }
}

module.exports = {
    connect,
    getDb: () => db,
    close: async () => {
        if (client) {
            await client.close();
            db = null;
        }
    }
}; 