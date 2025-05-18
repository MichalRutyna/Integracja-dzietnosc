import React, { useState, useEffect } from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend } from 'recharts';
import Login from './components/Login';
import { isAuthenticated, logout } from './services/authService';
import { fetchRegionalData } from './services/dataService';
import './App.css';

function App() {
  const [data, setData] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [checkingAuth, setCheckingAuth] = useState(true);

  // Check authentication status
  useEffect(() => {
    const checkAuth = async () => {
      try {
        const isAuth = await isAuthenticated();
        setIsLoggedIn(isAuth);
      } catch (error) {
        setIsLoggedIn(false);
      } finally {
        setCheckingAuth(false);
      }
    };
    checkAuth();
  }, []);

  const fetchData = async () => {
    try {
      setIsLoading(true);
      setError(null);
      
      const transformedData = await fetchRegionalData();
      console.log("transformedData:", transformedData);
      setData(transformedData);
    } catch (err) {
      console.error('Error fetching data:', err);
      setError('Failed to fetch data. Please try again later.');
      if (err.response?.status === 401) {
        setIsLoggedIn(false);
      }
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    if (isLoggedIn) {
      fetchData();
      console.log("isLoggedIn:", isLoggedIn);
    }
  }, [isLoggedIn]);

  const handleLoginSuccess = () => {
    setIsLoggedIn(true);
  };

  const handleLogout = () => {
    logout();
    setIsLoggedIn(false);
  };

  if (checkingAuth) {
    return <div className="loading">Checking authentication...</div>;
  }

  if (!isLoggedIn) {
    return <Login onLoginSuccess={handleLoginSuccess} />;
  }

  return (
    <div className="App">
      <header className="App-header">
        <h1>Regional Data Visualization</h1>
        <button onClick={handleLogout} className="logout-button">Logout</button>
      </header>
      <main>
        {isLoading && <div className="loading">Loading...</div>}
        {error && <div className="error">{error}</div>}
        {!isLoading && !error && (
          <div className="chart-container">
            <LineChart width={800} height={400} data={data}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="year" />
              <YAxis />
              <Tooltip />
              <Legend />
              <Line type="monotone" dataKey="North" stroke="#8884d8" />
              <Line type="monotone" dataKey="South" stroke="#82ca9d" />
              <Line type="monotone" dataKey="East" stroke="#ffc658" />
            </LineChart>
          </div>
        )}
      </main>
    </div>
  );
}

export default App; 