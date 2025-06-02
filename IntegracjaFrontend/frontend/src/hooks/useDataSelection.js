import { useState, useEffect } from 'react';

export const useDataSelection = (dataByDataset) => {
  const [selectedYears, setSelectedYears] = useState([]);
  const [selectedRegions, setSelectedRegions] = useState([]);
  const [availableYears, setAvailableYears] = useState([]);
  const [availableRegions, setAvailableRegions] = useState([]);

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
    // if (selectedRegions.length === 0 && sortedRegions.length > 0) {
    //   setSelectedRegions(sortedRegions);
    // }
  }, [dataByDataset, selectedYears.length, selectedRegions.length]);

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

  return {
    selectedYears,
    selectedRegions,
    availableYears,
    availableRegions,
    handleYearChange,
    handleRegionChange
  };
}; 