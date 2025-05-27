import axios from 'axios';

let taskId = '';

const API_URL = 'http://localhost:3001/api/download';

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

export const downloadData = async (dataset = '') => {
    try {
      const response = await axios.post(API_URL, {
        withCredentials: true
      }, {
        params: { dataset },
      });

      if (response.data && response.data.status === 'success') {
        taskId = response.data.taskId;
        return response.data.message;
      } else {
        console.error('Unexpected response format:', response.data);
        return [];
      }
    } catch (error) {
      console.error(`Error fetching ${dataset} data:`, error);
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
          return [response.data.message, response.data.progress];
        } else {
          console.error('Unexpected response format:', response.data);
          return [];
        }
      } catch (error) {
        console.error(`Error fetching status:`, error);
        return [];
      }
    }; 