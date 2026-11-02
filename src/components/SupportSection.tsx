import React, { useState } from 'react';
import { useApp } from '../context/AppContext';
import { 
  Headphones, 
  Phone, 
  Mail, 
  MapPin, 
  Clock, 
  ChevronDown, 
  Send, 
  CheckCircle2, 
  MessageSquare,
  HelpCircle
} from 'lucide-react';

export const SupportSection: React.FC = () => {
  const { config } = useApp();
  const [openFaq, setOpenFaq] = useState<string | null>(config.faqs[0]?.id || null);
  const [ticketSent, setTicketSent] = useState(false);
  const [formData, setFormData] = useState({
    name: '',
    phone: '',
    type: 'merchant',
    message: ''
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.name || !formData.phone) return;
    
    // Simulate instant ticket dispatch
    setTicketSent(true);
    setTimeout(() => {
      setFormData({ name: '', phone: '', type: 'merchant', message: '' });
    }, 1000);
  };

  const cleanPhone = config.whatsappNumber.replace(/[^0-9]/g, '');

  return (
    <section id="support" className="py-20 bg-white dark:bg-slate-950 transition-colors">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        
        {/* Section Header */}
        <div className="text-right max-w-3xl mb-14 space-y-3">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-emerald-50 dark:bg-emerald-950/40 border border-emerald-200 dark:border-emerald-800 text-xs font-bold text-emerald-700 dark:text-emerald-400">
            <Headphones className="w-3.5 h-3.5" />
            <span>خدمة العملاء والدعم الفني المباشر</span>
          </div>
          
          <h2 className="text-2xl sm:text-4xl font-extrabold text-slate-900 dark:text-white tracking-tight">
            نحن هنا لمساندتك والإجابة على كافة تساؤلاتك
          </h2>
          
          <p className="text-sm sm:text-base text-slate-600 dark:text-slate-400 leading-relaxed">
            فريق متخصص لمساعدة التجار في إعداد نقاط البيع، وحل أي استفسار تقني للعملاء على مدار الساعة.
          </p>
        </div>

        {/* 2-Column Support Hub Grid: Contact Cards & Form + FAQs Accordion */}
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-10">
          
          {/* Left Column (5 cols): Contact Cards & Direct Inquiry Form */}
          <div className="lg:col-span-5 space-y-6">
            
            {/* Quick Contact Info Cards */}
            <div className="p-6 rounded-3xl bg-slate-50 dark:bg-slate-900 border border-slate-200 dark:border-slate-800 space-y-4 text-right">
              <h3 className="text-base font-bold text-slate-900 dark:text-white mb-2">
                قنوات الاتصال المباشرة
              </h3>

              {/* WhatsApp Card */}
              <a
                href={`https://wa.me/${cleanPhone}?text=${encodeURIComponent(config.whatsappMessage)}`}
                target="_blank"
                rel="noreferrer"
                className="flex items-center justify-between p-3.5 rounded-2xl bg-emerald-50 dark:bg-emerald-950/40 border border-emerald-200 dark:border-emerald-800/60 hover:bg-emerald-100 dark:hover:bg-emerald-900/50 transition-colors group cursor-pointer"
              >
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 rounded-xl bg-emerald-600 text-white flex items-center justify-center">
                    <MessageSquare className="w-5 h-5" />
                  </div>
                  <div>
                    <div className="text-xs font-bold text-slate-900 dark:text-white">واتساب الدعم السريع</div>
                    <div className="text-xs font-mono text-emerald-700 dark:text-emerald-400 dir-ltr font-bold">
                      {config.whatsappNumber}
                    </div>
                  </div>
                </div>
                <span className="text-[11px] font-bold text-emerald-700 dark:text-emerald-300">محادثة الآن</span>
              </a>

              {/* Phone Card */}
              <div className="flex items-center gap-3 p-3.5 rounded-2xl bg-white dark:bg-slate-800/80 border border-slate-200 dark:border-slate-700/60">
                <div className="w-10 h-10 rounded-xl bg-blue-100 dark:bg-blue-900/50 text-blue-600 dark:text-blue-400 flex items-center justify-center shrink-0">
                  <Phone className="w-5 h-5" />
                </div>
                <div>
                  <div className="text-xs font-bold text-slate-900 dark:text-white">الهاتف المباشر</div>
                  <div className="text-xs font-mono text-slate-600 dark:text-slate-300 dir-ltr">{config.phoneSupport}</div>
                </div>
              </div>

              {/* Email Card */}
              <div className="flex items-center gap-3 p-3.5 rounded-2xl bg-white dark:bg-slate-800/80 border border-slate-200 dark:border-slate-700/60">
                <div className="w-10 h-10 rounded-xl bg-purple-100 dark:bg-purple-900/50 text-purple-600 dark:text-purple-400 flex items-center justify-center shrink-0">
                  <Mail className="w-5 h-5" />
                </div>
                <div>
                  <div className="text-xs font-bold text-slate-900 dark:text-white">البريد الإلكتروني الرسمي</div>
                  <div className="text-xs font-mono text-slate-600 dark:text-slate-300">{config.emailSupport}</div>
                </div>
              </div>

              {/* Working Hours */}
              <div className="flex items-start gap-3 p-3.5 rounded-2xl bg-white dark:bg-slate-800/80 border border-slate-200 dark:border-slate-700/60">
                <div className="w-10 h-10 rounded-xl bg-amber-100 dark:bg-amber-900/50 text-amber-600 dark:text-amber-400 flex items-center justify-center shrink-0">
                  <Clock className="w-5 h-5" />
                </div>
                <div>
                  <div className="text-xs font-bold text-slate-900 dark:text-white">ساعات العمل والخدمة</div>
                  <div className="text-xs text-slate-600 dark:text-slate-300 leading-relaxed">{config.workingHours}</div>
                </div>
              </div>

            </div>

            {/* Direct Ticket / Message Form */}
            <div className="p-6 rounded-3xl bg-slate-50 dark:bg-slate-900 border border-slate-200 dark:border-slate-800 text-right">
              <h3 className="text-base font-bold text-slate-900 dark:text-white mb-1">
                إرسال استفسار أو طلب مساعدة
              </h3>
              <p className="text-xs text-slate-500 dark:text-slate-400 mb-4">
                املأ النموذج وسيقوم مسؤول الدعم بالتواصل معك مباشرة.
              </p>

              {ticketSent ? (
                <div className="p-6 rounded-2xl bg-emerald-50 dark:bg-emerald-950/40 border border-emerald-300 dark:border-emerald-700 text-center space-y-2">
                  <CheckCircle2 className="w-8 h-8 text-emerald-600 dark:text-emerald-400 mx-auto" />
                  <h4 className="text-sm font-bold text-emerald-900 dark:text-emerald-100">تم استلام رسالتك بنجاح!</h4>
                  <p className="text-xs text-emerald-700 dark:text-emerald-300">سيتواصل معك فريق خدمة عملاء عمران جروب في أقرب وقت.</p>
                  <button 
                    onClick={() => setTicketSent(false)} 
                    className="mt-2 text-xs font-bold text-emerald-800 dark:text-emerald-400 underline cursor-pointer"
                  >
                    إرسال استفسار آخر
                  </button>
                </div>
              ) : (
                <form onSubmit={handleSubmit} className="space-y-3">
                  <div>
                    <label className="text-xs font-semibold text-slate-700 dark:text-slate-300 block mb-1">
                      الاسم الكريم *
                    </label>
                    <input 
                      type="text"
                      required
                      value={formData.name}
                      onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                      placeholder="مثال: محمد العمري"
                      className="w-full text-xs p-3 rounded-xl border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-blue-600"
                    />
                  </div>

                  <div>
                    <label className="text-xs font-semibold text-slate-700 dark:text-slate-300 block mb-1">
                      رقم الجوال أو الواتساب *
                    </label>
                    <input 
                      type="tel"
                      required
                      value={formData.phone}
                      onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                      placeholder="05xxxxxxx أو 77xxxxxxx"
                      className="w-full text-xs p-3 rounded-xl border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-blue-600 text-left dir-ltr"
                    />
                  </div>

                  <div>
                    <label className="text-xs font-semibold text-slate-700 dark:text-slate-300 block mb-1">
                      نوع الحساب أو الطلب
                    </label>
                    <select
                      value={formData.type}
                      onChange={(e) => setFormData({ ...formData, type: e.target.value })}
                      className="w-full text-xs p-3 rounded-xl border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-blue-600"
                    >
                      <option value="merchant">تاجر / صاحب نقطة بيع أو محل</option>
                      <option value="customer">عميل / متسوق بالتطبيق</option>
                      <option value="technical">استفسار تقني أو صيانة</option>
                      <option value="partnership">شراكة واستثمار تجاري</option>
                    </select>
                  </div>

                  <div>
                    <label className="text-xs font-semibold text-slate-700 dark:text-slate-300 block mb-1">
                      تفاصيل الرسالة
                    </label>
                    <textarea 
                      rows={3}
                      value={formData.message}
                      onChange={(e) => setFormData({ ...formData, message: e.target.value })}
                      placeholder="اكتب استفسارك هنا..."
                      className="w-full text-xs p-3 rounded-xl border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-blue-600 resize-none"
                    />
                  </div>

                  <button
                    type="submit"
                    className="w-full py-3 bg-blue-600 hover:bg-blue-500 text-white font-bold text-xs rounded-xl shadow-md transition-all flex items-center justify-center gap-2 cursor-pointer active:scale-95"
                  >
                    <Send className="w-4 h-4 rotate-180" />
                    <span>إرسال الطلب لفريق الدعم</span>
                  </button>
                </form>
              )}
            </div>

          </div>

          {/* Right Column (7 cols): FAQs Accordion */}
          <div className="lg:col-span-7 space-y-4 text-right">
            <div className="flex items-center gap-2 mb-2">
              <HelpCircle className="w-5 h-5 text-blue-600 dark:text-emerald-400" />
              <h3 className="text-xl font-bold text-slate-900 dark:text-white">
                الأسئلة الأكثر شيوعاً
              </h3>
            </div>
            
            <p className="text-xs sm:text-sm text-slate-500 dark:text-slate-400 mb-6">
              إجابات سريعة وواضحة على أهم استفسارات التجار والمستخدمين الجدد.
            </p>

            <div className="space-y-3">
              {config.faqs.map((faq) => {
                const isSelected = openFaq === faq.id;
                return (
                  <div
                    key={faq.id}
                    className="border border-slate-200 dark:border-slate-800 rounded-2xl bg-slate-50/60 dark:bg-slate-900 overflow-hidden transition-all"
                  >
                    <button
                      onClick={() => setOpenFaq(isSelected ? null : faq.id)}
                      className="w-full p-4 sm:p-5 flex items-center justify-between text-right gap-4 cursor-pointer hover:bg-slate-100/50 dark:hover:bg-slate-800/50 transition-colors"
                    >
                      <span className="text-sm font-bold text-slate-900 dark:text-white leading-snug">
                        {faq.question}
                      </span>
                      <ChevronDown
                        className={`w-5 h-5 text-slate-400 shrink-0 transition-transform duration-200 ${
                          isSelected ? 'rotate-180 text-blue-600 dark:text-emerald-400' : ''
                        }`}
                      />
                    </button>

                    {isSelected && (
                      <div className="px-4 sm:px-5 pb-5 pt-1 text-xs sm:text-sm text-slate-600 dark:text-slate-300 leading-relaxed border-t border-slate-200/50 dark:border-slate-800 animate-in fade-in duration-150">
                        {faq.answer}
                      </div>
                    )}
                  </div>
                );
              })}
            </div>

            {/* Quick Helper Banner */}
            <div className="p-6 rounded-3xl bg-gradient-to-r from-blue-50 to-indigo-50 dark:from-slate-900 dark:to-blue-950/40 border border-blue-200 dark:border-blue-800/50 mt-8 flex flex-col sm:flex-row items-center justify-between gap-4">
              <div>
                <h4 className="text-sm font-bold text-slate-900 dark:text-white">لم تجد إجابة على سؤالك؟</h4>
                <p className="text-xs text-slate-600 dark:text-slate-400 mt-0.5">مساعد الذكاء الاصطناعي الخاص بنا متاح للرد الفوري عليك في أي وقت.</p>
              </div>
              <span className="text-xs font-bold text-blue-600 dark:text-emerald-400">
                جرب الشات الذكي بالأسفل ↙
              </span>
            </div>

          </div>

        </div>

      </div>
    </section>
  );
};
