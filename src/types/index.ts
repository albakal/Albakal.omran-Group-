export interface StatItem {
  id: string;
  label: string;
  value: string;
  subtext: string;
  iconName: string;
}

export interface FeatureItem {
  id: string;
  title: string;
  description: string;
  iconName: string;
  highlight?: string;
}

export interface VideoTutorial {
  id: string;
  title: string;
  description: string;
  duration: string;
  videoUrl: string;
  thumbnailUrl?: string;
  category: 'merchants' | 'customers' | 'overview';
}

export interface FaqItem {
  id: string;
  question: string;
  answer: string;
  category: string;
}

export interface AppConfig {
  // General & Branding
  appName: string;
  groupName: string;
  slogan: string;
  logoUrl: string;
  primaryColor: string;
  accentColor: string;
  
  // Floating WhatsApp & Support
  whatsappNumber: string;
  whatsappMessage: string;
  whatsappEnabled: boolean;
  phoneSupport: string;
  emailSupport: string;
  headquartersAddress: string;
  workingHours: string;

  // Download Links
  googlePlayUrl: string;
  appStoreUrl: string;
  apkDownloadUrl: string;
  appVersion: string;
  appSize: string;
  releaseDate: string;

  // Hero Section
  heroBadge: string;
  heroTitle: string;
  heroHighlight: string;
  heroDescription: string;
  appMockupImage: string;
  promoBannerImage: string;

  // Statistics
  stats: StatItem[];

  // Features for Traders & Customers
  traderFeaturesTitle: string;
  traderFeaturesDesc: string;
  traderFeatures: FeatureItem[];
  
  customerFeaturesTitle: string;
  customerFeaturesDesc: string;
  customerFeatures: FeatureItem[];

  // About Company & Founder
  aboutTitle: string;
  aboutStory: string;
  visionText: string;
  missionText: string;
  headquartersImage: string;

  founderName: string;
  founderTitle: string;
  founderBio: string;
  founderQuote: string;
  founderImage: string;

  // Media & Videos
  mainVideoTitle: string;
  mainVideoDescription: string;
  mainVideoUrl: string;
  tutorials: VideoTutorial[];

  // FAQs
  faqs: FaqItem[];
}
