import React, { useState, useEffect } from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import RangeSlider from 'react-range-slider-input';
import 'react-range-slider-input/dist/style.css';
import Login from './components/Login';
import { isAuthenticated, logout } from './services/authService';
import { fetchRegionalData, fetchAvailableDatasets } from './services/dataService';
import './App.css';

// Color generation utilities
const generateShades = (hue) => [
  `hsl(${hue}, 70%, 50%)`,
  `hsl(${hue}, 70%, 65%)`,
  `hsl(${hue}, 70%, 80%)`
];

// Get distinct colors based on dataset index and total count
const getDatasetColors = (index, totalDatasets) => {
  // Space hues evenly around the color wheel
  const hueStep = 360 / totalDatasets;
  const hue = (index * hueStep + Math.random() * (hueStep * 0.5)) % 360;
  return generateShades(hue);
};

// Add this new utility function after the getDatasetColors function
const getYAxisId = (datasetIndex) => `y-axis-${datasetIndex}`;

// Add these utility functions after getYAxisId
const calculateDatasetDomain = (dataset, dataByDataset, selectedRegions, selectedYears) => {
  if (!dataByDataset[dataset]) return [0, 100];
  
  let min = Infinity;
  let max = -Infinity;
  
  dataByDataset[dataset].forEach(item => {
    if (selectedYears.includes(item.year)) {
      selectedRegions.forEach(region => {
        const value = item[region];
        if (value !== undefined && value !== null) {
          min = Math.min(min, value);
          max = Math.max(max, value);
        }
      });
    }
  });
  
  if (min === Infinity || max === -Infinity) return [0, 100];
  
  // Calculate 20% padding on both sides
  const range = max - min;
  const padding = range * 0.2;
  
  return [
    min - padding,
    max + padding
  ];
};

function App() {
  const [dataByDataset, setDataByDataset] = useState({});
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [checkingAuth, setCheckingAuth] = useState(true);
  const [availableDatasets, setAvailableDatasets] = useState([]);
  const [selectedDatasets, setSelectedDatasets] = useState([]);
  const [selectedYears, setSelectedYears] = useState([]);
  const [selectedRegions, setSelectedRegions] = useState([]);
  const [availableYears, setAvailableYears] = useState([]);
  const [availableRegions, setAvailableRegions] = useState([]);

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
        setSelectedDatasets([datasets[0]]);
      }
    } catch (err) {
      console.error('Error fetching datasets:', err);
      if (err.response?.status === 401) {
        setIsLoggedIn(false);
      }
    }
  };

  useEffect(() => {
    if (isLoggedIn) {
      fetchDatasets();
    }
  }, [isLoggedIn]);

  // Separate effect for data fetching to prevent unnecessary requests
  useEffect(() => {
    const fetchMissingDatasets = async () => {
      const newDatasets = selectedDatasets.filter(dataset => !dataByDataset[dataset]);
      
      if (newDatasets.length > 0) {
        setIsLoading(true);
        try {
          await Promise.all(newDatasets.map(async (dataset) => {
            const transformedData = await fetchRegionalData(dataset);
            setDataByDataset(prev => ({
              ...prev,
              [dataset]: transformedData
            }));
          }));
        } catch (err) {
          console.error('Error fetching datasets:', err);
          setError('Failed to fetch data. Please try again later.');
          if (err.response?.status === 401) {
            setIsLoggedIn(false);
          }
        } finally {
          setIsLoading(false);
        }
      }
    };

    if (selectedDatasets.length > 0) {
      fetchMissingDatasets();
    }
  }, [selectedDatasets, dataByDataset]);

  // Update available years and regions when data changes
  useEffect(() => {
    const years = new Set();
    const regions = new Set();

    Object.values(dataByDataset).forEach(dataset => {
      dataset.forEach(item => {
        years.add(item.year);
        // Add all keys except 'year' as they represent regions
        Object.keys(item).forEach(key => {
          if (key !== 'year') {
            regions.add(key);
          }
        });
      });
    });

    const sortedYears = Array.from(years).sort((a, b) => a - b);
    const sortedRegions = Array.from(regions).sort();

    setAvailableYears(sortedYears);
    setAvailableRegions(sortedRegions);

    // Initialize selections if empty
    if (selectedYears.length === 0 && sortedYears.length > 0) {
      setSelectedYears(sortedYears);
    }
    if (selectedRegions.length === 0 && sortedRegions.length > 0) {
      setSelectedRegions(sortedRegions);
    }
  }, [dataByDataset, selectedYears.length, selectedRegions.length]);

  const handleDatasetChange = (e) => {
    const options = e.target.options;
    const selected = [];
    for (let i = 0; i < options.length; i++) {
      if (options[i].selected) {
        selected.push(options[i].value);
      }
    }
    // Use requestAnimationFrame to ensure smooth transition
    requestAnimationFrame(() => {
      setSelectedDatasets(selected);
    });
  };

  const handleYearChange = (values) => {
    const [minYear, maxYear] = values;
    const years = [];
    for (let year = Math.round(minYear); year <= Math.round(maxYear); year++) {
      years.push(year);
    }
    setSelectedYears(years);
  };

  const handleRegionChange = (e) => {
    const region = e.target.value;
    setSelectedRegions(prev => {
      if (e.target.checked) {
        return [...prev, region];
      } else {
        return prev.filter(r => r !== region);
      }
    });
  };

  const handleLoginSuccess = () => {
    setIsLoggedIn(true);
  };

  const handleLogout = () => {
    logout();
    setIsLoggedIn(false);
  };

  // Combine data from all selected datasets
  const combinedData = React.useMemo(() => {
    if (selectedDatasets.length === 0 || Object.keys(dataByDataset).length === 0) return [];

    const yearMap = new Map();

    selectedDatasets.forEach(dataset => {
      const data = dataByDataset[dataset];
      if (!data) return;

      data.forEach(item => {
        if (!selectedYears.includes(item.year)) return;
        
        const year = item.year;
        if (!yearMap.has(year)) {
          yearMap.set(year, { year });
        }
        const yearData = yearMap.get(year);
        
        // Add dataset prefix to region names to avoid conflicts
        Object.keys(item).forEach(key => {
          if (key !== 'year' && selectedRegions.includes(key)) {
            yearData[`${dataset}_${key}`] = item[key];
          }
        });
      });
    });

    return Array.from(yearMap.values()).sort((a, b) => a.year - b.year);
  }, [selectedDatasets, dataByDataset, selectedYears, selectedRegions]);

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
          <div className="control-group">
            <label htmlFor="dataset-select">Select Datasets: </label>
            <select 
              id="dataset-select" 
              multiple
              value={selectedDatasets} 
              onChange={handleDatasetChange}
              disabled={isLoading || availableDatasets.length === 0}
              size={Math.min(4, availableDatasets.length)}
            >
              {availableDatasets.map(dataset => (
                <option key={dataset} value={dataset}>
                  {dataset.charAt(0).toUpperCase() + dataset.slice(1)}
                </option>
              ))}
            </select>
          </div>
        </div>
        
        {isLoading && <div className="loading">Loading...</div>}
        {error && <div className="error">{error}</div>}
        {!isLoading && !error && (
          <div className="chart-container" style={{ 
            width: '95vw', // Almost full viewport width
            height: '600px',
            margin: '0 auto',
            padding: '20px',
            overflow: 'hidden',
            maxWidth: '2000px' // Add maximum width to prevent excessive stretching
          }}>
            <h2>Regional Data Comparison</h2>
            <div style={{ 
              width: '100%',
              height: '100%',
              marginBottom: '20px'
            }}>
              <ResponsiveContainer width="100%" height="100%">
                <LineChart 
                  data={combinedData}
                  margin={{ 
                    top: 20, 
                    right: 100,
                    bottom: 30, 
                    left: 100 
                  }}
                >
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis 
                    dataKey="year" 
                    padding={{ left: 20, right: 20 }}
                  />
                  {selectedDatasets.map((dataset, datasetIndex) => {
                    const axisColor = getDatasetColors(datasetIndex, selectedDatasets.length)[0];
                    return (
                      <YAxis
                        key={`y-axis-${dataset}`}
                        yAxisId={getYAxisId(datasetIndex)}
                        orientation={datasetIndex === 0 ? "left" : "right"}
                        stroke={axisColor}
                        tick={{ fill: axisColor }}
                        domain={calculateDatasetDomain(dataset, dataByDataset, selectedRegions, selectedYears)}
                        tickFormatter={(value) => typeof value === 'number' ? Number(value.toFixed(2)).toLocaleString() : value}
                        label={{ 
                          value: dataset, 
                          angle: -90, 
                          position: 'insideBottom',
                          offset: 5,
                          style: { 
                            textAnchor: 'start',
                            fill: axisColor
                          }
                        }}
                      />
                    );
                  })}
                  <Tooltip 
                    formatter={(value) => typeof value === 'number' ? Number(value.toFixed(2)).toLocaleString() : value}
                    labelFormatter={(label) => `Year: ${label}`}
                  />
                  <Legend 
                    wrapperStyle={{ paddingTop: '20px' }}
                    verticalAlign="bottom"
                    height={36}
                  />
                  {selectedDatasets.map((dataset, datasetIndex) => {
                    const colors = getDatasetColors(datasetIndex, selectedDatasets.length);
                    return selectedRegions.map((region, index) => (
                      <Line
                        key={`${dataset}_${region}`}
                        type="monotone"
                        dataKey={`${dataset}_${region}`}
                        name={`${dataset} - ${region}`}
                        stroke={colors[index % colors.length]}
                        strokeWidth={2}
                        dot={{ r: 3 }}
                        yAxisId={getYAxisId(datasetIndex)}
                        connectNulls
                      />
                    ));
                  })}
                </LineChart>
              </ResponsiveContainer>
            </div>
          </div>
        )}

        <div className="controls-bottom">
          <div className="control-group year-control">
            <label>Select Years Range: </label>
            <div className="year-slider">
              <RangeSlider
                min={Math.min(...availableYears)}
                max={Math.max(...availableYears)}
                value={[Math.min(...selectedYears), Math.max(...selectedYears)]}
                onInput={handleYearChange}
                disabled={isLoading || availableYears.length === 0}
                step={1}
              />
              <div className="year-range-labels">
                <span>{Math.min(...selectedYears)}</span>
                <span>{Math.max(...selectedYears)}</span>
              </div>
            </div>
          </div>

          <div className="control-group region-control">
            <label>Select Regions: </label>
            <div className="region-checkboxes">
              {availableRegions.map(region => (
                <div key={region} className="checkbox-item">
                  <input
                    type="checkbox"
                    id={`region-${region}`}
                    value={region}
                    checked={selectedRegions.includes(region)}
                    onChange={handleRegionChange}
                    disabled={isLoading}
                  />
                  <label htmlFor={`region-${region}`}>{region}</label>
                </div>
              ))}
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}

export default App; 