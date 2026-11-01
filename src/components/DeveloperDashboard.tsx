import React from 'react';
import { AppItem } from '../types/store';
import { Upload, Trash2, ExternalLink, Download, Star, Package, Sparkles, Layers } from 'lucide-react';
import { playClickSound } from '../utils/audio';

interface DeveloperDashboardProps {
  myApps: AppItem[];
  onOpenUpload: () => void;
  onOpenDetails: (app: AppItem) => void;
  onDeleteApp: (id: string) => void;
  onExportCatalog: () => void;
  isArabic: boolean;
}

export const DeveloperDashboard: React.FC<DeveloperDashboardProps> = ({
  myApps,
  onOpenUpload,
  onOpenDetails,
  onDeleteApp,
  onExportCatalog,
  isArabic
}) => {
  const totalDownloads = myApps.reduce((acc, a) => acc + a.downloadCount, 0);
  const avgRating = myApps.length > 0
    ? (myApps.reduce((acc, a) => acc + a.rating, 0) / myApps.length).toFixed(1)
    : '5.0';

  return (
    <section className="space-y-6">
      {/* Overview Stat Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <div className="p-5 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800">
          <div className="flex items-center justify-between text-slate-500 dark:text-slate-400 text-xs">
            <span>{isArabic ? 'تطبيقاتك المرفوعة' : 'Your Uploads'}</span>
            <Package className="w-4 h-4 text-emerald-500" />
          </div>
          <div className="text-2xl font-black text-slate-900 dark:text-white mt-2 font-mono tabular-nums">
            {myApps.length}
          </div>
          <div className="text-xs text-slate-400 mt-1">
            {isArabic ? 'متاحة لجميع زوار المتجر' : 'Live in public store'}
          </div>
        </div>

        <div className="p-5 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800">
          <div className="flex items-center justify-between text-slate-500 dark:text-slate-400 text-xs">
            <span>{isArabic ? 'إجمالي التنزيلات' : 'Total Downloads'}</span>
            <Download className="w-4 h-4 text-emerald-500" />
          </div>
          <div className="text-2xl font-black text-slate-900 dark:text-white mt-2 font-mono tabular-nums">
            {totalDownloads.toLocaleString()}
          </div>
          <div className="text-xs text-emerald-500 mt-1">
            {isArabic ? 'تحديث فوري مع كل تنزيل' : 'Live counter updates'}
          </div>
        </div>

        <div className="p-5 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800">
          <div className="flex items-center justify-between text-slate-500 dark:text-slate-400 text-xs">
            <span>{isArabic ? 'متوسط التقييم' : 'Average Rating'}</span>
            <Star className="w-4 h-4 text-amber-500 fill-amber-400" />
          </div>
          <div className="text-2xl font-black text-slate-900 dark:text-white mt-2 font-mono tabular-nums">
            {avgRating} ★
          </div>
          <div className="text-xs text-slate-400 mt-1">
            {isArabic ? 'بناءً على مراجعات المستخدمين' : 'Based on user feedback'}
          </div>
        </div>
      </div>

      {/* Header and Actions */}
      <div className="flex flex-wrap items-center justify-between gap-4 pt-2">
        <div>
          <h2 className="text-lg font-bold text-slate-900 dark:text-white">
            {isArabic ? 'إدارة التطبيقات والألعاب الخاصة بك' : 'Manage Your Uploads'}
          </h2>
          <p className="text-xs text-slate-500">
            {isArabic
              ? 'يمكنك متابعة التنزيلات، التقييمات، وإضافة ألعاب وتطبيقات جديدة في أي وقت.'
              : 'Track downloads, reviews, or publish new updates anytime.'}
          </p>
        </div>

        <div className="flex items-center gap-2">
          <button
            onClick={onExportCatalog}
            className="flex items-center gap-1.5 px-3 py-2 text-xs font-semibold rounded-lg border border-slate-300 dark:border-slate-700 hover:bg-slate-100 dark:hover:bg-slate-800 text-slate-700 dark:text-slate-300 transition-colors"
          >
            <Download className="w-3.5 h-3.5" />
            <span>{isArabic ? 'تصدير نسخة احتياطية (JSON)' : 'Backup Catalog'}</span>
          </button>

          <button
            onClick={() => {
              playClickSound();
              onOpenUpload();
            }}
            className="flex items-center gap-1.5 px-4 py-2 text-xs font-bold text-white bg-emerald-600 hover:bg-emerald-500 rounded-lg shadow-sm transition-colors cursor-pointer"
          >
            <Upload className="w-3.5 h-3.5" />
            <span>{isArabic ? 'رفع تطبيق جديد' : 'Upload New'}</span>
          </button>
        </div>
      </div>

      {/* App List */}
      {myApps.length > 0 ? (
        <div className="divide-y divide-slate-200 dark:divide-slate-800 rounded-2xl border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900 overflow-hidden">
          {myApps.map((item) => (
            <div
              key={item.id}
              className="p-4 sm:p-5 flex flex-col sm:flex-row sm:items-center justify-between gap-4 hover:bg-slate-50 dark:hover:bg-slate-800/40 transition-colors"
            >
              <div className="flex items-center gap-3 min-w-0">
                <img
                  src={item.iconUrl}
                  alt={item.title}
                  referrerPolicy="no-referrer"
                  className="w-12 h-12 rounded-xl object-cover border border-slate-200 dark:border-slate-700 shrink-0"
                />
                <div className="min-w-0">
                  <div className="flex items-center gap-2">
                    <h3 className="text-sm font-bold text-slate-900 dark:text-white truncate">
                      {item.title}
                    </h3>
                    <span className="text-[11px] font-mono text-emerald-500">v{item.version}</span>
                  </div>
                  <div className="text-xs text-slate-500 flex items-center gap-2 mt-0.5">
                    <span>{item.category}</span>
                    <span aria-hidden="true">·</span>
                    <span className="font-mono tabular-nums">{item.sizeMb} MB</span>
                    <span aria-hidden="true">·</span>
                    <span className="font-mono tabular-nums">{item.downloadCount} {isArabic ? 'تنزيل' : 'downloads'}</span>
                    <span aria-hidden="true">·</span>
                    <span className="text-amber-500 font-mono">{item.rating.toFixed(1)} ★</span>
                  </div>
                </div>
              </div>

              <div className="flex items-center gap-2 self-end sm:self-center">
                <button
                  onClick={() => {
                    playClickSound();
                    const dl = item.downloadUrl || `${window.location.origin}${window.location.pathname}?download=${item.id}`;
                    navigator.clipboard?.writeText(dl);
                    alert(isArabic ? 'تم نسخ رابط التحميل المباشر للتطبيق!' : 'Direct download link copied!');
                  }}
                  className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg border border-emerald-500/30 text-emerald-600 dark:text-emerald-400 hover:bg-emerald-50 dark:hover:bg-emerald-950/30 text-xs font-semibold transition-colors cursor-pointer"
                  title={isArabic ? 'نسخ رابط التحميل المباشر' : 'Copy download link'}
                >
                  <Download className="w-3.5 h-3.5" />
                  <span>{isArabic ? 'نسخ رابط التحميل' : 'Copy Link'}</span>
                </button>

                <button
                  onClick={() => onOpenDetails(item)}
                  className="p-2 text-slate-500 hover:text-slate-900 dark:text-slate-400 dark:hover:text-white rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"
                  title="عرض التفاصيل"
                >
                  <ExternalLink className="w-4 h-4" />
                </button>

                <button
                  onClick={() => {
                    playClickSound();
                    if (confirm(isArabic ? 'هل أنت متأكد من حذف هذا التطبيق؟' : 'Are you sure you want to delete this app?')) {
                      onDeleteApp(item.id);
                    }
                  }}
                  className="p-2 text-rose-500 hover:text-rose-600 rounded-lg hover:bg-rose-50 dark:hover:bg-rose-950/30 transition-colors"
                  title="حذف التطبيق"
                >
                  <Trash2 className="w-4 h-4" />
                </button>
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div className="p-12 text-center rounded-2xl border border-dashed border-slate-300 dark:border-slate-800 bg-white/50 dark:bg-slate-900/50 space-y-4">
          <div className="w-16 h-16 mx-auto rounded-2xl bg-emerald-500/10 text-emerald-500 flex items-center justify-center">
            <Sparkles className="w-8 h-8" />
          </div>
          <div>
            <h3 className="text-base font-bold text-slate-900 dark:text-white">
              {isArabic ? 'لم تقم برفع أي تطبيق أو لعبة بعد' : 'No apps uploaded yet'}
            </h3>
            <p className="text-xs text-slate-500 mt-1 max-w-sm mx-auto">
              {isArabic
                ? 'يمكن لأي شخص رفع ملفات الـ APK، أو ألعاب الويب التفاعلية، ومشاركتها فوراً مع آلاف المستخدمين مجاناً.'
                : 'Anyone can upload APK packages or web games to share immediately with the community.'}
            </p>
          </div>
          <button
            onClick={() => {
              playClickSound();
              onOpenUpload();
            }}
            className="inline-flex items-center gap-2 px-5 py-2.5 bg-emerald-600 hover:bg-emerald-500 text-white rounded-xl text-xs font-bold shadow-md shadow-emerald-600/20 transition-all cursor-pointer"
          >
            <Upload className="w-4 h-4" />
            <span>{isArabic ? 'رفع أول تطبيق أو لعبة لك' : 'Upload Your First Project'}</span>
          </button>
        </div>
      )}
    </section>
  );
};
