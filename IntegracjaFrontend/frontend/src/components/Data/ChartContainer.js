import React from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer, ReferenceArea } from 'recharts';
import { getDatasetColors, getYAxisId, calculateDatasetDomain } from '../../utils/chartUtils';
import { referenceAreas } from '../../hooks/useAreas';
import { useAppLogic } from '../../hooks/useAppLogic';

export const ChartContainer = () => {
  const {
    combinedData,
    selectedDatasets,
    dataByDataset,
    selectedRegions,
    selectedYears,
    selectedAreas
  } = useAppLogic();

  const filteredReferenceAreas = referenceAreas.filter(area => selectedAreas.includes(area.name));
  console.log("combinedData:", selectedAreas);
  return (
    <div className="chart-container" style={{ 
      width: '95vw',
      height: '600px',
      margin: '0 auto',
      padding: '20px',
      overflow: 'hidden',
      maxWidth: '2000px'
    }}>
      <h2>Regional Data Comparison</h2>
      <div style={{ 
        width: '100%',
        height: '100%',
        marginBottom: '20px'
      }}>
        <ResponsiveContainer width="100%" height="100%">
          <LineChart 
            data={combinedData}
            margin={{ 
              top: 20, 
              right: 100,
              bottom: 30, 
              left: 100 
            }}
          >
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis 
              dataKey="year" 
              padding={{ left: 20, right: 20 }}
              scale="time"
              type="number"
              domain={['dataMin', 'dataMax']}
            />
            {selectedDatasets.map((dataset, datasetIndex) => {
              const axisColor = getDatasetColors(datasetIndex, selectedDatasets.length)[0];
              return (
                <YAxis
                  key={`y-axis-${dataset}`}
                  yAxisId={getYAxisId(datasetIndex)}
                  orientation={datasetIndex === 0 ? "left" : "right"}
                  stroke={axisColor}
                  tick={{ fill: axisColor }}
                  domain={calculateDatasetDomain(dataset, dataByDataset, selectedRegions, selectedYears)}
                  tickFormatter={(value) => typeof value === 'number' ? Number(value.toFixed(2)).toLocaleString() : value}
                  label={{ 
                    value: dataset, 
                    angle: -90, 
                    position: 'insideBottom',
                    offset: 5,
                    style: { 
                      textAnchor: 'start',
                      fill: axisColor
                    }
                  }}
                />
              );
            })}
            <Tooltip 
              formatter={(value) => typeof value === 'number' ? Number(value.toFixed(2)).toLocaleString() : value}
              labelFormatter={(label) => `Year: ${label}`}
            />
            <Legend 
              wrapperStyle={{ paddingTop: '20px' }}
              verticalAlign="bottom"
              height={36}
            />
            {selectedDatasets.map((dataset, datasetIndex) => {
              const colors = getDatasetColors(datasetIndex, selectedDatasets.length);
              return selectedRegions.map((region, index) => (
                <Line
                  key={`${dataset}_${region}`}
                  type="monotone"
                  dataKey={`${dataset}_${region}`}
                  name={`${dataset} - ${region}`}
                  stroke={colors[index % colors.length]}
                  strokeWidth={2}
                  dot={{ r: 3 }}
                  yAxisId={getYAxisId(datasetIndex)}
                  connectNulls
                />
              ));
            })}
            {filteredReferenceAreas.map((area, index) => (
              <ReferenceArea
                key={`reference-area-${index}`}
                label={{ 
                  value: area.name, 
                  position: 'bottom',
                  offset: -20,
                  fill: area.fill || "#00ff00",
                }}
                x1={area.x1}
                x2={area.x2}
                yAxisId={getYAxisId(0)}
                stroke={area.stroke || "#00ff00"}
                fill={area.fill || "#00ff00"}
                fillOpacity={area.fillOpacity || 0.3}
                strokeOpacity={area.strokeOpacity || 1}
                ifOverflow="visible"
              />
            ))}
          </LineChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
}; 