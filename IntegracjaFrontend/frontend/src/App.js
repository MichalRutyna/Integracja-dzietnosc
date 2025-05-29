import React, { useState } from 'react';
import { Tab, Tabs, TabList, TabPanel } from 'react-tabs';
import 'react-tabs/style/react-tabs.css';
import { ChartContainer } from './components/Data/ChartContainer';
import { AuthControls } from './components/Auth/AuthControls';
import { DataControls } from './components/Data/DataControls';
import { useAppLogic } from './hooks/useAppLogic';
import './App.css';
import DownloadTabContent from './components/Download/DownloadTabContent';
import ChangePassword from './components/Auth/ChangePassword';

import DeleteAccountButton from './components/Auth/Buttons/DeleteAccountButton';
import Logout from './components/Auth/Buttons/Logout';
function App() {
  const {
    isLoading,
    error,
    isLoggedIn,
    checkingAuth,
  } = useAppLogic();


  const [showChangePassword, setShowChangePassword] = useState(false);

  if (checkingAuth) {
    return <div className="loading">Checking authentication...</div>;
  }

  return (
    <div className="App">
      <header className="App-header">
        <h1>Regional Data Visualization</h1>
        {isLoggedIn && (
          <div className="header-buttons">
            <Logout />
            <button
              onClick={() => setShowChangePassword(true)}
              className="change-password-button small-button"
            >
              Change Password
            </button>
            <DeleteAccountButton />
          </div>
        )}
      </header>

      {showChangePassword && isLoggedIn && (
        <div className="modal-overlay">
          <div className="modal-content">
            <button
              className="close-button"
              onClick={() => setShowChangePassword(false)}
              style={{ width: '40%' }}
            >
              ×
            </button>
            <ChangePassword />
          </div>
        </div>
      )}

      <main>
        <AuthControls />

        {isLoggedIn && (
          <Tabs>
            <TabList>
              <Tab>Data Visualization</Tab>
              <Tab>Download Data</Tab>
            </TabList>

            <TabPanel>
              <DataControls />
              {isLoading && <div className="loading">Loading...</div>}
              {error && <div className="error">{error}</div>}
              {!isLoading && !error && (
                <ChartContainer />
              )}
            </TabPanel>

            <TabPanel>
              <DownloadTabContent />
            </TabPanel>
          </Tabs>
        )}
      </main>
    </div>
  );
}

export default App; 