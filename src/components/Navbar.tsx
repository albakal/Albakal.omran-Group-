import React from 'react';
import { useApp } from '../context/AppContext';
import { Moon, Sun, Download, SlidersHorizontal, MessageSquare, PhoneCall } from 'lucide-react';

interface NavbarProps {
  onNavigate: (sectionId: string) => void;
}

export const Navbar: React.FC<NavbarProps> = ({ onNavigate }) => {
  const { config, isDarkMode, toggleDarkMode, setIsAdminOpen, triggerApkDownload } = useApp();

  return (
    <header className="sticky top-0 z-40 w-full backdrop-blur-md bg-white/90 dark:bg-slate-950/90 border-b border-slate-200 dark:border-slate-800 transition-colors">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-20 gap-8">
          
          {/* Zone 1: Single element brand mark / wordmark */}
          <div 
            onClick={() => onNavigate('hero')}
            className="flex items-center gap-3 cursor-pointer select-none group shrink-0"
          >
            <img 
              src={config.logoUrl} 
              alt={config.groupName}
              className="h-12 w-auto max-w-[150px] object-contain rounded-lg transition-transform group-hover:scale-105"
              referrerPolicy="no-referrer"
              onError={(e) => {
                // fallback styled container if path is altered
                (e.target as HTMLElement).style.display = 'none';
              }}
            />
            <div className="hidden sm:flex flex-col text-right">
              <span className="text-lg font-bold tracking-tight text-slate-900 dark:text-white whitespace-nowrap">
                {config.groupName.split('|')[0].trim()}
              </span>
              <span className="text-xs text-blue-600 dark:text-emerald-400 font-medium whitespace-nowrap">
                {config.slogan}
              </span>
            </div>
          </div>

          {/* Zone 2: Clean single-line text navigation links */}
          <nav className="hidden lg:flex items-center gap-7 text-sm font-semibold text-slate-600 dark:text-slate-300">
            <button 
              onClick={() => onNavigate('hero')}
              className="hover:text-blue-600 dark:hover:text-emerald-400 transition-colors whitespace-nowrap cursor-pointer py-1"
            >
              الرئيسية والتطبيق
            </button>
            <button 
              onClick={() => onNavigate('features')}
              className="hover:text-blue-600 dark:hover:text-emerald-400 transition-colors whitespace-nowrap cursor-pointer py-1"
            >
              حلول التجار والعملاء
            </button>
            <button 
              onClick={() => onNavigate('about')}
              className="hover:text-blue-600 dark:hover:text-emerald-400 transition-colors whitespace-nowrap cursor-pointer py-1"
            >
              عن الشركة والمؤسس
            </button>
            <button 
              onClick={() => onNavigate('media')}
              className="hover:text-blue-600 dark:hover:text-emerald-400 transition-colors whitespace-nowrap cursor-pointer py-1"
            >
              شروحات الفيديو
            </button>
            <button 
              onClick={() => onNavigate('support')}
              className="hover:text-blue-600 dark:hover:text-emerald-400 transition-colors whitespace-nowrap cursor-pointer py-1"
            >
              الدعم وخدمة العملاء
            </button>
          </nav>

          {/* Zone 3: Actions (Theme toggle, Admin Control, Primary Download CTA) */}
          <div className="flex items-center gap-3 shrink-0">
            
            {/* Theme Toggle */}
            <button
              onClick={toggleDarkMode}
              title={isDarkMode ? 'التبديل إلى الوضع النهاري' : 'التبديل إلى الوضع الليلي'}
              className="p-2.5 text-slate-500 hover:text-slate-900 dark:text-slate-400 dark:hover:text-white rounded-xl bg-slate-100 dark:bg-slate-900 hover:bg-slate-200 dark:hover:bg-slate-800 transition-all cursor-pointer"
            >
              {isDarkMode ? <Sun className="w-5 h-5 text-amber-400" /> : <Moon className="w-5 h-5 text-blue-600" />}
            </button>

            {/* Admin Dashboard Trigger */}
            <button
              onClick={() => setIsAdminOpen(true)}
              title="لوحة تحكم الأدمن (تعديل المحتوى، الصور، والألوان والواتساب)"
              className="flex items-center gap-1.5 px-3 py-2 text-xs font-semibold text-slate-700 dark:text-slate-200 bg-slate-100 dark:bg-slate-800/80 hover:bg-blue-50 dark:hover:bg-slate-700 border border-slate-300 dark:border-slate-700 rounded-xl transition-all cursor-pointer"
            >
              <SlidersHorizontal className="w-4 h-4 text-blue-600 dark:text-emerald-400" />
              <span className="hidden md:inline">لوحة التحكم</span>
            </button>

            {/* Primary Download Button */}
            <button
              onClick={triggerApkDownload}
              className="flex items-center gap-2 px-4 py-2.5 text-xs sm:text-sm font-bold text-white bg-gradient-to-r from-blue-700 to-blue-600 hover:from-blue-600 hover:to-blue-500 dark:from-emerald-600 dark:to-blue-600 dark:hover:from-emerald-500 dark:hover:to-blue-500 rounded-xl shadow-md shadow-blue-900/20 hover:shadow-lg transition-all cursor-pointer whitespace-nowrap active:scale-95"
            >
              <Download className="w-4 h-4" />
              <span>تحميل التطبيق</span>
            </button>

          </div>

        </div>
      </div>
    </header>
  );
};
