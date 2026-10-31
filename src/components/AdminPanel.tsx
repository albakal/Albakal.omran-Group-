import React, { useState } from 'react';
import { useApp } from '../context/AppContext';
import { 
  X, 
  Save, 
  RotateCcw, 
  Download, 
  Upload, 
  Check, 
  SlidersHorizontal, 
  Image, 
  Palette, 
  MessageSquare, 
  Phone, 
  FileText, 
  Video, 
  UserCheck, 
  Database,
  ExternalLink,
  Plus,
  Trash2
} from 'lucide-react';
import { AppConfig } from '../types';

export const AdminPanel: React.FC = () => {
  const { config, updateConfig, resetConfig, exportConfig, importConfig, isAdminOpen, setIsAdminOpen } = useApp();
  
  // Local form state
  const [formData, setFormData] = useState<AppConfig>(config);
  const [activeTab, setActiveTab] = useState<'branding' | 'whatsapp' | 'hero' | 'about' | 'media' | 'database'>('whatsapp');
  const [saveSuccess, setSaveSuccess] = useState(false);
  const [importStatus, setImportStatus] = useState<string | null>(null);

  // Sync if config changes externally
  React.useEffect(() => {
    setFormData(config);
  }, [config]);

  if (!isAdminOpen) return null;

  const handleSave = () => {
    updateConfig(formData);
    setSaveSuccess(true);
    setTimeout(() => setSaveSuccess(false), 2000);
  };

  const handleReset = () => {
    if (window.confirm('هل أنت متأكد من رغبتك في استعادة الإعدادات الأصلية الافتراضية؟')) {
      resetConfig();
      setFormData(config);
      setSaveSuccess(true);
      setTimeout(() => setSaveSuccess(false), 2000);
    }
  };

  const handleFileUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (event) => {
        const content = event.target?.result as string;
        if (content) {
          const success = importConfig(content);
          if (success) {
            setImportStatus('تم استيراد الإعدادات بنجاح!');
            setTimeout(() => setImportStatus(null), 3000);
          } else {
            setImportStatus('خطأ في قراءة ملف JSON.');
          }
        }
      };
      reader.readAsText(file);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-2 sm:p-4 bg-black/70 backdrop-blur-md animate-in fade-in duration-200">
      
      <div className="w-full max-w-5xl h-[90vh] bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl shadow-2xl flex flex-col overflow-hidden text-right">
        
        {/* Top Header */}
        <div className="p-4 sm:p-5 border-b border-slate-200 dark:border-slate-800 flex items-center justify-between bg-slate-50 dark:bg-slate-950/70">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-blue-600 text-white flex items-center justify-center shadow-md">
              <SlidersHorizontal className="w-5 h-5" />
            </div>
            <div>
              <h2 className="text-base sm:text-lg font-extrabold text-slate-900 dark:text-white flex items-center gap-2">
                <span>لوحة التحكم الكاملة</span>
                <span className="text-xs font-semibold px-2 py-0.5 rounded-full bg-blue-100 dark:bg-blue-900/60 text-blue-700 dark:text-blue-300">
                  Omran Control
                </span>
              </h2>
              <p className="text-xs text-slate-500 dark:text-slate-400">
                تعديل محتوى الموقع، الهوية، الألوان، زر الواتساب، والصور فورياً
              </p>
            </div>
          </div>

          <div className="flex items-center gap-2">
            {saveSuccess && (
              <span className="flex items-center gap-1.5 text-xs font-bold text-emerald-600 dark:text-emerald-400 bg-emerald-50 dark:bg-emerald-950/60 px-3 py-1.5 rounded-xl border border-emerald-300 dark:border-emerald-800">
                <Check className="w-4 h-4" />
                <span>تم الحفظ الفوري!</span>
              </span>
            )}

            <button
              onClick={handleSave}
              className="px-4 py-2 bg-emerald-600 hover:bg-emerald-500 text-white font-bold text-xs sm:text-sm rounded-xl shadow-md transition-all flex items-center gap-2 cursor-pointer active:scale-95"
            >
              <Save className="w-4 h-4" />
              <span>حفظ التعديلات</span>
            </button>

            <button
              onClick={() => setIsAdminOpen(false)}
              className="p-2 text-slate-400 hover:text-slate-700 dark:hover:text-white rounded-xl hover:bg-slate-200 dark:hover:bg-slate-800 transition-colors cursor-pointer"
            >
              <X className="w-5 h-5" />
            </button>
          </div>
        </div>

        {/* Navigation Tabs Bar */}
        <div className="flex items-center overflow-x-auto p-2 bg-slate-100/70 dark:bg-slate-900 border-b border-slate-200 dark:border-slate-800 gap-1.5 text-xs font-bold scrollbar-none">
          <button
            onClick={() => setActiveTab('whatsapp')}
            className={`flex items-center gap-2 px-3.5 py-2 rounded-xl transition-all whitespace-nowrap cursor-pointer ${
              activeTab === 'whatsapp'
                ? 'bg-emerald-600 text-white shadow-sm'
                : 'text-slate-600 dark:text-slate-400 hover:bg-slate-200 dark:hover:bg-slate-800'
            }`}
          >
            <MessageSquare className="w-4 h-4" />
            <span>زر الواتساب والتواصل</span>
          </button>

          <button
            onClick={() => setActiveTab('branding')}
            className={`flex items-center gap-2 px-3.5 py-2 rounded-xl transition-all whitespace-nowrap cursor-pointer ${
              activeTab === 'branding'
                ? 'bg-blue-600 text-white shadow-sm'
                : 'text-slate-600 dark:text-slate-400 hover:bg-slate-200 dark:hover:bg-slate-800'
            }`}
          >
            <Palette className="w-4 h-4" />
            <span>الشعار، الاسم والألوان</span>
          </button>

          <button
            onClick={() => setActiveTab('hero')}
            className={`flex items-center gap-2 px-3.5 py-2 rounded-xl transition-all whitespace-nowrap cursor-pointer ${
              activeTab === 'hero'
                ? 'bg-blue-600 text-white shadow-sm'
                : 'text-slate-600 dark:text-slate-400 hover:bg-slate-200 dark:hover:bg-slate-800'
            }`}
          >
            <FileText className="w-4 h-4" />
            <span>الواجهة التسويقية والتحميل</span>
          </button>

          <button
            onClick={() => setActiveTab('about')}
            className={`flex items-center gap-2 px-3.5 py-2 rounded-xl transition-all whitespace-nowrap cursor-pointer ${
              activeTab === 'about'
                ? 'bg-blue-600 text-white shadow-sm'
                : 'text-slate-600 dark:text-slate-400 hover:bg-slate-200 dark:hover:bg-slate-800'
            }`}
          >
            <UserCheck className="w-4 h-4" />
            <span>عن الشركة والمؤسس</span>
          </button>

          <button
            onClick={() => setActiveTab('media')}
            className={`flex items-center gap-2 px-3.5 py-2 rounded-xl transition-all whitespace-nowrap cursor-pointer ${
              activeTab === 'media'
                ? 'bg-blue-600 text-white shadow-sm'
                : 'text-slate-600 dark:text-slate-400 hover:bg-slate-200 dark:hover:bg-slate-800'
            }`}
          >
            <Video className="w-4 h-4" />
            <span>الفيديوهات والوسائط</span>
          </button>

          <button
            onClick={() => setActiveTab('database')}
            className={`flex items-center gap-2 px-3.5 py-2 rounded-xl transition-all whitespace-nowrap cursor-pointer ${
              activeTab === 'database'
                ? 'bg-purple-600 text-white shadow-sm'
                : 'text-slate-600 dark:text-slate-400 hover:bg-slate-200 dark:hover:bg-slate-800'
            }`}
          >
            <Database className="w-4 h-4" />
            <span>البيانات والنسخ الاحتياطي</span>
          </button>
        </div>

        {/* Tab Content Body (Scrollable) */}
        <div className="flex-1 overflow-y-auto p-4 sm:p-6 space-y-6 text-slate-800 dark:text-slate-200 text-xs sm:text-sm">
          
          {/* TAB 1: WhatsApp & Floating Button Settings */}
          {activeTab === 'whatsapp' && (
            <div className="space-y-6 animate-in fade-in duration-200">
              <div className="p-4 rounded-2xl bg-emerald-50 dark:bg-emerald-950/40 border border-emerald-200 dark:border-emerald-800/60 flex items-start gap-3">
                <MessageSquare className="w-6 h-6 text-emerald-600 dark:text-emerald-400 shrink-0 mt-0.5" />
                <div>
                  <h4 className="font-bold text-emerald-900 dark:text-emerald-200 text-sm">
                    إعدادات زر الواتساب العائم في كل صفحات الموقع
                  </h4>
                  <p className="text-xs text-emerald-700 dark:text-emerald-300 mt-1">
                    يمكنك هنا تغيير رقم الواتساب الذي تصله رسائل العملاء، وتعديل الرسالة الافتتاحية التلقائية التي تظهر عند النقر على الزر.
                  </p>
                </div>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                <div>
                  <label className="font-bold block mb-1 text-slate-700 dark:text-slate-300">
                    رقم الواتساب المعتمد (مع مفتاح الدولة) *
                  </label>
                  <input
                    type="text"
                    value={formData.whatsappNumber}
                    onChange={(e) => setFormData({ ...formData, whatsappNumber: e.target.value })}
                    placeholder="مثال: +967771234567 أو +966500000000"
                    className="w-full text-xs sm:text-sm p-3 rounded-xl border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800 focus:ring-2 focus:ring-emerald-500 font-mono dir-ltr text-left"
                  />
                  <span className="text-[11px] text-slate-400 mt-1 block">
                    يتم تحويله تلقائياً لرابط wa.me لفتح التطبيق مباشرة.
                  </span>
                </div>

                <div>
                  <label className="font-bold block mb-1 text-slate-700 dark:text-slate-300">
                    حالة زر الواتساب العائم
                  </label>
                  <div className="flex items-center gap-3 pt-2">
                    <label className="relative inline-flex items-center cursor-pointer">
                      <input
                        type="checkbox"
                        checked={formData.whatsappEnabled}
                        onChange={(e) => setFormData({ ...formData, whatsappEnabled: e.target.checked })}
                        className="sr-only peer"
                      />
                      <div className="w-11 h-6 bg-slate-200 peer-focus:outline-none rounded-full peer dark:bg-slate-700 peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-slate-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-emerald-600"></div>
                    </label>
                    <span className="text-xs font-semibold">
                      {formData.whatsappEnabled ? 'مفعّل ويظهر في كل الصفحات' : 'معطّل مؤقتاً'}
                    </span>
                  </div>
                </div>
              </div>

              <div>
                <label className="font-bold block mb-1 text-slate-700 dark:text-slate-300">
                  نص الرسالة الافتتاحية التلقائية في واتساب
                </label>
                <textarea
                  rows={3}
                  value={formData.whatsappMessage}
                  onChange={(e) => setFormData({ ...formData, whatsappMessage: e.target.value })}
                  placeholder="مرحباً، أود الاستفسار عن تطبيق عمران..."
                  className="w-full text-xs sm:text-sm p-3 rounded-xl border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800 focus:ring-2 focus:ring-emerald-500 resize-none"
                />
              </div>

              <div className="border-t border-slate-200 dark:border-slate-800 pt-5 space-y-4">
                <h4 className="font-bold text-sm text-slate-900 dark:text-white">
                  بيانات الاتصال الإضافية لخدمة العملاء
                </h4>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <label className="text-xs font-semibold block mb-1">رقم هاتف خدمة العملاء</label>
                    <input
                      type="text"
                      value={formData.phoneSupport}
                      onChange={(e) => setFormData({ ...formData, phoneSupport: e.target.value })}
                      className="w-full text-xs p-2.5 rounded-xl border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800 dir-ltr text-left"
                    />
                  </div>

                  <div>
                    <label className="text-xs font-semibold block mb-1">البريد الإلكتروني للدعم</label>
                    <input
                      type="email"
                      value={formData.emailSupport}
                      onChange={(e) => setFormData({ ...formData, emailSupport: e.target.value })}
                      className="w-full text-xs p-2.5 rounded-xl border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800"
                    />
                  </div>

                  <div className="md:col-span-2">
                    <label className="text-xs font-semibold block mb-1">مواعيد وساعات عمل خدمة العملاء</label>
                    <input
                      type="text"
                      value={formData.workingHours}
                      onChange={(e) => setFormData({ ...formData, workingHours: e.target.value })}
                      className="w-full text-xs p-2.5 rounded-xl border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800"
                    />
                  </div>
                </div>
              </div>

            </div>
          )}

          {/* TAB 2: Branding, Logo & Colors */}
          {activeTab === 'branding' && (
            <div className="space-y-6 animate-in fade-in duration-200">
              
              <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                <div>
                  <label className="font-bold block mb-1 text-slate-700 dark:text-slate-300">
                    اسم التطبيق
                  </label>
                  <input
                    type="text"
                    value={formData.appName}
                    onChange={(e) => setFormData({ ...formData, appName: e.target.value })}
                    className="w-full text-xs sm:text-sm p-3 rounded-xl border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800 focus:ring-2 focus:ring-blue-500"
                  />
                </div>

                <div>
                  <label className="font-bold block mb-1 text-slate-700 dark:text-slate-300">
                    اسم المجموعة التجاري
                  </label>
                  <input
                    type="text"
                    value={formData.groupName}
                    onChange={(e) => setFormData({ ...formData, groupName: e.target.value })}
                    className="w-full text-xs sm:text-sm p-3 rounded-xl border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800 focus:ring-2 focus:ring-blue-500"
                  />
                </div>

                <div className="md:col-span-2">
                  <label className="font-bold block mb-1 text-slate-700 dark:text-slate-300">
                    الشعار اللفظي (Slogan)
                  </label>
                  <input
                    type="text"
                    value={formData.slogan}
                    onChange={(e) => setFormData({ ...formData, slogan: e.target.value })}
                    className="w-full text-xs sm:text-sm p-3 rounded-xl border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800 focus:ring-2 focus:ring-blue-500"
                  />
                </div>
              </div>

              {/* Logo Management */}
              <div className="p-4 rounded-2xl bg-slate-50 dark:bg-slate-800/60 border border-slate-200 dark:border-slate-700 space-y-3">
                <div className="flex items-center justify-between">
                  <h4 className="font-bold text-sm text-slate-900 dark:text-white flex items-center gap-2">
                    <Image className="w-4 h-4 text-blue-500" />
                    <span>شعار الموقع الرسمي</span>
                  </h4>
                  <button
                    onClick={() => setFormData({ ...formData, logoUrl: '/assets/aistudio/50170_e08dce1d.jpg' })}
                    className="text-xs text-blue-600 dark:text-blue-400 font-bold underline cursor-pointer"
                  >
                    استعادة الشعار الرسمي المعتمد
                  </button>
                </div>

                <div className="flex flex-col sm:flex-row items-center gap-4">
                  <div className="w-24 h-24 rounded-2xl bg-white p-2 border border-slate-200 flex items-center justify-center shrink-0 shadow-sm">
                    <img 
                      src={formData.logoUrl} 
                      alt="معاينة الشعار" 
                      className="max-h-full max-w-full object-contain"
                      referrerPolicy="no-referrer"
                    />
                  </div>
                  <div className="flex-1 w-full space-y-2">
                    <input
                      type="text"
                      value={formData.logoUrl}
                      onChange={(e) => setFormData({ ...formData, logoUrl: e.target.value })}
                      placeholder="رابط أو مسار الشعار"
                      className="w-full text-xs p-2.5 rounded-xl border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800 dir-ltr text-left"
                    />
                    <p className="text-[11px] text-slate-400">
                      الشعار الحالي هو الشعار المعتمد المرفوع من قبلك لمجموعة عمران.
                    </p>
                  </div>
                </div>
              </div>

              {/* Color Customizer */}
              <div className="p-4 rounded-2xl bg-slate-50 dark:bg-slate-800/60 border border-slate-200 dark:border-slate-700 space-y-3">
                <h4 className="font-bold text-sm text-slate-900 dark:text-white flex items-center gap-2">
                  <Palette className="w-4 h-4 text-emerald-500" />
                  <span>تخصيص الألوان الرئيسية</span>
                </h4>

                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                  <div className="flex items-center gap-3">
                    <input
                      type="color"
                      value={formData.primaryColor}
                      onChange={(e) => setFormData({ ...formData, primaryColor: e.target.value })}
                      className="w-10 h-10 rounded-xl cursor-pointer border border-slate-300"
                    />
                    <div>
                      <span className="text-xs font-bold block">اللون الأساسي (Primary)</span>
                      <span className="font-mono text-xs text-slate-400">{formData.primaryColor}</span>
                    </div>
                  </div>

                  <div className="flex items-center gap-3">
                    <input
                      type="color"
                      value={formData.accentColor}
                      onChange={(e) => setFormData({ ...formData, accentColor: e.target.value })}
                      className="w-10 h-10 rounded-xl cursor-pointer border border-slate-300"
                    />
                    <div>
                      <span className="text-xs font-bold block">لون التمييز (Accent Green)</span>
                      <span className="font-mono text-xs text-slate-400">{formData.accentColor}</span>
                    </div>
                  </div>
                </div>
              </div>

            </div>
          )}

          {/* TAB 3: Hero & Downloads */}
          {activeTab === 'hero' && (
            <div className="space-y-5 animate-in fade-in duration-200">
              <div>
                <label className="font-bold block mb-1 text-slate-700 dark:text-slate-300">
                  شارة الهيرو العلوية
                </label>
                <input
                  type="text"
                  value={formData.heroBadge}
                  onChange={(e) => setFormData({ ...formData, heroBadge: e.target.value })}
                  className="w-full text-xs sm:text-sm p-3 rounded-xl border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800"
                />
              </div>

              <div>
                <label className="font-bold block mb-1 text-slate-700 dark:text-slate-300">
                  عنوان الواجهة الرئيسي
                </label>
                <input
                  type="text"
                  value={formData.heroTitle}
                  onChange={(e) => setFormData({ ...formData, heroTitle: e.target.value })}
                  className="w-full text-xs sm:text-sm p-3 rounded-xl border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800"
                />
              </div>

              <div>
                <label className="font-bold block mb-1 text-slate-700 dark:text-slate-300">
                  الجملة التسويقية البارزة (Highlight)
                </label>
                <input
                  type="text"
                  value={formData.heroHighlight}
                  onChange={(e) => setFormData({ ...formData, heroHighlight: e.target.value })}
                  className="w-full text-xs sm:text-sm p-3 rounded-xl border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800"
                />
              </div>

              <div>
                <label className="font-bold block mb-1 text-slate-700 dark:text-slate-300">
                  الوصف التسويقي الشامل
                </label>
                <textarea
                  rows={3}
                  value={formData.heroDescription}
                  onChange={(e) => setFormData({ ...formData, heroDescription: e.target.value })}
                  className="w-full text-xs sm:text-sm p-3 rounded-xl border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800 resize-none"
                />
              </div>

              {/* Download Links */}
              <div className="border-t border-slate-200 dark:border-slate-800 pt-4 space-y-4">
                <h4 className="font-bold text-sm text-slate-900 dark:text-white">
                  روابط تحميل التطبيق وإصدار الـ APK
                </h4>

                <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
                  <div>
                    <label className="text-xs font-semibold block mb-1">رقم الإصدار</label>
                    <input
                      type="text"
                      value={formData.appVersion}
                      onChange={(e) => setFormData({ ...formData, appVersion: e.target.value })}
                      className="w-full text-xs p-2.5 rounded-xl border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800"
                    />
                  </div>

                  <div>
                    <label className="text-xs font-semibold block mb-1">حجم التطبيق</label>
                    <input
                      type="text"
                      value={formData.appSize}
                      onChange={(e) => setFormData({ ...formData, appSize: e.target.value })}
                      className="w-full text-xs p-2.5 rounded-xl border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800"
                    />
                  </div>

                  <div>
                    <label className="text-xs font-semibold block mb-1">تاريخ الإطلاق</label>
                    <input
                      type="text"
                      value={formData.releaseDate}
                      onChange={(e) => setFormData({ ...formData, releaseDate: e.target.value })}
                      className="w-full text-xs p-2.5 rounded-xl border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800"
                    />
                  </div>
                </div>

                <div className="space-y-3">
                  <div>
                    <label className="text-xs font-semibold block mb-1">رابط تحميل APK المباشر</label>
                    <input
                      type="text"
                      value={formData.apkDownloadUrl}
                      onChange={(e) => setFormData({ ...formData, apkDownloadUrl: e.target.value })}
                      className="w-full text-xs p-2.5 rounded-xl border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800 dir-ltr text-left"
                    />
                  </div>
                  <div>
                    <label className="text-xs font-semibold block mb-1">رابط Google Play</label>
                    <input
                      type="text"
                      value={formData.googlePlayUrl}
                      onChange={(e) => setFormData({ ...formData, googlePlayUrl: e.target.value })}
                      className="w-full text-xs p-2.5 rounded-xl border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800 dir-ltr text-left"
                    />
                  </div>
                  <div>
                    <label className="text-xs font-semibold block mb-1">رابط App Store</label>
                    <input
                      type="text"
                      value={formData.appStoreUrl}
                      onChange={(e) => setFormData({ ...formData, appStoreUrl: e.target.value })}
                      className="w-full text-xs p-2.5 rounded-xl border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800 dir-ltr text-left"
                    />
                  </div>
                </div>
              </div>

            </div>
          )}

          {/* TAB 4: About & Founder */}
          {activeTab === 'about' && (
            <div className="space-y-5 animate-in fade-in duration-200">
              <h4 className="font-bold text-sm text-slate-900 dark:text-white">
                معلومات مجموعة عمران وأنشطتها
              </h4>

              <div>
                <label className="font-semibold text-xs block mb-1">عنوان قسم عن الشركة</label>
                <input
                  type="text"
                  value={formData.aboutTitle}
                  onChange={(e) => setFormData({ ...formData, aboutTitle: e.target.value })}
                  className="w-full text-xs sm:text-sm p-2.5 rounded-xl border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800"
                />
              </div>

              <div>
                <label className="font-semibold text-xs block mb-1">نبذة وتاريخ المجموعة والأنشطة</label>
                <textarea
                  rows={3}
                  value={formData.aboutStory}
                  onChange={(e) => setFormData({ ...formData, aboutStory: e.target.value })}
                  className="w-full text-xs sm:text-sm p-2.5 rounded-xl border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800 resize-none"
                />
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label className="font-semibold text-xs block mb-1">الرؤية</label>
                  <textarea
                    rows={2}
                    value={formData.visionText}
                    onChange={(e) => setFormData({ ...formData, visionText: e.target.value })}
                    className="w-full text-xs p-2.5 rounded-xl border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800 resize-none"
                  />
                </div>
                <div>
                  <label className="font-semibold text-xs block mb-1">الرسالة</label>
                  <textarea
                    rows={2}
                    value={formData.missionText}
                    onChange={(e) => setFormData({ ...formData, missionText: e.target.value })}
                    className="w-full text-xs p-2.5 rounded-xl border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800 resize-none"
                  />
                </div>
              </div>

              <div className="border-t border-slate-200 dark:border-slate-800 pt-4 space-y-4">
                <h4 className="font-bold text-sm text-slate-900 dark:text-white">
                  معلومات المؤسس ورسالته
                </h4>

                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                  <div>
                    <label className="font-semibold text-xs block mb-1">اسم المؤسس</label>
                    <input
                      type="text"
                      value={formData.founderName}
                      onChange={(e) => setFormData({ ...formData, founderName: e.target.value })}
                      className="w-full text-xs sm:text-sm p-2.5 rounded-xl border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800"
                    />
                  </div>

                  <div>
                    <label className="font-semibold text-xs block mb-1">المسمى والصفة</label>
                    <input
                      type="text"
                      value={formData.founderTitle}
                      onChange={(e) => setFormData({ ...formData, founderTitle: e.target.value })}
                      className="w-full text-xs sm:text-sm p-2.5 rounded-xl border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800"
                    />
                  </div>
                </div>

                <div>
                  <label className="font-semibold text-xs block mb-1">سيرة المؤسس وفلسفته</label>
                  <textarea
                    rows={3}
                    value={formData.founderBio}
                    onChange={(e) => setFormData({ ...formData, founderBio: e.target.value })}
                    className="w-full text-xs sm:text-sm p-2.5 rounded-xl border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800 resize-none"
                  />
                </div>

                <div>
                  <label className="font-semibold text-xs block mb-1">مقولة المؤسس</label>
                  <input
                    type="text"
                    value={formData.founderQuote}
                    onChange={(e) => setFormData({ ...formData, founderQuote: e.target.value })}
                    className="w-full text-xs sm:text-sm p-2.5 rounded-xl border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800"
                  />
                </div>
              </div>

            </div>
          )}

          {/* TAB 5: Media & Videos */}
          {activeTab === 'media' && (
            <div className="space-y-5 animate-in fade-in duration-200">
              <h4 className="font-bold text-sm text-slate-900 dark:text-white">
                إدارة الفيديو التوضيحي الرئيسي
              </h4>

              <div>
                <label className="font-semibold text-xs block mb-1">عنوان الفيديو التوضيحي الرئيسي</label>
                <input
                  type="text"
                  value={formData.mainVideoTitle}
                  onChange={(e) => setFormData({ ...formData, mainVideoTitle: e.target.value })}
                  className="w-full text-xs sm:text-sm p-2.5 rounded-xl border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800"
                />
              </div>

              <div>
                <label className="font-semibold text-xs block mb-1">رابط الفيديو (YouTube Embed أو MP4)</label>
                <input
                  type="text"
                  value={formData.mainVideoUrl}
                  onChange={(e) => setFormData({ ...formData, mainVideoUrl: e.target.value })}
                  className="w-full text-xs sm:text-sm p-2.5 rounded-xl border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800 dir-ltr text-left font-mono"
                />
              </div>

              <div>
                <label className="font-semibold text-xs block mb-1">وصف الفيديو الرئيسي</label>
                <textarea
                  rows={2}
                  value={formData.mainVideoDescription}
                  onChange={(e) => setFormData({ ...formData, mainVideoDescription: e.target.value })}
                  className="w-full text-xs sm:text-sm p-2.5 rounded-xl border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800 resize-none"
                />
              </div>

              <div className="border-t border-slate-200 dark:border-slate-800 pt-4 space-y-3">
                <h4 className="font-bold text-sm text-slate-900 dark:text-white">
                  فيديوهات الشرح السريعة ({formData.tutorials.length})
                </h4>
                
                {formData.tutorials.map((tut, index) => (
                  <div key={tut.id} className="p-3 rounded-xl bg-slate-50 dark:bg-slate-800/70 border border-slate-200 dark:border-slate-700 space-y-2">
                    <div className="flex items-center justify-between">
                      <span className="font-bold text-xs text-blue-600 dark:text-blue-400">فيديو #{index + 1}</span>
                      <span className="text-[11px] font-mono text-slate-400">{tut.duration}</span>
                    </div>
                    <input
                      type="text"
                      value={tut.title}
                      onChange={(e) => {
                        const newTuts = [...formData.tutorials];
                        newTuts[index].title = e.target.value;
                        setFormData({ ...formData, tutorials: newTuts });
                      }}
                      className="w-full text-xs p-2 rounded-lg border border-slate-300 dark:border-slate-600 bg-white dark:bg-slate-800"
                    />
                  </div>
                ))}
              </div>

            </div>
          )}

          {/* TAB 6: Database & Backup */}
          {activeTab === 'database' && (
            <div className="space-y-6 animate-in fade-in duration-200">
              
              <div className="p-4 rounded-2xl bg-purple-50 dark:bg-purple-950/40 border border-purple-200 dark:border-purple-800/60">
                <h4 className="font-bold text-purple-900 dark:text-purple-200 text-sm mb-1">
                  قاعدة البيانات الديناميكية والنسخ الاحتياطي
                </h4>
                <p className="text-xs text-purple-700 dark:text-purple-300 leading-relaxed">
                  تعتمد المنصة على نظام تخزين ديناميكي مستمر يحفظ كافة التعديلات في الذاكرة السريعة للمتصفح، ويمكنك تصدير نسخة كاملة من البيانات كملف JSON لاستعادتها أو نقلها لأي جهاز آخر.
                </p>
              </div>

              {importStatus && (
                <div className="p-3 rounded-xl bg-blue-50 dark:bg-blue-950 border border-blue-300 text-xs text-blue-800 dark:text-blue-200 font-bold">
                  {importStatus}
                </div>
              )}

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                
                {/* Export Card */}
                <div className="p-5 rounded-2xl bg-slate-50 dark:bg-slate-800/60 border border-slate-200 dark:border-slate-700 text-right space-y-3">
                  <h4 className="font-bold text-sm">تصدير الإعدادات الحالية</h4>
                  <p className="text-xs text-slate-500 dark:text-slate-400">
                    تحميل ملف JSON متكامل يتضمن كافة نصوص وألوان وروابط الموقع.
                  </p>
                  <button
                    onClick={exportConfig}
                    className="w-full py-2.5 px-4 bg-purple-600 hover:bg-purple-500 text-white font-bold text-xs rounded-xl flex items-center justify-center gap-2 cursor-pointer shadow-md"
                  >
                    <Download className="w-4 h-4" />
                    <span>تصدير ملف Config JSON</span>
                  </button>
                </div>

                {/* Import Card */}
                <div className="p-5 rounded-2xl bg-slate-50 dark:bg-slate-800/60 border border-slate-200 dark:border-slate-700 text-right space-y-3">
                  <h4 className="font-bold text-sm">استيراد إعدادات سابقة</h4>
                  <p className="text-xs text-slate-500 dark:text-slate-400">
                    رفع ملف JSON لإعادة تطبيق التعديلات المحفوظة سابقاً.
                  </p>
                  <label className="w-full py-2.5 px-4 bg-slate-200 hover:bg-slate-300 dark:bg-slate-700 dark:hover:bg-slate-600 text-slate-800 dark:text-white font-bold text-xs rounded-xl flex items-center justify-center gap-2 cursor-pointer transition-colors">
                    <Upload className="w-4 h-4" />
                    <span>رفع واستيراد ملف JSON</span>
                    <input
                      type="file"
                      accept=".json"
                      onChange={handleFileUpload}
                      className="hidden"
                    />
                  </label>
                </div>

              </div>

              {/* Reset to Factory Defaults */}
              <div className="p-5 rounded-2xl bg-rose-50 dark:bg-rose-950/30 border border-rose-200 dark:border-rose-900/50 text-right flex flex-col sm:flex-row items-center justify-between gap-4">
                <div>
                  <h4 className="font-bold text-rose-900 dark:text-rose-200 text-sm">استعادة الإعدادات الافتراضية الأصلية</h4>
                  <p className="text-xs text-rose-700 dark:text-rose-300 mt-0.5">
                    إلغاء كافة التعديلات والعودة للنسخة الأساسية المصممة لمجموعة عمران.
                  </p>
                </div>
                <button
                  onClick={handleReset}
                  className="px-4 py-2 bg-rose-600 hover:bg-rose-500 text-white font-bold text-xs rounded-xl flex items-center gap-2 cursor-pointer whitespace-nowrap"
                >
                  <RotateCcw className="w-4 h-4" />
                  <span>إعادة ضبط المصنع</span>
                </button>
              </div>

            </div>
          )}

        </div>

        {/* Footer Actions */}
        <div className="p-4 border-t border-slate-200 dark:border-slate-800 flex items-center justify-between bg-slate-50 dark:bg-slate-950/70">
          <span className="text-xs text-slate-500 dark:text-slate-400 font-mono">
            نظام إدارة محتوى عمران جروب · v2.5
          </span>
          <div className="flex items-center gap-3">
            <button
              onClick={() => setIsAdminOpen(false)}
              className="px-4 py-2 text-xs font-semibold text-slate-600 dark:text-slate-300 hover:bg-slate-200 dark:hover:bg-slate-800 rounded-xl cursor-pointer"
            >
              إغلاق
            </button>
            <button
              onClick={handleSave}
              className="px-5 py-2 bg-emerald-600 hover:bg-emerald-500 text-white font-bold text-xs rounded-xl shadow-md transition-all flex items-center gap-1.5 cursor-pointer active:scale-95"
            >
              <Save className="w-4 h-4" />
              <span>حفظ وتطبيق الآن</span>
            </button>
          </div>
        </div>

      </div>

    </div>
  );
};
