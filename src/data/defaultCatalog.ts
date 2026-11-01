import { AppItem, AppCategory } from '../types/store';
import heroBanner from '../assets/images/store_hero_banner_1793558641758.jpg';
import spaceGame from '../assets/images/game_space_odyssey_1793558641908.jpg';
import productivityApp from '../assets/images/app_productivity_hub_1793558643035.jpg';
import desertGame from '../assets/images/game_desert_drifter_1793558642874.jpg';

export const HERO_BANNER_IMAGE = heroBanner;
export const GAME_SPACE_IMAGE = spaceGame;
export const APP_PRODUCTIVITY_IMAGE = productivityApp;
export const GAME_DESERT_IMAGE = desertGame;

export const DEFAULT_CATALOG: AppItem[] = [
  {
    id: 'game-space-odyssey',
    title: 'حرب النجوم: الصائد الكريستالي',
    titleEn: 'Stardust Stealth',
    tagline: 'مغامرة فضاء ملحمية ومطاردة الأجرام الكريستالية في أرجاء المجرة',
    description: 'انطلق بمركبتك الفضائية المتطورة عبر حقول الكويكبات الكريستالية اللامعة. تفادى الصخور المتحركة، واجمع بلورات الطاقة لترقية دروعك وأسلحتك، وواجه زعماء الفضاء في معارك شيقة وسريعة الوتيرة. تتميز اللعبة برسومات ثنائية الأبعاد عصرية وتحكم فائق الدقة.',
    type: 'game',
    category: 'Arcade',
    developer: {
      name: 'استوديو نيون',
      verified: true,
      avatar: '🚀'
    },
    iconUrl: GAME_SPACE_IMAGE,
    coverUrl: GAME_SPACE_IMAGE,
    screenshots: [
      GAME_SPACE_IMAGE,
      HERO_BANNER_IMAGE
    ],
    rating: 5.0,
    ratingCount: 0,
    downloadCount: 0,
    sizeMb: 42.5,
    version: '1.0.0',
    minAndroid: 'Android 8.0+',
    platforms: ['android', 'web', 'windows'],
    releaseDate: '2026-11-01',
    updatedDate: '2026-11-01',
    changelog: 'الإصدار الأولي المعتمد في متجر Play.',
    price: 0,
    ageRating: '7+',
    downloadUrl: '#download-space-odyssey',
    packageFileName: 'stardust_stealth.apk',
    hasPlayableDemo: true,
    playableGameType: 'space-shooter',
    reviews: []
  },
  {
    id: 'game-desert-rally',
    title: 'رالي الصحراء: دريفت الكثبان',
    titleEn: 'Desert Fury Rally',
    tagline: 'سباق سيارات الباغي الوعرة فوق رمال الصحراء الذهبية بسرعة خارقة',
    description: 'تحدى قسوة التضاريس الرملية وقم بقيادة أقوى مركبات الدفع الرباعي والباغي عبر مسارات صحراوية متعرجة عند غروب الشمس. انزلق برشاقة حول المنعطفات، وفعل التوربو لتجاوز المنافسين وحقق أفضل توقيت على لوحة المتصدرين.',
    type: 'game',
    category: 'Racing',
    developer: {
      name: 'توربو استوديو',
      verified: true,
      avatar: '🏎️'
    },
    iconUrl: GAME_DESERT_IMAGE,
    coverUrl: GAME_DESERT_IMAGE,
    screenshots: [
      GAME_DESERT_IMAGE,
      HERO_BANNER_IMAGE
    ],
    rating: 5.0,
    ratingCount: 0,
    downloadCount: 0,
    sizeMb: 58.2,
    version: '1.0.0',
    minAndroid: 'Android 9.0+',
    platforms: ['android', 'web'],
    releaseDate: '2026-11-01',
    updatedDate: '2026-11-01',
    changelog: 'الإصدار الأولي في متجر Play.',
    price: 0,
    ageRating: '3+',
    downloadUrl: '#download-desert-fury',
    packageFileName: 'desert_fury_rally.apk',
    hasPlayableDemo: true,
    playableGameType: 'desert-rally',
    reviews: []
  },
  {
    id: 'app-productivity-hub',
    title: 'منظم المهام والتركيز اليومي',
    titleEn: 'Focus & Calm Hub',
    tagline: 'تطبيق لإدارة المهام وتتبع عادات الإنجاز مع تقنية البومودورو وجلسات الهدوء',
    description: 'أداة احترافية خفيفة تساعدك على تنظيم يومك بذكاء. يجمع التطبيق بين جدول المهام اليومي، وتقنية مؤقت التركيز (Pomodoro)، مع إحصائيات بصرية دقيقة لمعدل إنتاجيتك بدون أي إعلانات مزعجة وبحماية كاملة لخصوصيتك.',
    type: 'app',
    category: 'Productivity',
    developer: {
      name: 'فريق الإتقان',
      verified: true,
      avatar: '⚡'
    },
    iconUrl: APP_PRODUCTIVITY_IMAGE,
    coverUrl: APP_PRODUCTIVITY_IMAGE,
    screenshots: [
      APP_PRODUCTIVITY_IMAGE
    ],
    rating: 5.0,
    ratingCount: 0,
    downloadCount: 0,
    sizeMb: 18.4,
    version: '1.0.0',
    minAndroid: 'Android 7.0+',
    platforms: ['android', 'web', 'windows'],
    releaseDate: '2026-11-01',
    updatedDate: '2026-11-01',
    changelog: 'الإصدار الأولي في متجر Play.',
    price: 0,
    ageRating: '3+',
    downloadUrl: '#download-focus-hub',
    packageFileName: 'focus_calm_hub.apk',
    hasPlayableDemo: true,
    playableGameType: 'code-runner',
    reviews: []
  },
  {
    id: 'app-smart-tasbeeh',
    title: 'المسبحة الرقمية الذكية والأذكار',
    titleEn: 'Smart Tasbeeh Pro',
    tagline: 'عداد أذكار وتسبيح ذكي مع اهتزاز لمسي، وسجل يومي، ومجموعة أذكار مأثورة',
    description: 'تطبيق مسبحة رقمية بتصميم هادئ ومريح للعين. يدعم التسبيح باللمس في أي مكان على الشاشة مع اهتزاز خفيف وتنبيهات صوتية هادئة عند إتمام كل دورة (33 أو 100). يشمل أذكار الصباح والمساء والاستغفار وحفظ الورد اليومي تلقائياً.',
    type: 'app',
    category: 'Utility',
    developer: {
      name: 'نور للتطبيقات',
      verified: true,
      avatar: '📿'
    },
    iconUrl: 'https://images.unsplash.com/photo-1591604129939-f1efa4d9f7fa?w=300&auto=format&fit=crop&q=80',
    coverUrl: 'https://images.unsplash.com/photo-1591604129939-f1efa4d9f7fa?w=900&auto=format&fit=crop&q=80',
    screenshots: [
      'https://images.unsplash.com/photo-1591604129939-f1efa4d9f7fa?w=600&auto=format&fit=crop&q=80'
    ],
    rating: 5.0,
    ratingCount: 0,
    downloadCount: 0,
    sizeMb: 8.6,
    version: '1.0.0',
    minAndroid: 'Android 6.0+',
    platforms: ['android', 'web'],
    releaseDate: '2026-11-01',
    updatedDate: '2026-11-01',
    changelog: 'الإصدار الأولي في متجر Play.',
    price: 0,
    ageRating: '3+',
    downloadUrl: '#download-smart-tasbeeh',
    packageFileName: 'smart_tasbeeh_pro.apk',
    hasPlayableDemo: true,
    playableGameType: 'smart-tasbeeh',
    reviews: []
  },
  {
    id: 'app-quick-code',
    title: 'محرر الأكواد السريع ومختبر الويب',
    titleEn: 'QuickCode Sandbox',
    tagline: 'محرر ومترجم فوري لأكواد HTML و CSS و JavaScript بدون إنترنت',
    description: 'بيئة تطوير برمجية مصغرة وسريعة لتجربة وتجريب الأفكار والأكواد البرمجية مباشرة على هاتفك أو حاسوبك. يدعم تمييز الصياغة، والمعاينة الحية الفورية للنتائج بنقرة واحدة، وتصدير المشاريع كملفات جاهزة.',
    type: 'app',
    category: 'Tools',
    developer: {
      name: 'كودكرافت',
      verified: true,
      avatar: '💻'
    },
    iconUrl: 'https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=300&auto=format&fit=crop&q=80',
    coverUrl: 'https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=900&auto=format&fit=crop&q=80',
    screenshots: [
      'https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=600&auto=format&fit=crop&q=80'
    ],
    rating: 5.0,
    ratingCount: 0,
    downloadCount: 0,
    sizeMb: 14.2,
    version: '1.0.0',
    minAndroid: 'Android 8.0+',
    platforms: ['android', 'web', 'windows'],
    releaseDate: '2026-11-01',
    updatedDate: '2026-11-01',
    changelog: 'الإصدار الأولي في متجر Play.',
    price: 0,
    ageRating: '3+',
    downloadUrl: '#download-quickcode',
    packageFileName: 'quickcode_sandbox.apk',
    hasPlayableDemo: true,
    playableGameType: 'code-runner',
    reviews: []
  }
];

export const CATEGORIES_LIST: { id: AppCategory | 'All'; labelAr: string; labelEn: string; icon: string }[] = [
  { id: 'All', labelAr: 'جميع التصنيفات', labelEn: 'All Categories', icon: '✨' },
  { id: 'Arcade', labelAr: 'أركيد', labelEn: 'Arcade', icon: '👾' },
  { id: 'Racing', labelAr: 'سباقات', labelEn: 'Racing', icon: '🏎️' },
  { id: 'Productivity', labelAr: 'إنتاجية', labelEn: 'Productivity', icon: '⚡' },
  { id: 'Tools', labelAr: 'أدوات ومطورين', labelEn: 'Tools', icon: '🛠️' },
  { id: 'Utility', labelAr: 'خدمات عامة', labelEn: 'Utilities', icon: '📱' },
  { id: 'Puzzle', labelAr: 'ألغاز', labelEn: 'Puzzles', icon: '🧩' },
  { id: 'Action', labelAr: 'أكشن', labelEn: 'Action', icon: '⚔️' },
  { id: 'Education', labelAr: 'تعليم', labelEn: 'Education', icon: '🎓' },
];
