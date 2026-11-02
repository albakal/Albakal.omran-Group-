import React, { useState } from 'react';
import { useApp } from '../context/AppContext';
import { Video, Play, Clock, CheckCircle2, Tv, Sparkles } from 'lucide-react';

interface MediaSectionProps {
  onOpenVideo: (url: string) => void;
}

export const MediaSection: React.FC<MediaSectionProps> = ({ onOpenVideo }) => {
  const { config } = useApp();
  const [filter, setFilter] = useState<'all' | 'merchants' | 'customers' | 'overview'>('all');

  const filteredTutorials = config.tutorials.filter(item => {
    if (filter === 'all') return true;
    return item.category === filter;
  });

  return (
    <section id="media" className="py-20 bg-slate-50/70 dark:bg-slate-900/60 border-t border-slate-200 dark:border-slate-800">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        
        {/* Section Header */}
        <div className="text-center max-w-3xl mx-auto mb-14 space-y-3">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-blue-50 dark:bg-blue-950/40 border border-blue-200 dark:border-blue-800 text-xs font-bold text-blue-700 dark:text-blue-300">
            <Tv className="w-3.5 h-3.5" />
            <span>الوسائط والشروحات المرئية</span>
          </div>
          
          <h2 className="text-2xl sm:text-4xl font-extrabold text-slate-900 dark:text-white tracking-tight">
            فيديوهات توضيحية تشرح طريقة عمل التطبيق
          </h2>
          
          <p className="text-sm sm:text-base text-slate-600 dark:text-slate-400">
            أدلة مرئية تفصيلية خطوة بخطوة لمساعدتك في الاستفادة القصوى من كافة مزايا وخدمات عمران جروب.
          </p>
        </div>

        {/* Featured Main Video Showcase */}
        <div className="mb-14 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xl overflow-hidden">
          <div className="grid grid-cols-1 lg:grid-cols-12 items-center">
            
            {/* Video Player / Interactive Frame (7 cols) */}
            <div className="lg:col-span-7 relative aspect-video bg-slate-950 flex items-center justify-center group overflow-hidden">
              {config.mainVideoUrl.includes('youtube') ? (
                <iframe
                  src={config.mainVideoUrl}
                  title={config.mainVideoTitle}
                  className="w-full h-full border-0"
                  allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                  allowFullScreen
                />
              ) : (
                <div 
                  onClick={() => onOpenVideo(config.mainVideoUrl)}
                  className="w-full h-full flex flex-col items-center justify-center bg-gradient-to-br from-blue-950 to-slate-950 cursor-pointer p-6 text-center"
                >
                  <div className="w-16 h-16 rounded-full bg-blue-600/90 hover:bg-blue-500 text-white flex items-center justify-center shadow-2xl transition-transform group-hover:scale-110 mb-3">
                    <Play className="w-7 h-7 fill-current ml-1" />
                  </div>
                  <span className="text-sm font-bold text-white">انقر لتشغيل الفيديو التوضيحي الرئيسي</span>
                  <span className="text-xs text-slate-400 mt-1">جودة عالية HD · صوت وصورة باللغة العربية</span>
                </div>
              )}
            </div>

            {/* Video Description & Highlights (5 cols) */}
            <div className="lg:col-span-5 p-6 sm:p-8 text-right space-y-4">
              <span className="text-xs font-bold text-blue-600 dark:text-emerald-400">
                الفيديو الإرشادي الشامل
              </span>
              
              <h3 className="text-xl sm:text-2xl font-bold text-slate-900 dark:text-white leading-snug">
                {config.mainVideoTitle}
              </h3>
              
              <p className="text-xs sm:text-sm text-slate-600 dark:text-slate-300 leading-relaxed">
                {config.mainVideoDescription}
              </p>

              <div className="pt-2 space-y-2 text-xs text-slate-700 dark:text-slate-300 font-medium">
                <div className="flex items-center gap-2">
                  <CheckCircle2 className="w-4 h-4 text-emerald-500" />
                  <span>طريقة التسجيل وتفعيل المتجر في دقيقتين</span>
                </div>
                <div className="flex items-center gap-2">
                  <CheckCircle2 className="w-4 h-4 text-emerald-500" />
                  <span>إصدار أول فاتورة ومشاركتها مع العميل</span>
                </div>
                <div className="flex items-center gap-2">
                  <CheckCircle2 className="w-4 h-4 text-emerald-500" />
                  <span>تحصيل المدفوعات النقدية والإلكترونية</span>
                </div>
              </div>

              <div className="pt-4">
                <button
                  onClick={() => onOpenVideo(config.mainVideoUrl)}
                  className="px-5 py-2.5 bg-blue-600 hover:bg-blue-500 text-white font-bold text-xs sm:text-sm rounded-xl transition-all flex items-center gap-2 cursor-pointer shadow-md"
                >
                  <Play className="w-4 h-4 fill-current" />
                  <span>فتح في شاشة كاملة</span>
                </button>
              </div>

            </div>

          </div>
        </div>

        {/* Video Tutorial Library Filter Tabs */}
        <div className="flex items-center justify-between flex-wrap gap-4 mb-6">
          <div className="text-right">
            <h3 className="text-lg font-bold text-slate-900 dark:text-white">
              سلسلة الشروحات السريعة
            </h3>
            <p className="text-xs text-slate-500 dark:text-slate-400">
              دروس تدريبية قصيرة ومحددة
            </p>
          </div>

          <div className="flex items-center gap-1.5 p-1 bg-white dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-xs font-semibold">
            <button
              onClick={() => setFilter('all')}
              className={`px-3 py-1.5 rounded-lg transition-colors cursor-pointer ${
                filter === 'all' 
                  ? 'bg-blue-600 text-white shadow-sm' 
                  : 'text-slate-600 dark:text-slate-300 hover:text-slate-900 dark:hover:text-white'
              }`}
            >
              الكل ({config.tutorials.length})
            </button>
            <button
              onClick={() => setFilter('merchants')}
              className={`px-3 py-1.5 rounded-lg transition-colors cursor-pointer ${
                filter === 'merchants' 
                  ? 'bg-blue-600 text-white shadow-sm' 
                  : 'text-slate-600 dark:text-slate-300 hover:text-slate-900 dark:hover:text-white'
              }`}
            >
              شروحات التجار
            </button>
            <button
              onClick={() => setFilter('customers')}
              className={`px-3 py-1.5 rounded-lg transition-colors cursor-pointer ${
                filter === 'customers' 
                  ? 'bg-blue-600 text-white shadow-sm' 
                  : 'text-slate-600 dark:text-slate-300 hover:text-slate-900 dark:hover:text-white'
              }`}
            >
              شروحات العملاء
            </button>
          </div>
        </div>

        {/* Tutorial Cards Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          {filteredTutorials.map((tut) => (
            <div
              key={tut.id}
              onClick={() => onOpenVideo(tut.videoUrl)}
              className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-4 shadow-sm hover:shadow-xl transition-all duration-300 text-right flex flex-col justify-between group cursor-pointer hover:border-blue-500/50"
            >
              <div>
                {/* Video Card Thumbnail Frame */}
                <div className="relative aspect-video rounded-xl bg-slate-950/90 mb-3 flex items-center justify-center overflow-hidden border border-slate-800">
                  <div className="w-10 h-10 rounded-full bg-blue-600/80 group-hover:bg-blue-500 text-white flex items-center justify-center transition-transform group-hover:scale-110">
                    <Play className="w-4 h-4 fill-current ml-0.5" />
                  </div>
                  <div className="absolute bottom-2 left-2 px-2 py-0.5 rounded bg-black/80 text-[10px] font-mono font-bold text-white flex items-center gap-1">
                    <Clock className="w-3 h-3 text-amber-400" />
                    <span>{tut.duration}</span>
                  </div>
                </div>

                <h4 className="text-sm font-bold text-slate-900 dark:text-white mb-1.5 leading-snug group-hover:text-blue-600 dark:group-hover:text-blue-400 transition-colors">
                  {tut.title}
                </h4>

                <p className="text-xs text-slate-600 dark:text-slate-400 line-clamp-2 leading-relaxed">
                  {tut.description}
                </p>
              </div>

              <div className="pt-3 mt-3 border-t border-slate-100 dark:border-slate-800/80 flex items-center justify-between text-xs text-blue-600 dark:text-blue-400 font-semibold">
                <span>مشاهدة الفيديو</span>
                <Play className="w-3.5 h-3.5 fill-current" />
              </div>
            </div>
          ))}
        </div>

      </div>
    </section>
  );
};
