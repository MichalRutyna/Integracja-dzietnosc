import axios from 'axios';

const API_URL = 'http://localhost:3001/api/data';

export const fetchAvailableDatasets = async () => {
  try {
    const response = await axios.get(API_URL + '/datasets', { 
      withCredentials: true 
    });
    
    if (response.data && response.data.status === 'success') {
      return response.data.datasets;
    } else {
      console.error('Unexpected response format:', response.data);
      return [];
    }
  } catch (error) {
    console.error('Error fetching available datasets:', error);
    return [];
  }
};

export const fetchRegionalData = async (dataset = '') => {
  try {
    const response = await axios.get(API_URL, {
      params: { dataset },
      withCredentials: true
    });
    
    // Transform data for chart
    const regionalData = response.data;
    
    return regionalData.reduce((acc, item) => {
      const yearData = acc.find(d => d.year === item.Year);
      if (yearData) {
        yearData[item.Region] = item.Value;
      } else {
        acc.push({
          year: item.Year,
          [item.Region]: item.Value
        });
      }
      return acc;
    }, []);
  } catch (error) {
    console.error(`Error fetching ${dataset || 'regional'} data:`, error);
    return [];
  }
}; 