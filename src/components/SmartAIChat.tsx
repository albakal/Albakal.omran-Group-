import React, { useState, useRef, useEffect } from 'react';
import { useApp } from '../context/AppContext';
import { 
  Bot, 
  Send, 
  X, 
  Sparkles, 
  ArrowLeft, 
  MessageCircle, 
  User, 
  Minimize2, 
  Maximize2,
  RefreshCw
} from 'lucide-react';

interface ChatMessage {
  id: string;
  sender: 'ai' | 'user';
  text: string;
  timestamp: string;
  quickActions?: { label: string; action: () => void }[];
}

export const SmartAIChat: React.FC = () => {
  const { config, triggerApkDownload } = useApp();
  const [isOpen, setIsOpen] = useState(false);
  const [input, setInput] = useState('');
  const [isTyping, setIsTyping] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const cleanPhone = config.whatsappNumber.replace(/[^0-9]/g, '');

  const initialMessages: ChatMessage[] = [
    {
      id: 'msg-1',
      sender: 'ai',
      text: `أهلاً بك! أنا مساعد الدعم الفني الذكي لمجموعة عمران (Omran Group). 
أنا هنا لمساعدتك في التعرف على مميزات التطبيق للتجار والعملاء، طريقة التحميل والتثبيت، وحلول نقاط البيع الذكية. كيف يمكنني خدمتك اليوم؟`,
      timestamp: new Date().toLocaleTimeString('ar-EG', { hour: '2-digit', minute: '2-digit' }),
      quickActions: [
        {
          label: 'كيف يوفر التطبيق تكلفة الكاشير للتاجر؟',
          action: () => handleSendQuestion('كيف يوفر التطبيق تكلفة الكاشير للتاجر؟')
        },
        {
          label: 'كيف أقوم بتحميل وتثبيت التطبيق APK؟',
          action: () => handleSendQuestion('كيف أقوم بتحميل وتثبيت التطبيق APK؟')
        },
        {
          label: 'خطوات تفعيل حساب التاجر ونقاط البيع',
          action: () => handleSendQuestion('خطوات تفعيل حساب التاجر ونقاط البيع')
        },
        {
          label: 'التواصل المباشر عبر واتساب عمران',
          action: () => {
            window.open(`https://wa.me/${cleanPhone}?text=${encodeURIComponent(config.whatsappMessage)}`, '_blank');
          }
        }
      ]
    }
  ];

  const [messages, setMessages] = useState<ChatMessage[]>(initialMessages);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    if (isOpen) {
      scrollToBottom();
    }
  }, [messages, isOpen]);

  // AI Knowledge-based Response Engine
  const generateAIResponse = (userQuery: string): string => {
    const q = userQuery.toLowerCase();

    if (q.includes('تحميل') || q.includes('تنزيل') || q.includes('apk') || q.includes('تثبيت') || q.includes('رابط')) {
      return `يمكنك تحميل تطبيق ${config.appName} بعدة طرق مباشرة:
1. التحميل المباشر بصيغة APK (حجمه ${config.appSize} وإصداره ${config.appVersion}) بنقرة واحدة من الزر العلوي في الموقع.
2. عبر متجر Google Play لأجهزة Android.
3. عبر متجر App Store لأجهزة iPhone.

التطبيق خفيف وسريع ويدعم التثبيت الفوري دون أي تعقيدات تقنية.`;
    }

    if (q.includes('تاجر') || q.includes('كاشير') || q.includes('اقتصادي') || q.includes('تكلفة') || q.includes('أرباح') || q.includes('عمولة')) {
      return `يقدم تطبيق عمران حلاً اقتصادياً استثنائياً للتجار:
- يوفر عليك شراء أجهزة كاشير مخصصة (تحويل هاتفك إلى نقطة بيع كاملة).
- نظام إدارة مخزون دقيق وتنبيهات آلية عند نفاد السلع.
- فواتير إلكترونية فورية قابلة للمشاركة على واتساب العميل.
- تسوية مالية مباشرة وسريعة لحسابك البنكي بدون عمولات مجحفة.
- إمكانية العمل أوفلاين (بدون إنترنت مؤقت) ومزامنة العمليات تلقائياً.`;
    }

    if (q.includes('عميل') || q.includes('زبون') || q.includes('تسوق') || q.includes('شراء') || q.includes('عروض')) {
      return `بالنسبة للعملاء والمتسوقين، يوفر التطبيق تجربة تسوق سهلة وموفرة:
- عروض وخصومات يومية حصرية مباشرة من التجار دون وسطاء.
- خيارات دفع مرنة (محافظ إلكترونية، بطاقات بنكية، دفع عند الاستلام).
- حفظ دائم لكافة الفواتير الإلكترونية وسجل المشتريات.
- تتبع حالة الطلبات والتوصيل أولاً بأول.`;
    }

    if (q.includes('مؤسس') || q.includes('عمران البكال') || q.includes('الشركة') || q.includes('عن عمران')) {
      return `مجموعة عمران (Omran Group) هي منظومة استثمارية وتقنية رائدة في تطوير الحلول التجارية الرقمية، ويرأسها المؤسس ${config.founderName} (${config.founderTitle}). 
رؤية المجموعة تنطلق من شعار «${config.slogan}» بهدف تمكين التاجر المحلي وتيسير الحياة اليومية للمستهلك عبر حلول اقتصادية عادلة ومستدامة.`;
    }

    if (q.includes('واتساب') || q.includes('رقم') || q.includes('تواصل') || q.includes('دعم') || q.includes('اتصال') || q.includes('خدمة عملاء')) {
      return `يمكنك التواصل مع فريق خدمة العملاء والدعم الفني مباشرة عبر:
- واتساب عمران جروب المعتمد: ${config.whatsappNumber}
- الهاتف المباشر: ${config.phoneSupport}
- البريد الإلكتروني: ${config.emailSupport}
- مواعيد العمل: ${config.workingHours}

أو انقر على زر واتساب العائم في أسفل يسار الشاشة لبدء محادثة فورية!`;
    }

    if (q.includes('سعر') || q.includes('اشتراك') || q.includes('باقة') || q.includes('مجاني')) {
      return `تطبيق عمران مصمم كحل اقتصادي مبسط؛ التسجيل متاح الآن للتجار مع فترة تجريبية مجانية وباقات مرنة بدون التزامات طويلة الأجل. يمكنك تنزيل التطبيق والبدء مباشرة أو التواصل مع فريق المبيعات عبر الواتساب لتخصيص باقة لمتجرك أو سلسلة فروعك.`;
    }

    return `شكراً لسؤالك! بخصوص "${userQuery}"، نحن في مجموعة عمران نضع بين يديك تطبيقاً شاملاً يجمع بين الكفاءة الاقتصادية وسهولة الاستخدام. 
يمكنك تجربة التطبيق بتحميل نسخة الـ APK الآن، أو التواصل مباشرة مع فريق الدعم المتخصص عبر الواتساب على الرقم (${config.whatsappNumber}) لمساعدتك بشكل شخصي.`;
  };

  const handleSendQuestion = (questionText: string) => {
    const userMsg: ChatMessage = {
      id: `user-${Date.now()}`,
      sender: 'user',
      text: questionText,
      timestamp: new Date().toLocaleTimeString('ar-EG', { hour: '2-digit', minute: '2-digit' })
    };

    setMessages(prev => [...prev, userMsg]);
    setIsTyping(true);

    setTimeout(() => {
      const reply = generateAIResponse(questionText);
      const aiMsg: ChatMessage = {
        id: `ai-${Date.now()}`,
        sender: 'ai',
        text: reply,
        timestamp: new Date().toLocaleTimeString('ar-EG', { hour: '2-digit', minute: '2-digit' }),
        quickActions: [
          {
            label: 'تنزيل ملف التطبيق APK',
            action: triggerApkDownload
          },
          {
            label: 'محادثة مسؤول الدعم على واتساب',
            action: () => {
              window.open(`https://wa.me/${cleanPhone}?text=${encodeURIComponent('مرحباً، لدي استفسار إضافي: ' + questionText)}`, '_blank');
            }
          }
        ]
      };
      setMessages(prev => [...prev, aiMsg]);
      setIsTyping(false);
    }, 600);
  };

  const handleFormSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!input.trim()) return;
    const q = input.trim();
    setInput('');
    handleSendQuestion(q);
  };

  return (
    <div className="fixed bottom-6 right-6 z-50 flex flex-col items-end select-none">
      
      {/* Expanded Chat Box */}
      {isOpen && (
        <div className="mb-3 w-80 sm:w-96 h-[520px] max-h-[80vh] bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl shadow-2xl flex flex-col overflow-hidden animate-in fade-in slide-in-from-bottom-4 duration-200 text-right">
          
          {/* Header */}
          <div className="p-4 bg-gradient-to-r from-blue-700 via-blue-600 to-indigo-700 text-white flex items-center justify-between shadow-sm">
            <div className="flex items-center gap-2.5">
              <div className="w-9 h-9 rounded-full bg-white/10 flex items-center justify-center border border-white/20">
                <Bot className="w-5 h-5 text-emerald-300" />
              </div>
              <div>
                <h4 className="font-bold text-sm flex items-center gap-1.5">
                  <span>مساعد عمران الذكي</span>
                  <span className="text-[10px] bg-emerald-500/30 text-emerald-200 px-1.5 py-0.5 rounded-full border border-emerald-400/30">
                    AI
                  </span>
                </h4>
                <p className="text-[11px] text-blue-100">استجابة فورية 24/7</p>
              </div>
            </div>

            <div className="flex items-center gap-1">
              <button
                onClick={() => setMessages(initialMessages)}
                title="إعادة بدء المحادثة"
                className="p-1.5 text-white/80 hover:text-white rounded-lg hover:bg-white/10 transition-colors cursor-pointer"
              >
                <RefreshCw className="w-4 h-4" />
              </button>
              <button
                onClick={() => setIsOpen(false)}
                className="p-1.5 text-white/80 hover:text-white rounded-lg hover:bg-white/10 transition-colors cursor-pointer"
              >
                <X className="w-5 h-5" />
              </button>
            </div>
          </div>

          {/* Messages Area */}
          <div className="flex-1 p-4 overflow-y-auto space-y-3 bg-slate-50 dark:bg-slate-950/60 text-xs sm:text-sm">
            {messages.map((msg) => (
              <div
                key={msg.id}
                className={`flex flex-col ${msg.sender === 'user' ? 'items-start' : 'items-end'}`}
              >
                <div
                  className={`max-w-[85%] p-3.5 rounded-2xl leading-relaxed whitespace-pre-line ${
                    msg.sender === 'user'
                      ? 'bg-blue-600 text-white rounded-br-xs text-right'
                      : 'bg-white dark:bg-slate-800 text-slate-800 dark:text-slate-100 rounded-bl-xs border border-slate-200 dark:border-slate-700/70 shadow-sm text-right'
                  }`}
                >
                  {msg.text}
                </div>

                <span className="text-[10px] text-slate-400 mt-1 px-1">
                  {msg.timestamp}
                </span>

                {/* Optional Quick Action Buttons attached to AI response */}
                {msg.quickActions && msg.quickActions.length > 0 && (
                  <div className="mt-2 flex flex-wrap gap-1.5 justify-end max-w-[95%]">
                    {msg.quickActions.map((qa, idx) => (
                      <button
                        key={idx}
                        onClick={qa.action}
                        className="px-2.5 py-1.5 bg-blue-50 dark:bg-blue-950/60 hover:bg-blue-100 dark:hover:bg-blue-900 border border-blue-200 dark:border-blue-800 rounded-xl text-[11px] font-semibold text-blue-700 dark:text-blue-300 transition-colors cursor-pointer text-right"
                      >
                        {qa.label}
                      </button>
                    ))}
                  </div>
                )}
              </div>
            ))}

            {isTyping && (
              <div className="flex items-center gap-1.5 p-3 bg-white dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-2xl rounded-bl-xs w-20 text-slate-400">
                <span className="w-2 h-2 rounded-full bg-blue-600 animate-bounce"></span>
                <span className="w-2 h-2 rounded-full bg-blue-600 animate-bounce [animation-delay:0.2s]"></span>
                <span className="w-2 h-2 rounded-full bg-blue-600 animate-bounce [animation-delay:0.4s]"></span>
              </div>
            )}

            <div ref={messagesEndRef} />
          </div>

          {/* Input Footer */}
          <form onSubmit={handleFormSubmit} className="p-3 bg-white dark:bg-slate-900 border-t border-slate-200 dark:border-slate-800 flex items-center gap-2">
            <input
              type="text"
              value={input}
              onChange={(e) => setInput(e.target.value)}
              placeholder="اكتب استفسارك هنا..."
              className="flex-1 text-xs sm:text-sm p-2.5 rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-blue-600"
            />
            <button
              type="submit"
              disabled={!input.trim()}
              className="p-2.5 bg-blue-600 hover:bg-blue-500 disabled:opacity-40 text-white rounded-xl shadow-md transition-all cursor-pointer active:scale-95"
            >
              <Send className="w-4 h-4 rotate-180" />
            </button>
          </form>

        </div>
      )}

      {/* Floating Trigger Button */}
      <button
        onClick={() => setIsOpen(!isOpen)}
        title="مساعد الدعم الفني الذكي لمجموعة عمران"
        className="group flex items-center gap-2.5 px-4 py-3 bg-gradient-to-r from-blue-700 to-indigo-700 hover:from-blue-600 hover:to-indigo-600 text-white rounded-full shadow-2xl shadow-blue-900/40 hover:shadow-blue-600/50 transition-all duration-300 hover:scale-105 active:scale-95 cursor-pointer border-2 border-white/20"
      >
        <div className="w-6 h-6 flex items-center justify-center">
          <Bot className="w-5 h-5 text-emerald-300 group-hover:rotate-12 transition-transform" />
        </div>
        <span className="font-bold text-sm tracking-wide hidden sm:inline whitespace-nowrap">
          المساعد الذكي
        </span>
      </button>

    </div>
  );
};
