# SOAP Data Visualization App

This is a full-stack application that fetches data from a SOAP service and visualizes it using React and Recharts.

## Project Structure

- `frontend/` - React application
- `backend/` - Express server

## Setup

1. Install dependencies for all parts of the application:
```bash
npm run install-all
```

2. Configure the SOAP endpoint:
   - Create a `.env` file in the `backend` directory
   - Add your SOAP service URL:
   ```
   SOAP_URL=your_soap_service_url
   ```

3. Start the application:
```bash
npm start
```

This will start both the backend server (on port 3001) and the frontend development server (on port 3000).

## Features

- Fetches data from SOAP service
- Displays data in an interactive line chart
- Supports multiple regions
- Responsive design

## Technologies Used

- Frontend:
  - React
  - Recharts for data visualization
  - Axios for HTTP requests
- Backend:
  - Express.js
  - SOAP client
  - CORS for cross-origin requests 