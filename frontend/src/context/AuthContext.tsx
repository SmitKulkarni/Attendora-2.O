import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { AuthUser, LoginCredentials, RegisterData, AuthResponse } from '../types';
import { apiService, getAuthToken, setAuthToken } from '../services/api';

interface AuthContextType {
  currentUser: AuthUser | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (credentials: LoginCredentials) => Promise<AuthResponse>;
  register: (data: RegisterData) => Promise<AuthResponse>;
  verifyEmail: (email: string, code: string) => Promise<AuthResponse>;
  resendCode: (email: string) => Promise<{ success: boolean; message: string; dev_code?: string }>;
  logout: () => Promise<void>;
  openAuthModal: (mode?: 'login' | 'register') => void;
  closeAuthModal: () => void;
  isAuthModalOpen: boolean;
  authModalMode: 'login' | 'register';
  switchAuthMode: (mode: 'login' | 'register') => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [currentUser, setCurrentUser] = useState<AuthUser | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [isAuthModalOpen, setIsAuthModalOpen] = useState<boolean>(false);
  const [authModalMode, setAuthModalMode] = useState<'login' | 'register'>('login');

  // Verify authentication on mount (handles page refresh & session persistence)
  useEffect(() => {
    let isMounted = true;
    const checkAuthStatus = async () => {
      try {
        const res = await apiService.getMe();
        if (isMounted && res.success && res.user) {
          setCurrentUser(res.user);
        } else if (isMounted) {
          setCurrentUser(null);
        }
      } catch {
        if (isMounted) setCurrentUser(null);
      } finally {
        if (isMounted) setIsLoading(false);
      }
    };

    checkAuthStatus();
    return () => {
      isMounted = false;
    };
  }, []);

  const login = async (credentials: LoginCredentials): Promise<AuthResponse> => {
    setIsLoading(true);
    try {
      const res = await apiService.login(credentials);
      if (res.success && res.user) {
        setCurrentUser(res.user);
        setIsAuthModalOpen(false);
      }
      return res;
    } catch (err: any) {
      return { success: false, message: err?.message || 'Login failed' };
    } finally {
      setIsLoading(false);
    }
  };

  const register = async (data: RegisterData): Promise<AuthResponse> => {
    setIsLoading(true);
    try {
      const res = await apiService.register(data);
      // Notice: registration returns requires_verification: true;
      // We do not immediately log the user in until email is verified!
      return res;
    } catch (err: any) {
      return { success: false, message: err?.message || 'Registration failed' };
    } finally {
      setIsLoading(false);
    }
  };

  const verifyEmail = async (email: string, code: string): Promise<AuthResponse> => {
    setIsLoading(true);
    try {
      const res = await apiService.verifyEmail(email, code);
      if (res.success && res.user) {
        setCurrentUser(res.user);
        setIsAuthModalOpen(false);
      }
      return res;
    } catch (err: any) {
      return { success: false, message: err?.message || 'Verification failed' };
    } finally {
      setIsLoading(false);
    }
  };

  const resendCode = async (email: string) => {
    try {
      return await apiService.resendVerificationCode(email);
    } catch (err: any) {
      return { success: false, message: err?.message || 'Failed to resend verification code' };
    }
  };

  const logout = async () => {
    setIsLoading(true);
    try {
      await apiService.logout();
    } finally {
      setAuthToken(null);
      setCurrentUser(null);
      setIsLoading(false);
    }
  };

  const openAuthModal = (mode: 'login' | 'register' = 'login') => {
    setAuthModalMode(mode);
    setIsAuthModalOpen(true);
  };

  const closeAuthModal = () => {
    setIsAuthModalOpen(false);
  };

  const switchAuthMode = (mode: 'login' | 'register') => {
    setAuthModalMode(mode);
  };

  return (
    <AuthContext.Provider
      value={{
        currentUser,
        isAuthenticated: !!currentUser,
        isLoading,
        login,
        register,
        verifyEmail,
        resendCode,
        logout,
        openAuthModal,
        closeAuthModal,
        isAuthModalOpen,
        authModalMode,
        switchAuthMode
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
