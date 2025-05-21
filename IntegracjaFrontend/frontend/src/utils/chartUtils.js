// Color generation utilities
export const generateShades = (hue) => [
  `hsl(${hue}, 70%, 50%)`,
  `hsl(${hue}, 70%, 65%)`,
  `hsl(${hue}, 70%, 80%)`
];

// Get distinct colors based on dataset index and total count
export const getDatasetColors = (index, totalDatasets) => {
  // Space hues evenly around the color wheel
  const hueStep = 360 / totalDatasets;
  const hue = (index * hueStep + Math.random() * (hueStep * 0.5)) % 360;
  return generateShades(hue);
};

// Utility function to get Y-axis ID
export const getYAxisId = (datasetIndex) => `y-axis-${datasetIndex}`;

// Calculate domain for a dataset
export const calculateDatasetDomain = (dataset, dataByDataset, selectedRegions, selectedYears) => {
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