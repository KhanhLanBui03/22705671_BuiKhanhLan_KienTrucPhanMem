const express = require('express');
const redis = require('redis');
const cors = require('cors');

const app = express();
const port = 8081;

app.use(cors());
app.use(express.json());

const client = redis.createClient({
    url: process.env.REDIS_URL || 'redis://localhost:6379'
});

client.on('error', err => console.log('Redis Client Error', err));

async function seedData() {
    await client.connect();
    
    const productsExist = await client.exists('products');
    if (!productsExist) {
        console.log('Seeding products...');
        const products = [
            { id: '1', name: 'iPhone 15 Pro', price: 999, image: 'https://placehold.co/400x400/png?text=iPhone+15+Pro', description: 'Titanium design, A17 Pro chip.' },
            { id: '2', name: 'MacBook Air M3', price: 1099, image: 'https://placehold.co/400x400/png?text=MacBook+Air+M3', description: 'Thin and light, power efficient.' },
            { id: '3', name: 'AirPods Pro 2', price: 249, image: 'https://placehold.co/400x400/png?text=AirPods+Pro+2', description: 'Active Noise Cancellation.' },
            { id: '4', name: 'Apple Watch Ultra 2', price: 799, image: 'https://placehold.co/400x400/png?text=Apple+Watch+Ultra+2', description: 'The most rugged watch.' }
        ];

        for (const product of products) {
            await client.hSet('products', product.id, JSON.stringify(product));
            // Initialize stock in PU4's domain but let's do it here for simplicity in this demo
            await client.set(`stock:${product.id}`, 100); 
        }
        console.log('Seeding complete.');
    }
}

seedData();

app.get('/products', async (req, res) => {
    try {
        const products = await client.hGetAll('products');
        const productList = Object.values(products).map(p => JSON.parse(p));
        res.json(productList);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

app.get('/products/:id', async (req, res) => {
    try {
        const product = await client.hGet('products', req.params.id);
        if (product) {
            res.json(JSON.parse(product));
        } else {
            res.status(404).json({ error: 'Product not found' });
        }
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

app.listen(port, () => {
    console.log(`Product PU listening at http://localhost:${port}`);
});
