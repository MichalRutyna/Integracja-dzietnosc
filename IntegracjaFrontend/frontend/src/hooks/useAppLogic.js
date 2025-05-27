import { useAuth } from './useAuth';
import { useDatasets } from './useDatasets';
import { useDataFetching } from './useDataFetching';
import { useDataSelection } from './useDataSelection';
import { useCombinedData } from './useCombinedData';
import { useAreas } from './useAreas';

export const useAppLogic = () => {
  const {
    isLoggedIn,
    checkingAuth,
    showRegister,
    setShowRegister,
    handleLoginSuccess,
    handleLogout,
    handleRegisterSuccess
  } = useAuth();

  const {
    availableDatasets,
    selectedDatasets,
    handleDatasetChange
  } = useDatasets(isLoggedIn);

  const {
    dataByDataset,
    isLoading,
    error
  } = useDataFetching(selectedDatasets, isLoggedIn);

  const {
    selectedYears,
    selectedRegions,
    availableYears,
    availableRegions,
    handleYearChange,
    handleRegionChange
  } = useDataSelection(dataByDataset);

  const combinedData = useCombinedData(selectedDatasets, dataByDataset, selectedYears, selectedRegions);

  const {
    availableReferenceAreas,
    selectedReferenceAreas,
    handleReferenceAreaChange
  } = useAreas();

  return {
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
  };
}; 