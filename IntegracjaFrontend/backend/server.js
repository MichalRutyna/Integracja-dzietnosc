const express = require('express');
const cors = require('cors');
const cookieParser = require('cookie-parser');

const db = require('./utils/database');


const app = express();
const port = process.env.PORT || 3001;
process.env.TZ = 'Europe/Warsaw';

// Configure CORS with specific options
const corsOptions = {
    origin: ['http://localhost:3000', 'http://localhost:3002'],
    methods: ['GET', 'POST'],
    allowedHeaders: ['Content-Type', 'Authorization'],
    credentials: true // Needed for cookies
};
app.use(cors(corsOptions));

app.use(express.json());
app.use(cookieParser());

const authRouter = require('./routers/authRouter');
const dataRouter = require('./routers/dataRouter');
const downloadRouter = require('./routers/downloadRouter');
app.use('/api/auth/', authRouter);
app.use('/api/data/', dataRouter);
app.use('/api/download/', downloadRouter);


async function startServer() {
    try {
        // Make sure database is initialized for dev
        // This also kills the connection to the database, as it should be separated
        await db.initializeDatabase();
        
        app.listen(port, () => {
            console.log(`Server is running on port ${port}`);
        });
    } catch (error) {
        console.error('Failed to start server:', error);
        process.exit(1);
    }
}

// Graceful shutdown
process.on('SIGTERM', async () => {
    console.log('SIGTERM received. Closing database connection...');
    await db.close();
    process.exit(0);
});

process.on('SIGINT', async () => {
    console.log('SIGINT received. Closing database connection...');
    await db.close();
    process.exit(0);
});

startServer(); 