import { useState, useEffect } from 'react';
import { fetchRegionalData } from '../services/dataService';

export const useDataFetching = (selectedDatasets, isLoggedIn) => {
  const [dataByDataset, setDataByDataset] = useState({});
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

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
            return false;
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

  return {
    dataByDataset,
    isLoading,
    error
  };
}; 