import React, { useState, useEffect, useCallback } from 'react';
import { fetchAvailableDatasets, downloadData, getDownloadStatus } from '../../services/downloadService';

const DownloadTabContent = () => {
  const [datasets, setDatasets] = useState([]);
  const [selectedDataset, setSelectedDataset] = useState('');
  const [progress, setProgress] = useState(0);
  const [status, setStatus] = useState('');
  const [logs, setLogs] = useState([]);


  useEffect(() => {
    const loadDatasets = async () => {
      const availableDatasets = await fetchAvailableDatasets();
      setDatasets(availableDatasets);
    };
    loadDatasets();
  }, []);

  const handleDownload = async () => {
    console.log('Downloading dataset:', selectedDataset);
  };
  
  return (
    <div className="download-tab-content">
      <h2>Download data into the database</h2>
      <div>
        <select 
          value={selectedDataset} 
          onChange={(e) => setSelectedDataset(e.target.value)}
        >
          <option value="">Select a dataset</option>
          {datasets.map((dataset, index) => (
            <option key={index} value={dataset}>
              {dataset}
            </option>
          ))}
        </select>
        <button onClick={handleDownload}>Download</button>
      </div>
      <div>
        <progress value={progress} max="100" />
        <p>{status}</p>
      </div>
      <div>
        <h3>Logs:</h3>
        <ul>
          {logs.map((log, index) => (
            <li key={index}>{log}</li>
          ))}
        </ul>
      </div>
    </div>
  );
};

export default DownloadTabContent; 