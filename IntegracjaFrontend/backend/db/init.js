const { MongoClient } = require('mongodb');
const path = require('path');
require('dotenv').config({ path: path.join(__dirname, '../.env') });

async function initializeDatabase() {
    const uri = `mongodb://${process.env.DB_USER || 'root'}:${process.env.DB_PASSWORD}@${process.env.DB_HOST || 'localhost'}:${process.env.DB_PORT || '27017'}/${process.env.DB_NAME || 'integration_db'}?authSource=admin`;
    
    const client = new MongoClient(uri, {
        useNewUrlParser: true,
        useUnifiedTopology: true,
    });

    try {
        await client.connect();
        const db = client.db(process.env.DB_NAME || 'integration_db');

        // Create collections if they don't exist
        const collections = await db.listCollections().toArray();
        const collectionNames = collections.map(c => c.name);

        // Create users collection with indexes
        if (!collectionNames.includes('users')) {
            await db.createCollection('users');
            await db.collection('users').createIndexes([
                { key: { username: 1 }, unique: true },
                { key: { created_at: 1 } }
            ]);
        }

        // Create sessions collection with indexes
        if (!collectionNames.includes('sessions')) {
            await db.createCollection('sessions');
            await db.collection('sessions').createIndexes([
                { key: { user_id: 1 } },
                { key: { token: 1 }, unique: true },
                { key: { expires_at: 1 }, expireAfterSeconds: 0 } // TTL index
            ]);
        }

        console.log('Database initialized successfully!');
    } catch (error) {
        console.error('Error initializing database:', error);
        process.exit(1);
    } finally {
        await client.close();
    }
}

// Run the initialization
initializeDatabase();

module.exports = { initializeDatabase }; 