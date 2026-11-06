import React, { useState } from 'react';
import {
  Shield,
  Mail,
  Lock,
  User,
  Eye,
  EyeOff,
  LogIn,
  UserPlus,
  AlertCircle,
  CheckCircle2,
  Building2,
  KeyRound,
  ArrowLeft,
  RotateCw,
  Sparkles,
  GraduationCap
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { UserRole } from '../types';

type AuthView = 'LOGIN' | 'REGISTER' | 'VERIFY';

export const AuthScreen: React.FC = () => {
  const { login, register, verifyEmail, resendCode, isLoading } = useAuth();

  const [view, setView] = useState<AuthView>('LOGIN');

  // Form input states (Clean, zero demo data)
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [name, setName] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [role, setRole] = useState<UserRole>('STUDENT');
  const [department, setDepartment] = useState('Computer Science');
  const [showPassword, setShowPassword] = useState(false);

  // Email verification state
  const [verifyEmailAddress, setVerifyEmailAddress] = useState('');
  const [verificationCode, setVerificationCode] = useState('');
  const [devPreviewCode, setDevPreviewCode] = useState<string | null>(null);
  const [resendCooldown, setResendCooldown] = useState(0);

  // Error and feedback state
  const [clientErrors, setClientErrors] = useState<Record<string, string>>({});
  const [serverError, setServerError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

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

  const validateLoginForm = (): boolean => {
    const errs: Record<string, string> = {};
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!email.trim()) {
      errs.email = 'Email address is required';
    } else if (!emailRegex.test(email.trim())) {
      errs.email = 'Please enter a valid email address';
    }

    if (!password) {
      errs.password = 'Password is required';
    }

    setClientErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const validateRegisterForm = (): boolean => {
    const errs: Record<string, string> = {};
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!name.trim()) {
      errs.name = 'Full name is required';
    } else if (name.trim().length < 2) {
      errs.name = 'Name must be at least 2 characters';
    }

    if (!email.trim()) {
      errs.email = 'Email address is required';
    } else if (!emailRegex.test(email.trim())) {
      errs.email = 'Please enter a valid email address';
    }

    if (!password) {
      errs.password = 'Password is required';
    } else if (password.length < 8) {
      errs.password = 'Password must be at least 8 characters';
    } else if (!/(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&#^()_+\-=[\]{};':"\\|,.<>/?])/.test(password)) {
      errs.password = 'Must contain uppercase, lowercase, number, and special symbol';
    }

    if (password !== confirmPassword) {
      errs.confirmPassword = 'Passwords do not match';
    }

    setClientErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const handleLoginSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setServerError(null);
    setSuccessMessage(null);

    if (!validateLoginForm()) return;

    const res = await login({ email: email.trim(), password });
    if (!res.success) {
      if (res.requires_verification) {
        // User account exists but email is not yet verified!
        setVerifyEmailAddress(res.email || email.trim());
        setDevPreviewCode(res.dev_code || null);
        setView('VERIFY');
        setServerError('Please verify your email before logging in. Enter the code sent to your inbox.');
        startResendTimer();
      } else {
        setServerError(res.message || 'Invalid email or password');
      }
    }
  };

  const handleRegisterSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setServerError(null);
    setSuccessMessage(null);

    if (!validateRegisterForm()) return;

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
      setView('VERIFY');
      setSuccessMessage('Account created! Please check your email for the 6-digit verification code.');
      startResendTimer();
    } else if (!res.success) {
      setServerError(res.message || 'Registration failed');
      if (res.errors) {
        setClientErrors(res.errors);
      }
    }
  };

  const handleVerifySubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setServerError(null);

    const cleanCode = verificationCode.trim();
    if (!cleanCode || cleanCode.length !== 6) {
      setServerError('Please enter a valid 6-digit verification code.');
      return;
    }

    const res = await verifyEmail(verifyEmailAddress, cleanCode);
    if (!res.success) {
      setServerError(res.message || 'Invalid verification code. Please check and try again.');
    }
  };

  const handleResendCode = async () => {
    if (resendCooldown > 0) return;
    setServerError(null);

    const res = await resendCode(verifyEmailAddress);
    if (res.success) {
      setSuccessMessage('A fresh verification code has been dispatched to your email.');
      if (res.dev_code) {
        setDevPreviewCode(res.dev_code);
      }
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
    <div className="min-h-screen w-full flex bg-[#F8FAFC]">
      {/* Left Column: Visual Academic Hero (Desktop) */}
      <div className="hidden lg:flex lg:w-1/2 bg-gradient-to-br from-[#004D57] via-[#006673] to-[#0A808F] p-12 flex-col justify-between text-white relative overflow-hidden">
        {/* Background Decorative Rings */}
        <div className="absolute -top-24 -left-24 w-96 h-96 rounded-full bg-white/5 blur-2xl pointer-events-none" />
        <div className="absolute -bottom-24 -right-24 w-96 h-96 rounded-full bg-teal-400/10 blur-3xl pointer-events-none" />

        {/* Top Logo */}
        <div className="relative z-10 flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl bg-white/20 backdrop-blur-md flex items-center justify-center font-bold text-lg text-white shadow-inner">
            <GraduationCap size={24} />
          </div>
          <div>
            <span className="text-xl font-extrabold tracking-tight">Attendora</span>
            <span className="block text-[11px] font-semibold text-teal-200 tracking-wider uppercase">
              Attendance Intelligence SaaS
            </span>
          </div>
        </div>

        {/* Center Content */}
        <div className="relative z-10 max-w-md my-auto">
          <div className="inline-flex items-center gap-2 px-3 py-1.5 rounded-full bg-white/10 backdrop-blur-md text-teal-200 text-xs font-semibold mb-6 border border-white/10">
            <Sparkles size={14} />
            Institutional Cloud Platform
          </div>

          <h1 className="text-4xl font-extrabold tracking-tight leading-tight mb-4">
            Unified Attendance & Academic Forensics
          </h1>
          <p className="text-teal-100/90 text-sm leading-relaxed mb-8">
            Eliminate proxy attendance, monitor institutional retention thresholds in real time, and deploy proactive Gemini AI academic interventions.
          </p>

          {/* Feature Highlights */}
          <div className="space-y-4">
            <div className="flex items-start gap-3">
              <div className="w-6 h-6 rounded-lg bg-teal-500/20 flex items-center justify-center shrink-0 mt-0.5 text-teal-300">
                ✓
              </div>
              <div>
                <h4 className="text-xs font-bold text-white">Dynamic Rolling TOTP QR & Geofencing</h4>
                <p className="text-[11px] text-teal-100/70">Anti-proxy check-in with 50-meter GPS perimeter validation.</p>
              </div>
            </div>

            <div className="flex items-start gap-3">
              <div className="w-6 h-6 rounded-lg bg-teal-500/20 flex items-center justify-center shrink-0 mt-0.5 text-teal-300">
                ✓
              </div>
              <div>
                <h4 className="text-xs font-bold text-white">Gemini AI Predictive Dropout Risk</h4>
                <p className="text-[11px] text-teal-100/70">Automatic early debarment warning and personalized counseling memos.</p>
              </div>
            </div>

            <div className="flex items-start gap-3">
              <div className="w-6 h-6 rounded-lg bg-teal-500/20 flex items-center justify-center shrink-0 mt-0.5 text-teal-300">
                ✓
              </div>
              <div>
                <h4 className="text-xs font-bold text-white">PostgreSQL Multi-Tenant RLS</h4>
                <p className="text-[11px] text-teal-100/70">Strict multi-campus isolation with statutory 48-hour audit immutability.</p>
              </div>
            </div>
          </div>
        </div>

        {/* Footer */}
        <div className="relative z-10 text-xs text-teal-200/60 flex items-center justify-between border-t border-white/10 pt-6">
          <span>&copy; 2026 Attendora University Systems</span>
          <span>Security Certified &bullet; ISO 27001</span>
        </div>
      </div>

      {/* Right Column: Authentication Form Container */}
      <div className="flex-1 flex items-center justify-center p-6 sm:p-10 lg:p-14 overflow-y-auto">
        <div className="w-full max-w-md bg-white rounded-2xl shadow-xl border border-slate-200/80 p-8 sm:p-10">

          {/* Mobile Header Logo */}
          <div className="flex lg:hidden items-center gap-2 mb-6">
            <div className="w-8 h-8 rounded-lg bg-[#006673] flex items-center justify-center text-white">
              <GraduationCap size={18} />
            </div>
            <span className="text-lg font-bold text-[#006673]">Attendora</span>
          </div>

          {/* Alerts: Error & Success */}
          {serverError && (
            <div className="mb-5 p-3.5 rounded-xl bg-red-50 border border-red-200 text-red-700 text-xs flex items-start gap-2.5 animate-shake">
              <AlertCircle size={16} className="shrink-0 text-red-500 mt-0.5" />
              <span className="leading-relaxed">{serverError}</span>
            </div>
          )}

          {successMessage && (
            <div className="mb-5 p-3.5 rounded-xl bg-emerald-50 border border-emerald-200 text-emerald-700 text-xs flex items-start gap-2.5">
              <CheckCircle2 size={16} className="shrink-0 text-emerald-500 mt-0.5" />
              <span className="leading-relaxed">{successMessage}</span>
            </div>
          )}

          {/* ======================================================== */}
          {/* VIEW 1: SIGN IN FORM */}
          {/* ======================================================== */}
          {view === 'LOGIN' && (
            <div>
              <div className="mb-6">
                <h2 className="text-2xl font-bold text-slate-800 tracking-tight">Sign In</h2>
                <p className="text-xs text-slate-500 mt-1">
                  Enter your registered institutional credentials to access your dashboard.
                </p>
              </div>

              <form onSubmit={handleLoginSubmit} className="space-y-4">
                {/* Email Address */}
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1.5">
                    Institutional Email Address
                  </label>
                  <div className="relative">
                    <Mail size={16} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" />
                    <input
                      type="email"
                      placeholder="e.g. name@university.edu"
                      value={email}
                      onChange={(e) => setEmail(e.target.value)}
                      className={`w-full pl-10 pr-3 py-2.5 text-xs border rounded-xl focus:outline-none focus:ring-2 focus:ring-[#006673]/20 transition-all ${
                        clientErrors.email ? 'border-red-400 bg-red-50/20' : 'border-slate-200 focus:border-[#006673]'
                      }`}
                    />
                  </div>
                  {clientErrors.email && (
                    <p className="text-[11px] text-red-500 mt-1">{clientErrors.email}</p>
                  )}
                </div>

                {/* Password */}
                <div>
                  <div className="flex items-center justify-between mb-1.5">
                    <label className="text-xs font-semibold text-slate-700">Password</label>
                  </div>
                  <div className="relative">
                    <Lock size={16} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" />
                    <input
                      type={showPassword ? 'text' : 'password'}
                      placeholder="Enter your password"
                      value={password}
                      onChange={(e) => setPassword(e.target.value)}
                      className={`w-full pl-10 pr-10 py-2.5 text-xs border rounded-xl focus:outline-none focus:ring-2 focus:ring-[#006673]/20 transition-all ${
                        clientErrors.password ? 'border-red-400 bg-red-50/20' : 'border-slate-200 focus:border-[#006673]'
                      }`}
                    />
                    <button
                      type="button"
                      onClick={() => setShowPassword(!showPassword)}
                      className="absolute right-3.5 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600"
                    >
                      {showPassword ? <EyeOff size={16} /> : <Eye size={16} />}
                    </button>
                  </div>
                  {clientErrors.password && (
                    <p className="text-[11px] text-red-500 mt-1">{clientErrors.password}</p>
                  )}
                </div>

                {/* Submit Sign In */}
                <button
                  type="submit"
                  disabled={isLoading}
                  className="w-full mt-2 py-3 px-4 bg-[#006673] hover:bg-[#00525d] text-white text-xs font-bold rounded-xl shadow-md hover:shadow-lg transition-all flex items-center justify-center gap-2 disabled:opacity-50 cursor-pointer"
                >
                  {isLoading ? (
                    <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                  ) : (
                    <>
                      <LogIn size={16} />
                      Sign In to Attendora
                    </>
                  )}
                </button>
              </form>

              {/* Bottom Switcher */}
              <div className="mt-8 pt-6 border-t border-slate-100 text-center text-xs text-slate-500">
                Don't have an institutional account?{' '}
                <button
                  type="button"
                  onClick={() => {
                    setView('REGISTER');
                    setServerError(null);
                    setSuccessMessage(null);
                    setClientErrors({});
                  }}
                  className="text-[#006673] font-bold hover:underline cursor-pointer"
                >
                  Create an account
                </button>
              </div>
            </div>
          )}

          {/* ======================================================== */}
          {/* VIEW 2: CREATE ACCOUNT (REGISTER) */}
          {/* ======================================================== */}
          {view === 'REGISTER' && (
            <div>
              <div className="mb-6">
                <h2 className="text-2xl font-bold text-slate-800 tracking-tight">Create Account</h2>
                <p className="text-xs text-slate-500 mt-1">
                  Register with your institutional email. An email verification code will be sent.
                </p>
              </div>

              <form onSubmit={handleRegisterSubmit} className="space-y-3.5">
                {/* Full Name */}
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Full Legal Name</label>
                  <div className="relative">
                    <User size={16} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" />
                    <input
                      type="text"
                      placeholder="e.g. Priya Saxena"
                      value={name}
                      onChange={(e) => setName(e.target.value)}
                      className={`w-full pl-10 pr-3 py-2 text-xs border rounded-xl focus:outline-none focus:ring-2 focus:ring-[#006673]/20 ${
                        clientErrors.name ? 'border-red-400 bg-red-50/20' : 'border-slate-200 focus:border-[#006673]'
                      }`}
                    />
                  </div>
                  {clientErrors.name && (
                    <p className="text-[11px] text-red-500 mt-1">{clientErrors.name}</p>
                  )}
                </div>

                {/* Email Address */}
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
                        clientErrors.email ? 'border-red-400 bg-red-50/20' : 'border-slate-200 focus:border-[#006673]'
                      }`}
                    />
                  </div>
                  {clientErrors.email && (
                    <p className="text-[11px] text-red-500 mt-1">{clientErrors.email}</p>
                  )}
                </div>

                {/* Role & Department */}
                <div className="grid grid-cols-2 gap-2.5">
                  <div>
                    <label className="block text-xs font-semibold text-slate-700 mb-1">System Role</label>
                    <div className="relative">
                      <Shield size={15} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
                      <select
                        value={role}
                        onChange={(e) => setRole(e.target.value as UserRole)}
                        className="w-full pl-9 pr-2 py-2 text-xs border border-slate-200 rounded-xl focus:outline-none focus:border-[#006673] bg-white text-slate-700"
                      >
                        <option value="STUDENT">Student</option>
                        <option value="TEACHER">Faculty / Professor</option>
                        <option value="ADMIN">Administrator</option>
                      </select>
                    </div>
                  </div>

                  <div>
                    <label className="block text-xs font-semibold text-slate-700 mb-1">Department</label>
                    <div className="relative">
                      <Building2 size={15} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
                      <select
                        value={department}
                        onChange={(e) => setDepartment(e.target.value)}
                        className="w-full pl-9 pr-2 py-2 text-xs border border-slate-200 rounded-xl focus:outline-none focus:border-[#006673] bg-white text-slate-700"
                      >
                        <option value="Computer Science">Computer Sci</option>
                        <option value="Information Technology">Info Tech</option>
                        <option value="Mechanical Engineering">Mechanical</option>
                        <option value="Data Science">Data Science</option>
                      </select>
                    </div>
                  </div>
                </div>

                {/* Password */}
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Create Password</label>
                  <div className="relative">
                    <Lock size={16} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" />
                    <input
                      type={showPassword ? 'text' : 'password'}
                      placeholder="••••••••"
                      value={password}
                      onChange={(e) => setPassword(e.target.value)}
                      className={`w-full pl-10 pr-10 py-2 text-xs border rounded-xl focus:outline-none focus:ring-2 focus:ring-[#006673]/20 ${
                        clientErrors.password ? 'border-red-400 bg-red-50/20' : 'border-slate-200 focus:border-[#006673]'
                      }`}
                    />
                    <button
                      type="button"
                      onClick={() => setShowPassword(!showPassword)}
                      className="absolute right-3.5 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600"
                    >
                      {showPassword ? <EyeOff size={16} /> : <Eye size={16} />}
                    </button>
                  </div>
                  {clientErrors.password && (
                    <p className="text-[11px] text-red-500 mt-1">{clientErrors.password}</p>
                  )}

                  {/* Password requirements checker */}
                  {password.length > 0 && (
                    <div className="mt-2 p-2 bg-slate-50 rounded-xl border border-slate-100 text-[11px] space-y-1 text-slate-600">
                      <div className="flex items-center justify-between mb-0.5">
                        <span className="font-semibold text-slate-500 text-[10px]">Strength:</span>
                        <span className={`font-bold text-[10px] ${
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

                {/* Confirm Password */}
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Confirm Password</label>
                  <div className="relative">
                    <Lock size={16} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" />
                    <input
                      type={showPassword ? 'text' : 'password'}
                      placeholder="Re-enter password"
                      value={confirmPassword}
                      onChange={(e) => setConfirmPassword(e.target.value)}
                      className={`w-full pl-10 pr-3 py-2 text-xs border rounded-xl focus:outline-none focus:ring-2 focus:ring-[#006673]/20 ${
                        clientErrors.confirmPassword ? 'border-red-400 bg-red-50/20' : 'border-slate-200 focus:border-[#006673]'
                      }`}
                    />
                  </div>
                  {clientErrors.confirmPassword && (
                    <p className="text-[11px] text-red-500 mt-1">{clientErrors.confirmPassword}</p>
                  )}
                </div>

                {/* Submit Register */}
                <button
                  type="submit"
                  disabled={isLoading}
                  className="w-full mt-3 py-3 px-4 bg-[#006673] hover:bg-[#00525d] text-white text-xs font-bold rounded-xl shadow-md hover:shadow-lg transition-all flex items-center justify-center gap-2 disabled:opacity-50 cursor-pointer"
                >
                  {isLoading ? (
                    <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                  ) : (
                    <>
                      <UserPlus size={16} />
                      Register & Receive Verification Code
                    </>
                  )}
                </button>
              </form>

              {/* Bottom Switcher */}
              <div className="mt-6 pt-5 border-t border-slate-100 text-center text-xs text-slate-500">
                Already have an account?{' '}
                <button
                  type="button"
                  onClick={() => {
                    setView('LOGIN');
                    setServerError(null);
                    setSuccessMessage(null);
                    setClientErrors({});
                  }}
                  className="text-[#006673] font-bold hover:underline cursor-pointer"
                >
                  Sign in
                </button>
              </div>
            </div>
          )}

          {/* ======================================================== */}
          {/* VIEW 3: EMAIL VERIFICATION (OTP) */}
          {/* ======================================================== */}
          {view === 'VERIFY' && (
            <div>
              <div className="text-center mb-6">
                <div className="w-14 h-14 rounded-2xl bg-teal-50 border border-teal-200 text-[#006673] flex items-center justify-center mx-auto mb-3 shadow-inner">
                  <KeyRound size={28} />
                </div>
                <h2 className="text-2xl font-bold text-slate-800 tracking-tight">Verify Your Email</h2>
                <p className="text-xs text-slate-500 mt-1.5 leading-relaxed">
                  We've sent a 6-digit verification code to:
                  <span className="block font-semibold text-slate-700 mt-0.5">{verifyEmailAddress}</span>
                </p>
              </div>

              {/* Dev mode preview code helper */}
              {devPreviewCode && (
                <div className="mb-4 p-2.5 bg-teal-50/70 border border-teal-200 rounded-xl text-center">
                  <span className="text-[11px] text-teal-800 font-medium">
                    Test Mode Verification Code: <strong className="font-mono text-xs">{devPreviewCode}</strong>
                  </span>
                </div>
              )}

              <form onSubmit={handleVerifySubmit} className="space-y-4">
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
                    className="w-full py-3 px-4 text-center font-mono text-2xl tracking-[8px] font-bold border border-slate-300 rounded-xl focus:outline-none focus:border-[#006673] focus:ring-2 focus:ring-[#006673]/20 bg-slate-50 text-slate-800"
                    autoFocus
                  />
                </div>

                <button
                  type="submit"
                  disabled={isLoading || verificationCode.trim().length !== 6}
                  className="w-full py-3 px-4 bg-[#006673] hover:bg-[#00525d] text-white text-xs font-bold rounded-xl shadow-md hover:shadow-lg transition-all flex items-center justify-center gap-2 disabled:opacity-50 cursor-pointer"
                >
                  {isLoading ? (
                    <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                  ) : (
                    <>
                      <CheckCircle2 size={16} />
                      Verify & Activate Account
                    </>
                  )}
                </button>
              </form>

              {/* Resend and Switch options */}
              <div className="mt-6 space-y-3 text-center">
                <button
                  type="button"
                  onClick={handleResendCode}
                  disabled={resendCooldown > 0}
                  className="text-xs text-[#006673] font-semibold hover:underline flex items-center justify-center gap-1.5 mx-auto disabled:text-slate-400 cursor-pointer"
                >
                  <RotateCw size={13} className={resendCooldown > 0 ? 'animate-spin' : ''} />
                  {resendCooldown > 0 ? `Resend code in ${resendCooldown}s` : 'Resend verification code'}
                </button>

                <div className="pt-3 border-t border-slate-100">
                  <button
                    type="button"
                    onClick={() => {
                      setView('LOGIN');
                      setServerError(null);
                      setSuccessMessage(null);
                    }}
                    className="text-xs text-slate-500 hover:text-slate-700 flex items-center justify-center gap-1 mx-auto cursor-pointer"
                  >
                    <ArrowLeft size={13} />
                    Return to Sign In
                  </button>
                </div>
              </div>
            </div>
          )}

        </div>
      </div>
    </div>
  );
};

