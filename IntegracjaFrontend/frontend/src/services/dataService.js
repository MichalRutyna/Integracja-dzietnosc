import axios from 'axios';

export const fetchRegionalData = async () => {
  const response = await axios.get('http://localhost:3001/api/data');
  
  // Transform data for chart
  const regionalData = response.data;
  console.log('Raw data from server:', regionalData); // Debug log
  
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
}; 