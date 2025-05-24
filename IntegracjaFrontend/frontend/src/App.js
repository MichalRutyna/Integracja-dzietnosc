import React from 'react';
import { Tab, Tabs, TabList, TabPanel } from 'react-tabs';
import 'react-tabs/style/react-tabs.css';
import { ChartContainer } from './components/Data/ChartContainer';
import { AuthControls } from './components/Auth/AuthControls';
import { DataControls } from './components/Data/DataControls';
import { useAppLogic } from './hooks/useAppLogic';
import './App.css';
import DownloadTabContent from './components/Download/DownloadTabContent';

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
    combinedData
  } = useAppLogic();

  if (checkingAuth) {
    return <div className="loading">Checking authentication...</div>;
  }

  return (
    <div className="App">
      <header className="App-header">
        <h1>Regional Data Visualization</h1>
        {isLoggedIn && (
          <button onClick={handleLogout} className="logout-button">Logout</button>
        )}
      </header>
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