import React, { useState } from 'react';
import { useApp } from '../context/AppContext';
import { 
  Store, 
  Smartphone, 
  Boxes, 
  BarChart3, 
  Coins, 
  Percent, 
  CreditCard, 
  Receipt, 
  Headphones, 
  CheckCircle2, 
  ShoppingBag,
  ArrowRight
} from 'lucide-react';

export const FeaturesSection: React.FC = () => {
  const { config, triggerApkDownload } = useApp();
  const [activeTab, setActiveTab] = useState<'traders' | 'customers'>('traders');

  const getFeatureIcon = (name: string) => {
    switch (name) {
      case 'Smartphone': return <Smartphone className="w-6 h-6 text-blue-600 dark:text-blue-400" />;
      case 'Boxes': return <Boxes className="w-6 h-6 text-emerald-600 dark:text-emerald-400" />;
      case 'BarChart3': return <BarChart3 className="w-6 h-6 text-purple-600 dark:text-purple-400" />;
      case 'Coins': return <Coins className="w-6 h-6 text-amber-600 dark:text-amber-400" />;
      case 'Percent': return <Percent className="w-6 h-6 text-rose-600 dark:text-rose-400" />;
      case 'CreditCard': return <CreditCard className="w-6 h-6 text-blue-600 dark:text-blue-400" />;
      case 'Receipt': return <Receipt className="w-6 h-6 text-emerald-600 dark:text-emerald-400" />;
      case 'Headphones': return <Headphones className="w-6 h-6 text-teal-600 dark:text-teal-400" />;
      default: return <Store className="w-6 h-6 text-blue-600 dark:text-blue-400" />;
    }
  };

  return (
    <section id="features" className="py-20 bg-slate-50/60 dark:bg-slate-900/40 border-y border-slate-200 dark:border-slate-800">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        
        {/* Section Header */}
        <div className="text-center max-w-3xl mx-auto mb-12 space-y-3">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-emerald-50 dark:bg-emerald-950/40 border border-emerald-200 dark:border-emerald-800 text-xs font-bold text-emerald-700 dark:text-emerald-400">
            <span>حل اقتصادي متكامل وسهل الاستخدام</span>
          </div>
          
          <h2 className="text-2xl sm:text-4xl font-extrabold text-slate-900 dark:text-white tracking-tight">
            منظومة ذكية تخدم طرفي المعادلة التجارية
          </h2>
          
          <p className="text-sm sm:text-base text-slate-600 dark:text-slate-400 leading-relaxed">
            صُمم تطبيق عمران ليكون الشريك التقني الأوفر للتاجر لإدارة مبيعاته ومخزونه، والمنصة الأسهل للعميل للتسوق والحصول على أفضل العروض.
          </p>

          {/* Interactive Filter Segmented Control (Tabs) */}
          <div className="pt-4 flex justify-center">
            <div className="inline-flex p-1.5 rounded-2xl bg-white dark:bg-slate-800 border border-slate-200 dark:border-slate-700 shadow-sm">
              <button
                onClick={() => setActiveTab('traders')}
                className={`flex items-center gap-2 px-6 py-2.5 rounded-xl text-xs sm:text-sm font-bold transition-all cursor-pointer ${
                  activeTab === 'traders'
                    ? 'bg-blue-600 text-white shadow-md'
                    : 'text-slate-600 dark:text-slate-300 hover:text-slate-900 dark:hover:text-white'
                }`}
              >
                <Store className="w-4 h-4" />
                <span>فوائد التطبيق للتجار والمحلات</span>
              </button>
              
              <button
                onClick={() => setActiveTab('customers')}
                className={`flex items-center gap-2 px-6 py-2.5 rounded-xl text-xs sm:text-sm font-bold transition-all cursor-pointer ${
                  activeTab === 'customers'
                    ? 'bg-emerald-600 text-white shadow-md'
                    : 'text-slate-600 dark:text-slate-300 hover:text-slate-900 dark:hover:text-white'
                }`}
              >
                <ShoppingBag className="w-4 h-4" />
                <span>مزايا التطبيق للعملاء والمتسوقين</span>
              </button>
            </div>
          </div>

        </div>

        {/* Tab 1: Traders Features Grid */}
        {activeTab === 'traders' && (
          <div className="space-y-8 animate-in fade-in duration-300">
            
            <div className="text-right border-r-4 border-blue-600 pr-4">
              <h3 className="text-xl font-bold text-slate-900 dark:text-white">
                {config.traderFeaturesTitle}
              </h3>
              <p className="text-sm text-slate-600 dark:text-slate-400 mt-1">
                {config.traderFeaturesDesc}
              </p>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
              {config.traderFeatures.map((item) => (
                <div 
                  key={item.id}
                  className="p-6 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 hover:border-blue-500/50 dark:hover:border-blue-500/50 shadow-sm hover:shadow-xl transition-all duration-300 text-right flex flex-col justify-between group"
                >
                  <div>
                    <div className="flex items-center justify-between mb-4">
                      <div className="p-3 rounded-2xl bg-slate-50 dark:bg-slate-800 border border-slate-100 dark:border-slate-700/60 group-hover:scale-110 transition-transform">
                        {getFeatureIcon(item.iconName)}
                      </div>
                      {item.highlight && (
                        <span className="text-[11px] font-bold text-blue-700 dark:text-blue-300 bg-blue-50 dark:bg-blue-950/60 px-2.5 py-1 rounded-lg">
                          {item.highlight}
                        </span>
                      )}
                    </div>

                    <h4 className="text-base font-bold text-slate-900 dark:text-white mb-2 leading-snug">
                      {item.title}
                    </h4>

                    <p className="text-xs text-slate-600 dark:text-slate-400 leading-relaxed">
                      {item.description}
                    </p>
                  </div>

                  <div className="pt-4 mt-4 border-t border-slate-100 dark:border-slate-800/80 flex items-center justify-between text-xs font-semibold text-blue-600 dark:text-blue-400">
                    <span>ميزة اقتصادية مدمجة</span>
                    <CheckCircle2 className="w-4 h-4 text-emerald-500" />
                  </div>
                </div>
              ))}
            </div>

          </div>
        )}

        {/* Tab 2: Customers Features Grid */}
        {activeTab === 'customers' && (
          <div className="space-y-8 animate-in fade-in duration-300">
            
            <div className="text-right border-r-4 border-emerald-600 pr-4">
              <h3 className="text-xl font-bold text-slate-900 dark:text-white">
                {config.customerFeaturesTitle}
              </h3>
              <p className="text-sm text-slate-600 dark:text-slate-400 mt-1">
                {config.customerFeaturesDesc}
              </p>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
              {config.customerFeatures.map((item) => (
                <div 
                  key={item.id}
                  className="p-6 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 hover:border-emerald-500/50 dark:hover:border-emerald-500/50 shadow-sm hover:shadow-xl transition-all duration-300 text-right flex flex-col justify-between group"
                >
                  <div>
                    <div className="flex items-center justify-between mb-4">
                      <div className="p-3 rounded-2xl bg-slate-50 dark:bg-slate-800 border border-slate-100 dark:border-slate-700/60 group-hover:scale-110 transition-transform">
                        {getFeatureIcon(item.iconName)}
                      </div>
                      {item.highlight && (
                        <span className="text-[11px] font-bold text-emerald-700 dark:text-emerald-300 bg-emerald-50 dark:bg-emerald-950/60 px-2.5 py-1 rounded-lg">
                          {item.highlight}
                        </span>
                      )}
                    </div>

                    <h4 className="text-base font-bold text-slate-900 dark:text-white mb-2 leading-snug">
                      {item.title}
                    </h4>

                    <p className="text-xs text-slate-600 dark:text-slate-400 leading-relaxed">
                      {item.description}
                    </p>
                  </div>

                  <div className="pt-4 mt-4 border-t border-slate-100 dark:border-slate-800/80 flex items-center justify-between text-xs font-semibold text-emerald-600 dark:text-emerald-400">
                    <span>تجربة سهلة وموفرة</span>
                    <CheckCircle2 className="w-4 h-4 text-emerald-500" />
                  </div>
                </div>
              ))}
            </div>

          </div>
        )}

        {/* Quick CTA strip */}
        <div className="mt-12 p-6 sm:p-8 rounded-3xl bg-gradient-to-r from-blue-900 to-slate-900 dark:from-slate-900 dark:to-blue-950 border border-blue-800/40 text-white flex flex-col sm:flex-row items-center justify-between gap-6 text-right">
          <div>
            <h4 className="text-lg font-bold">هل أنت مستعد لتجربة تطبيق عمران وإطلاق متجرك؟</h4>
            <p className="text-xs sm:text-sm text-slate-300 mt-1">حمّل التطبيق الآن برابط مباشر وابدأ في أقل من 3 دقائق بدون أي رسوم تسجيل خفية.</p>
          </div>
          <button
            onClick={triggerApkDownload}
            className="px-6 py-3 bg-emerald-600 hover:bg-emerald-500 text-white font-bold text-sm rounded-xl shadow-lg transition-all cursor-pointer whitespace-nowrap active:scale-95"
          >
            تنزيل التطبيق الآن (APK مباشر)
          </button>
        </div>

      </div>
    </section>
  );
};
