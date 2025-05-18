const { Pool } = require('pg');
const fs = require('fs').promises;
const path = require('path');
require('dotenv').config({ path: path.join(__dirname, '../.env') });

async function initializeDatabase() {
    // First, connect to postgres database to create our app database
    const pool = new Pool({
        host: process.env.DB_HOST || 'localhost',
        user: process.env.DB_USER || 'postgres',
        password: process.env.DB_PASSWORD,
        database: 'postgres', // Connect to default postgres database first
        port: process.env.DB_PORT || 5432
    });

    try {
        // Create the database if it doesn't exist
        await pool.query(`
            SELECT 'CREATE DATABASE ${process.env.DB_NAME || 'integration_db'}'
            WHERE NOT EXISTS (
                SELECT FROM pg_database WHERE datname = '${process.env.DB_NAME || 'integration_db'}'
            )
        `);

        // Close connection to postgres database
        await pool.end();

        // Connect to our newly created database
        const appPool = new Pool({
            host: process.env.DB_HOST || 'localhost',
            user: process.env.DB_USER || 'postgres',
            password: process.env.DB_PASSWORD,
            database: process.env.DB_NAME || 'integration_db',
            port: process.env.DB_PORT || 5432
        });

        // Read and execute schema.sql
        const schemaSQL = await fs.readFile(path.join(__dirname, 'schema.sql'), 'utf8');
        await appPool.query(schemaSQL);

        console.log('Database initialized successfully!');
        await appPool.end();
    } catch (error) {
        console.error('Error initializing database:', error);
        process.exit(1);
    }
}

// Run the initialization
initializeDatabase(); 