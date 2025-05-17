import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend } from 'recharts';
import Login from './components/Login';
import { isAuthenticated, getToken, logout } from './services/authService';
import './App.css';

function App() {
  const [data, setData] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isLoggedIn, setIsLoggedIn] = useState(isAuthenticated());

  const fetchData = async () => {
    try {
      setIsLoading(true);
      setError(null);

      const token = getToken();
      const response = await axios.get('/api/data', {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      // Transform data for chart
      const regionalData = response.data;
      const transformedData = regionalData.reduce((acc, item) => {
        const yearData = acc.find(d => d.year === item.year);
        if (yearData) {
          yearData[item.region] = item.value;
        } else {
          acc.push({
            year: item.year,
            [item.region]: item.value
          });
        }
        return acc;
      }, []);
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