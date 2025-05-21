import React from 'react';
import RangeSlider from 'react-range-slider-input';
import 'react-range-slider-input/dist/style.css';
import './DataControls.css';

export const DataControls = ({
  isLoading,
  availableDatasets,
  selectedDatasets,
  handleDatasetChange,
  availableYears,
  selectedYears,
  handleYearChange,
  availableRegions,
  selectedRegions,
  handleRegionChange
}) => (
  <div className="controls">
    <div className="control-group dataset-control">
      <label htmlFor="dataset-select">Select Datasets: </label>
      <select 
        id="dataset-select" 
        multiple
        value={selectedDatasets} 
        onChange={handleDatasetChange}
        disabled={isLoading || availableDatasets.length === 0}
        size={Math.min(4, availableDatasets.length)}
      >
        {availableDatasets.map(dataset => (
          <option key={dataset} value={dataset}>
            {dataset.charAt(0).toUpperCase() + dataset.slice(1)}
          </option>
        ))}
      </select>
    </div>

    <div className="control-group year-control">
      <label>Select Years Range: </label>
      <div className="year-slider">
        <RangeSlider
          min={Math.min(...availableYears)}
          max={Math.max(...availableYears)}
          value={[Math.min(...selectedYears), Math.max(...selectedYears)]}
          onInput={handleYearChange}
          disabled={isLoading || availableYears.length === 0}
          step={1}
        />
        <div className="year-range-labels">
          <span>{Math.min(...selectedYears)}</span>
          <span>{Math.max(...selectedYears)}</span>
        </div>
      </div>
    </div>

    <div className="control-group region-control">
      <label>Select Regions: </label>
      <div className="region-checkboxes">
        {availableRegions.map(region => (
          <div key={region} className="checkbox-item">
            <input
              type="checkbox"
              id={`region-${region}`}
              value={region}
              checked={selectedRegions.includes(region)}
              onChange={handleRegionChange}
              disabled={isLoading}
            />
            <label htmlFor={`region-${region}`}>{region}</label>
          </div>
        ))}
      </div>
    </div>
  </div>
); 