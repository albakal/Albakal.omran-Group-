import React from 'react';
import { useApp } from '../context/AppContext';
import { Download, MessageSquare, Phone, Mail, MapPin, ArrowUp } from 'lucide-react';

interface FooterProps {
  onNavigate: (sectionId: string) => void;
}

export const Footer: React.FC<FooterProps> = ({ onNavigate }) => {
  const { config, triggerApkDownload, setIsAdminOpen } = useApp();

  const scrollToTop = () => {
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const cleanPhone = config.whatsappNumber.replace(/[^0-9]/g, '');

  return (
    <footer className="bg-slate-900 dark:bg-slate-950 text-slate-300 border-t border-slate-800 pt-16 pb-12 transition-colors">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        
        {/* Main Footer Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-12 gap-10 pb-12 border-b border-slate-800 text-right">
          
          {/* Brand Info (5 cols) */}
          <div className="lg:col-span-5 space-y-4">
            <div className="flex items-center gap-3">
              <img 
                src={config.logoUrl} 
                alt={config.groupName}
                className="h-12 w-auto max-w-[150px] object-contain rounded-lg"
                referrerPolicy="no-referrer"
              />
              <div>
                <h3 className="text-lg font-bold text-white tracking-tight">
                  {config.groupName}
                </h3>
                <p className="text-xs text-emerald-400 font-medium">
                  {config.slogan}
                </p>
              </div>
            </div>

            <p className="text-xs sm:text-sm text-slate-400 leading-relaxed max-w-sm">
              المنظومة الاقتصادية المتكاملة لحلول التجارة، نقاط البيع السحابية، والمدفوعات الرقمية للتجار والعملاء في العالم العربي.
            </p>

            <div className="pt-2 flex items-center gap-3">
              <button
                onClick={triggerApkDownload}
                className="px-4 py-2 bg-blue-600 hover:bg-blue-500 text-white font-bold text-xs rounded-xl flex items-center gap-2 shadow-sm transition-all cursor-pointer"
              >
                <Download className="w-4 h-4" />
                <span>تحميل التطبيق APK</span>
              </button>

              <a
                href={`https://wa.me/${cleanPhone}?text=${encodeURIComponent(config.whatsappMessage)}`}
                target="_blank"
                rel="noreferrer"
                className="px-4 py-2 bg-emerald-600 hover:bg-emerald-500 text-white font-bold text-xs rounded-xl flex items-center gap-2 shadow-sm transition-all cursor-pointer"
              >
                <MessageSquare className="w-4 h-4" />
                <span>واتساب الدعم المباشر</span>
              </a>
            </div>
          </div>

          {/* Quick Links (3 cols) */}
          <div className="lg:col-span-3 space-y-3">
            <h4 className="text-sm font-bold text-white uppercase tracking-wider">
              روابط المنصة
            </h4>
            <ul className="space-y-2 text-xs sm:text-sm">
              <li>
                <button 
                  onClick={() => onNavigate('hero')}
                  className="hover:text-emerald-400 transition-colors cursor-pointer"
                >
                  الرئيسية وتحميل التطبيق
                </button>
              </li>
              <li>
                <button 
                  onClick={() => onNavigate('features')}
                  className="hover:text-emerald-400 transition-colors cursor-pointer"
                >
                  مميزات التجار ونقاط البيع
                </button>
              </li>
              <li>
                <button 
                  onClick={() => onNavigate('about')}
                  className="hover:text-emerald-400 transition-colors cursor-pointer"
                >
                  عن عمران جروب والمؤسس
                </button>
              </li>
              <li>
                <button 
                  onClick={() => onNavigate('media')}
                  className="hover:text-emerald-400 transition-colors cursor-pointer"
                >
                  شروحات الفيديو التدريبية
                </button>
              </li>
              <li>
                <button 
                  onClick={() => onNavigate('support')}
                  className="hover:text-emerald-400 transition-colors cursor-pointer"
                >
                  خدمة العملاء والأسئلة الشائعة
                </button>
              </li>
            </ul>
          </div>

          {/* Contact Details (4 cols) */}
          <div className="lg:col-span-4 space-y-3">
            <h4 className="text-sm font-bold text-white uppercase tracking-wider">
              بيانات التواصل المباشر
            </h4>
            <div className="space-y-2 text-xs text-slate-400">
              <div className="flex items-center gap-2">
                <Phone className="w-4 h-4 text-emerald-400 shrink-0" />
                <span className="font-mono dir-ltr">{config.phoneSupport}</span>
              </div>
              <div className="flex items-center gap-2">
                <MessageSquare className="w-4 h-4 text-emerald-400 shrink-0" />
                <span>واتساب: <span className="font-mono dir-ltr">{config.whatsappNumber}</span></span>
              </div>
              <div className="flex items-center gap-2">
                <Mail className="w-4 h-4 text-emerald-400 shrink-0" />
                <span>{config.emailSupport}</span>
              </div>
              <div className="flex items-start gap-2 pt-1">
                <MapPin className="w-4 h-4 text-emerald-400 shrink-0 mt-0.5" />
                <span className="leading-relaxed">{config.headquartersAddress}</span>
              </div>
            </div>
          </div>

        </div>

        {/* Bottom Bar */}
        <div className="pt-8 flex flex-col sm:flex-row items-center justify-between gap-4 text-xs text-slate-500">
          <div>
            جميع الحقوق محفوظة © {new Date().getFullYear()} {config.groupName}. حلول ذكية .. لحياة أسهل.
          </div>

          <div className="flex items-center gap-4">
            <button
              onClick={() => setIsAdminOpen(true)}
              className="text-slate-400 hover:text-white underline cursor-pointer"
            >
              دخول لوحة التحكم (Admin)
            </button>
            <span>·</span>
            <button
              onClick={scrollToTop}
              className="flex items-center gap-1 text-slate-400 hover:text-white transition-colors cursor-pointer"
            >
              <span>العودة للأعلى</span>
              <ArrowUp className="w-3.5 h-3.5" />
            </button>
          </div>
        </div>

      </div>
    </footer>
  );
};
