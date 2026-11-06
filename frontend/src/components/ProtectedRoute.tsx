import React, { ReactNode } from 'react';
import { useAuth } from '../context/AuthContext';
import { UserRole } from '../types';
import { ShieldAlert, LogIn, Lock, ArrowLeft } from 'lucide-react';

interface ProtectedRouteProps {
  children: ReactNode;
  requiredRoles?: UserRole[];
  fallback?: ReactNode;
}

export const ProtectedRoute: React.FC<ProtectedRouteProps> = ({
  children,
  requiredRoles,
  fallback
}) => {
  const { currentUser, isAuthenticated, isLoading, openAuthModal, logout } = useAuth();

  if (isLoading) {
    return (
      <div className="flex-1 flex flex-col items-center justify-center min-h-[400px] p-8 text-center animate-fade-in">
        <div className="w-12 h-12 border-4 border-[#006673]/20 border-t-[#006673] rounded-full animate-spin mb-4" />
        <h3 className="text-base font-bold text-slate-800">Authenticating Session</h3>
        <p className="text-xs text-slate-500 mt-1">Verifying cryptographic token and role credentials...</p>
      </div>
    );
  }

  if (!isAuthenticated) {
    if (fallback) return <>{fallback}</>;
    return (
      <div className="flex-1 flex flex-col items-center justify-center min-h-[450px] p-8 text-center animate-fade-in">
        <div className="w-16 h-16 rounded-2xl bg-teal-50 border border-teal-200 text-[#006673] flex items-center justify-center mb-4 shadow-sm">
          <Lock size={32} />
        </div>
        <h2 className="text-xl font-bold text-slate-800 mb-2">Authentication Required</h2>
        <p className="text-xs text-slate-500 max-w-md mb-6 leading-relaxed">
          This portal section is cryptographically secured. Please sign in with your institutional credentials to continue.
        </p>
        <button
          onClick={() => openAuthModal('login')}
          className="inline-flex items-center gap-2 px-5 py-2.5 bg-[#006673] hover:bg-[#00525d] text-white text-xs font-bold rounded-xl shadow-md hover:shadow-lg transition-all"
        >
          <LogIn size={16} />
          Sign In to Access
        </button>
      </div>
    );
  }

  // RBAC Role Check
  if (requiredRoles && requiredRoles.length > 0 && currentUser) {
    const userRole = currentUser.role.toUpperCase() as UserRole;
    const allowed = requiredRoles.map((r) => r.toUpperCase());
    if (!allowed.includes(userRole)) {
      return (
        <div className="flex-1 flex flex-col items-center justify-center min-h-[450px] p-8 text-center animate-fade-in">
          <div className="w-16 h-16 rounded-2xl bg-amber-50 border border-amber-200 text-amber-600 flex items-center justify-center mb-4 shadow-sm">
            <ShieldAlert size={32} />
          </div>
          <h2 className="text-xl font-bold text-slate-800 mb-2">403 Forbidden — Access Restricted</h2>
          <p className="text-xs text-slate-500 max-w-md mb-2 leading-relaxed">
            Your current account role is <span className="font-bold text-amber-700 uppercase">[{userRole}]</span>.
            This module strictly requires permissions for: <span className="font-bold text-slate-700">{allowed.join(', ')}</span>.
          </p>
          <p className="text-[11px] text-slate-400 mb-6">
            If you need administrative or faculty access, please sign in with an authorized institutional account.
          </p>
          <div className="flex items-center gap-3">
            <button
              onClick={() => logout().then(() => openAuthModal('login'))}
              className="inline-flex items-center gap-2 px-4 py-2 bg-slate-800 hover:bg-slate-900 text-white text-xs font-semibold rounded-xl transition-all shadow-sm"
            >
              <ArrowLeft size={14} />
              Switch Account
            </button>
          </div>
        </div>
      );
    }
  }

  return <>{children}</>;
};

