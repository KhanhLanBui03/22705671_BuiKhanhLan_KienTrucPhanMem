const express = require('express');
const redis = require('redis');
const cors = require('cors');

const app = express();
const port = 8082;

app.use(cors());
app.use(express.json());

const client = redis.createClient({
    url: process.env.REDIS_URL || 'redis://localhost:6379'
});

client.on('error', err => console.log('Redis Client Error', err));
client.connect();

app.post('/cart/add', async (req, res) => {
    const { userId, productId, quantity } = req.body;
    if (!userId || !productId) return res.status(400).json({ error: 'Missing userId or productId' });

    try {
        const cartKey = `cart:${userId}`;
        // Store as hash: productId -> quantity
        await client.hIncrBy(cartKey, productId, quantity || 1);
        res.json({ success: true, message: 'Item added to cart' });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

app.get('/cart/:userId', async (req, res) => {
    const { userId } = req.params;
    try {
        const cartKey = `cart:${userId}`;
        const items = await client.hGetAll(cartKey);
        res.json(items);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// Helper for Order service to clear cart
app.delete('/cart/:userId', async (req, res) => {
    const { userId } = req.params;
    try {
        await client.del(`cart:${userId}`);
        res.json({ success: true });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

app.listen(port, () => {
    console.log(`Cart PU listening at http://localhost:${port}`);
});
