import React, { useState } from 'react';
import { StoreTab } from '../types/store';
import { UserProfile } from '../types/auth';
import { PlayLogo } from './PlayLogo';
import { Plus, Sun, Moon, Heart, Gamepad2, Layers, Home, UploadCloud, LogIn, LogOut, ShieldCheck, User } from 'lucide-react';
import { playClickSound } from '../utils/audio';

interface NavbarProps {
  currentTab: StoreTab;
  onSelectTab: (tab: StoreTab) => void;
  onOpenUpload: () => void;
  favoritesCount: number;
  myAppsCount: number;
  isDark: boolean;
  onToggleTheme: () => void;
  isArabic: boolean;
  onToggleLanguage: () => void;
  currentUser: UserProfile | null;
  onOpenSignIn: () => void;
  onSignOut: () => void;
}

export const Navbar: React.FC<NavbarProps> = ({
  currentTab,
  onSelectTab,
  onOpenUpload,
  favoritesCount,
  myAppsCount,
  isDark,
  onToggleTheme,
  isArabic,
  onToggleLanguage,
  currentUser,
  onOpenSignIn,
  onSignOut
}) => {
  const [showUserMenu, setShowUserMenu] = useState(false);

  const handleNavClick = (tab: StoreTab) => {
    playClickSound();
    onSelectTab(tab);
  };

  return (
    <header className="sticky top-0 z-40 w-full border-b border-slate-200 dark:border-slate-800 bg-white/95 dark:bg-slate-950/95 backdrop-blur-md transition-colors">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between gap-8 h-16">
          
          {/* Zone 1: Single text element wordmark with Play Logo */}
          <button
            onClick={() => handleNavClick('all')}
            className="flex items-center gap-2.5 text-lg font-black tracking-tight text-slate-900 dark:text-white whitespace-nowrap shrink-0 group focus:outline-none"
          >
            <PlayLogo size={28} className="group-hover:scale-105 transition-transform" />
            <span className="font-bold text-slate-900 dark:text-white">
              {isArabic ? 'متجر Play' : 'Play Store'}
            </span>
          </button>

          {/* Zone 2: 4-5 clean single-line text navigation links */}
          <nav className="hidden md:flex items-center gap-6 text-sm font-medium text-slate-600 dark:text-slate-300">
            <button
              onClick={() => handleNavClick('all')}
              className={`flex items-center gap-1.5 transition-colors whitespace-nowrap shrink-0 pb-0.5 border-b-2 ${
                currentTab === 'all'
                  ? 'border-emerald-500 text-slate-900 dark:text-white font-bold'
                  : 'border-transparent hover:text-slate-900 dark:hover:text-white'
              }`}
            >
              <Home className="w-4 h-4" />
              <span>{isArabic ? 'الرئيسية' : 'Home'}</span>
            </button>

            <button
              onClick={() => handleNavClick('apps')}
              className={`flex items-center gap-1.5 transition-colors whitespace-nowrap shrink-0 pb-0.5 border-b-2 ${
                currentTab === 'apps'
                  ? 'border-emerald-500 text-slate-900 dark:text-white font-bold'
                  : 'border-transparent hover:text-slate-900 dark:hover:text-white'
              }`}
            >
              <Layers className="w-4 h-4" />
              <span>{isArabic ? 'التطبيقات' : 'Apps'}</span>
            </button>

            <button
              onClick={() => handleNavClick('games')}
              className={`flex items-center gap-1.5 transition-colors whitespace-nowrap shrink-0 pb-0.5 border-b-2 ${
                currentTab === 'games'
                  ? 'border-emerald-500 text-slate-900 dark:text-white font-bold'
                  : 'border-transparent hover:text-slate-900 dark:hover:text-white'
              }`}
            >
              <Gamepad2 className="w-4 h-4" />
              <span>{isArabic ? 'الألعاب' : 'Games'}</span>
            </button>

            <button
              onClick={() => handleNavClick('my-apps')}
              className={`flex items-center gap-1.5 transition-colors whitespace-nowrap shrink-0 pb-0.5 border-b-2 ${
                currentTab === 'my-apps'
                  ? 'border-emerald-500 text-slate-900 dark:text-white font-bold'
                  : 'border-transparent hover:text-slate-900 dark:hover:text-white'
              }`}
            >
              <UploadCloud className="w-4 h-4" />
              <span>{isArabic ? 'تطبيقاتي المرفوعة' : 'My Uploads'}</span>
              {myAppsCount > 0 && (
                <span className="text-xs px-1.5 py-0.2 bg-slate-200 dark:bg-slate-800 text-slate-700 dark:text-slate-300 rounded font-mono">
                  {myAppsCount}
                </span>
              )}
            </button>

            <button
              onClick={() => handleNavClick('favorites')}
              className={`flex items-center gap-1.5 transition-colors whitespace-nowrap shrink-0 pb-0.5 border-b-2 ${
                currentTab === 'favorites'
                  ? 'border-emerald-500 text-slate-900 dark:text-white font-bold'
                  : 'border-transparent hover:text-slate-900 dark:hover:text-white'
              }`}
            >
              <Heart className="w-4 h-4" />
              <span>{isArabic ? 'المفضلة' : 'Favorites'}</span>
              {favoritesCount > 0 && (
                <span className="text-xs px-1.5 py-0.2 bg-slate-200 dark:bg-slate-800 text-slate-700 dark:text-slate-300 rounded font-mono">
                  {favoritesCount}
                </span>
              )}
            </button>
          </nav>

          {/* Zone 3: Primary Action + Discreet Developer Account */}
          <div className="flex items-center gap-3 shrink-0">
            <button
              onClick={onToggleLanguage}
              className="text-xs font-semibold px-2 py-1 text-slate-600 dark:text-slate-300 hover:text-slate-900 dark:hover:text-white transition-colors"
            >
              {isArabic ? 'EN' : 'عربي'}
            </button>

            <button
              onClick={onToggleTheme}
              className="p-2 rounded-lg text-slate-500 hover:text-slate-900 dark:text-slate-400 dark:hover:text-white transition-colors"
              aria-label="Toggle Theme"
            >
              {isDark ? <Sun className="w-4 h-4" /> : <Moon className="w-4 h-4" />}
            </button>

            {/* Discreet Developer Account (Email is completely hidden) */}
            {currentUser ? (
              <div className="relative">
                <button
                  onClick={() => setShowUserMenu(!showUserMenu)}
                  className="flex items-center gap-1.5 p-1.5 px-3 rounded-full border border-slate-200 dark:border-slate-700 bg-slate-100 dark:bg-slate-800 hover:border-emerald-500 transition-colors"
                >
                  <div className="w-6 h-6 rounded-full bg-emerald-600 text-white font-bold flex items-center justify-center text-xs shadow-xs">
                    <User className="w-3.5 h-3.5" />
                  </div>
                  <span className="text-xs font-semibold text-slate-700 dark:text-slate-300 hidden sm:block">
                    {isArabic ? 'لوحة المطور' : 'Dev Console'}
                  </span>
                </button>

                {showUserMenu && (
                  <div className="absolute left-0 mt-2 w-56 rounded-xl border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900 shadow-xl p-3 z-50 text-xs space-y-2">
                    <div className="p-2 rounded-lg bg-slate-50 dark:bg-slate-800/60">
                      <div className="font-bold text-slate-900 dark:text-white flex items-center gap-1.5">
                        <ShieldCheck className="w-3.5 h-3.5 text-emerald-500" />
                        <span>{currentUser.name}</span>
                      </div>
                      <div className="text-[11px] text-slate-500 mt-0.5">
                        {isArabic ? 'حساب مطور معتمد' : 'Verified Developer'}
                      </div>
                    </div>

                    <button
                      onClick={() => {
                        setShowUserMenu(false);
                        onSelectTab('my-apps');
                      }}
                      className="w-full flex items-center gap-2 p-2 rounded-lg text-slate-700 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors text-start"
                    >
                      <UploadCloud className="w-3.5 h-3.5" />
                      <span>{isArabic ? 'إدارة تطبيقاتي' : 'Manage My Apps'}</span>
                    </button>

                    <button
                      onClick={() => {
                        setShowUserMenu(false);
                        onSignOut();
                      }}
                      className="w-full flex items-center gap-2 p-2 rounded-lg text-rose-500 hover:bg-rose-50 dark:hover:bg-rose-950/30 transition-colors text-start"
                    >
                      <LogOut className="w-3.5 h-3.5" />
                      <span>{isArabic ? 'تسجيل الخروج' : 'Sign Out'}</span>
                    </button>
                  </div>
                )}
              </div>
            ) : (
              <button
                onClick={() => {
                  playClickSound();
                  onOpenSignIn();
                }}
                className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg border border-slate-300 dark:border-slate-700 hover:bg-slate-100 dark:hover:bg-slate-800 text-xs font-semibold text-slate-700 dark:text-slate-200 transition-colors"
              >
                <LogIn className="w-3.5 h-3.5 text-emerald-500" />
                <span>{isArabic ? 'دخول المطورين' : 'Dev Login'}</span>
              </button>
            )}

            {/* Primary Action Button */}
            <button
              onClick={() => {
                playClickSound();
                onOpenUpload();
              }}
              className="flex items-center gap-2 px-4 py-2 text-xs font-bold text-white bg-emerald-600 rounded-lg hover:bg-emerald-500 active:scale-98 shadow-sm transition-all whitespace-nowrap shrink-0 cursor-pointer"
            >
              <Plus className="w-4 h-4" />
              <span>{isArabic ? 'رفع تطبيق أو لعبة' : 'Upload App'}</span>
            </button>
          </div>
        </div>

        {/* Mobile secondary tab strip */}
        <div className="md:hidden flex items-center justify-between overflow-x-auto py-2.5 border-t border-slate-100 dark:border-slate-800/60 text-xs font-medium text-slate-600 dark:text-slate-400 gap-3">
          <button
            onClick={() => handleNavClick('all')}
            className={`whitespace-nowrap ${currentTab === 'all' ? 'text-emerald-500 font-bold' : ''}`}
          >
            {isArabic ? 'الرئيسية' : 'Home'}
          </button>
          <button
            onClick={() => handleNavClick('apps')}
            className={`whitespace-nowrap ${currentTab === 'apps' ? 'text-emerald-500 font-bold' : ''}`}
          >
            {isArabic ? 'التطبيقات' : 'Apps'}
          </button>
          <button
            onClick={() => handleNavClick('games')}
            className={`whitespace-nowrap ${currentTab === 'games' ? 'text-emerald-500 font-bold' : ''}`}
          >
            {isArabic ? 'الألعاب' : 'Games'}
          </button>
          <button
            onClick={() => handleNavClick('my-apps')}
            className={`whitespace-nowrap ${currentTab === 'my-apps' ? 'text-emerald-500 font-bold' : ''}`}
          >
            {isArabic ? `تطبيقاتي (${myAppsCount})` : `My Uploads (${myAppsCount})`}
          </button>
          <button
            onClick={() => handleNavClick('favorites')}
            className={`whitespace-nowrap ${currentTab === 'favorites' ? 'text-emerald-500 font-bold' : ''}`}
          >
            {isArabic ? `المفضلة (${favoritesCount})` : `Favorites (${favoritesCount})`}
          </button>
        </div>
      </div>
    </header>
  );
};
