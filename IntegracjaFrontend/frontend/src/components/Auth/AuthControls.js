import React from 'react';
import Login from './Login';
import Register from './Register';

export const AuthControls = ({ isLoggedIn, showRegister, handleLoginSuccess, handleLogout, handleRegisterSuccess, setShowRegister }) => {
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