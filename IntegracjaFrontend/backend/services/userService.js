const bcrypt = require('bcrypt');
const { ObjectId } = require('mongodb');

const SALT_ROUNDS = 12;

class UserService {
    async createUser(database, username, password) {
        // Validate input
        this.validateCredentials(username, password);

        const users = database.collection('users');

        // Check if user already exists
        const existingUser = await users.findOne({ username });
        if (existingUser) {
            throw new Error('Username already exists');
        }

        // Hash password and create user
        const passwordHash = await bcrypt.hash(password, SALT_ROUNDS);
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
    }

    async verifyUser(database, username, password) {
        const users = database.collection('users');

        // Get user
        const user = await users.findOne({ username });
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

        return {
            id: user._id,
            username: user.username
        };
    }

    async updateUser(database, userId, updates) {
        const users = database.collection('users');

        // Validate updates (optional)
        if (updates.username && updates.username.length < 3) {
            throw new Error('Username must be at least 3 characters long');
        }
        if (updates.password) {
            updates.password_hash = await bcrypt.hash(updates.password, SALT_ROUNDS);
            delete updates.password;
        }

        const result = await users.updateOne(
            { _id: new ObjectId(userId) },
            { $set: updates }
        );

        if (result.matchedCount === 0) {
            throw new Error('User not found');
        }

        return { success: true };
    }

    async deleteUser(database, userId) {
        const users = database.collection('users');

        const result = await users.deleteOne({ _id: userId });

        if (result.deletedCount === 0) {
            throw new Error('User not found');
        }

        return { success: true };
    }

    validateCredentials(username, password) {
        if (!username || username.length < 3) {
            throw new Error('Username must be at least 3 characters long');
        }
        if (!password || password.length < 8) {
            throw new Error('Password must be at least 8 characters long');
        }
    }
}

module.exports = new UserService(); 