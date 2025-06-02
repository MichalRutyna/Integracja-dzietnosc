import { useState, useEffect } from 'react';
import { fetchAvailableDatasets } from '../services/dataService';

export const useDatasets = (isLoggedIn) => {
  const [availableDatasets, setAvailableDatasets] = useState([]);
  const [selectedDatasets, setSelectedDatasets] = useState([]);

  const fetchDatasets = async () => {
    try {
      console.log("fetching datasets")
      const datasets = await fetchAvailableDatasets();
      setAvailableDatasets(datasets);
      if (datasets.length > 0) {
        setSelectedDatasets([datasets[0]]);
      }
    } catch (err) {
      console.error('Error fetching datasets:', err);
      if (err.response?.status === 401) {
        return false;
      }
    }
  };

  useEffect(() => {
    if (isLoggedIn) {
      fetchDatasets();
    }
  }, [isLoggedIn]);

  const handleDatasetChange = (e) => {
    const options = e.target.options;
    const selected = [];
    for (let i = 0; i < options.length; i++) {
      if (options[i].selected) {
        selected.push(options[i].value);
      }
    }
    requestAnimationFrame(() => {
      setSelectedDatasets(selected);
    });
  };

  return {
    availableDatasets,
    selectedDatasets,
    handleDatasetChange,
    fetchDatasets
  };
}; 