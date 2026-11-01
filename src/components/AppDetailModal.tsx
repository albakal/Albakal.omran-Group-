import React, { useState } from 'react';
import { AppItem, AppReview } from '../types/store';
import { X, Play, Download, Star, Heart, Share2, ShieldCheck, Check, Sparkles, Send, ThumbsUp } from 'lucide-react';
import { playClickSound, playSuccessSound } from '../utils/audio';

interface AppDetailModalProps {
  app: AppItem;
  onClose: () => void;
  onPlayDemo: (app: AppItem) => void;
  onDownload: (app: AppItem) => void;
  isFavorite: boolean;
  onToggleFavorite: (id: string) => void;
  onAddReview: (appId: string, review: AppReview) => void;
  isArabic: boolean;
}

export const AppDetailModal: React.FC<AppDetailModalProps> = ({
  app,
  onClose,
  onPlayDemo,
  onDownload,
  isFavorite,
  onToggleFavorite,
  onAddReview,
  isArabic
}) => {
  const [activeTab, setActiveTab] = useState<'about' | 'reviews'>('about');
  const [downloadProgress, setDownloadProgress] = useState<number | null>(null);
  const [copiedLink, setCopiedLink] = useState(false);
  const [selectedScreenshot, setSelectedScreenshot] = useState<string | null>(null);

  // Review form
  const [reviewerName, setReviewerName] = useState('');
  const [ratingStars, setRatingStars] = useState(5);
  const [reviewComment, setReviewComment] = useState('');
  const [reviewSubmitted, setReviewSubmitted] = useState(false);

  const handleStartDownload = () => {
    if (downloadProgress !== null) return;
    playClickSound();
    setDownloadProgress(5);

    const interval = setInterval(() => {
      setDownloadProgress((prev) => {
        if (prev === null) return 0;
        if (prev >= 100) {
          clearInterval(interval);
          playSuccessSound();
          onDownload(app);

          // Trigger simulated sample download file
          const blob = new Blob([`Package: ${app.title}\nVersion: ${app.version}\nDeveloper: ${app.developer.name}\nSize: ${app.sizeMb}MB\nPlatform: Orbit Store verified download`], { type: 'application/octet-stream' });
          const url = URL.createObjectURL(blob);
          const a = document.createElement('a');
          a.href = url;
          a.download = app.packageFileName || `${app.title.replace(/\s+/g, '_')}.apk`;
          document.body.appendChild(a);
          a.click();
          a.remove();
          URL.revokeObjectURL(url);

          setTimeout(() => setDownloadProgress(null), 3000);
          return 100;
        }
        return prev + 15;
      });
    }, 180);
  };

  const handleShare = () => {
    playClickSound();
    navigator.clipboard?.writeText(window.location.href);
    setCopiedLink(true);
    setTimeout(() => setCopiedLink(false), 2500);
  };

  const handleSubmitReview = (e: React.FormEvent) => {
    e.preventDefault();
    if (!reviewerName.trim() || !reviewComment.trim()) return;

    const newRev: AppReview = {
      id: `rev-${Date.now()}`,
      userName: reviewerName.trim(),
      rating: ratingStars,
      date: isArabic ? 'الآن' : 'Just now',
      comment: reviewComment.trim(),
      helpfulCount: 0
    };

    onAddReview(app.id, newRev);
    setReviewSubmitted(true);
    playSuccessSound();
    setReviewComment('');
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-3 sm:p-6 bg-slate-950/80 backdrop-blur-md overflow-y-auto">
      <div className="relative w-full max-w-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl shadow-2xl overflow-hidden flex flex-col my-auto transition-colors">
        
        {/* Cover / Header Banner */}
        <div className="relative h-44 sm:h-56 w-full bg-slate-950 overflow-hidden shrink-0">
          <img
            src={app.coverUrl || app.iconUrl}
            alt={app.title}
            referrerPolicy="no-referrer"
            className="w-full h-full object-cover filter brightness-90"
          />
          <div className="absolute inset-0 bg-gradient-to-t from-slate-950 via-slate-950/50 to-transparent" />

          {/* Close & Share Buttons */}
          <div className="absolute top-4 right-4 sm:top-5 sm:right-5 flex items-center gap-2 z-10">
            <button
              onClick={handleShare}
              className="p-2 rounded-full bg-slate-900/70 hover:bg-slate-900 text-white backdrop-blur-md transition-colors"
              title="مشاركة الرابط"
            >
              {copiedLink ? <Check className="w-4 h-4 text-emerald-400" /> : <Share2 className="w-4 h-4" />}
            </button>

            <button
              onClick={() => {
                playClickSound();
                onToggleFavorite(app.id);
              }}
              className="p-2 rounded-full bg-slate-900/70 hover:bg-slate-900 text-white backdrop-blur-md transition-colors"
              title="إضافة للمفضلة"
            >
              <Heart className={`w-4 h-4 ${isFavorite ? 'fill-rose-500 text-rose-500' : ''}`} />
            </button>

            <button
              onClick={onClose}
              className="p-2 rounded-full bg-slate-900/70 hover:bg-slate-900 text-white backdrop-blur-md transition-colors"
              title="إغلاق"
            >
              <X className="w-4 h-4" />
            </button>
          </div>

          {/* Floating App Info Over Banner */}
          <div className="absolute bottom-4 left-4 right-4 sm:bottom-6 sm:left-6 sm:right-6 flex items-end gap-4 z-10">
            <div className="relative w-16 h-16 sm:w-20 sm:h-20 rounded-2xl overflow-hidden border-2 border-white/20 shadow-xl bg-slate-800 shrink-0">
              <img
                src={app.iconUrl}
                alt={app.title}
                referrerPolicy="no-referrer"
                className="w-full h-full object-cover"
              />
            </div>

            <div className="flex-1 min-w-0 text-white">
              <h2 className="text-lg sm:text-2xl font-black truncate">{app.title}</h2>
              <div className="flex items-center gap-2 text-xs text-slate-300 mt-0.5">
                <span>{app.developer.name}</span>
                {app.developer.verified && (
                  <span className="flex items-center gap-0.5 text-emerald-400 font-medium">
                    <ShieldCheck className="w-3.5 h-3.5" />
                    <span>{isArabic ? 'موثق' : 'Verified'}</span>
                  </span>
                )}
                <span aria-hidden="true">·</span>
                <span>{app.category}</span>
                {app.isUserUploaded && (
                  <>
                    <span aria-hidden="true">·</span>
                    <span className="text-amber-400 font-semibold">{isArabic ? 'مرفوع من المجتمع' : 'Community'}</span>
                  </>
                )}
              </div>
            </div>
          </div>
        </div>

        {/* Metrics Row (Strictly zero-pill unboxed text) */}
        <div className="grid grid-cols-4 gap-2 px-6 py-3.5 border-b border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950/50 text-center text-xs">
          <div>
            <div className="text-slate-500 dark:text-slate-400">{isArabic ? 'التقييم' : 'Rating'}</div>
            <div className="font-bold text-slate-900 dark:text-white flex items-center justify-center gap-1 font-mono mt-0.5">
              <span>{app.rating.toFixed(1)}</span>
              <Star className="w-3.5 h-3.5 fill-amber-400 text-amber-400" />
            </div>
          </div>
          <div>
            <div className="text-slate-500 dark:text-slate-400">{isArabic ? 'التنزيلات' : 'Downloads'}</div>
            <div className="font-bold text-slate-900 dark:text-white font-mono tabular-nums mt-0.5">
              {app.downloadCount.toLocaleString()}
            </div>
          </div>
          <div>
            <div className="text-slate-500 dark:text-slate-400">{isArabic ? 'الحجم' : 'Size'}</div>
            <div className="font-bold text-slate-900 dark:text-white font-mono tabular-nums mt-0.5">
              {app.sizeMb} MB
            </div>
          </div>
          <div>
            <div className="text-slate-500 dark:text-slate-400">{isArabic ? 'التصنيف العمري' : 'Age'}</div>
            <div className="font-bold text-slate-900 dark:text-white font-mono mt-0.5">
              {app.ageRating}
            </div>
          </div>
        </div>

        {/* Action Bar (Continuous purchase module) */}
        <div className="p-4 sm:p-6 border-b border-slate-200 dark:border-slate-800 flex flex-wrap items-center gap-3">
          {/* Main Download Button */}
          <button
            onClick={handleStartDownload}
            disabled={downloadProgress !== null && downloadProgress < 100}
            className={`flex-1 min-w-[200px] flex items-center justify-center gap-2 py-3 px-6 rounded-xl font-bold text-sm text-white transition-all cursor-pointer shadow-md ${
              downloadProgress === 100
                ? 'bg-emerald-600'
                : downloadProgress !== null
                ? 'bg-slate-700 cursor-wait'
                : 'bg-emerald-600 hover:bg-emerald-500 active:scale-98 shadow-emerald-600/20'
            }`}
          >
            {downloadProgress === null && (
              <>
                <Download className="w-4 h-4" />
                <span>
                  {app.price === 0
                    ? isArabic ? 'تحميل وتثبيت مجاناً (APK)' : 'Download Free (APK)'
                    : isArabic ? `شراء وتنزيل ($${app.price})` : `Buy & Download ($${app.price})`}
                </span>
              </>
            )}

            {downloadProgress !== null && downloadProgress < 100 && (
              <span className="font-mono tabular-nums">
                {isArabic ? `جاري التنزيل... ${downloadProgress}%` : `Downloading... ${downloadProgress}%`}
              </span>
            )}

            {downloadProgress === 100 && (
              <>
                <Check className="w-4 h-4" />
                <span>{isArabic ? 'تم التنزيل بنجاح!' : 'Download Complete!'}</span>
              </>
            )}
          </button>

          {/* Instant Browser Play Demo Button */}
          {app.hasPlayableDemo && (
            <button
              onClick={() => {
                playClickSound();
                onPlayDemo(app);
              }}
              className="flex items-center gap-2 py-3 px-5 rounded-xl font-bold text-sm text-slate-900 dark:text-white bg-slate-100 hover:bg-slate-200 dark:bg-slate-800 dark:hover:bg-slate-700 active:scale-98 border border-slate-300 dark:border-slate-700 transition-all cursor-pointer"
            >
              <Play className="w-4 h-4 text-emerald-500 fill-emerald-500" />
              <span>{isArabic ? 'تجربة فورية في المتصفح' : 'Instant Web Play'}</span>
            </button>
          )}
        </div>

        {/* Screenshots Carousel */}
        {app.screenshots && app.screenshots.length > 0 && (
          <div className="p-4 sm:p-6 border-b border-slate-200 dark:border-slate-800">
            <h4 className="text-xs font-bold text-slate-500 uppercase tracking-wider mb-3">
              {isArabic ? 'معاينة ولقطات الشاشة' : 'Screenshots Preview'}
            </h4>
            <div className="flex gap-3 overflow-x-auto pb-2">
              {app.screenshots.map((img, i) => (
                <button
                  key={i}
                  type="button"
                  onClick={() => setSelectedScreenshot(img)}
                  className="w-44 sm:w-56 aspect-[16/10] rounded-xl overflow-hidden border border-slate-200 dark:border-slate-700/80 shrink-0 hover:scale-102 transition-transform cursor-pointer"
                >
                  <img
                    src={img}
                    alt={`screenshot ${i}`}
                    referrerPolicy="no-referrer"
                    className="w-full h-full object-cover"
                  />
                </button>
              ))}
            </div>
          </div>
        )}

        {/* Content Tabs (Clean non-pill tabs) */}
        <div className="flex items-center gap-6 px-6 border-b border-slate-200 dark:border-slate-800 text-sm font-semibold text-slate-500">
          <button
            onClick={() => setActiveTab('about')}
            className={`py-3 border-b-2 transition-colors ${
              activeTab === 'about'
                ? 'border-emerald-500 text-slate-900 dark:text-white font-bold'
                : 'border-transparent hover:text-slate-800 dark:hover:text-slate-200'
            }`}
          >
            {isArabic ? 'عن التطبيق والمواصفات' : 'About & Specs'}
          </button>
          <button
            onClick={() => setActiveTab('reviews')}
            className={`py-3 border-b-2 transition-colors flex items-center gap-1.5 ${
              activeTab === 'reviews'
                ? 'border-emerald-500 text-slate-900 dark:text-white font-bold'
                : 'border-transparent hover:text-slate-800 dark:hover:text-slate-200'
            }`}
          >
            <span>{isArabic ? 'التقييمات والمراجعات' : 'Reviews'}</span>
            <span className="text-xs font-mono text-slate-400">({app.reviews?.length || 0})</span>
          </button>
        </div>

        {/* Tabs Content */}
        <div className="p-6 space-y-6 overflow-y-auto max-h-[40vh]">
          {activeTab === 'about' && (
            <div className="space-y-6">
              <div>
                <h4 className="text-xs font-bold text-slate-500 uppercase tracking-wider mb-2">
                  {isArabic ? 'الوصف' : 'Description'}
                </h4>
                <p className="text-sm text-slate-700 dark:text-slate-300 leading-relaxed whitespace-pre-line">
                  {app.description}
                </p>
              </div>

              {app.changelog && (
                <div className="p-4 rounded-xl bg-slate-50 dark:bg-slate-800/40 border border-slate-200 dark:border-slate-800 space-y-1">
                  <div className="text-xs font-bold text-slate-900 dark:text-white flex items-center gap-1.5">
                    <Sparkles className="w-3.5 h-3.5 text-emerald-500" />
                    <span>{isArabic ? `ما الجديد في الإصدار ${app.version}` : `What's New in v${app.version}`}</span>
                  </div>
                  <p className="text-xs text-slate-600 dark:text-slate-300">
                    {app.changelog}
                  </p>
                </div>
              )}

              {/* Technical Specifications Table */}
              <div>
                <h4 className="text-xs font-bold text-slate-500 uppercase tracking-wider mb-2">
                  {isArabic ? 'المعلومات التقنية' : 'Technical Specifications'}
                </h4>
                <div className="grid grid-cols-2 sm:grid-cols-3 gap-3 text-xs">
                  <div className="p-2.5 rounded-lg border border-slate-200 dark:border-slate-800">
                    <div className="text-slate-400">{isArabic ? 'الإصدار الحالي' : 'Version'}</div>
                    <div className="font-semibold text-slate-800 dark:text-slate-200 font-mono mt-0.5">{app.version}</div>
                  </div>
                  <div className="p-2.5 rounded-lg border border-slate-200 dark:border-slate-800">
                    <div className="text-slate-400">{isArabic ? 'تاريخ التحديث' : 'Updated'}</div>
                    <div className="font-semibold text-slate-800 dark:text-slate-200 mt-0.5">{app.updatedDate}</div>
                  </div>
                  <div className="p-2.5 rounded-lg border border-slate-200 dark:border-slate-800">
                    <div className="text-slate-400">{isArabic ? 'الحد الأدنى للنظام' : 'Min OS'}</div>
                    <div className="font-semibold text-slate-800 dark:text-slate-200 mt-0.5">{app.minAndroid || 'Android 8.0+'}</div>
                  </div>
                  <div className="p-2.5 rounded-lg border border-slate-200 dark:border-slate-800">
                    <div className="text-slate-400">{isArabic ? 'المنصات المدعومة' : 'Platforms'}</div>
                    <div className="font-semibold text-slate-800 dark:text-slate-200 mt-0.5 uppercase">
                      {app.platforms.join(' / ')}
                    </div>
                  </div>
                  <div className="p-2.5 rounded-lg border border-slate-200 dark:border-slate-800">
                    <div className="text-slate-400">{isArabic ? 'اسم الحزمة' : 'Package File'}</div>
                    <div className="font-semibold text-slate-800 dark:text-slate-200 font-mono truncate mt-0.5">
                      {app.packageFileName || 'app-release.apk'}
                    </div>
                  </div>
                  <div className="p-2.5 rounded-lg border border-slate-200 dark:border-slate-800">
                    <div className="text-slate-400">{isArabic ? 'فحص الأمان' : 'Security Scan'}</div>
                    <div className="font-semibold text-emerald-500 mt-0.5 flex items-center gap-1">
                      <ShieldCheck className="w-3.5 h-3.5" />
                      <span>{isArabic ? 'سليم 100%' : 'Clean & Safe'}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          )}

          {activeTab === 'reviews' && (
            <div className="space-y-6">
              {/* Add review form */}
              <form onSubmit={handleSubmitReview} className="p-4 rounded-xl bg-slate-50 dark:bg-slate-800/40 border border-slate-200 dark:border-slate-800 space-y-3">
                <div className="text-xs font-bold text-slate-900 dark:text-white">
                  {isArabic ? 'أضف تقييمك ورأيك حول هذا التطبيق' : 'Write a Review'}
                </div>

                <div className="flex flex-wrap items-center gap-3">
                  <div className="flex items-center gap-1">
                    {[1, 2, 3, 4, 5].map((star) => (
                      <button
                        key={star}
                        type="button"
                        onClick={() => setRatingStars(star)}
                        className="p-1 hover:scale-110 transition-transform cursor-pointer"
                      >
                        <Star
                          className={`w-5 h-5 ${
                            star <= ratingStars
                              ? 'fill-amber-400 text-amber-400'
                              : 'text-slate-300 dark:text-slate-600'
                          }`}
                        />
                      </button>
                    ))}
                  </div>

                  <input
                    type="text"
                    value={reviewerName}
                    onChange={(e) => setReviewerName(e.target.value)}
                    placeholder={isArabic ? 'اسمك الكريم...' : 'Your Name...'}
                    className="flex-1 min-w-[140px] px-3 py-1.5 text-xs rounded-lg border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-900 text-slate-900 dark:text-white"
                  />
                </div>

                <div className="flex items-center gap-2">
                  <input
                    type="text"
                    value={reviewComment}
                    onChange={(e) => setReviewComment(e.target.value)}
                    placeholder={isArabic ? 'شارك تجربتك مع المستخدمين الآخرين...' : 'Share your honest feedback...'}
                    className="flex-1 px-3 py-2 text-xs rounded-lg border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-900 text-slate-900 dark:text-white"
                  />

                  <button
                    type="submit"
                    className="flex items-center gap-1.5 px-4 py-2 bg-emerald-600 hover:bg-emerald-500 text-white rounded-lg text-xs font-bold transition-colors cursor-pointer"
                  >
                    <Send className="w-3.5 h-3.5" />
                    <span>{isArabic ? 'إرسال' : 'Post'}</span>
                  </button>
                </div>

                {reviewSubmitted && (
                  <p className="text-xs text-emerald-500 font-medium">
                    {isArabic ? '✓ شكراً لك، تم نشر تقييمك وتحديث معدل التقييم!' : '✓ Review submitted successfully!'}
                  </p>
                )}
              </form>

              {/* Reviews List */}
              <div className="space-y-3">
                {app.reviews && app.reviews.length > 0 ? (
                  app.reviews.map((rev) => (
                    <div
                      key={rev.id}
                      className="p-3.5 rounded-xl border border-slate-200 dark:border-slate-800 space-y-1.5"
                    >
                      <div className="flex items-center justify-between">
                        <div className="flex items-center gap-2">
                          <span className="text-xs font-bold text-slate-900 dark:text-white">
                            {rev.userName}
                          </span>
                          <span className="text-[11px] text-slate-400">· {rev.date}</span>
                        </div>
                        <div className="flex items-center gap-0.5">
                          {Array.from({ length: 5 }).map((_, i) => (
                            <Star
                              key={i}
                              className={`w-3 h-3 ${
                                i < rev.rating
                                  ? 'fill-amber-400 text-amber-400'
                                  : 'text-slate-300 dark:text-slate-700'
                              }`}
                            />
                          ))}
                        </div>
                      </div>
                      <p className="text-xs text-slate-600 dark:text-slate-300">
                        {rev.comment}
                      </p>
                    </div>
                  ))
                ) : (
                  <p className="text-xs text-slate-400 text-center py-4">
                    {isArabic ? 'لا توجد تقييمات بعد، كن أول من يقيّم هذا العمل!' : 'No reviews yet. Be the first to review!'}
                  </p>
                )}
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Lightbox for screenshots */}
      {selectedScreenshot && (
        <div
          onClick={() => setSelectedScreenshot(null)}
          className="fixed inset-0 z-60 bg-black/90 flex items-center justify-center p-4 cursor-zoom-out"
        >
          <img
            src={selectedScreenshot}
            alt="Expanded Screenshot"
            className="max-w-4xl max-h-[90vh] rounded-xl object-contain shadow-2xl"
          />
        </div>
      )}
    </div>
  );
};
