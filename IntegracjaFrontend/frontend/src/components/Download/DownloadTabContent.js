import React from 'react';
import { useDownloading } from '../../hooks/useDownloading';
import './DownloadTabContent.css';

const DownloadTabContent = () => {
  const {
    datasets,
    selectedDataset,
    setSelectedDataset,
    progress,
    status,
    isDownloading,
    handleDownload,
  } = useDownloading();

  return (
    <div className="download-tab-content">
      <h2 className="download-title">Download data into the database</h2>
      <div className="download-controls">
        <select
          className="dataset-select"
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
        <button
          className="download-button"
          onClick={handleDownload}
          disabled={isDownloading || !selectedDataset}
        >
          {isDownloading ? 'Downloading...' : 'Download'}
        </button>
      </div>
      <div className="progress-container">
        <progress className="progress-bar" value={progress} max="100" />
        <p className="status-text">{status}</p>
      </div>
    </div>
  );
};

export default DownloadTabContent; 