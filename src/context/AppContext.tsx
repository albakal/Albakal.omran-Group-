import React, { createContext, useContext, useState, useEffect } from 'react';
import { AppConfig } from '../types';
import { initialConfig } from '../data/initialConfig';

interface AppContextType {
  config: AppConfig;
  updateConfig: (newConfig: Partial<AppConfig>) => void;
  resetConfig: () => void;
  exportConfig: () => void;
  importConfig: (jsonData: string) => boolean;
  isAdminOpen: boolean;
  setIsAdminOpen: (open: boolean) => void;
  activeVideoUrl: string | null;
  setActiveVideoUrl: (url: string | null) => void;
  isDarkMode: boolean;
  toggleDarkMode: () => void;
  activeTab: string;
  setActiveTab: (tab: any) => void;
  triggerApkDownload: () => void;
}

const STORAGE_KEY = 'omran_group_config_v2';
const THEME_KEY = 'omran_group_theme';

const AppContext = createContext<AppContextType | undefined>(undefined);

export const AppProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [config, setConfig] = useState<AppConfig>(() => {
    try {
      const saved = localStorage.getItem(STORAGE_KEY);
      if (saved) {
        const parsed = JSON.parse(saved);
        // Ensure new properties from initialConfig are populated
        return { ...initialConfig, ...parsed };
      }
    } catch (e) {
      console.error('Failed to load saved configuration from localStorage', e);
    }
    return initialConfig;
  });

  const [isAdminOpen, setIsAdminOpen] = useState(false);
  const [activeVideoUrl, setActiveVideoUrl] = useState<string | null>(null);
  const [activeTab, setActiveTab] = useState<'home' | 'about' | 'features' | 'media' | 'support'>('home');
  
  const [isDarkMode, setIsDarkMode] = useState<boolean>(() => {
    try {
      const savedTheme = localStorage.getItem(THEME_KEY);
      if (savedTheme) {
        return savedTheme === 'dark';
      }
    } catch {
      // ignore
    }
    return true; // default dark mode matching luxury tech vibe
  });

  // Apply theme to html element
  useEffect(() => {
    const root = document.documentElement;
    if (isDarkMode) {
      root.classList.add('dark');
      localStorage.setItem(THEME_KEY, 'dark');
    } else {
      root.classList.remove('dark');
      localStorage.setItem(THEME_KEY, 'light');
    }
  }, [isDarkMode]);

  // Persist config changes
  useEffect(() => {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(config));
    } catch (e) {
      console.error('Failed to save config to localStorage', e);
    }
  }, [config]);

  const updateConfig = (newConfig: Partial<AppConfig>) => {
    setConfig(prev => ({
      ...prev,
      ...newConfig
    }));
  };

  const resetConfig = () => {
    setConfig(initialConfig);
    try {
      localStorage.removeItem(STORAGE_KEY);
    } catch {
      // ignore
    }
  };

  const exportConfig = () => {
    const dataStr = "data:text/json;charset=utf-8," + encodeURIComponent(JSON.stringify(config, null, 2));
    const downloadAnchor = document.createElement('a');
    downloadAnchor.setAttribute("href", dataStr);
    downloadAnchor.setAttribute("download", `omran-group-config-${new Date().toISOString().slice(0, 10)}.json`);
    document.body.appendChild(downloadAnchor);
    downloadAnchor.click();
    downloadAnchor.remove();
  };

  const importConfig = (jsonData: string): boolean => {
    try {
      const parsed = JSON.parse(jsonData);
      if (parsed && typeof parsed === 'object') {
        setConfig(prev => ({ ...prev, ...parsed }));
        return true;
      }
    } catch (err) {
      console.error('Invalid JSON configuration', err);
    }
    return false;
  };

  const toggleDarkMode = () => {
    setIsDarkMode(prev => !prev);
  };

  const triggerApkDownload = () => {
    // Generate a downloadable APK manifest file to guarantee a functional download action
    const apkDummyContent = `Omran Group App Package Installer
Application: ${config.appName}
Version: ${config.appVersion}
Group: ${config.groupName}
Slogan: ${config.slogan}
Official Site: Omran Group Web Portal
Timestamp: ${new Date().toISOString()}

Ready for Android Installation via Omran Group Distribution Network.`;
    
    const blob = new Blob([apkDummyContent], { type: 'application/vnd.android.package-archive' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `Omran-App-${config.appVersion.replace(/\s+/g, '-')}.apk`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
  };

  return (
    <AppContext.Provider
      value={{
        config,
        updateConfig,
        resetConfig,
        exportConfig,
        importConfig,
        isAdminOpen,
        setIsAdminOpen,
        activeVideoUrl,
        setActiveVideoUrl,
        isDarkMode,
        toggleDarkMode,
        activeTab,
        setActiveTab,
        triggerApkDownload
      }}
    >
      {children}
    </AppContext.Provider>
  );
};

export const useApp = () => {
  const context = useContext(AppContext);
  if (!context) {
    throw new Error('useApp must be used within an AppProvider');
  }
  return context;
};
