const { MongoClient } = require('mongodb');
const { readSecret } = require('./secrets');
const path = require('path');
require('dotenv').config({ path: path.join(__dirname, '../.env') });

let client = null;
let db = null;

async function getConnectionUri() {
    const dbUser = await readSecret('db_user');
    const dbPassword = await readSecret('db_password');
    const dbName = await readSecret('db_name');
    
    return `mongodb://${dbUser}:${dbPassword}@${process.env.DB_HOST || 'localhost'}:${process.env.DB_PORT || '27017'}/${dbName}?authSource=admin`;
}

async function initializeClient() {
    if (client) return client;
    
    const uri = await getConnectionUri();
    client = new MongoClient(uri);
    return client;
}

async function getDb() {
    if (db) return db;
    
    try {
        client = await initializeClient();
        await client.connect();
        const dbName = await readSecret('db_name');
        db = client.db(dbName);
        console.log('Connected to MongoDB');
        return db;
    } catch (error) {
        console.error('MongoDB connection error:', error);
        throw error;
    }
}

async function closeConnection() {
    if (client) {
        await client.close();
        client = null;
        db = null;
    }
}

async function initializeCollections() {
    const database = await getDb();
    
    // Create collections if they don't exist
    const collections = await database.listCollections().toArray();
    const collectionNames = collections.map(c => c.name);

    if (!collectionNames.includes('users')) {
        await database.createCollection('users');
        await database.collection('users').createIndexes([
            { key: { username: 1 }, unique: true },
            { key: { created_at: 1 } }
        ]);
    }

    // Not used sessions
    if (!collectionNames.includes('sessions')) {
        await database.createCollection('sessions');
        await database.collection('sessions').createIndexes([
            { key: { user_id: 1 } },
            { key: { token: 1 }, unique: true },
            { key: { expires_at: 1 }, expireAfterSeconds: 0 } // TTL index
        ]);
    }

    console.log('Database collections initialized successfully!');
}

async function initializeDatabase() {
    try {
        await initializeCollections();
        console.log('Database initialized successfully!');
    } catch (error) {
        console.error('Error initializing database:', error);
        process.exit(1);
    } finally {
        await closeConnection();
    }
}

// Run initialization if this file is run directly
if (require.main === module) {
    initializeDatabase();
}

module.exports = {
    connect: getDb,           
    getDb,
    close: closeConnection,  
    
    // Initialization
    initializeDatabase,
    initializeCollections
}; 