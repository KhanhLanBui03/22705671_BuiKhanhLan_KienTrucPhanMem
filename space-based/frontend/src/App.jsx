import { useState, useEffect } from 'react'
import axios from 'axios'
import { ShoppingCart, Zap, Package, CheckCircle2 } from 'lucide-react'
import './App.css'

const PRODUCT_PU = 'http://localhost:8081'
const CART_PU = 'http://localhost:8082'
const ORDER_PU = 'http://localhost:8083'
const INVENTORY_PU = 'http://localhost:8084'

// Hardcoded userId for demo
const USER_ID = 'user_123'

function App() {
  const [products, setProducts] = useState([])
  const [cart, setCart] = useState({})
  const [stocks, setStocks] = useState({})
  const [isCartOpen, setIsCartOpen] = useState(false)
  const [loading, setLoading] = useState(false)
  const [orderStatus, setOrderStatus] = useState(null)

  useEffect(() => {
    fetchProducts()
    fetchCart()
  }, [])

  const fetchProducts = async () => {
    try {
      const res = await axios.get(`${PRODUCT_PU}/products`)
      setProducts(res.data)
      // Fetch stock for each
      res.data.forEach(p => fetchStock(p.id))
    } catch (err) {
      console.error('Failed to fetch products', err)
    }
  }

  const fetchStock = async (id) => {
    try {
      const res = await axios.get(`${INVENTORY_PU}/stock/${id}`)
      setStocks(prev => ({ ...prev, [id]: res.data.stock }))
    } catch (err) {
      console.error('Failed to fetch stock', err)
    }
  }

  const fetchCart = async () => {
    try {
      const res = await axios.get(`${CART_PU}/cart/${USER_ID}`)
      setCart(res.data)
    } catch (err) {
      console.error('Failed to fetch cart', err)
    }
  }

  const addToCart = async (productId) => {
    try {
      await axios.post(`${CART_PU}/cart/add`, { userId: USER_ID, productId, quantity: 1 })
      fetchCart()
      setIsCartOpen(true)
    } catch (err) {
      alert('Failed to add to cart')
    }
  }

  const handleCheckout = async () => {
    setLoading(true)
    try {
      const res = await axios.post(`${ORDER_PU}/checkout`, { userId: USER_ID })
      setOrderStatus('success')
      setCart({})
      setIsCartOpen(false)
      // Refresh stocks
      products.forEach(p => fetchStock(p.id))
    } catch (err) {
      alert(err.response?.data?.error || 'Checkout failed')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="app">
      <header className="glass">
        <div className="container header-content">
          <div className="logo">
            <Zap size={32} className="accent-color" />
            <h1>FLASH<span>SALE</span></h1>
          </div>
          <button className="btn btn-outline cart-trigger" onClick={() => setIsCartOpen(true)}>
            <ShoppingCart size={20} />
            <span>{Object.values(cart).reduce((a, b) => a + parseInt(b), 0)} Items</span>
          </button>
        </div>
      </header>

      <main className="container">
        <div className="hero">
          <div className="badge badge-flash">Limited Time Offer</div>
          <h2>Space-Based Architecture Demo</h2>
          <p>High-performance flash sale simulation with real-time stock processing.</p>
        </div>

        <div className="product-grid">
          {products.map(product => (
            <div key={product.id} className="product-card glass">
              <img src={product.image} alt={product.name} />
              <div className="product-info">
                <h3>{product.name}</h3>
                <p className="description">{product.description}</p>
                <div className="price-row">
                  <span className="price">${product.price}</span>
                  <span className={`stock-label ${stocks[product.id] < 10 ? 'low-stock' : ''}`}>
                    {stocks[product.id] ?? '-'} in stock
                  </span>
                </div>
                <button 
                  className="btn btn-primary w-full" 
                  onClick={() => addToCart(product.id)}
                  disabled={stocks[product.id] === 0}
                >
                  {stocks[product.id] === 0 ? 'Out of Stock' : 'Add to Cart'}
                </button>
              </div>
            </div>
          ))}
        </div>
      </main>

      {isCartOpen && (
        <div className="cart-overlay" onClick={() => setIsCartOpen(false)}>
          <div className="cart-drawer glass" onClick={e => e.stopPropagation()}>
            <div className="drawer-header">
              <h3>Your Cart</h3>
              <button className="close-btn" onClick={() => setIsCartOpen(false)}>&times;</button>
            </div>
            <div className="drawer-content">
              {Object.keys(cart).length === 0 ? (
                <div className="empty-cart">
                  <Package size={48} className="muted" />
                  <p>Your cart is empty</p>
                </div>
              ) : (
                <div className="cart-items">
                  {Object.entries(cart).map(([id, qty]) => {
                    const product = products.find(p => p.id === id)
                    return (
                      <div key={id} className="cart-item">
                        <img src={product?.image} alt={product?.name} />
                        <div className="item-details">
                          <h4>{product?.name}</h4>
                          <p>${product?.price} x {qty}</p>
                        </div>
                      </div>
                    )
                  })}
                </div>
              )}
            </div>
            {Object.keys(cart).length > 0 && (
              <div className="drawer-footer">
                <button 
                  className="btn btn-primary w-full" 
                  onClick={handleCheckout}
                  disabled={loading}
                >
                  {loading ? 'Processing...' : 'Checkout Now'}
                </button>
              </div>
            )}
          </div>
        </div>
      )}

      {orderStatus === 'success' && (
        <div className="modal-overlay">
          <div className="modal glass">
            <CheckCircle2 size={64} className="success-icon" />
            <h2>Order Successful!</h2>
            <p>Your flash sale order has been processed via Space-Based Architecture.</p>
            <button className="btn btn-primary" onClick={() => setOrderStatus(null)}>Continue Shopping</button>
          </div>
        </div>
      )}
    </div>
  )
}

export default App
