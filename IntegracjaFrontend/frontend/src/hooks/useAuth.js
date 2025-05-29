import { useState, useEffect } from 'react';
import { isAuthenticated, logout, changePassword, deleteUser } from '../services/authService';

export const useAuth = () => {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [checkingAuth, setCheckingAuth] = useState(true);
  const [showRegister, setShowRegister] = useState(false);

  useEffect(() => {
    const checkAuth = async () => {
      try {
        const isAuth = await isAuthenticated();
        setIsLoggedIn(isAuth);
      } catch (error) {
        setIsLoggedIn(false);
      } finally {
        setCheckingAuth(false);
      }
    };
    checkAuth();
  }, []);

  const handleLoginSuccess = () => {
    setIsLoggedIn(true);
  };

  const handleLogout = () => {
    logout();
    setIsLoggedIn(false);
  };
  
  const handleDeleteUser = () => {
    deleteUser();
    setIsLoggedIn(false);
  };

  const handleChangePassword = (newPassword) => {
    changePassword(newPassword);
  };

  const handleRegisterSuccess = () => {
    setIsLoggedIn(true);
    setShowRegister(false);
  };

  return {
    isLoggedIn,
    checkingAuth,
    showRegister,
    setShowRegister,
    handleLoginSuccess,
    handleLogout,
    handleRegisterSuccess,
    handleDeleteUser,
    handleChangePassword
  };
}; 