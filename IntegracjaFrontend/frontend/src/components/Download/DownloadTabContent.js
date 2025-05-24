import React, { useState, useEffect, useCallback } from 'react';
import { fetchAvailableDatasets, downloadData, getDownloadStatus } from '../../services/downloadService';
import { fetchAvailableDatasets as fetchInDatabaseDatasets } from '../../services/dataService';

const DownloadTabContent = () => {
  const [datasets, setDatasets] = useState([]);
  const [selectedDataset, setSelectedDataset] = useState('');
  const [progress, setProgress] = useState(0);
  const [status, setStatus] = useState('');
  const [isDownloading, setIsDownloading] = useState(false);


  useEffect(() => {
    const loadDatasets = async () => {
      const availableDatasets = await fetchAvailableDatasets();
      const inDatabaseDatasets = await fetchInDatabaseDatasets();
      console.log(availableDatasets);
      console.log(inDatabaseDatasets);
      setDatasets(availableDatasets);
    };
    loadDatasets();
  }, []);

  const handleDownload = async () => {
    setIsDownloading(true);
    const message = await downloadData(selectedDataset);
    console.log('Downloading dataset:', selectedDataset);
    setStatus(message);
    
    const statusInterval = setInterval(async () => {
        const isComplete = await handleGetStatus();
        if (isComplete) {
            setIsDownloading(false);
            console.log('Download completed');
            clearInterval(statusInterval);
        }
    }, 200);
  };

  const handleGetStatus = async () => {
    const [message, currentProgress] = await getDownloadStatus();
    setStatus(message);
    setProgress(currentProgress);
    return currentProgress === 100;
  };

  return (
    <div className="download-tab-content">
      <h2>Download data into the database</h2>
      <div>
        <select 
          value={selectedDataset} 
          onChange={(e) => setSelectedDataset(e.target.value)}
          disabled={isDownloading}
        >
          <option value="">Select a dataset</option>
          {datasets.map((dataset, index) => (
            <option key={index} value={dataset}>
              {dataset}
            </option>
          ))}
        </select>
        <button onClick={handleDownload} disabled={isDownloading || !selectedDataset}>
          {isDownloading ? 'Downloading...' : 'Download'}
        </button>
      </div>
      <div>
        <progress value={progress} max="100" />
        <p>{status}</p>
      </div>
    </div>
  );
};

export default DownloadTabContent; 