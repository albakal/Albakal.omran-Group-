/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React from 'react';
import { AppProvider, useApp } from './context/AppContext';
import { Navbar } from './components/Navbar';
import { HeroSection } from './components/HeroSection';
import { FeaturesSection } from './components/FeaturesSection';
import { AboutSection } from './components/AboutSection';
import { MediaSection } from './components/MediaSection';
import { SupportSection } from './components/SupportSection';
import { Footer } from './components/Footer';
import { FloatingWhatsApp } from './components/FloatingWhatsApp';
import { SmartAIChat } from './components/SmartAIChat';
import { AdminPanel } from './components/AdminPanel';
import { VideoModal } from './components/VideoModal';

const MainContent: React.FC = () => {
  const { config, setActiveVideoUrl } = useApp();

  const handleNavigate = (sectionId: string) => {
    const el = document.getElementById(sectionId);
    if (el) {
      el.scrollIntoView({ behavior: 'smooth' });
    }
  };

  return (
    <div className="min-h-screen flex flex-col bg-white dark:bg-slate-950 text-slate-900 dark:text-slate-100 transition-colors">
      {/* Top Bar Navigation */}
      <Navbar onNavigate={handleNavigate} />

      {/* Main Content Sections */}
      <main className="flex-1">
        {/* Hero & Marketing Section with Direct Download & Promo Banner */}
        <HeroSection 
          onWatchVideo={() => setActiveVideoUrl(config.mainVideoUrl)} 
          onExploreFeatures={() => handleNavigate('features')}
        />

        {/* Features & Economic Benefits for Merchants and Customers */}
        <FeaturesSection />

        {/* About Omran Group, Activities & Founder Profile */}
        <AboutSection />

        {/* Media Center & Explainer Videos */}
        <MediaSection onOpenVideo={(url) => setActiveVideoUrl(url)} />

        {/* Customer Support Hub & FAQs */}
        <SupportSection />
      </main>

      {/* Quiet Footer */}
      <Footer onNavigate={handleNavigate} />

      {/* Persistent Floating WhatsApp Quick-Contact Button (Available on all pages) */}
      <FloatingWhatsApp />

      {/* Persistent Smart AI Support Chat Assistant */}
      <SmartAIChat />

      {/* Admin Dashboard Modal */}
      <AdminPanel />

      {/* Explainer Video Player Modal */}
      <VideoModal />
    </div>
  );
};

export default function App() {
  return (
    <AppProvider>
      <MainContent />
    </AppProvider>
  );
}
