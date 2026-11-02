import React from 'react';
import { useApp } from '../context/AppContext';
import { Download, Play, Smartphone, CheckCircle, Store, TrendingDown, Zap, ShieldCheck, ArrowDownToLine, Sparkles } from 'lucide-react';

interface HeroSectionProps {
  onWatchVideo: () => void;
  onExploreFeatures: () => void;
}

export const HeroSection: React.FC<HeroSectionProps> = ({ onWatchVideo, onExploreFeatures }) => {
  const { config, triggerApkDownload } = useApp();

  const getStatIcon = (name: string) => {
    switch (name) {
      case 'Store': return <Store className="w-5 h-5 text-blue-500 dark:text-emerald-400" />;
      case 'TrendingDown': return <TrendingDown className="w-5 h-5 text-emerald-500 dark:text-emerald-400" />;
      case 'Zap': return <Zap className="w-5 h-5 text-amber-500 dark:text-amber-400" />;
      case 'ShieldCheck': return <ShieldCheck className="w-5 h-5 text-teal-500 dark:text-teal-400" />;
      default: return <Sparkles className="w-5 h-5 text-blue-500" />;
    }
  };

  return (
    <section id="hero" className="relative pt-12 pb-20 overflow-hidden">
      
      {/* Background Ambient Glows */}
      <div className="absolute top-0 right-1/4 w-96 h-96 bg-blue-600/10 dark:bg-blue-600/15 rounded-full blur-3xl pointer-events-none" />
      <div className="absolute top-1/3 left-10 w-80 h-80 bg-emerald-500/10 dark:bg-emerald-500/15 rounded-full blur-3xl pointer-events-none" />

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
        
        {/* Main 2-Column Split: Copy on Right (in RTL), Mockup on Left */}
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-12 lg:gap-8 items-center mb-16">
          
          {/* Column 1: Marketing Copy (7 cols) */}
          <div className="lg:col-span-7 space-y-6 text-right">
            
            {/* Subtle Brand Badge (Unboxed text with border) */}
            <div className="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-blue-50 dark:bg-blue-950/50 border border-blue-200 dark:border-blue-800 text-xs font-semibold text-blue-700 dark:text-blue-300">
              <span className="w-2 h-2 rounded-full bg-emerald-500"></span>
              <span>{config.heroBadge}</span>
            </div>

            {/* Headline */}
            <h1 className="text-3xl sm:text-4xl lg:text-5xl font-extrabold text-slate-900 dark:text-white leading-[1.25] tracking-tight">
              {config.heroTitle} : <br className="hidden sm:inline" />
              <span className="bg-gradient-to-l from-blue-700 via-blue-600 to-emerald-600 dark:from-blue-400 dark:via-emerald-400 dark:to-teal-300 bg-clip-text text-transparent">
                {config.heroHighlight}
              </span>
            </h1>

            {/* Description */}
            <p className="text-base sm:text-lg text-slate-600 dark:text-slate-300 leading-relaxed max-w-2xl">
              {config.heroDescription}
            </p>

            {/* Micro Value Highlights */}
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 pt-2 text-sm text-slate-700 dark:text-slate-200 font-medium">
              <div className="flex items-center gap-2">
                <CheckCircle className="w-4 h-4 text-emerald-500 shrink-0" />
                <span>حل اقتصادي للتجار يغنيك عن تكاليف الكاشير</span>
              </div>
              <div className="flex items-center gap-2">
                <CheckCircle className="w-4 h-4 text-emerald-500 shrink-0" />
                <span>فواتير إلكترونية فورية ومشاركة سهلة للعميل</span>
              </div>
              <div className="flex items-center gap-2">
                <CheckCircle className="w-4 h-4 text-emerald-500 shrink-0" />
                <span>تحميل مباشر سريع وتثبيت بضغطة واحدة</span>
              </div>
              <div className="flex items-center gap-2">
                <CheckCircle className="w-4 h-4 text-emerald-500 shrink-0" />
                <span>دعم فني واستجابة بالذكاء الاصطناعي 24/7</span>
              </div>
            </div>

            {/* Action Buttons & Download Row */}
            <div className="pt-4 flex flex-wrap items-center gap-4">
              
              {/* Direct APK Download Button (Primary) */}
              <button
                onClick={triggerApkDownload}
                className="flex items-center gap-3 px-6 py-3.5 bg-gradient-to-r from-blue-600 to-blue-700 hover:from-blue-500 hover:to-blue-600 text-white font-bold rounded-2xl shadow-xl shadow-blue-600/25 hover:shadow-blue-600/35 transition-all cursor-pointer active:scale-95"
              >
                <ArrowDownToLine className="w-5 h-5 text-emerald-300" />
                <div className="text-right">
                  <div className="text-[11px] font-normal text-blue-100">تحميل مباشر وسريع</div>
                  <div className="text-sm font-extrabold">تنزيل التطبيق APK ({config.appSize})</div>
                </div>
              </button>

              {/* Watch Explainer Video Button */}
              <button
                onClick={onWatchVideo}
                className="flex items-center gap-3 px-5 py-3.5 bg-white dark:bg-slate-900 hover:bg-slate-50 dark:hover:bg-slate-800 text-slate-800 dark:text-white font-bold rounded-2xl border border-slate-300 dark:border-slate-700 shadow-sm transition-all cursor-pointer active:scale-95"
              >
                <div className="w-8 h-8 rounded-full bg-blue-100 dark:bg-blue-900/50 flex items-center justify-center text-blue-600 dark:text-blue-400">
                  <Play className="w-4 h-4 fill-current ml-0.5" />
                </div>
                <div className="text-right">
                  <div className="text-[11px] font-normal text-slate-500 dark:text-slate-400">شرح بالفيديو</div>
                  <div className="text-sm">طريقة عمل التطبيق</div>
                </div>
              </button>

            </div>

            {/* App Store / Google Play links */}
            <div className="flex flex-wrap items-center gap-3 pt-2 text-xs text-slate-500 dark:text-slate-400">
              <span className="font-semibold text-slate-700 dark:text-slate-300">متاح أيضاً على:</span>
              <a 
                href={config.googlePlayUrl} 
                target="_blank" 
                rel="noreferrer"
                className="px-3 py-1.5 rounded-lg bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 dark:hover:bg-slate-700 text-slate-700 dark:text-slate-200 transition-colors flex items-center gap-1.5"
              >
                <span className="font-medium">Google Play</span>
              </a>
              <a 
                href={config.appStoreUrl} 
                target="_blank" 
                rel="noreferrer"
                className="px-3 py-1.5 rounded-lg bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 dark:hover:bg-slate-700 text-slate-700 dark:text-slate-200 transition-colors flex items-center gap-1.5"
              >
                <span className="font-medium">App Store</span>
              </a>
              <span className="text-slate-400">·</span>
              <span className="font-mono text-[11px]">{config.appVersion}</span>
            </div>

          </div>

          {/* Column 2: Visual Phone Mockup & Live Experience Card (5 cols) */}
          <div className="lg:col-span-5 relative flex justify-center">
            
            <div className="relative w-full max-w-sm">
              
              {/* Outer decorative card frame */}
              <div className="relative rounded-3xl p-3 bg-gradient-to-b from-blue-500/20 via-slate-200/50 to-emerald-500/20 dark:from-blue-600/30 dark:via-slate-800/60 dark:to-emerald-500/30 border border-slate-200 dark:border-slate-800 shadow-2xl backdrop-blur-sm">
                
                <img 
                  src={config.appMockupImage} 
                  alt={config.appName}
                  className="w-full h-auto rounded-2xl object-cover shadow-inner"
                  referrerPolicy="no-referrer"
                  onError={(e) => {
                    // Fallback visual container if local path issues
                    (e.target as HTMLElement).style.display = 'none';
                  }}
                />

                {/* Floating merchant benefit tag */}
                <div className="absolute top-8 -right-4 sm:-right-6 bg-white/95 dark:bg-slate-900/95 backdrop-blur-md border border-slate-200 dark:border-slate-800 p-3 rounded-2xl shadow-xl flex items-center gap-3 text-right">
                  <div className="w-10 h-10 rounded-xl bg-emerald-100 dark:bg-emerald-950/60 text-emerald-600 dark:text-emerald-400 flex items-center justify-center shrink-0">
                    <Store className="w-5 h-5" />
                  </div>
                  <div>
                    <div className="text-xs font-bold text-slate-900 dark:text-white">نظام نقاط بيع محمول</div>
                    <div className="text-[11px] text-emerald-600 dark:text-emerald-400 font-medium">بدون أجهزة كاشير معقدة</div>
                  </div>
                </div>

                {/* Floating customer satisfaction tag */}
                <div className="absolute bottom-10 -left-4 sm:-left-6 bg-white/95 dark:bg-slate-900/95 backdrop-blur-md border border-slate-200 dark:border-slate-800 p-3 rounded-2xl shadow-xl flex items-center gap-3 text-right">
                  <div className="w-10 h-10 rounded-xl bg-blue-100 dark:bg-blue-950/60 text-blue-600 dark:text-blue-400 flex items-center justify-center shrink-0">
                    <Zap className="w-5 h-5" />
                  </div>
                  <div>
                    <div className="text-xs font-bold text-slate-900 dark:text-white">معاملات دفع فورية</div>
                    <div className="text-[11px] text-blue-600 dark:text-blue-400 font-medium">أقل من ثانية واحدة للعملية</div>
                  </div>
                </div>

              </div>

            </div>

          </div>

        </div>

        {/* 4-Column Statistics Strip (Full Width Row) */}
        <div className="grid grid-cols-2 lg:grid-cols-4 gap-4 p-6 sm:p-8 bg-white dark:bg-slate-900/80 border border-slate-200 dark:border-slate-800 rounded-3xl shadow-lg">
          {config.stats.map((st) => (
            <div key={st.id} className="flex flex-col text-right p-3">
              <div className="flex items-center gap-2 mb-2">
                <div className="p-2 rounded-xl bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-300">
                  {getStatIcon(st.iconName)}
                </div>
                <span className="text-2xl sm:text-3xl font-extrabold text-slate-900 dark:text-white tabular-nums tracking-tight">
                  {st.value}
                </span>
              </div>
              <span className="text-sm font-bold text-slate-800 dark:text-slate-200 mb-1">
                {st.label}
              </span>
              <span className="text-xs text-slate-500 dark:text-slate-400">
                {st.subtext}
              </span>
            </div>
          ))}
        </div>

        {/* Promotional Banner Showcase */}
        <div className="mt-12 rounded-3xl overflow-hidden border border-slate-200 dark:border-slate-800 bg-slate-900 relative shadow-xl">
          <div className="relative h-64 sm:h-80 w-full">
            <img 
              src={config.promoBannerImage} 
              alt="إعلان تطبيق عمران جروب" 
              className="w-full h-full object-cover"
              referrerPolicy="no-referrer"
            />
            <div className="absolute inset-0 bg-gradient-to-t from-slate-950 via-slate-950/60 to-transparent flex flex-col justify-end p-6 sm:p-10 text-right">
              <span className="text-xs uppercase font-bold tracking-widest text-emerald-400 mb-1">
                إعلان تطبيق عمران جروب
              </span>
              <h3 className="text-xl sm:text-2xl font-bold text-white mb-2">
                انضم الآن لأسرع منظومة تجارية رقمية في المنطقة
              </h3>
              <p className="text-xs sm:text-sm text-slate-300 max-w-xl mb-4">
                سواء كنت تاجراً تبحث عن مضاعفة أرباحك وإدارة مبيعاتك بيسر، أو عميلاً يرغب بتسوق ذكي وتوفير حقيقي، تطبيق عمران هو وجهتك الأولى.
              </p>
              <div className="flex items-center gap-3">
                <button 
                  onClick={triggerApkDownload}
                  className="px-5 py-2.5 bg-emerald-600 hover:bg-emerald-500 text-white font-bold text-xs sm:text-sm rounded-xl shadow-md transition-all cursor-pointer flex items-center gap-2"
                >
                  <Download className="w-4 h-4" />
                  <span>تثبيت التطبيق الآن</span>
                </button>
                <button
                  onClick={onWatchVideo}
                  className="px-4 py-2.5 bg-white/10 hover:bg-white/20 text-white font-semibold text-xs sm:text-sm rounded-xl backdrop-blur-md transition-all cursor-pointer flex items-center gap-2"
                >
                  <Play className="w-3.5 h-3.5 fill-current" />
                  <span>مشاهدة العرض الترويجي</span>
                </button>
              </div>
            </div>
          </div>
        </div>

      </div>

    </section>
  );
};
