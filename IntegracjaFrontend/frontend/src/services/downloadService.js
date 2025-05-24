import axios from 'axios';

let taskId = '';

const API_URL = 'http://localhost:3001/api/download';

export const fetchAvailableDatasets = async () => {
    try {
        const response = await axios.get(API_URL + '/datasets', { 
          withCredentials: true 
        });
        console.log(response.data);
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

export const downloadData = async (dataset = '') => {
    try {
      const response = await axios.get(API_URL, {
        params: { dataset },
        withCredentials: true
      });

      if (response.data && response.data.status === 'success') {
        taskId = response.data.taskId;
        return response.data.message;
      } else {
        console.error('Unexpected response format:', response.data);
        return [];
      }
    } catch (error) {
      console.error(`Error fetching ${dataset || 'regional'} data:`, error);
      return [];
    }
  }; 

export const getDownloadStatus = async () => {
    try {
        const response = await axios.get(API_URL + '/status', {
          params: { taskId },
          withCredentials: true
        });
  
        if (response.data && response.data.status === 'success') {
          taskId = response.data.taskId;
          return response.data.message;
        } else {
          console.error('Unexpected response format:', response.data);
          return [];
        }
      } catch (error) {
        console.error(`Error fetching status:`, error);
        return [];
      }
    }; 