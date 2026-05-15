const express = require('express');
const redis = require('redis');
const cors = require('cors');
const axios = require('axios');

const app = express();
const port = 8083;

app.use(cors());
app.use(express.json());

const client = redis.createClient({
    url: process.env.REDIS_URL || 'redis://localhost:6379'
});

client.on('error', err => console.log('Redis Client Error', err));
client.connect();

const INVENTORY_URL = process.env.INVENTORY_URL || 'http://localhost:8084';
const CART_URL = process.env.CART_URL || 'http://localhost:8082';

app.post('/checkout', async (req, res) => {
    const { userId } = req.body;
    if (!userId) return res.status(400).json({ error: 'Missing userId' });

    try {
        // 1. Get cart from Cart PU
        const cartResponse = await axios.get(`${CART_URL}/cart/${userId}`);
        const cart = cartResponse.data;

        if (Object.keys(cart).length === 0) {
            return res.status(400).json({ error: 'Cart is empty' });
        }

        // 2. Decrease stock for each item in Cart via Inventory PU
        // In a real high-load scenario, we might want to do this in parallel or via a more optimized path.
        const items = Object.entries(cart);
        const results = [];

        for (const [productId, quantity] of items) {
            try {
                const invResponse = await axios.post(`${INVENTORY_URL}/stock/decrease`, {
                    productId,
                    quantity
                });
                results.push({ productId, success: true });
            } catch (err) {
                // If one fails (out of stock), we should ideally rollback others.
                // For this demo, we'll just report the failure.
                return res.status(400).json({ 
                    error: `Failed to checkout item ${productId}: ${err.response?.data?.error || err.message}`,
                    partialResults: results
                });
            }
        }

        // 3. Create order in Redis
        const orderId = `order:${Date.now()}:${userId}`;
        const orderData = {
            userId,
            items: cart,
            timestamp: new Date().toISOString(),
            status: 'COMPLETED'
        };
        await client.set(orderId, JSON.stringify(orderData));

        // 4. Clear cart
        await axios.delete(`${CART_URL}/cart/${userId}`);

        res.json({ success: true, orderId, orderData });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

app.listen(port, () => {
    console.log(`Order PU listening at http://localhost:${port}`);
});
