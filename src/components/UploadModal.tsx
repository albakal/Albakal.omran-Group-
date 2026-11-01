import React, { useState } from 'react';
import { AppItem, AppType, AppCategory } from '../types/store';
import { UserProfile } from '../types/auth';
import { PlayLogo } from './PlayLogo';
import { X, Upload, Image as ImageIcon, CheckCircle, Sparkles, Gamepad2, Layers, FileCode, Wand2, RefreshCw, Copy, Check, Share2, Download } from 'lucide-react';
import confetti from 'canvas-confetti';
import { playSuccessSound, playClickSound } from '../utils/audio';
import { extractDataFromApkFile } from '../utils/apkExtractor';

interface UploadModalProps {
  currentUser: UserProfile;
  onClose: () => void;
  onAppCreated: (newApp: AppItem) => void;
  isArabic: boolean;
}

export const UploadModal: React.FC<UploadModalProps> = ({ currentUser, onClose, onAppCreated, isArabic }) => {
  const [step, setStep] = useState<1 | 2 | 3 | 4>(1);

  // Form State
  const [type, setType] = useState<AppType>('game');
  const [title, setTitle] = useState('');
  const [tagline, setTagline] = useState('');
  const [description, setDescription] = useState('');
  const [category, setCategory] = useState<AppCategory>('Arcade');
  const [price, setPrice] = useState<number>(0);

  // Media
  const [customIconUrl, setCustomIconUrl] = useState('');
  const [customCoverUrl, setCustomCoverUrl] = useState('');
  const [screenshots, setScreenshots] = useState<string[]>([]);
  const [isExtracting, setIsExtracting] = useState(false);
  const [extractionSuccessMessage, setExtractionSuccessMessage] = useState<string | null>(null);

  // Package & Execution
  const [uploadedFileName, setUploadedFileName] = useState('');
  const [uploadedFileSizeMb, setUploadedFileSizeMb] = useState(25.4);
  const [hasPlayableDemo, setHasPlayableDemo] = useState(true);
  const [playableGameType, setPlayableGameType] = useState<'space-shooter' | 'desert-rally' | 'smart-tasbeeh' | 'code-runner' | 'custom-iframe'>('space-shooter');
  const [iframeUrl, setIframeUrl] = useState('');
  const [customDownloadUrl, setCustomDownloadUrl] = useState('');

  // Developer & Versioning (Clean, no exposed private login details)
  const [developerName, setDeveloperName] = useState('مطور معتمد');
  const [version, setVersion] = useState('1.0.0');
  const [packageId, setPackageId] = useState('');
  const [minAndroid, setMinAndroid] = useState('Android 8.0+');
  const [ageRating, setAgeRating] = useState<'3+' | '7+' | '12+' | '16+' | '18+'>('3+');
  const [changelog, setChangelog] = useState('الإصدار الأول المعتمد في متجر Play.');

  // Post-upload Success State with Shareable Download Link
  const [createdApp, setCreatedApp] = useState<AppItem | null>(null);
  const [copiedLink, setCopiedLink] = useState(false);
  const [copiedDirectDownload, setCopiedDirectDownload] = useState(false);

  const [errors, setErrors] = useState<{ [key: string]: string }>({});

  // Auto-extract icon & screenshots from APK
  const handlePackageFileUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    setUploadedFileName(file.name);
    setUploadedFileSizeMb(Number((file.size / (1024 * 1024)).toFixed(1)));
    setIsExtracting(true);
    setExtractionSuccessMessage(null);

    try {
      const extracted = await extractDataFromApkFile(file);

      if (!title || title.trim() === '') {
        setTitle(extracted.title || file.name.replace(/\.[^/.]+$/, ''));
      }
      if (!packageId) {
        setPackageId(extracted.packageId || `com.play.${file.name.replace(/\.[^/.]+$/, '').toLowerCase()}`);
      }

      if (extracted.iconUrl) {
        setCustomIconUrl(extracted.iconUrl);
      }

      if (extracted.screenshots && extracted.screenshots.length > 0) {
        setScreenshots(extracted.screenshots);
        if (!customCoverUrl) {
          setCustomCoverUrl(extracted.screenshots[0]);
        }
      }

      setExtractionSuccessMessage(
        isArabic
          ? `✓ تم فحص الحزمة واستخراج الأيقونة و ${extracted.screenshots.length} صور تلقائياً!`
          : `✓ Package analyzed! Extracted icon & ${extracted.screenshots.length} images automatically!`
      );
      playSuccessSound();
    } catch (err) {
      console.error('Extraction error:', err);
    } finally {
      setIsExtracting(false);
    }
  };

  const handleIconFileUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = () => {
        if (typeof reader.result === 'string') {
          setCustomIconUrl(reader.result);
        }
      };
      reader.readAsDataURL(file);
    }
  };

  const handleScreenshotUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const files = e.target.files;
    if (files && files.length > 0) {
      Array.from(files).slice(0, 4).forEach((file) => {
        const reader = new FileReader();
        reader.onload = () => {
          if (typeof reader.result === 'string') {
            setScreenshots((prev) => [...prev.slice(0, 3), reader.result as string]);
          }
        };
        reader.readAsDataURL(file);
      });
    }
  };

  const validateStep = (s: number): boolean => {
    const newErrors: { [key: string]: string } = {};

    if (s === 1) {
      if (!title.trim()) newErrors.title = isArabic ? 'يرجى إدخال اسم التطبيق أو اللعبة' : 'Title is required';
      if (!tagline.trim()) newErrors.tagline = isArabic ? 'يرجى كتابة نبذة سريعة' : 'Tagline is required';
      if (!description.trim()) newErrors.description = isArabic ? 'يرجى كتابة وصف تفصيلي' : 'Description is required';
    }

    if (s === 4) {
      if (!developerName.trim()) newErrors.developerName = isArabic ? 'يرجى كتابة اسم المطور' : 'Developer name is required';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleNextStep = () => {
    playClickSound();
    if (validateStep(step)) {
      setStep((prev) => Math.min(prev + 1, 4) as 1 | 2 | 3 | 4);
    }
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!validateStep(4)) return;

    const appId = `play-app-${Date.now()}`;
    const defaultSvgIcon = `data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 128 128"><rect width="128" height="128" rx="28" fill="%230086F8"/><text x="50%" y="54%" dominant-baseline="middle" text-anchor="middle" font-size="64" font-weight="bold" fill="white">${(title.charAt(0) || 'P').toUpperCase()}</text></svg>`;

    const finalIcon = customIconUrl || defaultSvgIcon;
    const finalCover = customCoverUrl || (screenshots.length > 0 ? screenshots[0] : finalIcon);

    // Generate direct download URL (either custom entered link, or direct sharable URL)
    const directDl = customDownloadUrl.trim() || `${window.location.origin}${window.location.pathname}?download=${appId}`;

    const newApp: AppItem = {
      id: appId,
      title,
      tagline,
      description,
      type,
      category,
      developer: {
        name: developerName || 'مطور معتمد',
        verified: true,
        avatar: 'M'
      },
      iconUrl: finalIcon,
      coverUrl: finalCover,
      screenshots: screenshots.length > 0 ? screenshots : [finalCover],
      rating: 5.0,
      ratingCount: 0,
      downloadCount: 0,
      sizeMb: uploadedFileSizeMb || 25.0,
      version: version || '1.0.0',
      minAndroid: minAndroid || 'Android 8.0+',
      platforms: type === 'game' ? ['android', 'web'] : ['android', 'web', 'windows'],
      releaseDate: new Date().toISOString().split('T')[0],
      updatedDate: new Date().toISOString().split('T')[0],
      changelog: changelog || 'الإصدار الأول في متجر Play.',
      price: price || 0,
      ageRating,
      downloadUrl: directDl,
      packageFileName: uploadedFileName || `${title.toLowerCase().replace(/\s+/g, '_')}_v${version}.apk`,
      hasPlayableDemo,
      playableGameType: hasPlayableDemo ? playableGameType : undefined,
      iframeUrl: iframeUrl || undefined,
      isUserUploaded: true,
      reviews: []
    };

    confetti({
      particleCount: 100,
      spread: 80,
      origin: { y: 0.6 }
    });

    playSuccessSound();
    setCreatedApp(newApp);
    onAppCreated(newApp);
  };

  const copyStoreLink = () => {
    if (!createdApp) return;
    const shareUrl = `${window.location.origin}${window.location.pathname}?app=${createdApp.id}`;
    navigator.clipboard?.writeText(shareUrl);
    setCopiedLink(true);
    playClickSound();
    setTimeout(() => setCopiedLink(false), 2500);
  };

  const copyDownloadLink = () => {
    if (!createdApp) return;
    const dlLink = createdApp.downloadUrl || `${window.location.origin}${window.location.pathname}?download=${createdApp.id}`;
    navigator.clipboard?.writeText(dlLink);
    setCopiedDirectDownload(true);
    playClickSound();
    setTimeout(() => setCopiedDirectDownload(false), 2500);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-3 sm:p-6 bg-slate-950/80 backdrop-blur-md overflow-y-auto">
      <div className="relative w-full max-w-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl shadow-2xl overflow-hidden flex flex-col my-auto transition-colors">
        
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950/60">
          <div className="flex items-center gap-3">
            <PlayLogo size={28} />
            <div>
              <h2 className="text-base font-bold text-slate-900 dark:text-white flex items-center gap-2">
                <span>{isArabic ? 'رفع تطبيق أو لعبة جديدة' : 'Upload New App or Game'}</span>
                <span className="text-xs px-2 py-0.5 rounded bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 font-mono">
                  Play Console
                </span>
              </h2>
              <div className="text-xs text-slate-500">
                {isArabic ? 'منصة النشر المباشر مع استخراج الأيقونة والصور' : 'Direct publishing with auto image extraction'}
              </div>
            </div>
          </div>

          <button
            onClick={onClose}
            className="p-1.5 text-slate-400 hover:text-slate-700 dark:hover:text-slate-200 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Success Screen with Shareable Download Link */}
        {createdApp ? (
          <div className="p-6 sm:p-8 space-y-6 text-center">
            <div className="w-16 h-16 mx-auto rounded-2xl bg-emerald-500/10 text-emerald-500 flex items-center justify-center shadow-lg border border-emerald-500/20">
              <CheckCircle className="w-8 h-8" />
            </div>

            <div>
              <h3 className="text-xl font-black text-slate-900 dark:text-white">
                {isArabic ? 'تم نشر التطبيق بنجاح في متجر Play!' : 'App Published Successfully!'}
              </h3>
              <p className="text-xs text-slate-500 mt-1 max-w-md mx-auto">
                {isArabic
                  ? 'يمكنك الآن نسخ رابط التحميل المباشر لتصديره أو إرساله ومشاركته مع أي شخص لتحميل التطبيق فوراً.'
                  : 'You can now copy the direct download link to export or share with anyone.'}
              </p>
            </div>

            {/* App Preview Card */}
            <div className="p-4 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-800/40 flex items-center gap-3 text-start">
              <img
                src={createdApp.iconUrl}
                alt={createdApp.title}
                className="w-14 h-14 rounded-xl object-cover border border-slate-300 dark:border-slate-700 shrink-0"
              />
              <div className="flex-1 min-w-0">
                <div className="text-sm font-bold text-slate-900 dark:text-white truncate">
                  {createdApp.title}
                </div>
                <div className="text-xs text-slate-500 truncate">
                  {createdApp.tagline}
                </div>
                <div className="text-[11px] font-mono text-emerald-600 dark:text-emerald-400 mt-0.5">
                  v{createdApp.version} · {createdApp.sizeMb} MB · {createdApp.category}
                </div>
              </div>
            </div>

            {/* Direct Links Action Box */}
            <div className="space-y-3 pt-2">
              {/* Option 1: Direct Download Link */}
              <div className="p-3.5 rounded-xl border border-emerald-500/40 bg-emerald-50/40 dark:bg-emerald-950/20 space-y-2">
                <div className="text-xs font-bold text-emerald-800 dark:text-emerald-300 flex items-center justify-between">
                  <span className="flex items-center gap-1.5">
                    <Download className="w-4 h-4 text-emerald-500" />
                    <span>{isArabic ? 'رابط التحميل المباشر لملف التطبيق (Download Link):' : 'Direct Download Link:'}</span>
                  </span>
                </div>
                <div className="flex items-center gap-2">
                  <input
                    type="text"
                    readOnly
                    value={createdApp.downloadUrl}
                    className="flex-1 px-3 py-2 bg-white dark:bg-slate-900 border border-slate-300 dark:border-slate-700 rounded-lg text-xs font-mono text-slate-700 dark:text-slate-300 select-all"
                  />
                  <button
                    onClick={copyDownloadLink}
                    className="flex items-center gap-1.5 px-4 py-2 bg-emerald-600 hover:bg-emerald-500 text-white rounded-lg text-xs font-bold shadow-sm transition-colors cursor-pointer shrink-0"
                  >
                    {copiedDirectDownload ? <Check className="w-3.5 h-3.5" /> : <Copy className="w-3.5 h-3.5" />}
                    <span>{copiedDirectDownload ? (isArabic ? 'تم النسخ!' : 'Copied!') : (isArabic ? 'نسخ الرابط' : 'Copy')}</span>
                  </button>
                </div>
              </div>

              {/* Option 2: Store Page Link */}
              <div className="p-3.5 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-800/30 space-y-2">
                <div className="text-xs font-bold text-slate-700 dark:text-slate-300 flex items-center justify-between">
                  <span className="flex items-center gap-1.5">
                    <Share2 className="w-4 h-4 text-slate-500" />
                    <span>{isArabic ? 'رابط صفحة التطبيق في متجر Play (Store Link):' : 'Play Store Page Link:'}</span>
                  </span>
                </div>
                <div className="flex items-center gap-2">
                  <input
                    type="text"
                    readOnly
                    value={`${window.location.origin}${window.location.pathname}?app=${createdApp.id}`}
                    className="flex-1 px-3 py-2 bg-white dark:bg-slate-900 border border-slate-300 dark:border-slate-700 rounded-lg text-xs font-mono text-slate-700 dark:text-slate-300 select-all"
                  />
                  <button
                    onClick={copyStoreLink}
                    className="flex items-center gap-1.5 px-4 py-2 bg-slate-800 hover:bg-slate-700 text-white rounded-lg text-xs font-bold transition-colors cursor-pointer shrink-0"
                  >
                    {copiedLink ? <Check className="w-3.5 h-3.5" /> : <Copy className="w-3.5 h-3.5" />}
                    <span>{copiedLink ? (isArabic ? 'تم النسخ!' : 'Copied!') : (isArabic ? 'نسخ الرابط' : 'Copy')}</span>
                  </button>
                </div>
              </div>
            </div>

            <div className="pt-2">
              <button
                onClick={onClose}
                className="w-full py-2.5 bg-slate-900 dark:bg-slate-800 hover:bg-slate-800 dark:hover:bg-slate-700 text-white rounded-xl text-xs font-bold transition-colors cursor-pointer"
              >
                {isArabic ? 'الذهاب إلى المتجر واستعراض التطبيق' : 'Go to Store'}
              </button>
            </div>
          </div>
        ) : (
          <>
            {/* Step Indicator */}
            <div className="flex items-center justify-between px-6 py-2.5 bg-slate-100/70 dark:bg-slate-800/40 border-b border-slate-200 dark:border-slate-800 text-xs font-semibold text-slate-500">
              <button
                type="button"
                onClick={() => setStep(1)}
                className={`pb-1 border-b-2 transition-colors ${
                  step === 1 ? 'border-emerald-500 text-emerald-600 dark:text-emerald-400' : 'border-transparent'
                }`}
              >
                1. {isArabic ? 'البيانات الأساسية' : 'General Info'}
              </button>
              <span className="text-slate-400">/</span>
              <button
                type="button"
                onClick={() => validateStep(1) && setStep(2)}
                className={`pb-1 border-b-2 transition-colors ${
                  step === 2 ? 'border-emerald-500 text-emerald-600 dark:text-emerald-400' : 'border-transparent'
                }`}
              >
                2. {isArabic ? 'الحزمة واستخراج الصور' : 'APK & Extraction'}
              </button>
              <span className="text-slate-400">/</span>
              <button
                type="button"
                onClick={() => validateStep(1) && setStep(3)}
                className={`pb-1 border-b-2 transition-colors ${
                  step === 3 ? 'border-emerald-500 text-emerald-600 dark:text-emerald-400' : 'border-transparent'
                }`}
              >
                3. {isArabic ? 'الصور والأيقونة' : 'Visual Assets'}
              </button>
              <span className="text-slate-400">/</span>
              <button
                type="button"
                onClick={() => validateStep(1) && setStep(4)}
                className={`pb-1 border-b-2 transition-colors ${
                  step === 4 ? 'border-emerald-500 text-emerald-600 dark:text-emerald-400' : 'border-transparent'
                }`}
              >
                4. {isArabic ? 'رابط التحميل والنشر' : 'Download Link & Publish'}
              </button>
            </div>

            {/* Form Body */}
            <form onSubmit={handleSubmit} className="p-6 space-y-5 overflow-y-auto max-h-[65vh]">
              {/* STEP 1: Basic Details */}
              {step === 1 && (
                <div className="space-y-4">
                  <div>
                    <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1.5">
                      {isArabic ? 'نوع العمل' : 'Store Type'}
                    </label>
                    <div className="grid grid-cols-2 gap-3">
                      <button
                        type="button"
                        onClick={() => {
                          setType('game');
                          setCategory('Arcade');
                          setPlayableGameType('space-shooter');
                        }}
                        className={`flex items-center justify-center gap-2 p-3 rounded-xl border text-sm font-bold transition-all cursor-pointer ${
                          type === 'game'
                            ? 'border-emerald-500 bg-emerald-50 dark:bg-emerald-950/30 text-emerald-700 dark:text-emerald-300'
                            : 'border-slate-200 dark:border-slate-700 hover:bg-slate-50 dark:hover:bg-slate-800 text-slate-600 dark:text-slate-300'
                        }`}
                      >
                        <Gamepad2 className="w-4 h-4" />
                        <span>{isArabic ? 'لعبة (Game)' : 'Game'}</span>
                      </button>

                      <button
                        type="button"
                        onClick={() => {
                          setType('app');
                          setCategory('Productivity');
                          setPlayableGameType('code-runner');
                        }}
                        className={`flex items-center justify-center gap-2 p-3 rounded-xl border text-sm font-bold transition-all cursor-pointer ${
                          type === 'app'
                            ? 'border-emerald-500 bg-emerald-50 dark:bg-emerald-950/30 text-emerald-700 dark:text-emerald-300'
                            : 'border-slate-200 dark:border-slate-700 hover:bg-slate-50 dark:hover:bg-slate-800 text-slate-600 dark:text-slate-300'
                        }`}
                      >
                        <Layers className="w-4 h-4" />
                        <span>{isArabic ? 'تطبيق (App)' : 'Application'}</span>
                      </button>
                    </div>
                  </div>

                  <div>
                    <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">
                      {isArabic ? 'اسم التطبيق أو اللعبة *' : 'App Title *'}
                    </label>
                    <input
                      type="text"
                      value={title}
                      onChange={(e) => setTitle(e.target.value)}
                      placeholder={isArabic ? 'مثال: محارب الكواكب، المنظم الذكي...' : 'e.g. Astro Raider'}
                      className="w-full px-3.5 py-2.5 rounded-lg border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-900 dark:text-white text-sm focus:ring-2 focus:ring-emerald-500 focus:outline-none"
                    />
                    {errors.title && <p className="text-xs text-rose-500 mt-1">{errors.title}</p>}
                  </div>

                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                    <div>
                      <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">
                        {isArabic ? 'التصنيف *' : 'Category *'}
                      </label>
                      <select
                        value={category}
                        onChange={(e) => setCategory(e.target.value as AppCategory)}
                        className="w-full px-3.5 py-2.5 rounded-lg border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-900 dark:text-white text-sm focus:ring-2 focus:ring-emerald-500 focus:outline-none"
                      >
                        <option value="Arcade">أركيد (Arcade)</option>
                        <option value="Racing">سباقات (Racing)</option>
                        <option value="Action">أكشن (Action)</option>
                        <option value="Productivity">إنتاجية (Productivity)</option>
                        <option value="Tools">أدوات ومطورين (Tools)</option>
                        <option value="Utility">خدمات عامة (Utility)</option>
                        <option value="Puzzle">ألغاز وذكاء (Puzzle)</option>
                        <option value="Education">تعليم (Education)</option>
                        <option value="Social">تواصل (Social)</option>
                      </select>
                    </div>

                    <div>
                      <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">
                        {isArabic ? 'السعر (0 = مجاني)' : 'Price (0 = Free)'}
                      </label>
                      <input
                        type="number"
                        min="0"
                        step="0.5"
                        value={price}
                        onChange={(e) => setPrice(Number(e.target.value))}
                        className="w-full px-3.5 py-2.5 rounded-lg border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-900 dark:text-white text-sm font-mono"
                      />
                    </div>
                  </div>

                  <div>
                    <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">
                      {isArabic ? 'شعار تسويقي قصير (Tagline) *' : 'Short Tagline *'}
                    </label>
                    <input
                      type="text"
                      value={tagline}
                      onChange={(e) => setTagline(e.target.value)}
                      placeholder={isArabic ? 'عبارة موجزة تلخص فكرة التطبيق...' : 'A brief compelling tagline'}
                      className="w-full px-3.5 py-2.5 rounded-lg border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-900 dark:text-white text-sm focus:ring-2 focus:ring-emerald-500 focus:outline-none"
                    />
                    {errors.tagline && <p className="text-xs text-rose-500 mt-1">{errors.tagline}</p>}
                  </div>

                  <div>
                    <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">
                      {isArabic ? 'الوصف الكامل للمستخدمين *' : 'Full Description *'}
                    </label>
                    <textarea
                      rows={3}
                      value={description}
                      onChange={(e) => setDescription(e.target.value)}
                      placeholder={isArabic ? 'تفاصيل الميزات، طريقة اللعب، وطرق الاستخدام...' : 'Provide comprehensive description...'}
                      className="w-full px-3.5 py-2.5 rounded-lg border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-900 dark:text-white text-sm focus:ring-2 focus:ring-emerald-500 focus:outline-none"
                    />
                    {errors.description && <p className="text-xs text-rose-500 mt-1">{errors.description}</p>}
                  </div>
                </div>
              )}

              {/* STEP 2: Package File & Automatic Extraction */}
              {step === 2 && (
                <div className="space-y-4">
                  <div className="p-4 rounded-xl border-2 border-dashed border-emerald-500/50 bg-emerald-50/20 dark:bg-emerald-950/20 text-center space-y-3">
                    <div className="w-12 h-12 mx-auto rounded-xl bg-emerald-600 text-white flex items-center justify-center shadow-md">
                      <Wand2 className="w-6 h-6" />
                    </div>
                    <div>
                      <h3 className="text-sm font-bold text-slate-900 dark:text-white">
                        {isArabic ? 'رفع ملف التطبيق واستخراج الصور تلقائياً' : 'Upload APK & Auto-Extract Images'}
                      </h3>
                      <p className="text-xs text-slate-500 dark:text-slate-400 mt-0.5 max-w-md mx-auto">
                        {isArabic
                          ? 'بمجرد اختيار ملف الـ APK أو الـ ZIP، سيقوم متجر Play بفحص الحزمة واستخراج الأيقونة ولقطات الشاشة منها مباشرة كما في Google Play!'
                          : 'Upload your APK or ZIP package. Play Store will inspect the archive and extract icon and screenshots automatically!'}
                      </p>
                    </div>

                    <div className="flex flex-col items-center gap-2 pt-2">
                      <label className="inline-flex items-center gap-2 px-6 py-2.5 bg-emerald-600 hover:bg-emerald-500 text-white rounded-xl text-xs font-bold shadow-md cursor-pointer transition-all">
                        {isExtracting ? (
                          <RefreshCw className="w-4 h-4 animate-spin" />
                        ) : (
                          <Upload className="w-4 h-4" />
                        )}
                        <span>{isExtracting ? (isArabic ? 'جاري الفحص والاستخراج...' : 'Extracting...') : (isArabic ? 'اختر ملف الـ APK أو الحزمة' : 'Browse APK File')}</span>
                        <input
                          type="file"
                          accept=".apk,.zip,.xapk,.aab,application/*"
                          onChange={handlePackageFileUpload}
                          className="hidden"
                        />
                      </label>

                      {uploadedFileName && (
                        <div className="text-xs font-mono text-slate-700 dark:text-slate-300 flex items-center gap-2">
                          <FileCode className="w-4 h-4 text-emerald-500" />
                          <span>{uploadedFileName}</span>
                          <span className="text-slate-400">({uploadedFileSizeMb} MB)</span>
                        </div>
                      )}
                    </div>

                    {extractionSuccessMessage && (
                      <div className="p-2.5 rounded-lg bg-emerald-500/10 border border-emerald-500/30 text-xs text-emerald-700 dark:text-emerald-300 font-medium flex items-center justify-center gap-1.5">
                        <CheckCircle className="w-4 h-4 text-emerald-500" />
                        <span>{extractionSuccessMessage}</span>
                      </div>
                    )}
                  </div>

                  {/* Instant Web Sandbox Toggle */}
                  <div className="p-4 border border-slate-200 dark:border-slate-800 rounded-xl bg-slate-50 dark:bg-slate-800/30 space-y-3">
                    <div className="flex items-center justify-between">
                      <div>
                        <div className="text-xs font-bold text-slate-900 dark:text-white flex items-center gap-1.5">
                          <Sparkles className="w-4 h-4 text-emerald-500" />
                          <span>{isArabic ? 'تفعيل التجربة الفورية في المتصفح' : 'Enable Instant Web Preview'}</span>
                        </div>
                        <p className="text-[11px] text-slate-500 mt-0.5">
                          {isArabic ? 'يتيح للمستخدمين تشغيل اللعبة أو التطبيق فوراً بدون تحميل!' : 'Users can play or test the app directly in their browser'}
                        </p>
                      </div>
                      <input
                        type="checkbox"
                        checked={hasPlayableDemo}
                        onChange={(e) => setHasPlayableDemo(e.target.checked)}
                        className="w-4 h-4 accent-emerald-500 rounded cursor-pointer"
                      />
                    </div>

                    {hasPlayableDemo && (
                      <div className="pt-2 border-t border-slate-200 dark:border-slate-700/60 space-y-3">
                        <label className="block text-xs font-bold text-slate-700 dark:text-slate-300">
                          {isArabic ? 'اختر محرك المعاينة الحية:' : 'Live Preview Engine:'}
                        </label>
                        <select
                          value={playableGameType}
                          onChange={(e) => setPlayableGameType(e.target.value as any)}
                          className="w-full px-3 py-2 rounded-lg border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800 text-xs text-slate-900 dark:text-white"
                        >
                          <option value="space-shooter">محرك أركيد الفضاء 2D (Space Arcade Canvas)</option>
                          <option value="desert-rally">محرك رالي الصحراء (Desert Rally Canvas)</option>
                          <option value="smart-tasbeeh">تطبيق العداد التفاعلي (Interactive Counter)</option>
                          <option value="code-runner">بيئة تشغيل أكواد الويب (Web Code Sandbox)</option>
                          <option value="custom-iframe">رابط لعبة ويب مخصصة / Iframe URL</option>
                        </select>

                        {playableGameType === 'custom-iframe' && (
                          <input
                            type="url"
                            value={iframeUrl}
                            onChange={(e) => setIframeUrl(e.target.value)}
                            placeholder="https://example.com/game"
                            className="w-full px-3 py-2 rounded-lg border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800 text-xs text-slate-900 dark:text-white"
                          />
                        )}
                      </div>
                    )}
                  </div>
                </div>
              )}

              {/* STEP 3: Visual Assets */}
              {step === 3 && (
                <div className="space-y-5">
                  <div>
                    <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-2">
                      {isArabic ? 'أيقونة التطبيق (المستخرجة أو المرفوعة)' : 'App Icon (Extracted or Uploaded)'}
                    </label>
                    
                    <div className="flex items-center gap-4 p-3 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-800/40">
                      <div className="w-16 h-16 rounded-2xl overflow-hidden border-2 border-emerald-500 shadow-md bg-slate-900 shrink-0 flex items-center justify-center">
                        {customIconUrl ? (
                          <img src={customIconUrl} alt="Icon" className="w-full h-full object-cover" />
                        ) : (
                          <ImageIcon className="w-8 h-8 text-slate-400" />
                        )}
                      </div>

                      <div className="space-y-1.5 flex-1">
                        <div className="text-xs font-bold text-slate-900 dark:text-white">
                          {customIconUrl ? (isArabic ? 'تم استخراج وتعيين أيقونة للتطبيق ✓' : 'App icon ready ✓') : (isArabic ? 'لم يتم تعيين أيقونة بعد' : 'No icon yet')}
                        </div>
                        <label className="inline-flex items-center gap-1.5 px-3 py-1.5 border border-slate-300 dark:border-slate-700 hover:bg-slate-200 dark:hover:bg-slate-700 rounded-lg text-xs font-semibold cursor-pointer transition-colors">
                          <Upload className="w-3.5 h-3.5" />
                          <span>{isArabic ? 'تغيير أو رفع أيقونة أخرى' : 'Upload custom icon'}</span>
                          <input type="file" accept="image/*" onChange={handleIconFileUpload} className="hidden" />
                        </label>
                      </div>
                    </div>
                  </div>

                  <div>
                    <div className="flex items-center justify-between mb-2">
                      <label className="block text-xs font-bold text-slate-700 dark:text-slate-300">
                        {isArabic ? 'معرض لقطات الشاشة والصور' : 'Screenshots Gallery'}
                      </label>
                      <label className="text-xs text-emerald-600 dark:text-emerald-400 hover:underline cursor-pointer flex items-center gap-1 font-semibold">
                        <Upload className="w-3.5 h-3.5" />
                        <span>{isArabic ? 'إضافة صور إضافية' : 'Add more'}</span>
                        <input type="file" multiple accept="image/*" onChange={handleScreenshotUpload} className="hidden" />
                      </label>
                    </div>

                    {screenshots.length > 0 ? (
                      <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
                        {screenshots.map((s, idx) => (
                          <div key={idx} className="relative aspect-[16/10] rounded-xl overflow-hidden border border-slate-300 dark:border-slate-700 group">
                            <img src={s} alt={`screenshot ${idx}`} className="w-full h-full object-cover" />
                            <button
                              type="button"
                              onClick={() => setScreenshots((prev) => prev.filter((_, i) => i !== idx))}
                              className="absolute top-1.5 right-1.5 w-5 h-5 rounded-full bg-slate-950/80 text-white text-xs flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity"
                            >
                              ✕
                            </button>
                          </div>
                        ))}
                      </div>
                    ) : (
                      <div className="p-6 text-center border-2 border-dashed border-slate-300 dark:border-slate-700 rounded-xl text-xs text-slate-500">
                        {isArabic ? 'لا توجد لقطات شاشة بعد، يمكنك رفعها أو استخراجها من ملف الـ APK.' : 'No screenshots yet.'}
                      </div>
                    )}
                  </div>
                </div>
              )}

              {/* STEP 4: Direct Download Link & Publishing */}
              {step === 4 && (
                <div className="space-y-4">
                  {/* Direct Download Link Field */}
                  <div className="p-4 rounded-xl border border-emerald-500/40 bg-emerald-50/30 dark:bg-emerald-950/20 space-y-2">
                    <label className="block text-xs font-bold text-slate-900 dark:text-white">
                      {isArabic ? 'رابط التحميل المباشر للتطبيق (يمكنك نسخه وإرساله لأي شخص) *' : 'Direct Download URL *'}
                    </label>
                    <input
                      type="text"
                      value={customDownloadUrl}
                      onChange={(e) => setCustomDownloadUrl(e.target.value)}
                      placeholder={isArabic ? 'أدخل رابط مباشر (Google Drive, GitHub, Mediafire...) أو اتركه فارغاً لتوليد رابط تلقائي' : 'Enter direct link or leave empty for auto-generated link'}
                      className="w-full px-3.5 py-2.5 rounded-lg border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-900 text-xs font-mono text-slate-900 dark:text-white focus:ring-2 focus:ring-emerald-500"
                    />
                    <p className="text-[11px] text-slate-500">
                      {isArabic
                        ? '💡 بمجرد النشر، سيوفر لك المتجر زراً لنسخ هذا الرابط ورابط صفحة المتجر لإرساله فوراً لأي مستخدم.'
                        : '💡 Once published, a 1-click copy button will be provided to share the download with anyone.'}
                    </p>
                  </div>

                  <div className="grid grid-cols-2 gap-3">
                    <div>
                      <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">
                        {isArabic ? 'اسم الناشر أو الاستوديو' : 'Publisher Name'}
                      </label>
                      <input
                        type="text"
                        value={developerName}
                        onChange={(e) => setDeveloperName(e.target.value)}
                        className="w-full px-3 py-2 rounded-lg border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800 text-xs text-slate-900 dark:text-white"
                      />
                    </div>

                    <div>
                      <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">
                        {isArabic ? 'رقم الإصدار' : 'Version'}
                      </label>
                      <input
                        type="text"
                        value={version}
                        onChange={(e) => setVersion(e.target.value)}
                        className="w-full px-3 py-2 rounded-lg border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800 text-xs font-mono text-slate-900 dark:text-white"
                      />
                    </div>
                  </div>

                  <div className="grid grid-cols-2 gap-3">
                    <div>
                      <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">
                        {isArabic ? 'التصنيف العمري' : 'Age Rating'}
                      </label>
                      <select
                        value={ageRating}
                        onChange={(e) => setAgeRating(e.target.value as any)}
                        className="w-full px-3 py-2 rounded-lg border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800 text-xs text-slate-900 dark:text-white"
                      >
                        <option value="3+">3+ (مناسب للجميع)</option>
                        <option value="7+">7+</option>
                        <option value="12+">12+</option>
                        <option value="16+">16+</option>
                        <option value="18+">18+</option>
                      </select>
                    </div>

                    <div>
                      <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">
                        {isArabic ? 'معرف الحزمة (Package ID)' : 'Package ID'}
                      </label>
                      <input
                        type="text"
                        value={packageId}
                        onChange={(e) => setPackageId(e.target.value)}
                        placeholder="com.play.app"
                        className="w-full px-3 py-2 rounded-lg border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800 text-xs font-mono text-slate-900 dark:text-white"
                      />
                    </div>
                  </div>

                  <div>
                    <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">
                      {isArabic ? 'ما الجديد في هذا التحديث (Release Notes)' : 'What is New'}
                    </label>
                    <textarea
                      rows={2}
                      value={changelog}
                      onChange={(e) => setChangelog(e.target.value)}
                      className="w-full px-3 py-2 rounded-lg border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800 text-xs text-slate-900 dark:text-white"
                    />
                  </div>
                </div>
              )}

              {/* Navigation Buttons */}
              <div className="flex items-center justify-between pt-4 border-t border-slate-200 dark:border-slate-800">
                {step > 1 ? (
                  <button
                    type="button"
                    onClick={() => setStep((prev) => (prev - 1) as any)}
                    className="px-4 py-2 text-xs font-semibold text-slate-600 dark:text-slate-300 hover:text-slate-900 dark:hover:text-white transition-colors"
                  >
                    {isArabic ? '← السابق' : '← Previous'}
                  </button>
                ) : <div />}

                {step < 4 ? (
                  <button
                    type="button"
                    onClick={handleNextStep}
                    className="px-5 py-2 text-xs font-bold text-white bg-emerald-600 hover:bg-emerald-500 rounded-lg shadow-sm transition-all cursor-pointer"
                  >
                    {isArabic ? 'متابعة الخطوة التالية ←' : 'Next Step →'}
                  </button>
                ) : (
                  <button
                    type="submit"
                    className="flex items-center gap-2 px-6 py-2.5 text-xs font-bold text-white bg-emerald-600 hover:bg-emerald-500 rounded-lg shadow-md shadow-emerald-600/20 active:scale-98 transition-all cursor-pointer"
                  >
                    <PlayLogo size={16} />
                    <span>{isArabic ? 'نشر في متجر Play والحصول على رابط التحميل' : 'Publish & Get Download Link'}</span>
                  </button>
                )}
              </div>
            </form>
          </>
        )}
      </div>
    </div>
  );
};
