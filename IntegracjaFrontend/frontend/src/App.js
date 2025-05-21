import React, { useState, useEffect } from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend } from 'recharts';
import Login from './components/Login';
import { isAuthenticated, logout } from './services/authService';
import { fetchRegionalData, fetchAvailableDatasets } from './services/dataService';
import './App.css';

function App() {
  const [data, setData] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [checkingAuth, setCheckingAuth] = useState(true);
  const [availableDatasets, setAvailableDatasets] = useState([]);
  const [selectedDataset, setSelectedDataset] = useState('');

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

  const fetchDatasets = async () => {
    try {
      const datasets = await fetchAvailableDatasets();
      setAvailableDatasets(datasets);
      if (datasets.length > 0) {
        setSelectedDataset(datasets[0]);
      }
    } catch (err) {
      console.error('Error fetching datasets:', err);
      if (err.response?.status === 401) {
        setIsLoggedIn(false);
      }
    }
  };

  const fetchData = async (dataset) => {
    try {
      setIsLoading(true);
      setError(null);
      
      const transformedData = await fetchRegionalData(dataset);
      console.log(`Transformed ${dataset} data:`, transformedData);
      setData(transformedData);
    } catch (err) {
      console.error(`Error fetching ${dataset} data:`, err);
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
      fetchDatasets();
    }
  }, [isLoggedIn]);

  useEffect(() => {
    if (selectedDataset) {
      fetchData(selectedDataset);
    }
  }, [selectedDataset]);

  const handleDatasetChange = (e) => {
    setSelectedDataset(e.target.value);
  };

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
        <div className="controls">
          <label htmlFor="dataset-select">Select Dataset: </label>
          <select 
            id="dataset-select" 
            value={selectedDataset} 
            onChange={handleDatasetChange}
            disabled={isLoading || availableDatasets.length === 0}
          >
            {availableDatasets.map(dataset => (
              <option key={dataset} value={dataset}>
                {dataset.charAt(0).toUpperCase() + dataset.slice(1)}
              </option>
            ))}
          </select>
        </div>
        
        {isLoading && <div className="loading">Loading...</div>}
        {error && <div className="error">{error}</div>}
        {!isLoading && !error && (
          <div className="chart-container">
            <h2>{selectedDataset.charAt(0).toUpperCase() + selectedDataset.slice(1)} by Region</h2>
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