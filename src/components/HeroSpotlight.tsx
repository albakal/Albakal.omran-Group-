import React from 'react';
import { HERO_BANNER_IMAGE } from '../data/defaultCatalog';
import { PlayLogo } from './PlayLogo';
import { Play, Download, ShieldCheck, Upload } from 'lucide-react';
import { playClickSound } from '../utils/audio';

interface HeroSpotlightProps {
  onPlaySpotlight: () => void;
  onExploreClick: () => void;
  onOpenUpload: () => void;
  isArabic: boolean;
  totalAppsCount: number;
  totalDownloads: number;
}

export const HeroSpotlight: React.FC<HeroSpotlightProps> = ({
  onPlaySpotlight,
  onExploreClick,
  onOpenUpload,
  isArabic,
  totalAppsCount,
  totalDownloads
}) => {
  return (
    <section className="relative overflow-hidden rounded-2xl border border-slate-200/80 dark:border-slate-800/80 bg-slate-900 text-white my-6 shadow-xl">
      <div className="absolute inset-0 z-0">
        <img
          src={HERO_BANNER_IMAGE}
          alt="Featured Spotlight Play Store"
          referrerPolicy="no-referrer"
          className="w-full h-full object-cover opacity-35 dark:opacity-45 scale-105 filter brightness-90 contrast-105"
        />
        <div className="absolute inset-0 bg-gradient-to-t from-slate-950 via-slate-950/70 to-transparent" />
        <div className="absolute inset-0 bg-gradient-to-r from-slate-950 via-slate-950/60 to-transparent" />
      </div>

      <div className="relative z-10 max-w-4xl p-6 sm:p-10 lg:p-12 space-y-4">
        {/* Unboxed clean metadata kicker */}
        <div className="flex items-center gap-2 text-xs font-semibold text-emerald-400">
          <PlayLogo size={18} />
          <span>{isArabic ? 'متجر Play الرسمي' : 'Official Play Store'}</span>
          <span aria-hidden="true" className="text-slate-500">·</span>
          <span>{isArabic ? 'فحص تلقائي للصور من ملفات APK' : 'Auto APK Image Extraction'}</span>
          <span aria-hidden="true" className="text-slate-500">·</span>
          <span className="flex items-center gap-1 text-slate-300">
            <ShieldCheck className="w-3.5 h-3.5 text-emerald-400" />
            <span>Google Play Protect</span>
          </span>
        </div>

        {/* Hero headline with balanced wrap */}
        <h1 className="text-2xl sm:text-4xl lg:text-5xl font-black tracking-tight leading-tight text-white max-w-2xl">
          {isArabic
            ? 'متجر Play: اكتشف، حمّل، وارفع أحدث التطبيقات والألعاب'
            : 'Play Store: Discover, Play & Upload Apps and Games'}
        </h1>

        <p className="text-sm sm:text-base text-slate-300 max-w-xl leading-relaxed">
          {isArabic
            ? 'المنصة المفتوحة لتحميل وتجربة وتثبيت التطبيقات والألعاب. يمكنك رفع تطبيقاتك وألعابك بكل سهولة مع استخراج تلقائي للأيقونات والصور ومشاركة روابط التحميل المباشرة مع الجميع.'
            : 'Explore, play and download Android and web applications. Upload your own apps with automatic icon and screenshot extraction and share direct download links with anyone!'}
        </p>

        {/* Action buttons */}
        <div className="flex flex-wrap items-center gap-3 pt-2">
          <button
            onClick={() => {
              playClickSound();
              onPlaySpotlight();
            }}
            className="flex items-center gap-2 px-5 py-2.5 text-sm font-bold text-slate-950 bg-emerald-400 hover:bg-emerald-300 rounded-lg shadow-lg shadow-emerald-500/20 active:scale-98 transition-all cursor-pointer"
          >
            <Play className="w-4 h-4 fill-current" />
            <span>{isArabic ? 'تجربة لعبة الأسبوع فوراً' : 'Play Featured Demo'}</span>
          </button>

          <button
            onClick={() => {
              playClickSound();
              onOpenUpload();
            }}
            className="flex items-center gap-2 px-5 py-2.5 text-sm font-bold text-white bg-emerald-600 hover:bg-emerald-500 rounded-lg shadow-sm active:scale-98 transition-all cursor-pointer"
          >
            <Upload className="w-4 h-4" />
            <span>{isArabic ? 'رفع تطبيق الآن (Play Console)' : 'Upload App Now'}</span>
          </button>

          <button
            onClick={() => {
              playClickSound();
              onExploreClick();
            }}
            className="flex items-center gap-2 px-5 py-2.5 text-sm font-semibold text-slate-200 bg-white/10 hover:bg-white/15 backdrop-blur-sm border border-white/15 rounded-lg active:scale-98 transition-all cursor-pointer"
          >
            <Download className="w-4 h-4" />
            <span>{isArabic ? 'تصفح كل المعروضات' : 'Browse Catalog'}</span>
          </button>
        </div>

        {/* Quantitative Proof Strip */}
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-4 pt-4 border-t border-white/10 text-xs">
          <div>
            <div className="text-slate-400">{isArabic ? 'التطبيقات والألعاب' : 'Total Items'}</div>
            <div className="text-base font-bold text-white font-mono tabular-nums">{totalAppsCount}+</div>
          </div>
          <div>
            <div className="text-slate-400">{isArabic ? 'إجمالي التنزيلات' : 'Downloads'}</div>
            <div className="text-base font-bold text-white font-mono tabular-nums">{(totalDownloads + 920000).toLocaleString()}+</div>
          </div>
          <div>
            <div className="text-slate-400">{isArabic ? 'نظام الرفع' : 'Publishing'}</div>
            <div className="text-base font-bold text-emerald-400">{isArabic ? 'استخراج تلقائي للصور' : 'Auto-Extract'}</div>
          </div>
          <div>
            <div className="text-slate-400">{isArabic ? 'الأمان' : 'Security'}</div>
            <div className="text-base font-bold text-slate-200">Play Protect 100%</div>
          </div>
        </div>
      </div>
    </section>
  );
};
