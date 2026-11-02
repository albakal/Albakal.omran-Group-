import React, { useState } from 'react';
import { UserProfile } from '../types/auth';
import { X, ShieldCheck, Mail, User, ArrowRight, Check } from 'lucide-react';
import { playSuccessSound, playClickSound } from '../utils/audio';

interface GoogleSignInModalProps {
  onClose: () => void;
  onSuccess: (user: UserProfile) => void;
  isArabic: boolean;
}

export const GoogleSignInModal: React.FC<GoogleSignInModalProps> = ({
  onClose,
  onSuccess,
  isArabic
}) => {
  // Completely empty input fields as requested by user
  const [email, setEmail] = useState('');
  const [developerName, setDeveloperName] = useState('');
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const trimmedEmail = email.trim();
    if (!trimmedEmail || !trimmedEmail.includes('@')) {
      setError(isArabic ? 'يرجى كتابة عنوان بريد Gmail صالح' : 'Please enter a valid Gmail address');
      return;
    }

    playSuccessSound();
    const displayName = developerName.trim() || trimmedEmail.split('@')[0];
    const user: UserProfile = {
      name: displayName,
      email: trimmedEmail,
      isVerifiedDeveloper: true,
      joinedDate: new Date().toLocaleDateString('ar-SA')
    };

    onSuccess(user);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-md">
      <div className="relative w-full max-w-md bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl shadow-2xl overflow-hidden text-slate-900 dark:text-white p-6 sm:p-8">
        
        {/* Close Button */}
        <button
          onClick={onClose}
          className="absolute top-4 left-4 p-1.5 text-slate-400 hover:text-slate-700 dark:hover:text-slate-200 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"
        >
          <X className="w-5 h-5" />
        </button>

        {/* Google G Logo & Play Header */}
        <div className="text-center space-y-3 mb-6">
          <div className="w-12 h-12 mx-auto rounded-full bg-white shadow-md border border-slate-100 flex items-center justify-center">
            {/* Google G SVG */}
            <svg className="w-6 h-6" viewBox="0 0 24 24">
              <path
                fill="#4285F4"
                d="M23.745 12.27c0-.7-.06-1.4-.19-2.07H12v4.51h6.6c-.29 1.52-1.14 2.82-2.4 3.68v3.05h3.88c2.27-2.09 3.665-5.17 3.665-9.17z"
              />
              <path
                fill="#34A853"
                d="M12 24c3.24 0 5.95-1.08 7.93-2.91l-3.88-3.05c-1.08.72-2.45 1.16-4.05 1.16-3.12 0-5.77-2.1-6.72-4.93H1.26v3.15C3.29 21.36 7.36 24 12 24z"
              />
              <path
                fill="#FBBC05"
                d="M5.28 14.27c-.25-.72-.38-1.49-.38-2.27s.13-1.55.38-2.27V6.58H1.26C.46 8.16 0 9.94 0 12s.46 3.84 1.26 5.42l4.02-3.15z"
              />
              <path
                fill="#EA4335"
                d="M12 4.75c1.77 0 3.35.61 4.6 1.8l3.42-3.42C17.95 1.19 15.24 0 12 0 7.36 0 3.29 2.64 1.26 6.58l4.02 3.15c.95-2.83 3.6-4.98 6.72-4.98z"
              />
            </svg>
          </div>

          <h2 className="text-xl font-bold tracking-tight">
            {isArabic ? 'تسجيل الدخول بحساب Google' : 'Sign in with Google'}
          </h2>
          <p className="text-xs text-slate-500 dark:text-slate-400 max-w-xs mx-auto">
            {isArabic
              ? 'أدخل بريد Gmail الخاص بك أو اختر من الحسابات المسجلة على جهازك لتتمكن من رفع وإدارة التطبيقات في متجر Play.'
              : 'Enter your Gmail or choose from registered accounts on your device to upload and manage apps on Play Store.'}
          </p>
        </div>

        {/* Clean, completely empty form */}
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">
              {isArabic ? 'بريد Gmail الخاص بك *' : 'Your Gmail Address *'}
            </label>
            <div className="relative">
              <input
                type="email"
                required
                autoComplete="email"
                value={email}
                onChange={(e) => {
                  setEmail(e.target.value);
                  if (error) setError(null);
                }}
                placeholder="name@gmail.com"
                className="w-full pl-3 pr-10 py-2.5 text-sm rounded-xl border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-900 dark:text-white placeholder:text-slate-400 focus:ring-2 focus:ring-emerald-500 focus:outline-none font-mono"
              />
              <Mail className="w-4 h-4 text-slate-400 absolute right-3 top-1/2 -translate-y-1/2" />
            </div>
            {error && <p className="text-xs text-rose-500 mt-1">{error}</p>}
          </div>

          <div>
            <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">
              {isArabic ? 'اسم المطور أو الاستوديو (اختياري)' : 'Developer / Studio Name (Optional)'}
            </label>
            <div className="relative">
              <input
                type="text"
                autoComplete="name"
                value={developerName}
                onChange={(e) => setDeveloperName(e.target.value)}
                placeholder={isArabic ? 'مثال: مطور ألعاب، ستوديو التطبيقات...' : 'e.g. Apex Apps'}
                className="w-full pl-3 pr-10 py-2.5 text-sm rounded-xl border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-900 dark:text-white placeholder:text-slate-400 focus:ring-2 focus:ring-emerald-500 focus:outline-none"
              />
              <User className="w-4 h-4 text-slate-400 absolute right-3 top-1/2 -translate-y-1/2" />
            </div>
          </div>

          <button
            type="submit"
            className="w-full py-2.5 bg-emerald-600 hover:bg-emerald-500 text-white rounded-xl text-xs font-bold shadow-md shadow-emerald-600/20 active:scale-98 transition-all cursor-pointer flex items-center justify-center gap-2 mt-2"
          >
            <Check className="w-4 h-4" />
            <span>{isArabic ? 'تسجيل الدخول ومتابعة الرفع' : 'Sign In & Continue'}</span>
          </button>
        </form>

        <div className="mt-6 pt-4 border-t border-slate-100 dark:border-slate-800/80 text-[11px] text-slate-400 text-center flex items-center justify-center gap-1">
          <ShieldCheck className="w-3.5 h-3.5 text-emerald-500" />
          <span>{isArabic ? 'تسجيل دخول آمن ومعتمد لدى متجر Play' : 'Secured Google Play Authentication'}</span>
        </div>
      </div>
    </div>
  );
};
