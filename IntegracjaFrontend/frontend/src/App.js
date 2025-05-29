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

function App() {
  const {
    dataByDataset,
    isLoading,
    error,
    isLoggedIn,
    checkingAuth,
    availableDatasets,
    selectedDatasets,
    selectedYears,
    selectedRegions,
    availableYears,
    availableRegions,
    showRegister,
    handleDatasetChange,
    handleYearChange,
    handleRegionChange,
    handleLoginSuccess,
    handleLogout,
    handleRegisterSuccess,
    setShowRegister,
    combinedData,
    availableReferenceAreas,
    selectedReferenceAreas,
    handleReferenceAreaChange
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
            <button onClick={handleLogout} className="logout-button small-button">Logout</button>
            <button 
              onClick={() => setShowChangePassword(true)} 
              className="change-password-button small-button"
            >
              Change Password
            </button>
          </div>
        )}
      </header>
      
      {showChangePassword && isLoggedIn && (
        <div className="modal-overlay">
          <div className="modal-content">
            <button 
              className="close-button" 
              onClick={() => setShowChangePassword(false)}
              style={{width: '40%'}}
            >
              ×
            </button>
            <ChangePassword onSuccess={() => setShowChangePassword(false)} />
          </div>
        </div>
      )}
      
      <main>
        <AuthControls 
          isLoggedIn={isLoggedIn} 
          showRegister={showRegister} 
          handleLoginSuccess={handleLoginSuccess} 
          handleLogout={handleLogout} 
          handleRegisterSuccess={handleRegisterSuccess} 
          setShowRegister={setShowRegister} 
        />
        
        {isLoggedIn && (
          <Tabs>
            <TabList>
              <Tab>Data Visualization</Tab>
              <Tab>Download Data</Tab>
            </TabList>

            <TabPanel>
              <DataControls 
                isLoading={isLoading}
                availableDatasets={availableDatasets}
                selectedDatasets={selectedDatasets}
                handleDatasetChange={handleDatasetChange}
                availableYears={availableYears}
                selectedYears={selectedYears}
                handleYearChange={handleYearChange}
                availableRegions={availableRegions}
                selectedRegions={selectedRegions}
                handleRegionChange={handleRegionChange}
                availableReferenceAreas={availableReferenceAreas}
                selectedReferenceAreas={selectedReferenceAreas}
                handleReferenceAreaChange={handleReferenceAreaChange}
              />
              
              {isLoading && <div className="loading">Loading...</div>}
              {error && <div className="error">{error}</div>}
              {!isLoading && !error && (
                <ChartContainer 
                  combinedData={combinedData}
                  selectedDatasets={selectedDatasets}
                  dataByDataset={dataByDataset}
                  selectedRegions={selectedRegions}
                  selectedYears={selectedYears}
                  selectedAreas={selectedReferenceAreas}
                />
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