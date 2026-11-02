import React, { useState } from 'react';
import { useApp } from '../context/AppContext';
import { MessageCircle, X, Send, Phone, CheckCircle2 } from 'lucide-react';

export const FloatingWhatsApp: React.FC = () => {
  const { config } = useApp();
  const [isOpen, setIsOpen] = useState(false);
  const [customMsg, setCustomMsg] = useState(config.whatsappMessage);

  if (!config.whatsappEnabled) return null;

  // Clean phone number for wa.me format (remove spaces, dashes, parentheses)
  const cleanPhone = config.whatsappNumber.replace(/[^0-9]/g, '');
  const finalMsg = customMsg.trim() || config.whatsappMessage;
  const whatsappUrl = `https://wa.me/${cleanPhone}?text=${encodeURIComponent(finalMsg)}`;

  const handleOpenWhatsApp = () => {
    window.open(whatsappUrl, '_blank', 'noopener,noreferrer');
  };

  return (
    <div className="fixed bottom-6 left-6 z-50 flex flex-col items-start select-none">
      
      {/* Expanded Quick Chat Card */}
      {isOpen && (
        <div className="mb-3 w-80 sm:w-96 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl shadow-2xl overflow-hidden transition-all animate-in fade-in slide-in-from-bottom-4 duration-200 text-right">
          
          {/* Header */}
          <div className="bg-gradient-to-r from-emerald-600 to-teal-700 p-4 text-white flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div className="relative">
                <div className="w-10 h-10 rounded-full bg-white/10 flex items-center justify-center border border-white/20">
                  <Phone className="w-5 h-5 text-white" />
                </div>
                <span className="absolute bottom-0 right-0 w-3 h-3 bg-emerald-400 border-2 border-emerald-700 rounded-full"></span>
              </div>
              <div>
                <h4 className="font-bold text-sm">خدمة عملاء عمران جروب</h4>
                <p className="text-xs text-emerald-100 flex items-center gap-1">
                  <CheckCircle2 className="w-3.5 h-3.5 text-emerald-300" />
                  متاحون عبر واتساب الآن
                </p>
              </div>
            </div>
            
            <button 
              onClick={() => setIsOpen(false)}
              className="p-1 rounded-lg hover:bg-white/20 text-white transition-colors cursor-pointer"
            >
              <X className="w-5 h-5" />
            </button>
          </div>

          {/* Body */}
          <div className="p-4 space-y-3 bg-slate-50 dark:bg-slate-900/60 text-slate-800 dark:text-slate-200 text-sm">
            <div className="p-3 bg-white dark:bg-slate-800 rounded-xl border border-slate-200/80 dark:border-slate-700/80 text-xs leading-relaxed">
              <span className="font-semibold text-emerald-600 dark:text-emerald-400 block mb-1">
                مرحباً بك في مجموعة عمران!
              </span>
              يسعدنا تلقي استفسارات التجار والعملاء حول التطبيق وخدمات نقاط البيع على مدار الساعة.
            </div>

            {/* Display Current WhatsApp Number from Control Panel */}
            <div className="flex items-center justify-between px-3 py-2 bg-emerald-50 dark:bg-emerald-950/30 border border-emerald-200 dark:border-emerald-800/50 rounded-xl text-xs">
              <span className="text-slate-500 dark:text-slate-400">رقم الواتساب المعتمد:</span>
              <span className="font-mono font-bold text-emerald-700 dark:text-emerald-400 dir-ltr text-sm">
                {config.whatsappNumber}
              </span>
            </div>

            {/* Editable or custom prompt before launching */}
            <div>
              <label className="text-xs text-slate-500 dark:text-slate-400 block mb-1 font-medium">
                نص الرسالة الافتتاحية:
              </label>
              <textarea 
                value={customMsg}
                onChange={(e) => setCustomMsg(e.target.value)}
                rows={2}
                className="w-full text-xs p-2.5 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 focus:outline-none focus:ring-2 focus:ring-emerald-500 text-slate-800 dark:text-slate-100 resize-none"
                placeholder="اكتب رسالتك هنا..."
              />
            </div>

            {/* Action CTA */}
            <button
              onClick={handleOpenWhatsApp}
              className="w-full py-2.5 px-4 bg-emerald-600 hover:bg-emerald-500 active:bg-emerald-700 text-white font-bold rounded-xl flex items-center justify-center gap-2 shadow-lg shadow-emerald-600/20 hover:shadow-emerald-600/30 transition-all cursor-pointer text-sm"
            >
              <Send className="w-4 h-4 rotate-180" />
              <span>بدء المحادثة على واتساب</span>
            </button>
            
            <p className="text-[11px] text-center text-slate-400 dark:text-slate-500">
              يمكنك تعديل هذا الرقم مباشرة من لوحة التحكم في أي وقت
            </p>
          </div>

        </div>
      )}

      {/* The Floating Round Button */}
      <button
        onClick={() => setIsOpen(!isOpen)}
        title="تواصل مباشر عبر واتساب عمران جروب"
        className="group relative flex items-center gap-2.5 px-4 py-3 bg-gradient-to-r from-emerald-600 to-green-600 hover:from-emerald-500 hover:to-green-500 text-white rounded-full shadow-2xl shadow-emerald-900/40 hover:shadow-emerald-600/50 transition-all duration-300 hover:scale-105 active:scale-95 cursor-pointer border-2 border-white/20"
      >
        {/* Animated Ping Radar */}
        <span className="absolute -top-1 -right-1 flex h-3.5 w-3.5">
          <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-300 opacity-75"></span>
          <span className="relative inline-flex rounded-full h-3.5 w-3.5 bg-emerald-400"></span>
        </span>

        {/* WhatsApp Icon */}
        <div className="w-6 h-6 flex items-center justify-center">
          <svg 
            viewBox="0 0 24 24" 
            className="w-6 h-6 fill-current"
            xmlns="http://www.w3.org/2000/svg"
          >
            <path d="M12.04 2C6.58 2 2.13 6.45 2.13 11.91C2.13 13.66 2.59 15.36 3.45 16.86L2.05 22L7.3 20.62C8.75 21.41 10.38 21.83 12.04 21.83C17.5 21.83 21.95 17.38 21.95 11.92C21.95 9.27 20.92 6.78 19.05 4.91C17.18 3.03 14.69 2 12.04 2M12.05 3.67C14.25 3.67 16.31 4.53 17.87 6.09C19.42 7.65 20.28 9.72 20.28 11.92C20.28 16.46 16.58 20.15 12.04 20.15C10.56 20.15 9.11 19.76 7.85 19L7.55 18.83L4.43 19.65L5.26 16.61L5.06 16.29C4.24 15 3.8 13.47 3.8 11.91C3.81 7.37 7.5 3.67 12.05 3.67M9.53 7.35C9.32 7.35 8.97 7.43 8.69 7.74C8.4 8.05 7.6 8.8 7.6 10.33C7.6 11.86 8.71 13.34 8.87 13.55C9.03 13.76 11.05 16.88 14.15 18.22C14.89 18.54 15.46 18.73 15.91 18.87C16.65 19.11 17.32 19.07 17.85 18.99C18.45 18.9 19.68 18.24 19.94 17.52C20.2 16.79 20.2 16.18 20.12 16.05C20.04 15.92 19.83 15.85 19.51 15.69C19.2 15.53 17.65 14.77 17.36 14.67C17.08 14.56 16.87 14.51 16.66 14.82C16.45 15.14 15.84 15.85 15.66 16.05C15.48 16.26 15.29 16.29 14.98 16.13C14.67 15.97 13.68 15.65 12.5 14.6C11.59 13.79 10.97 12.78 10.82 12.52C10.66 12.26 10.8 12.12 10.96 11.96C11.1 11.83 11.27 11.61 11.43 11.42C11.58 11.24 11.64 11.11 11.74 10.9C11.84 10.69 11.79 10.51 11.71 10.35C11.63 10.2 11.02 8.69 10.76 8.08C10.51 7.48 10.26 7.56 10.07 7.55C9.89 7.55 9.68 7.35 9.53 7.35Z" />
          </svg>
        </div>

        <span className="font-bold text-sm tracking-wide hidden sm:inline whitespace-nowrap">
          واتساب عمران
        </span>
      </button>

    </div>
  );
};
