/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React, { useState, useEffect, useMemo } from 'react';
import { AppItem, StoreTab, AppType, AppCategory, SortOption, AppReview } from './types/store';
import { UserProfile, getStoredUser, saveStoredUser, DEFAULT_USER_EMAIL } from './types/auth';
import {
  getStoredCatalog,
  addAppToCatalog,
  deleteAppFromCatalog,
  incrementDownloadCount,
  addReviewToApp,
  getFavorites,
  toggleFavorite,
  exportCatalogJson
} from './utils/storage';
import { Navbar } from './components/Navbar';
import { PlayLogo } from './components/PlayLogo';
import { HeroSpotlight } from './components/HeroSpotlight';
import { FilterBar } from './components/FilterBar';
import { AppCard } from './components/AppCard';
import { UploadModal } from './components/UploadModal';
import { PlayableModal } from './components/PlayableModal';
import { AppDetailModal } from './components/AppDetailModal';
import { DeveloperDashboard } from './components/DeveloperDashboard';
import { GoogleSignInModal } from './components/GoogleSignInModal';
import { playClickSound, playSuccessSound } from './utils/audio';
import { Sparkles, ArrowUp, ShieldCheck } from 'lucide-react';

export default function App() {
  const [catalog, setCatalog] = useState<AppItem[]>([]);
  const [currentTab, setCurrentTab] = useState<StoreTab>('all');
  const [selectedType, setSelectedType] = useState<AppType | 'all'>('all');
  const [selectedCategory, setSelectedCategory] = useState<AppCategory | 'All'>('All');
  const [searchQuery, setSearchQuery] = useState('');
  const [sortOption, setSortOption] = useState<SortOption>('popular');
  const [favorites, setFavorites] = useState<string[]>([]);

  // Authentication State
  const [currentUser, setCurrentUser] = useState<UserProfile | null>(null);
  const [isSignInOpen, setIsSignInOpen] = useState(false);

  // Modals state
  const [isUploadOpen, setIsUploadOpen] = useState(false);
  const [activeDetailApp, setActiveDetailApp] = useState<AppItem | null>(null);
  const [activePlayableApp, setActivePlayableApp] = useState<AppItem | null>(null);

  // Appearance & Localization
  const [isDark, setIsDark] = useState(true);
  const [isArabic, setIsArabic] = useState(true);
  const [toastMessage, setToastMessage] = useState<string | null>(null);

  // Initialize data on mount
  useEffect(() => {
    setCatalog(getStoredCatalog());
    setFavorites(getFavorites());
    const stored = getStoredUser();
    if (stored) {
      setCurrentUser(stored);
    } else {
      // Default initial session with user's Gmail
      const defaultUser: UserProfile = {
        name: 'محمد للتطوير',
        email: DEFAULT_USER_EMAIL,
        isVerifiedDeveloper: true,
        joinedDate: '2026-11-01'
      };
      setCurrentUser(defaultUser);
      saveStoredUser(defaultUser);
    }
  }, []);

  // Sync theme to DOM
  useEffect(() => {
    if (isDark) {
      document.documentElement.classList.add('dark');
      document.body.classList.add('dark');
      document.body.classList.remove('bg-slate-50', 'text-slate-900');
      document.body.classList.add('bg-slate-950', 'text-slate-100');
    } else {
      document.documentElement.classList.remove('dark');
      document.body.classList.remove('dark');
      document.body.classList.remove('bg-slate-950', 'text-slate-100');
      document.body.classList.add('bg-slate-50', 'text-slate-900');
    }
  }, [isDark]);

  // Sync language direction to DOM
  useEffect(() => {
    document.documentElement.dir = isArabic ? 'rtl' : 'ltr';
    document.documentElement.lang = isArabic ? 'ar' : 'en';
  }, [isArabic]);

  const showToast = (msg: string) => {
    setToastMessage(msg);
    setTimeout(() => setToastMessage(null), 3000);
  };

  const handleToggleTheme = () => {
    playClickSound();
    setIsDark(!isDark);
  };

  const handleToggleLanguage = () => {
    playClickSound();
    setIsArabic(!isArabic);
  };

  const handleTabChange = (tab: StoreTab) => {
    setCurrentTab(tab);
    if (tab === 'apps') {
      setSelectedType('app');
    } else if (tab === 'games') {
      setSelectedType('game');
    } else if (tab === 'all') {
      setSelectedType('all');
    }
  };

  const handleSignInSuccess = (user: UserProfile) => {
    setCurrentUser(user);
    saveStoredUser(user);
    setIsSignInOpen(false);
    showToast(isArabic ? `تم تسجيل الدخول بنجاح بحساب ${user.email} ✓` : `Signed in as ${user.email} ✓`);
    playSuccessSound();
  };

  const handleSignOut = () => {
    setCurrentUser(null);
    saveStoredUser(null);
    showToast(isArabic ? 'تم تسجيل الخروج' : 'Signed out');
  };

  // When user clicks Upload, ensure they are signed in with Google
  const handleRequestUpload = () => {
    playClickSound();
    if (!currentUser) {
      showToast(isArabic ? 'يرجى تسجيل الدخول بحساب Google (Gmail) لتتمكن من رفع التطبيقات' : 'Please sign in with Google to upload apps');
      setIsSignInOpen(true);
      return;
    }
    setIsUploadOpen(true);
  };

  const handleToggleFavorite = (id: string) => {
    const updated = toggleFavorite(id);
    setFavorites(updated);
    showToast(
      updated.includes(id)
        ? (isArabic ? 'تمت الإضافة للمفضلة ❤️' : 'Added to favorites ❤️')
        : (isArabic ? 'تمت الإزالة من المفضلة' : 'Removed from favorites')
    );
  };

  const handleAppCreated = (newApp: AppItem) => {
    const updated = addAppToCatalog(newApp);
    setCatalog(updated);
    setIsUploadOpen(false);
    showToast(isArabic ? `تم نشر "${newApp.title}" في متجر Play بنجاح! 🎉` : `Published "${newApp.title}" to Play Store! 🎉`);
    
    // Switch to My Apps tab to showcase the new upload
    setCurrentTab('my-apps');
  };

  const handleDeleteApp = (id: string) => {
    const updated = deleteAppFromCatalog(id);
    setCatalog(updated);
    if (activeDetailApp?.id === id) setActiveDetailApp(null);
    showToast(isArabic ? 'تم حذف التطبيق من متجر Play' : 'App removed from Play Store');
  };

  const handleDownloadApp = (app: AppItem) => {
    const updated = incrementDownloadCount(app.id);
    setCatalog(updated);
    showToast(isArabic ? `بدأ تحميل حزمة "${app.title}"` : `Downloading "${app.title}" package`);
  };

  const handleAddReview = (appId: string, review: AppReview) => {
    const updated = addReviewToApp(appId, review);
    setCatalog(updated);
    if (activeDetailApp?.id === appId) {
      const refreshed = updated.find(a => a.id === appId);
      if (refreshed) setActiveDetailApp({ ...refreshed });
    }
    showToast(isArabic ? 'شكراً لتقييمك!' : 'Thank you for your review!');
  };

  // Filtered & Sorted items
  const filteredCatalog = useMemo(() => {
    let result = [...catalog];

    // Tab filter
    if (currentTab === 'favorites') {
      result = result.filter(item => favorites.includes(item.id));
    } else if (currentTab === 'my-apps') {
      result = result.filter(item => item.isUserUploaded);
    } else if (currentTab === 'apps') {
      result = result.filter(item => item.type === 'app');
    } else if (currentTab === 'games') {
      result = result.filter(item => item.type === 'game');
    }

    // Type filter from secondary selector
    if (currentTab === 'all' && selectedType !== 'all') {
      result = result.filter(item => item.type === selectedType);
    }

    // Category filter
    if (selectedCategory !== 'All') {
      result = result.filter(item => item.category === selectedCategory);
    }

    // Search query filter
    if (searchQuery.trim() !== '') {
      const q = searchQuery.toLowerCase().trim();
      result = result.filter(item =>
        item.title.toLowerCase().includes(q) ||
        (item.titleEn && item.titleEn.toLowerCase().includes(q)) ||
        item.tagline.toLowerCase().includes(q) ||
        item.description.toLowerCase().includes(q) ||
        item.developer.name.toLowerCase().includes(q) ||
        item.category.toLowerCase().includes(q)
      );
    }

    // Sorting
    result.sort((a, b) => {
      if (sortOption === 'popular') return b.downloadCount - a.downloadCount;
      if (sortOption === 'rating') return b.rating - a.rating;
      if (sortOption === 'newest') return new Date(b.releaseDate).getTime() - new Date(a.releaseDate).getTime();
      if (sortOption === 'size') return a.sizeMb - b.sizeMb;
      return 0;
    });

    return result;
  }, [catalog, currentTab, selectedType, selectedCategory, searchQuery, sortOption, favorites]);

  // Featured spotlight is the first game with playable demo
  const spotlightItem = catalog.find(i => i.hasPlayableDemo && i.playableGameType === 'space-shooter') || catalog[0];

  const totalDownloads = useMemo(() => {
    return catalog.reduce((acc, item) => acc + item.downloadCount, 0);
  }, [catalog]);

  const userUploadedApps = useMemo(() => {
    return catalog.filter(i => i.isUserUploaded);
  }, [catalog]);

  return (
    <div className="min-h-screen flex flex-col bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100 transition-colors">
      
      {/* Top Bar Navigation */}
      <Navbar
        currentTab={currentTab}
        onSelectTab={handleTabChange}
        onOpenUpload={handleRequestUpload}
        favoritesCount={favorites.length}
        myAppsCount={userUploadedApps.length}
        isDark={isDark}
        onToggleTheme={handleToggleTheme}
        isArabic={isArabic}
        onToggleLanguage={handleToggleLanguage}
        currentUser={currentUser}
        onOpenSignIn={() => setIsSignInOpen(true)}
        onSignOut={handleSignOut}
      />

      {/* Main Container */}
      <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-6">
        
        {/* Render Hero Spotlight only on Home Tab */}
        {currentTab === 'all' && (
          <HeroSpotlight
            onPlaySpotlight={() => {
              if (spotlightItem) setActivePlayableApp(spotlightItem);
            }}
            onExploreClick={() => {
              const catalogElement = document.getElementById('catalog-view');
              catalogElement?.scrollIntoView({ behavior: 'smooth' });
            }}
            onOpenUpload={handleRequestUpload}
            isArabic={isArabic}
            totalAppsCount={catalog.length}
            totalDownloads={totalDownloads}
          />
        )}

        {/* View Router */}
        {currentTab === 'my-apps' ? (
          <DeveloperDashboard
            myApps={userUploadedApps}
            onOpenUpload={handleRequestUpload}
            onOpenDetails={(app) => setActiveDetailApp(app)}
            onDeleteApp={handleDeleteApp}
            onExportCatalog={() => exportCatalogJson(catalog)}
            isArabic={isArabic}
          />
        ) : (
          <div id="catalog-view" className="space-y-6">
            {/* Discovery & Filter Bar */}
            <FilterBar
              searchQuery={searchQuery}
              onSearchChange={setSearchQuery}
              selectedType={selectedType}
              onSelectType={setSelectedType}
              selectedCategory={selectedCategory}
              onSelectCategory={setSelectedCategory}
              sortOption={sortOption}
              onSortChange={setSortOption}
              totalFilteredCount={filteredCatalog.length}
              isArabic={isArabic}
            />

            {/* Grid of Apps & Games */}
            {filteredCatalog.length > 0 ? (
              <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
                {filteredCatalog.map((item) => (
                  <AppCard
                    key={item.id}
                    app={item}
                    onOpenDetails={(app) => setActiveDetailApp(app)}
                    onPlayDemo={(app) => setActivePlayableApp(app)}
                    isFavorite={favorites.includes(item.id)}
                    onToggleFavorite={handleToggleFavorite}
                    isArabic={isArabic}
                  />
                ))}
              </div>
            ) : (
              <div className="p-16 text-center rounded-2xl border border-dashed border-slate-300 dark:border-slate-800 bg-white/40 dark:bg-slate-900/40 space-y-4">
                <div className="w-12 h-12 mx-auto rounded-xl bg-slate-200 dark:bg-slate-800 text-slate-500 flex items-center justify-center">
                  <Sparkles className="w-6 h-6" />
                </div>
                <h3 className="text-sm font-bold text-slate-800 dark:text-slate-200">
                  {isArabic ? 'لم يتم العثور على نتائج مطابقة' : 'No items match your search'}
                </h3>
                <p className="text-xs text-slate-500 max-w-sm mx-auto">
                  {isArabic
                    ? 'جرب البحث بكلمات أخرى أو أزل الفلاتر النشطة، أو ارفع تطبيقك الآن!'
                    : 'Try different search keywords or clear filters, or upload your app!'}
                </p>
                <button
                  onClick={handleRequestUpload}
                  className="px-4 py-2 bg-emerald-600 hover:bg-emerald-500 text-white rounded-lg text-xs font-bold transition-colors cursor-pointer"
                >
                  {isArabic ? 'رفع تطبيق في متجر Play +' : 'Upload App to Play Store +'}
                </button>
              </div>
            )}
          </div>
        )}
      </main>

      {/* Footer */}
      <footer className="mt-16 border-t border-slate-200 dark:border-slate-800/80 bg-white dark:bg-slate-950 py-10 transition-colors">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex flex-col sm:flex-row items-center justify-between gap-4 text-xs text-slate-500">
          <div className="flex items-center gap-2.5">
            <PlayLogo size={20} />
            <span className="font-bold text-slate-900 dark:text-white">
              {isArabic ? 'متجر Play' : 'Play Store'}
            </span>
            <span aria-hidden="true">·</span>
            <span className="flex items-center gap-1 text-slate-400">
              <ShieldCheck className="w-3.5 h-3.5 text-emerald-500" />
              <span>Google Play Protect</span>
            </span>
            <span aria-hidden="true">·</span>
            <span>{isArabic ? 'منصة رفع وتوزيع التطبيقات والألعاب المعتمدة' : 'Official App & Game Distribution Platform'}</span>
          </div>

          <div className="flex items-center gap-4">
            <button
              onClick={handleRequestUpload}
              className="text-emerald-600 dark:text-emerald-400 font-semibold hover:underline"
            >
              {isArabic ? 'Play Console (رفع تطبيق)' : 'Play Console'}
            </button>
            <span aria-hidden="true">·</span>
            <button
              onClick={() => window.scrollTo({ top: 0, behavior: 'smooth' })}
              className="flex items-center gap-1 hover:text-slate-900 dark:hover:text-white transition-colors"
            >
              <span>{isArabic ? 'للأعلى' : 'Back to top'}</span>
              <ArrowUp className="w-3.5 h-3.5" />
            </button>
          </div>
        </div>
      </footer>

      {/* Google Sign In Modal */}
      {isSignInOpen && (
        <GoogleSignInModal
          onClose={() => setIsSignInOpen(false)}
          onSuccess={handleSignInSuccess}
          isArabic={isArabic}
        />
      )}

      {/* Upload Wizard Modal */}
      {isUploadOpen && currentUser && (
        <UploadModal
          currentUser={currentUser}
          onClose={() => setIsUploadOpen(false)}
          onAppCreated={handleAppCreated}
          isArabic={isArabic}
        />
      )}

      {/* App Details Modal (PDP) */}
      {activeDetailApp && (
        <AppDetailModal
          app={activeDetailApp}
          onClose={() => setActiveDetailApp(null)}
          onPlayDemo={(app) => {
            setActiveDetailApp(null);
            setActivePlayableApp(app);
          }}
          onDownload={handleDownloadApp}
          isFavorite={favorites.includes(activeDetailApp.id)}
          onToggleFavorite={handleToggleFavorite}
          onAddReview={handleAddReview}
          isArabic={isArabic}
        />
      )}

      {/* Playable Live Sandbox Modal */}
      {activePlayableApp && (
        <PlayableModal
          app={activePlayableApp}
          onClose={() => setActivePlayableApp(null)}
          isArabic={isArabic}
        />
      )}

      {/* Floating Toast Notification */}
      {toastMessage && (
        <div className="fixed bottom-6 right-6 z-60 px-4 py-2.5 bg-slate-900 text-white border border-slate-700/80 rounded-xl shadow-2xl text-xs font-semibold animate-in fade-in slide-in-from-bottom-3 duration-200">
          {toastMessage}
        </div>
      )}
    </div>
  );
}
