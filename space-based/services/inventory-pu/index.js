const express = require('express');
const redis = require('redis');
const cors = require('cors');

const app = express();
const port = 8084;

app.use(cors());
app.use(express.json());

const client = redis.createClient({
    url: process.env.REDIS_URL || 'redis://localhost:6379'
});

client.on('error', err => console.log('Redis Client Error', err));
client.connect();

app.get('/stock/:productId', async (req, res) => {
    try {
        const stock = await client.get(`stock:${req.params.productId}`);
        res.json({ productId: req.params.productId, stock: parseInt(stock) || 0 });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

app.post('/stock/decrease', async (req, res) => {
    const { productId, quantity } = req.body;
    const qty = parseInt(quantity) || 1;

    try {
        const stockKey = `stock:${productId}`;
        
        // Use WATCH for optimistic locking or just DECRBY if we don't mind negative stock.
        // For a flash sale, we MUST not oversell.
        
        // Using a Lua script for atomic "check and decrease"
        const script = `
            local current = redis.call('get', KEYS[1])
            if not current or tonumber(current) < tonumber(ARGV[1]) then
                return -1
            end
            return redis.call('decrby', KEYS[1], ARGV[1])
        `;
        
        const result = await client.eval(script, {
            keys: [stockKey],
            arguments: [qty.toString()]
        });

        if (result === -1) {
            return res.status(400).json({ success: false, error: 'Out of stock' });
        }

        res.json({ success: true, remaining: result });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

app.listen(port, () => {
    console.log(`Inventory PU listening at http://localhost:${port}`);
});
