const express = require('express');
const soap = require('soap');

const { restAuthMiddleware } = require('../middleware/auth');


const router = express.Router()
const soap_url = process.env.SOAP_URL || 'http://localhost:8080/data-service?wsdl';

// Get available datasets
router.get('/api/soap/datasets', restAuthMiddleware, async (req, res) => {
    try {
        const client = await soap.createClientAsync(soap_url);
        const token = req.cookies?.authToken || 
                     (req.headers.authorization && req.headers.authorization.split(' ')[1]);

        const soapHeader = {
            "tns:Security": {
                "tns:BearerToken": token
            }
        };
        client.addSoapHeader(soapHeader);

        const args = {}; 
        const result = await client.getAvailableDatasetsAsync(args);
        
        res.json({ 
            status: 'success',
            datasets: result[0].datasets 
        });
    } catch (error) {
        console.error('SOAP request failed:', error);
        res.status(500).json({ 
            status: 'error',
            message: 'Failed to fetch available datasets'
        });
    }
});

router.get('/api/soap/data', restAuthMiddleware, async (req, res) => {
    try {
        const client = await soap.createClientAsync(soap_url);
        const token = req.cookies?.authToken || 
                     (req.headers.authorization && req.headers.authorization.split(' ')[1]);

        // Get dataset from query parameter
        const dataset = req.query.dataset || '';

        const soapHeader = {
            "tns:Security": {
                "tns:BearerToken": token
            }
        };
        client.addSoapHeader(soapHeader);

        const args = { 
            dataset: dataset
        }; 
        const result = await client.getRegionalDataAsync(args);
        
        res.json(result[0].result);
    } catch (error) {
        console.error('SOAP request failed:', error);
        res.status(500).json({ 
            status: 'error',
            message: 'Failed to fetch data'
        });
    }
});

module.exports = router;