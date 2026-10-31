import React from 'react';
import { useApp } from '../context/AppContext';
import { Building2, Compass, Target, Quote, Award, Sparkles, ShieldCheck } from 'lucide-react';

export const AboutSection: React.FC = () => {
  const { config } = useApp();

  return (
    <section id="about" className="py-20 bg-white dark:bg-slate-950 transition-colors">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        
        {/* Section Header */}
        <div className="text-right max-w-3xl mb-16 space-y-3">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-blue-50 dark:bg-blue-950/40 border border-blue-200 dark:border-blue-800 text-xs font-bold text-blue-700 dark:text-blue-300">
            <Building2 className="w-3.5 h-3.5" />
            <span>من نحن · مسيرة الابتكار والتمكين</span>
          </div>
          
          <h2 className="text-2xl sm:text-4xl font-extrabold text-slate-900 dark:text-white tracking-tight">
            {config.aboutTitle}
          </h2>
          
          <p className="text-sm sm:text-base text-slate-600 dark:text-slate-300 leading-relaxed">
            {config.aboutStory}
          </p>
        </div>

        {/* 2-Column Row: Company Vision & Activities + Headquarters Image */}
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 items-stretch mb-20">
          
          {/* Left / Secondary: Headquarters Image (5 cols) */}
          <div className="lg:col-span-5 relative rounded-3xl overflow-hidden border border-slate-200 dark:border-slate-800 shadow-xl group">
            <img 
              src={config.headquartersImage} 
              alt="مقر مجموعة عمران" 
              className="w-full h-full min-h-[320px] object-cover transition-transform duration-500 group-hover:scale-105"
              referrerPolicy="no-referrer"
            />
            <div className="absolute inset-0 bg-gradient-to-t from-slate-950/90 via-slate-950/30 to-transparent flex flex-col justify-end p-6 text-right">
              <span className="text-xs font-semibold text-emerald-400">المركز التكنولوجي الرئيسي</span>
              <h4 className="text-base font-bold text-white">مقر مجموعة عمران الاستثماري</h4>
              <p className="text-xs text-slate-300 mt-1">{config.headquartersAddress}</p>
            </div>
          </div>

          {/* Right: Vision, Mission & Core Activities (7 cols) */}
          <div className="lg:col-span-7 flex flex-col justify-between space-y-6">
            
            {/* Vision Card */}
            <div className="p-6 sm:p-7 rounded-3xl bg-slate-50 dark:bg-slate-900 border border-slate-200 dark:border-slate-800 text-right space-y-2">
              <div className="flex items-center gap-2.5 text-blue-600 dark:text-blue-400 font-bold text-sm">
                <Compass className="w-5 h-5" />
                <span>رؤيتنا الاستراتيجية</span>
              </div>
              <p className="text-xs sm:text-sm text-slate-700 dark:text-slate-300 leading-relaxed">
                {config.visionText}
              </p>
            </div>

            {/* Mission Card */}
            <div className="p-6 sm:p-7 rounded-3xl bg-slate-50 dark:bg-slate-900 border border-slate-200 dark:border-slate-800 text-right space-y-2">
              <div className="flex items-center gap-2.5 text-emerald-600 dark:text-emerald-400 font-bold text-sm">
                <Target className="w-5 h-5" />
                <span>رسالتنا تجاه السوق والشركاء</span>
              </div>
              <p className="text-xs sm:text-sm text-slate-700 dark:text-slate-300 leading-relaxed">
                {config.missionText}
              </p>
            </div>

            {/* Activities grid */}
            <div className="grid grid-cols-1 sm:grid-cols-3 gap-3 text-right">
              <div className="p-4 rounded-2xl bg-white dark:bg-slate-900/60 border border-slate-200 dark:border-slate-800">
                <div className="text-xs font-bold text-slate-900 dark:text-white mb-1">حلول التجارة السحابية</div>
                <div className="text-[11px] text-slate-500 dark:text-slate-400">تطوير منصات نقاط بيع وإدارة مخازن</div>
              </div>
              <div className="p-4 rounded-2xl bg-white dark:bg-slate-900/60 border border-slate-200 dark:border-slate-800">
                <div className="text-xs font-bold text-slate-900 dark:text-white mb-1">المدفوعات الرقمية</div>
                <div className="text-[11px] text-slate-500 dark:text-slate-400">بوابات دفع وسداد فوري آمن</div>
              </div>
              <div className="p-4 rounded-2xl bg-white dark:bg-slate-900/60 border border-slate-200 dark:border-slate-800">
                <div className="text-xs font-bold text-slate-900 dark:text-white mb-1">التمكين الاقتصادي</div>
                <div className="text-[11px] text-slate-500 dark:text-slate-400">باقات مخفضة لدعم صغار التجار</div>
              </div>
            </div>

          </div>

        </div>

        {/* Founder Feature Spotlight Card */}
        <div className="rounded-3xl bg-gradient-to-br from-slate-900 via-blue-950 to-slate-900 text-white border border-slate-800 shadow-2xl overflow-hidden p-6 sm:p-10">
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 items-center">
            
            {/* Founder Portrait (4 cols) */}
            <div className="lg:col-span-4 flex justify-center">
              <div className="relative w-full max-w-xs aspect-[3/4] rounded-2xl overflow-hidden border-2 border-emerald-500/40 shadow-2xl group">
                <img 
                  src={config.founderImage} 
                  alt={config.founderName} 
                  className="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105"
                  referrerPolicy="no-referrer"
                />
                <div className="absolute bottom-0 inset-x-0 bg-gradient-to-t from-slate-950 via-slate-950/60 to-transparent p-4 text-center">
                  <h4 className="text-base font-bold text-white">{config.founderName}</h4>
                  <p className="text-xs text-emerald-400 font-medium">{config.founderTitle}</p>
                </div>
              </div>
            </div>

            {/* Founder Bio & Philosophy (8 cols) */}
            <div className="lg:col-span-8 space-y-6 text-right">
              
              <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-emerald-500/10 border border-emerald-500/30 text-xs font-bold text-emerald-400">
                <Award className="w-3.5 h-3.5" />
                <span>كلمة وقيادة المؤسس</span>
              </div>

              <div>
                <h3 className="text-2xl sm:text-3xl font-extrabold text-white mb-1">
                  {config.founderName}
                </h3>
                <p className="text-sm text-blue-300 font-medium">
                  {config.founderTitle}
                </p>
              </div>

              <p className="text-sm sm:text-base text-slate-200 leading-relaxed">
                {config.founderBio}
              </p>

              {/* Founder Quote */}
              <div className="p-5 rounded-2xl bg-white/5 border border-white/10 relative text-right">
                <Quote className="w-6 h-6 text-emerald-400 opacity-60 mb-2 rotate-180" />
                <p className="text-sm font-semibold text-emerald-100 italic leading-relaxed">
                  {config.founderQuote}
                </p>
              </div>

              <div className="pt-2 flex flex-wrap items-center gap-4 text-xs text-slate-400">
                <div className="flex items-center gap-1.5">
                  <ShieldCheck className="w-4 h-4 text-emerald-400" />
                  <span>ريادة واستثمار مستدام</span>
                </div>
                <span>·</span>
                <div className="flex items-center gap-1.5">
                  <Sparkles className="w-4 h-4 text-amber-400" />
                  <span>حلول ذكية .. لحياة أسهل</span>
                </div>
              </div>

            </div>

          </div>
        </div>

      </div>
    </section>
  );
};
