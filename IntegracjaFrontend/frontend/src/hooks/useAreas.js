import { useState } from 'react';

const referenceAreas = require('../areas.json');

export const useAreas = () => {
  const [selectedReferenceAreas, setSelectedReferenceAreas] = useState([]);
  
  const handleReferenceAreaChange = (e) => {
    const area = e.target.value;
    setSelectedReferenceAreas(prev => {
      if (e.target.checked) {
        return [...prev, area];
      } else {
        return prev.filter(a => a !== area);
      }
    });
  };

  return {
    availableReferenceAreas: referenceAreas.map(area => area.name),
    selectedReferenceAreas,
    handleReferenceAreaChange,
  };
};

export { referenceAreas };