import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { User, Film, Ticket, LogOut, CheckCircle, XCircle, Clock } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import './App.css';

const API_USER = 'http://10.242.134.57:8081/api/users';
const API_MOVIE = 'http://10.242.134.101:8082/api/movies';
const API_BOOKING = 'http://10.242.134.57:8083/api/bookings';

function App() {
  const [user, setUser] = useState(null);
  const [view, setView] = useState('movies');
  const [movies, setMovies] = useState([]);
  const [bookings, setBookings] = useState([]);
  const [loginData, setLoginData] = useState({ username: '', password: '' });
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (user) {
      fetchMovies();
      fetchBookings();
      const interval = setInterval(fetchBookings, 3000); // Poll for status updates
      return () => clearInterval(interval);
    }
  }, [user]);

  const fetchMovies = async () => {
    try {
      const res = await axios.get(API_MOVIE);
      setMovies(res.data);
    } catch (e) { console.error(e); }
  };

  const fetchBookings = async () => {
    try {
      const res = await axios.get(API_BOOKING);
      setBookings(res.data.filter(b => b.userId === user.username));
    } catch (e) { console.error(e); }
  };

  const handleLogin = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      // Mock login for demo, since user-service uses admin/password
      if (loginData.username === 'admin' && loginData.password === 'password') {
        setUser({ username: loginData.username });
      } else {
        // Try real register then login
        await axios.post(`${API_USER}/register`, loginData);
        setUser({ username: loginData.username });
      }
    } catch (e) {
      alert("Login/Register failed. Try admin/password");
    }
    setLoading(false);
  };

  const handleBooking = async (movieId) => {
    try {
      await axios.post(API_BOOKING, { movieId, userId: user.username });
      fetchBookings();
      setView('bookings');
    } catch (e) { console.error(e); }
  };

  if (!user) {
    return (
      <div className="container">
        <motion.div 
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="card login-form"
        >
          <h2 style={{ textAlign: 'center', marginBottom: '1rem' }}>Movie Ticket System</h2>
          <form onSubmit={handleLogin} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            <div className="input-group">
              <label>Username</label>
              <input 
                type="text" 
                value={loginData.username} 
                onChange={e => setLoginData({...loginData, username: e.target.value})}
                required 
              />
            </div>
            <div className="input-group">
              <label>Password</label>
              <input 
                type="password" 
                value={loginData.password} 
                onChange={e => setLoginData({...loginData, password: e.target.value})}
                required 
              />
            </div>
            <button type="submit" className="btn" style={{ justifyContent: 'center' }} disabled={loading}>
              {loading ? 'Processing...' : 'Login / Register'}
            </button>
          </form>
          <p style={{ fontSize: '0.8rem', color: 'var(--text-muted)', textAlign: 'center' }}>
            Hint: Use admin / password
          </p>
        </motion.div>
      </div>
    );
  }

  return (
    <div className="container">
      <header className="header">
        <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
          <Film size={32} color="var(--primary)" />
          <h1 style={{ fontSize: '1.5rem' }}>CinemaFlow</h1>
        </div>
        <div style={{ display: 'flex', gap: '1rem', alignItems: 'center' }}>
          <button 
            className={`btn ${view === 'movies' ? '' : 'btn-outline'}`} 
            onClick={() => setView('movies')}
          >
            <Film size={18} /> Movies
          </button>
          <button 
            className={`btn ${view === 'bookings' ? '' : 'btn-outline'}`} 
            onClick={() => setView('bookings')}
          >
            <Ticket size={18} /> My Bookings
          </button>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginLeft: '1rem', padding: '0.5rem', background: 'rgba(255,255,255,0.05)', borderRadius: '0.5rem' }}>
            <User size={18} />
            <span style={{ fontWeight: 600 }}>{user.username}</span>
            <LogOut size={18} style={{ cursor: 'pointer', marginLeft: '0.5rem' }} onClick={() => setUser(null)} />
          </div>
        </div>
      </header>

      <main>
        <AnimatePresence mode="wait">
          {view === 'movies' ? (
            <motion.div 
              key="movies"
              initial={{ opacity: 0, x: -20 }}
              animate={{ opacity: 1, x: 0 }}
              exit={{ opacity: 0, x: 20 }}
              className="movie-grid"
            >
              {movies.map(movie => (
                <div key={movie.id} className="card">
                  <div style={{ height: '200px', background: 'linear-gradient(45deg, #312e81, #1e1b4b)', borderRadius: '0.5rem', marginBottom: '1rem', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                    <Film size={48} color="rgba(255,255,255,0.2)" />
                  </div>
                  <h3>{movie.title}</h3>
                  <p style={{ color: 'var(--text-muted)', marginBottom: '1rem' }}>{movie.genre}</p>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <span style={{ fontSize: '1.25rem', fontWeight: 700 }}>{movie.price.toLocaleString()} VNĐ</span>
                    <button className="btn" onClick={() => handleBooking(movie.id)}>Book Now</button>
                  </div>
                </div>
              ))}
            </motion.div>
          ) : (
            <motion.div 
              key="bookings"
              initial={{ opacity: 0, x: 20 }}
              animate={{ opacity: 1, x: 0 }}
              exit={{ opacity: 0, x: -20 }}
              style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}
            >
              <h2>Your Bookings</h2>
              {bookings.length === 0 && <p>No bookings found.</p>}
              {bookings.map(booking => (
                <div key={booking.id} className="card" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '1.5rem' }}>
                    <div className={`status-badge status-${booking.status}`} style={{ width: '40px', height: '40px', display: 'flex', alignItems: 'center', justifyContent: 'center', borderRadius: '12px' }}>
                      {booking.status === 'SUCCESS' && <CheckCircle size={20} />}
                      {booking.status === 'FAILED' && <XCircle size={20} />}
                      {booking.status === 'PENDING' && <Clock size={20} />}
                    </div>
                    <div>
                      <h4 style={{ fontSize: '1.1rem' }}>Booking #{booking.id}</h4>
                      <p style={{ color: 'var(--text-muted)' }}>Movie ID: {booking.movieId}</p>
                    </div>
                  </div>
                  <div className={`status-badge status-${booking.status}`}>
                    {booking.status}
                  </div>
                </div>
              ))}
            </motion.div>
          )}
        </AnimatePresence>
      </main>
    </div>
  );
}

export default App;
