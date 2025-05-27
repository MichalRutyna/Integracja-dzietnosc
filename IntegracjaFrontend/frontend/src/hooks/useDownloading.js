import { useState, useEffect } from 'react';
import { fetchAvailableDatasets, downloadData, getDownloadStatus } from '../services/downloadService';
import { fetchAvailableDatasets as fetchInDatabaseDatasets } from '../services/dataService';

export const useDownloading = () => {
  const [datasets, setDatasets] = useState([]);
  const [selectedDataset, setSelectedDataset] = useState('');
  const [progress, setProgress] = useState(0);
  const [status, setStatus] = useState('');
  const [isDownloading, setIsDownloading] = useState(false);

  useEffect(() => {
    const loadDatasets = async () => {
      const availableDatasets = await fetchAvailableDatasets();
      const inDatabaseDatasets = await fetchInDatabaseDatasets();
      
      // Only datasets not already in the database
      setDatasets(availableDatasets.filter(dataset => !inDatabaseDatasets.includes(dataset)));
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

  return {
    datasets,
    selectedDataset,
    setSelectedDataset,
    progress,
    status,
    isDownloading,
    handleDownload,
  };
};
