const express = require('express');
const axios = require('axios');

const { restAuthMiddleware } = require('../middleware/auth');


const router = express.Router()
const download_url = process.env.INTERACTOR_URL || 'http://localhost:8090/api';

// Get available datasets to download
router.get('/api/download/datasets', restAuthMiddleware, async (req, res) => {
    try {
        const response = await axios.get(download_url + '/datasets');
        
        if (response.data && response.status === 200) {
            res.json({ 
                status: 'success',
                datasets: response.data
            });
        } else {
            throw new Error('Request failed: ' +response.headers);
        }     
    } catch (error) {
        console.error('Download request failed:', error);
        res.status(500).json({ 
            status: 'error',
            message: 'Failed to fetch available datasets'
        });
    }
});

router.post('/api/download', restAuthMiddleware, async (req, res) => {
    try {
        // Get dataset from query parameter
        const dataset = req.query.dataset || '';

        const response = await axios.post(download_url + '/download', {}, {
            params: { dataset },
        });
        if (response.data && response.status === 202) {
            res.json({ 
                status: 'success',
                message: response.data.message,
                progress: response.data.progress,
                taskId: response.data.taskId
            });
        } else {
            throw new Error('Request failed: ' + response.data);
        }
    } catch (error) {
        console.error('Download request failed:', error);
        res.status(500).json({ 
            status: 'error',
            message: 'Failed to fetch data'
        });
    }
});

router.get('/api/download/status', restAuthMiddleware, async (req, res) => {
    try {
        const taskId = req.query.taskId || '';
        const response = await axios.get(download_url + '/status', {
            params: { taskId },
        });

        if (response.data && response.status === 200) {
            res.json({
                request_status: "success",
                message: response.data.status,
                progress: response.data.progress,
            });
        } else {
            throw new Error('Request failed: ' + response.data);
        }
    } catch (error) {
        console.error('Download status request failed:', error);
        res.status(500).json({ 
            status: 'error',
            message: 'Failed to fetch download status'
        });
    }
});

module.exports = router;