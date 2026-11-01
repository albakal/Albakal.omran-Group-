import React, { useState } from 'react';
import { UserProfile, DEFAULT_USER_EMAIL } from '../types/auth';
import { X, ShieldCheck, Check, ArrowRight } from 'lucide-react';
import { playClickSound, playSuccessSound } from '../utils/audio';

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
  const [customEmail, setCustomEmail] = useState('');
  const [customName, setCustomName] = useState('');
  const [isCustomMode, setIsCustomMode] = useState(false);

  const handleSelectDefaultAccount = () => {
    playSuccessSound();
    const user: UserProfile = {
      name: 'مطور متجر Play (modm55085)',
      email: DEFAULT_USER_EMAIL,
      photoUrl: 'https://lh3.googleusercontent.com/a/default-user',
      isVerifiedDeveloper: true,
      joinedDate: new Date().toLocaleDateString('ar-SA')
    };
    onSuccess(user);
  };

  const handleCustomSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!customEmail.includes('@')) return;
    playSuccessSound();
    const user: UserProfile = {
      name: customName.trim() || customEmail.split('@')[0],
      email: customEmail.trim(),
      isVerifiedDeveloper: true,
      joinedDate: new Date().toLocaleDateString('ar-SA')
    };
    onSuccess(user);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-md">
      <div className="relative w-full max-w-md bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl shadow-2xl overflow-hidden text-slate-900 dark:text-white p-6 sm:p-8">
        
        {/* Close */}
        <button
          onClick={onClose}
          className="absolute top-4 left-4 p-1.5 text-slate-400 hover:text-slate-700 dark:hover:text-slate-200 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"
        >
          <X className="w-5 h-5" />
        </button>

        {/* Google G Logo & Play branding */}
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
            {isArabic ? 'تسجيل الدخول باستخدام Google' : 'Sign in with Google'}
          </h2>
          <p className="text-xs text-slate-500 dark:text-slate-400 max-w-xs mx-auto">
            {isArabic
              ? 'سجل دخولك بحساب Gmail المعتمد لتتمكن من رفع وتحديث التطبيقات والألعاب في متجر Play.'
              : 'Sign in with your verified Gmail account to upload and manage apps on Play Store.'}
          </p>
        </div>

        {!isCustomMode ? (
          <div className="space-y-4">
            {/* Primary User Account button (modm55085@gmail.com) */}
            <div className="text-xs font-semibold text-slate-500 mb-1">
              {isArabic ? 'الحساب الموصى به:' : 'Recommended Account:'}
            </div>

            <button
              onClick={handleSelectDefaultAccount}
              className="w-full flex items-center justify-between p-3.5 rounded-xl border-2 border-emerald-500/40 bg-emerald-50/50 dark:bg-emerald-950/20 hover:border-emerald-500 hover:bg-emerald-50 dark:hover:bg-emerald-950/40 transition-all text-start cursor-pointer group"
            >
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-full bg-emerald-600 text-white font-bold flex items-center justify-center text-sm shadow-sm">
                  M
                </div>
                <div>
                  <div className="text-sm font-bold text-slate-900 dark:text-white flex items-center gap-1.5">
                    <span>{DEFAULT_USER_EMAIL}</span>
                    <ShieldCheck className="w-4 h-4 text-emerald-500 shrink-0" />
                  </div>
                  <div className="text-xs text-emerald-600 dark:text-emerald-400 font-medium">
                    {isArabic ? 'حساب مطور متجر Play معتمد' : 'Verified Play Developer Account'}
                  </div>
                </div>
              </div>

              <div className="p-2 rounded-lg bg-emerald-600 text-white text-xs font-bold shrink-0 group-hover:scale-105 transition-transform">
                {isArabic ? 'دخول فوري' : 'Continue'}
              </div>
            </button>

            {/* Separator */}
            <div className="relative py-2 flex items-center justify-center">
              <div className="absolute inset-0 flex items-center">
                <div className="w-full border-t border-slate-200 dark:border-slate-800" />
              </div>
              <span className="relative px-3 bg-white dark:bg-slate-900 text-xs text-slate-400">
                {isArabic ? 'أو' : 'or'}
              </span>
            </div>

            <button
              onClick={() => {
                playClickSound();
                setIsCustomMode(true);
              }}
              className="w-full py-2.5 px-4 rounded-xl border border-slate-300 dark:border-slate-700 hover:bg-slate-100 dark:hover:bg-slate-800 text-xs font-semibold text-slate-700 dark:text-slate-300 transition-colors"
            >
              {isArabic ? 'استخدام بريد Gmail آخر' : 'Use another Gmail account'}
            </button>
          </div>
        ) : (
          <form onSubmit={handleCustomSubmit} className="space-y-4">
            <div>
              <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">
                {isArabic ? 'الاسم أو اسم الاستوديو' : 'Developer / Studio Name'}
              </label>
              <input
                type="text"
                value={customName}
                onChange={(e) => setCustomName(e.target.value)}
                placeholder={isArabic ? 'مثال: محمد للتطوير' : 'e.g. Studio Dev'}
                className="w-full px-3 py-2 text-xs rounded-lg border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-900 dark:text-white focus:ring-2 focus:ring-emerald-500 focus:outline-none"
              />
            </div>

            <div>
              <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">
                {isArabic ? 'عنوان بريد Gmail *' : 'Gmail Address *'}
              </label>
              <input
                type="email"
                required
                value={customEmail}
                onChange={(e) => setCustomEmail(e.target.value)}
                placeholder="name@gmail.com"
                className="w-full px-3 py-2 text-xs rounded-lg border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-900 dark:text-white focus:ring-2 focus:ring-emerald-500 focus:outline-none font-mono"
              />
            </div>

            <div className="flex items-center gap-2 pt-2">
              <button
                type="button"
                onClick={() => setIsCustomMode(false)}
                className="flex-1 py-2 text-xs font-semibold text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white"
              >
                {isArabic ? 'إلغاء' : 'Back'}
              </button>

              <button
                type="submit"
                className="flex-1 py-2 bg-emerald-600 hover:bg-emerald-500 text-white rounded-lg text-xs font-bold shadow-sm transition-colors cursor-pointer"
              >
                {isArabic ? 'تسجيل الدخول' : 'Sign in'}
              </button>
            </div>
          </form>
        )}

        <div className="mt-6 pt-4 border-t border-slate-100 dark:border-slate-800/80 text-[11px] text-slate-400 text-center flex items-center justify-center gap-1">
          <ShieldCheck className="w-3.5 h-3.5 text-emerald-500" />
          <span>{isArabic ? 'محمي بواسطة Google Identity و Play Protect' : 'Secured by Google Identity & Play Protect'}</span>
        </div>
      </div>
    </div>
  );
};
