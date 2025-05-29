import React from 'react';
import Login from './Login';
import Register from './Register';
import { useAppLogic } from '../../hooks/useAppLogic';

export const AuthControls = () => {
  const {
    isLoggedIn,
    showRegister,
    handleLoginSuccess,
    handleRegisterSuccess,
    setShowRegister
  } = useAppLogic();


  if (!isLoggedIn) {
    return showRegister ? (
      <Register 
        onRegisterSuccess={handleRegisterSuccess} 
        onBack={() => setShowRegister(false)}
      />
    ) : (
      <Login 
        onLoginSuccess={handleLoginSuccess} 
        onShowRegister={() => setShowRegister(true)}
      />
    );
  }

  return null;
}; 