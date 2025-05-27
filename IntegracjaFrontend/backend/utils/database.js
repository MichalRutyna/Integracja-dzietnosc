const { MongoClient } = require('mongodb');
const { readSecret } = require('./secrets');
const path = require('path');
require('dotenv').config({ path: path.join(__dirname, '../.env') });

let client = null;
let db = null;

// Connection options with pooling configuration
const connectionOptions = {
    maxPoolSize: 10,
    minPoolSize: 5,
    maxIdleTimeMS: 60000,
    connectTimeoutMS: 5000,
    socketTimeoutMS: 45000,
    serverSelectionTimeoutMS: 5000,
    retryWrites: true,
    retryReads: true
};

async function getConnectionUri() {
    let dbUser, dbPassword, dbName;
    dbUser = readSecret('db_user');
    dbPassword = readSecret('db_password');
    dbName = readSecret('db_name');
    
    return `mongodb://${dbUser}:${dbPassword}@${process.env.DB_HOST || 'localhost'}:${process.env.DB_PORT || '27017'}/${dbName}?authSource=admin`;
}

async function initializeClient() {
    if (client) return client;
    
    const uri = await getConnectionUri();
    client = new MongoClient(uri, connectionOptions);
    return client;
}

async function getDb() {
    if (db) return db;
    
    try {
        client = await initializeClient();
        await client.connect();
        const dbName = readSecret('db_name');
        db = client.db(dbName);
        
        // Add connection event listeners
        client.on('connectionPoolCreated', (event) => {
            console.log('MongoDB connection pool created');
        });

        client.on('connectionPoolClosed', (event) => {
            console.log('MongoDB connection pool closed');
        });

        console.log('Connected to MongoDB');
        return db;
    } catch (error) {
        console.error('MongoDB connection error:', error);
        throw error;
    }
}

// Modified to handle connection pool properly
async function closeConnection() {
    if (client) {
        try {
            await client.close(true); // Force close all connections in the pool
            client = null;
            db = null;
            console.log('MongoDB connection closed successfully');
        } catch (error) {
            console.error('Error closing MongoDB connection:', error);
            throw error;
        }
    }
}

async function initializeCollections() {
    const database = await getDb();
    
    // Create collections if they don't exist
    const collections = await database.listCollections().toArray();
    const collectionNames = collections.map(c => c.name);

    let changed = false;

    if (!collectionNames.includes('users')) {
        await database.createCollection('users');
        await database.collection('users').createIndexes([
            { key: { username: 1 }, unique: true },
            { key: { created_at: 1 } }
        ]);
        changed = true;
    }

    // Not used sessions
    if (!collectionNames.includes('sessions')) {
        await database.createCollection('sessions');
        await database.collection('sessions').createIndexes([
            { key: { user_id: 1 } },
            { key: { token: 1 }, unique: true },
            { key: { expires_at: 1 }, expireAfterSeconds: 0 } // TTL index
        ]);
        changed = true;
    }

    if (changed) {
        console.log('Database collections initialized successfully!');
    }
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
    initializeDatabase
}; 