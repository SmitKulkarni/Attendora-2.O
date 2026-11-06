import React, { useState } from 'react';
import {
  X,
  Lock,
  Mail,
  User as UserIcon,
  Shield,
  Eye,
  EyeOff,
  LogIn,
  UserPlus,
  AlertCircle,
  CheckCircle2,
  Building2,
  KeyRound,
  RotateCw,
  ArrowLeft
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { UserRole } from '../types';

export const AuthModal: React.FC = () => {
  const {
    isAuthModalOpen,
    closeAuthModal,
    authModalMode,
    switchAuthMode,
    login,
    register,
    verifyEmail,
    resendCode,
    isLoading
  } = useAuth();

  // Mode: 'login' | 'register' | 'verify'
  const [isVerifying, setIsVerifying] = useState(false);
  const [verifyEmailAddress, setVerifyEmailAddress] = useState('');
  const [verificationCode, setVerificationCode] = useState('');
  const [devPreviewCode, setDevPreviewCode] = useState<string | null>(null);
  const [resendCooldown, setResendCooldown] = useState(0);

  // Form states (clean, zero demo presets)
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [name, setName] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [role, setRole] = useState<UserRole>('STUDENT');
  const [department, setDepartment] = useState('Computer Science');
  const [showPassword, setShowPassword] = useState(false);

  // Validation errors
  const [clientErrors, setClientErrors] = useState<Record<string, string>>({});
  const [serverError, setServerError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  if (!isAuthModalOpen) return null;

  const startResendTimer = () => {
    setResendCooldown(60);
    const interval = setInterval(() => {
      setResendCooldown((prev) => {
        if (prev <= 1) {
          clearInterval(interval);
          return 0;
        }
        return prev - 1;
      });
    }, 1000);
  };

  const validateForm = (): boolean => {
    const errs: Record<string, string> = {};
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!email.trim()) {
      errs.email = 'Email address is required';
    } else if (!emailRegex.test(email.trim())) {
      errs.email = 'Please enter a valid email address';
    }

    if (!password) {
      errs.password = 'Password is required';
    } else if (authModalMode === 'register' && password.length < 8) {
      errs.password = 'Password must be at least 8 characters long';
    }

    if (authModalMode === 'register') {
      if (!name.trim()) {
        errs.name = 'Full name is required';
      }
      if (password !== confirmPassword) {
        errs.confirmPassword = 'Passwords do not match';
      }
    }

    setClientErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setServerError(null);
    setSuccessMessage(null);

    if (!validateForm()) return;

    if (authModalMode === 'login') {
      const res = await login({ email: email.trim(), password });
      if (!res.success) {
        if (res.requires_verification) {
          setVerifyEmailAddress(res.email || email.trim());
          setDevPreviewCode(res.dev_code || null);
          setIsVerifying(true);
          setServerError('Please verify your email to continue. Code sent to inbox.');
          startResendTimer();
        } else {
          setServerError(res.message || 'Invalid email or password');
        }
      }
    } else {
      const res = await register({
        name: name.trim(),
        email: email.trim(),
        password,
        confirm_password: confirmPassword,
        role,
        department
      });
      if (res.success && res.requires_verification) {
        setVerifyEmailAddress(email.trim());
        setDevPreviewCode(res.dev_code || null);
        setIsVerifying(true);
        setSuccessMessage('Account created! Please enter the 6-digit verification code.');
        startResendTimer();
      } else if (!res.success) {
        setServerError(res.message || 'Registration failed');
        if (res.errors) {
          setClientErrors(res.errors);
        }
      }
    }
  };

  const handleVerifySubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setServerError(null);

    const cleanCode = verificationCode.trim();
    if (!cleanCode || cleanCode.length !== 6) {
      setServerError('Please enter a valid 6-digit code.');
      return;
    }

    const res = await verifyEmail(verifyEmailAddress, cleanCode);
    if (!res.success) {
      setServerError(res.message || 'Verification failed. Try again.');
    }
  };

  const handleResend = async () => {
    if (resendCooldown > 0) return;
    setServerError(null);

    const res = await resendCode(verifyEmailAddress);
    if (res.success) {
      setSuccessMessage('A fresh verification code has been dispatched.');
      if (res.dev_code) setDevPreviewCode(res.dev_code);
      startResendTimer();
    } else {
      setServerError(res.message || 'Failed to resend code');
    }
  };

  // Password complexity helpers for registration
  const hasMinLength = password.length >= 8;
  const hasUpper = /[A-Z]/.test(password);
  const hasLower = /[a-z]/.test(password);
  const hasNumber = /\d/.test(password);
  const hasSpecial = /[@$!%*?&#^()_+\-=[\]{};':"\\|,.<>/?]/.test(password);
  const passwordStrengthCount = [hasMinLength, hasUpper, hasLower, hasNumber, hasSpecial].filter(Boolean).length;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm animate-fade-in">
      <div className="relative w-full max-w-lg bg-white rounded-2xl shadow-2xl overflow-hidden border border-slate-200">
        
        {/* Modal Header */}
        <div className="bg-gradient-to-r from-[#006673] to-[#004d57] text-white p-6 relative">
          <button
            onClick={closeAuthModal}
            className="absolute top-5 right-5 p-1 rounded-full text-white/80 hover:text-white hover:bg-white/10 transition-colors cursor-pointer"
          >
            <X size={20} />
          </button>

          <div className="flex items-center gap-2 mb-2">
            <span className="w-8 h-8 rounded-lg bg-white/20 flex items-center justify-center text-white font-bold text-sm shadow-inner">
              <Shield size={18} />
            </span>
            <span className="text-xs uppercase tracking-wider font-semibold text-teal-200">
              Attendora Identity & Access
            </span>
          </div>

          <h2 className="text-2xl font-bold font-plus-jakarta">
            {isVerifying ? 'Email Verification' : authModalMode === 'login' ? 'Sign in to Attendora' : 'Create Attendora Account'}
          </h2>
          <p className="text-teal-100 text-xs mt-1">
            {isVerifying
              ? `Verification code sent to ${verifyEmailAddress}`
              : authModalMode === 'login'
              ? 'Enter your institutional credentials to access your portal'
              : 'Register for role-based attendance telemetry and cloud access'}
          </p>

          {/* Mode Switch Tabs (hidden during verification) */}
          {!isVerifying && (
            <div className="flex mt-5 bg-black/20 p-1 rounded-xl">
              <button
                type="button"
                onClick={() => { switchAuthMode('login'); setServerError(null); setSuccessMessage(null); }}
                className={`flex-1 py-2 text-xs font-semibold rounded-lg flex items-center justify-center gap-2 transition-all cursor-pointer ${
                  authModalMode === 'login'
                    ? 'bg-white text-[#006673] shadow-md'
                    : 'text-white/80 hover:text-white'
                }`}
              >
                <LogIn size={15} />
                Sign In
              </button>
              <button
                type="button"
                onClick={() => { switchAuthMode('register'); setServerError(null); setSuccessMessage(null); }}
                className={`flex-1 py-2 text-xs font-semibold rounded-lg flex items-center justify-center gap-2 transition-all cursor-pointer ${
                  authModalMode === 'register'
                    ? 'bg-white text-[#006673] shadow-md'
                    : 'text-white/80 hover:text-white'
                }`}
              >
                <UserPlus size={15} />
                Create Account
              </button>
            </div>
          )}
        </div>

        {/* Modal Body */}
        <div className="p-6 max-h-[75vh] overflow-y-auto">
          {/* Server Error Alert */}
          {serverError && (
            <div className="mb-4 p-3 rounded-xl bg-red-50 border border-red-200 text-red-700 text-xs flex items-center gap-2 animate-shake">
              <AlertCircle size={16} className="shrink-0 text-red-500" />
              <span>{serverError}</span>
            </div>
          )}

          {/* Success Message Alert */}
          {successMessage && (
            <div className="mb-4 p-3 rounded-xl bg-emerald-50 border border-emerald-200 text-emerald-700 text-xs flex items-center gap-2">
              <CheckCircle2 size={16} className="shrink-0 text-emerald-500" />
              <span>{successMessage}</span>
            </div>
          )}

          {/* VERIFY VIEW */}
          {isVerifying ? (
            <form onSubmit={handleVerifySubmit} className="space-y-4">
              {devPreviewCode && (
                <div className="p-2.5 bg-teal-50 border border-teal-200 rounded-xl text-center text-xs text-teal-800">
                  Test Mode Verification Code: <strong className="font-mono">{devPreviewCode}</strong>
                </div>
              )}

              <div>
                <label className="block text-xs font-semibold text-slate-700 text-center mb-2">
                  Enter 6-Digit Code
                </label>
                <input
                  type="text"
                  maxLength={6}
                  placeholder="123456"
                  value={verificationCode}
                  onChange={(e) => setVerificationCode(e.target.value.replace(/\D/g, ''))}
                  className="w-full py-3 px-4 text-center font-mono text-2xl tracking-[8px] font-bold border border-slate-300 rounded-xl focus:outline-none focus:border-[#006673] bg-slate-50 text-slate-800"
                  autoFocus
                />
              </div>

              <button
                type="submit"
                disabled={isLoading || verificationCode.trim().length !== 6}
                className="w-full py-2.5 px-4 bg-[#006673] hover:bg-[#00525d] text-white text-xs font-bold rounded-xl shadow-md transition-all flex items-center justify-center gap-2 disabled:opacity-50 cursor-pointer"
              >
                <CheckCircle2 size={15} />
                Verify & Activate
              </button>

              <div className="flex items-center justify-between pt-3 text-xs text-slate-500">
                <button
                  type="button"
                  onClick={handleResend}
                  disabled={resendCooldown > 0}
                  className="text-[#006673] font-semibold hover:underline flex items-center gap-1 cursor-pointer disabled:text-slate-400"
                >
                  <RotateCw size={12} className={resendCooldown > 0 ? 'animate-spin' : ''} />
                  {resendCooldown > 0 ? `Resend (${resendCooldown}s)` : 'Resend code'}
                </button>
                <button
                  type="button"
                  onClick={() => setIsVerifying(false)}
                  className="hover:text-slate-800 flex items-center gap-1 cursor-pointer"
                >
                  <ArrowLeft size={12} /> Back
                </button>
              </div>
            </form>
          ) : (
            /* LOGIN / REGISTER FORM */
            <form onSubmit={handleSubmit} className="space-y-4">
              {/* Registration Name */}
              {authModalMode === 'register' && (
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Full Legal Name</label>
                  <div className="relative">
                    <UserIcon size={16} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" />
                    <input
                      type="text"
                      placeholder="e.g. Priya Saxena"
                      value={name}
                      onChange={(e) => setName(e.target.value)}
                      className={`w-full pl-10 pr-3 py-2 text-xs border rounded-xl focus:outline-none focus:ring-2 focus:ring-[#006673]/20 ${
                        clientErrors.name ? 'border-red-400 bg-red-50/30' : 'border-slate-200 focus:border-[#006673]'
                      }`}
                    />
                  </div>
                  {clientErrors.name && (
                    <p className="text-[11px] text-red-500 mt-1">{clientErrors.name}</p>
                  )}
                </div>
              )}

              {/* Email Field */}
              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Institutional Email</label>
                <div className="relative">
                  <Mail size={16} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" />
                  <input
                    type="email"
                    placeholder="name@university.edu"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    className={`w-full pl-10 pr-3 py-2 text-xs border rounded-xl focus:outline-none focus:ring-2 focus:ring-[#006673]/20 ${
                      clientErrors.email ? 'border-red-400 bg-red-50/30' : 'border-slate-200 focus:border-[#006673]'
                    }`}
                  />
                </div>
                {clientErrors.email && (
                  <p className="text-[11px] text-red-500 mt-1">{clientErrors.email}</p>
                )}
              </div>

              {/* Role & Department (Registration Only) */}
              {authModalMode === 'register' && (
                <div className="grid grid-cols-2 gap-3">
                  <div>
                    <label className="block text-xs font-semibold text-slate-700 mb-1">System Role</label>
                    <div className="relative">
                      <Shield size={16} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" />
                      <select
                        value={role}
                        onChange={(e) => setRole(e.target.value as UserRole)}
                        className="w-full pl-10 pr-3 py-2 text-xs border border-slate-200 rounded-xl focus:outline-none focus:border-[#006673] bg-white"
                      >
                        <option value="STUDENT">Student</option>
                        <option value="TEACHER">Faculty / Teacher</option>
                        <option value="ADMIN">Institutional Admin</option>
                      </select>
                    </div>
                  </div>

                  <div>
                    <label className="block text-xs font-semibold text-slate-700 mb-1">Department</label>
                    <div className="relative">
                      <Building2 size={16} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" />
                      <select
                        value={department}
                        onChange={(e) => setDepartment(e.target.value)}
                        className="w-full pl-10 pr-3 py-2 text-xs border border-slate-200 rounded-xl focus:outline-none focus:border-[#006673] bg-white"
                      >
                        <option value="Computer Science">Computer Science</option>
                        <option value="Information Technology">Information Tech</option>
                        <option value="Mechanical Engineering">Mechanical Eng</option>
                        <option value="Data Science">Data Science</option>
                      </select>
                    </div>
                  </div>
                </div>
              )}

              {/* Password Field */}
              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Password</label>
                <div className="relative">
                  <Lock size={16} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" />
                  <input
                    type={showPassword ? 'text' : 'password'}
                    placeholder="••••••••"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    className={`w-full pl-10 pr-10 py-2 text-xs border rounded-xl focus:outline-none focus:ring-2 focus:ring-[#006673]/20 ${
                      clientErrors.password ? 'border-red-400 bg-red-50/30' : 'border-slate-200 focus:border-[#006673]'
                    }`}
                  />
                  <button
                    type="button"
                    onClick={() => setShowPassword(!showPassword)}
                    className="absolute right-3.5 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600 cursor-pointer"
                  >
                    {showPassword ? <EyeOff size={16} /> : <Eye size={16} />}
                  </button>
                </div>
                {clientErrors.password && (
                  <p className="text-[11px] text-red-500 mt-1">{clientErrors.password}</p>
                )}

                {/* Password complexity indicators for registration */}
                {authModalMode === 'register' && password.length > 0 && (
                  <div className="mt-2 p-2.5 bg-slate-50 rounded-xl border border-slate-100 text-[11px] space-y-1 text-slate-600">
                    <div className="flex items-center justify-between mb-1">
                      <span className="font-semibold text-slate-500">Security Requirement:</span>
                      <span className={`font-bold ${
                        passwordStrengthCount >= 4 ? 'text-emerald-600' : 'text-amber-600'
                      }`}>
                        {passwordStrengthCount >= 4 ? 'Strong' : 'Moderate'}
                      </span>
                    </div>
                    <div className="grid grid-cols-2 gap-1 text-[10px]">
                      <span className={`flex items-center gap-1 ${hasMinLength ? 'text-emerald-600' : 'text-slate-400'}`}>
                        <CheckCircle2 size={11} /> 8+ Characters
                      </span>
                      <span className={`flex items-center gap-1 ${hasUpper && hasLower ? 'text-emerald-600' : 'text-slate-400'}`}>
                        <CheckCircle2 size={11} /> Upper & Lowercase
                      </span>
                      <span className={`flex items-center gap-1 ${hasNumber ? 'text-emerald-600' : 'text-slate-400'}`}>
                        <CheckCircle2 size={11} /> At least 1 Number
                      </span>
                      <span className={`flex items-center gap-1 ${hasSpecial ? 'text-emerald-600' : 'text-slate-400'}`}>
                        <CheckCircle2 size={11} /> Special Symbol
                      </span>
                    </div>
                  </div>
                )}
              </div>

              {/* Confirm Password (Registration Only) */}
              {authModalMode === 'register' && (
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Confirm Password</label>
                  <div className="relative">
                    <Lock size={16} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" />
                    <input
                      type={showPassword ? 'text' : 'password'}
                      placeholder="••••••••"
                      value={confirmPassword}
                      onChange={(e) => setConfirmPassword(e.target.value)}
                      className={`w-full pl-10 pr-3 py-2 text-xs border rounded-xl focus:outline-none focus:ring-2 focus:ring-[#006673]/20 ${
                        clientErrors.confirmPassword ? 'border-red-400 bg-red-50/30' : 'border-slate-200 focus:border-[#006673]'
                      }`}
                    />
                  </div>
                  {clientErrors.confirmPassword && (
                    <p className="text-[11px] text-red-500 mt-1">{clientErrors.confirmPassword}</p>
                  )}
                </div>
              )}

              {/* Submit Button */}
              <button
                type="submit"
                disabled={isLoading}
                className="w-full mt-2 py-2.5 px-4 bg-[#006673] hover:bg-[#00525d] text-white text-xs font-bold rounded-xl shadow-md hover:shadow-lg transition-all flex items-center justify-center gap-2 disabled:opacity-50 cursor-pointer"
              >
                {isLoading ? (
                  <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                ) : authModalMode === 'login' ? (
                  <>
                    <LogIn size={15} />
                    Sign In
                  </>
                ) : (
                  <>
                    <UserPlus size={15} />
                    Create Account & Send Code
                  </>
                )}
              </button>
            </form>
          )}

          {/* Footer Switcher */}
          {!isVerifying && (
            <div className="mt-5 text-center text-xs text-slate-500">
              {authModalMode === 'login' ? (
                <p>
                  Don't have an institutional account?{' '}
                  <button
                    type="button"
                    onClick={() => { switchAuthMode('register'); setServerError(null); }}
                    className="text-[#006673] font-bold hover:underline cursor-pointer"
                  >
                    Create one now
                  </button>
                </p>
              ) : (
                <p>
                  Already have an account?{' '}
                  <button
                    type="button"
                    onClick={() => { switchAuthMode('login'); setServerError(null); }}
                    className="text-[#006673] font-bold hover:underline cursor-pointer"
                  >
                    Sign in instead
                  </button>
                </p>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
