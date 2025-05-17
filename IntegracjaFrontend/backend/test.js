const axios = require('axios');
const soap = require('soap');

async function testService() {
    try {
        console.log('Step 1: Getting JWT token...');
        const loginResponse = await axios.post('http://localhost:3001/api/login', {
            username: 'admin',
            password: 'password'
        });
        const token = loginResponse.data.token;
        console.log('Token received:', token);

        console.log('\nStep 2: Creating SOAP client...');
        const client = await soap.createClientAsync('http://localhost:3001/wsdl?wsdl');
        
        console.log('Step 3: Adding security header...');
        const soapHeader = {
            Security: {
                BearerToken: token,
            }
        };
        client.addSoapHeader(soapHeader);

        console.log('Step 4: Making SOAP request...');
        const result = await client.getRegionalDataAsync({});
        console.log('\nSOAP Response:', JSON.stringify(result[0], null, 2));

        console.log('\nStep 5: Testing REST endpoint...');
        const restResponse = await axios.get('http://localhost:3001/api/data', {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        console.log('REST Response:', JSON.stringify(restResponse.data, null, 2));

    } catch (error) {
        console.error('Error:', error.message);
        if (error.response) {
            console.error('Response data:', error.response.data);
            console.error('Response status:', error.response.status);
        } else if (error.body) {
            console.error('SOAP Fault:', error.body);
        }
    }
}

// Run the test
console.log('Starting test...\n');
testService(); 