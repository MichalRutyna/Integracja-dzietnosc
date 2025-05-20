const bcrypt = require('bcrypt');
const db = require('../config/database');

const SALT_ROUNDS = 12;

class UserService {
    async createUser(username, password) {
        // Validate password
        if (!password || password.length < 8) {
            throw new Error('Password must be at least 8 characters long');
        }

        // Validate username
        if (!username || username.length < 3) {
            throw new Error('Username must be at least 3 characters long');
        }

        try {
            const database = await db.connect();
            const users = database.collection('users');

            // Check if user already exists
            const existingUser = await users.findOne({ username });

            if (existingUser) {
                throw new Error('Username already exists');
            }

            // Hash password
            const passwordHash = await bcrypt.hash(password, SALT_ROUNDS);

            // Insert new user
            const result = await users.insertOne({
                username,
                password_hash: passwordHash,
                created_at: new Date(),
                last_login: null,
                is_active: true
            });

            return {
                id: result.insertedId,
                username
            };
        } catch (error) {
            throw error;
        }
    }

    async verifyUser(username, password) {
        try {
            const database = await db.connect();
            const users = database.collection('users');

            // Get user
            const user = await users.findOne({ username });

            // Check if user exists
            if (!user) {
                return null;
            }

            // Verify password
            const validPassword = await bcrypt.compare(password, user.password_hash);

            if (!validPassword) {
                return null;
            }

            // Update last login
            await users.updateOne(
                { _id: user._id },
                { $set: { last_login: new Date() } }
            );

            // Return user data (excluding password)
            return {
                id: user._id,
                username: user.username
            };
        } catch (error) {
            throw error;
        }
    }
}

module.exports = new UserService(); 