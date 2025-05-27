import { useMemo } from 'react';

export const useCombinedData = (selectedDatasets, dataByDataset, selectedYears, selectedRegions) => {
  return useMemo(() => {
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
    console.log(yearMap);
    return Array.from(yearMap.values()).sort((a, b) => a.year - b.year);
  }, [selectedDatasets, dataByDataset, selectedYears, selectedRegions]);
}; 