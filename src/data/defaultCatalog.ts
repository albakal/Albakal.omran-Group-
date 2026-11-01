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
    titleEn: 'Stardust Stealth: Crystal Odyssey',
    tagline: 'مغامرة فضاء ملحمية ومطاردة الأجرام الكريستالية في أرجاء المجرة',
    description: 'انطلق بمركبتك الفضائية المتطورة عبر حقول الكويكبات الكريستالية اللامعة. تفادى الصخور المتحركة، واجمع بلورات الطاقة لترقية دروعك وأسلحتك، وواجه زعماء الفضاء في معارك شيقة وسريعة الوتيرة. تتميز اللعبة برسومات ثنائية الأبعاد عصرية وتحكم فائق الدقة.',
    type: 'game',
    category: 'Arcade',
    developer: {
      name: 'استوديو نيون للألعاب',
      email: 'neon.games@example.com',
      verified: true,
      avatar: '🚀'
    },
    iconUrl: GAME_SPACE_IMAGE,
    coverUrl: GAME_SPACE_IMAGE,
    screenshots: [
      GAME_SPACE_IMAGE,
      HERO_BANNER_IMAGE
    ],
    rating: 4.9,
    ratingCount: 3840,
    downloadCount: 142500,
    sizeMb: 42.5,
    version: '2.4.1',
    minAndroid: 'Android 8.0+',
    platforms: ['android', 'web', 'windows'],
    releaseDate: '2026-01-15',
    updatedDate: '2026-10-20',
    changelog: 'إضافة مرحلة حزام الكويكبات الفوسفوري، وتحسين استجابة إطلاق الليزر ومؤثرات الصوت المحيطي.',
    price: 0,
    ageRating: '7+',
    downloadUrl: '#download-space-odyssey',
    packageFileName: 'stardust_stealth_v2.4.1.apk',
    hasPlayableDemo: true,
    playableGameType: 'space-shooter',
    reviews: [
      {
        id: 'rev-1',
        userName: 'طارق الزهراني',
        rating: 5,
        date: 'منذ يومين',
        comment: 'لعبة خرافية وسلسة جداً! الرائع أنني جربتها مباشرة في المتصفح قبل تحميل ملف الـ APK.',
        helpfulCount: 42
      },
      {
        id: 'rev-2',
        userName: 'سارة خالد',
        rating: 5,
        date: 'منذ 5 أيام',
        comment: 'تصميم المؤثرات البصرية مذهل والألوان الكريستالية ممتعة للعين.',
        helpfulCount: 19
      }
    ]
  },
  {
    id: 'game-desert-rally',
    title: 'رالي الصحراء: دريفت الكثبان',
    titleEn: 'Desert Fury: Dune Rally',
    tagline: 'سباق سيارات الباغي الوعرة فوق رمال الصحراء الذهبية بسرعة خارقة',
    description: 'تحدى قسوة التضاريس الرملية وقم بقيادة أقوى مركبات الدفع الرباعي والباغي عبر مسارات صحراوية متعرجة عند غروب الشمس. انزلق برشاقة حول المنعطفات، وفعل التوربو لتجاوز المنافسين وحقق أفضل توقيت على لوحة المتصدرين.',
    type: 'game',
    category: 'Racing',
    developer: {
      name: 'فريق توربو الصحراء',
      email: 'turbo.desert@example.com',
      verified: true,
      avatar: '🏎️'
    },
    iconUrl: GAME_DESERT_IMAGE,
    coverUrl: GAME_DESERT_IMAGE,
    screenshots: [
      GAME_DESERT_IMAGE,
      HERO_BANNER_IMAGE
    ],
    rating: 4.8,
    ratingCount: 2910,
    downloadCount: 98400,
    sizeMb: 58.2,
    version: '1.9.0',
    minAndroid: 'Android 9.0+',
    platforms: ['android', 'web'],
    releaseDate: '2026-03-02',
    updatedDate: '2026-09-18',
    changelog: 'تحسين فيزياء احتكاك الرمال، وإضافة سيارة باغي معدلة جديدة ونظام مكافآت الانزلاق.',
    price: 0,
    ageRating: '3+',
    downloadUrl: '#download-desert-fury',
    packageFileName: 'desert_fury_rally_v1.9.0.apk',
    hasPlayableDemo: true,
    playableGameType: 'desert-rally',
    reviews: [
      {
        id: 'rev-3',
        userName: 'فيصل العتيبي',
        rating: 5,
        date: 'منذ أسبوع',
        comment: 'الفيزياء ممتعة وسرعة الاستجابة ممتازة، وميزة التجربة الفورية في المتجر مريحة جداً.',
        helpfulCount: 31
      }
    ]
  },
  {
    id: 'app-productivity-hub',
    title: 'منظم المهام والتركيز اليومي',
    titleEn: 'Focus & Calm Work Hub',
    tagline: 'تطبيق لإدارة المهام وتتبع عادات الإنجاز مع تقنية البومودورو وجلسات الهدوء',
    description: 'أداة احترافية خفيفة تساعدك على تنظيم يومك بذكاء. يجمع التطبيق بين جدول المهام اليومي، وتقنية مؤقت التركيز (Pomodoro)، مع إحصائيات بصرية دقيقة لمعدل إنتاجيتك بدون أي إعلانات مزعجة وبحماية كاملة لخصوصيتك.',
    type: 'app',
    category: 'Productivity',
    developer: {
      name: 'معامل الإتقان البرمجية',
      email: 'itqan.labs@example.com',
      verified: true,
      avatar: '⚡'
    },
    iconUrl: APP_PRODUCTIVITY_IMAGE,
    coverUrl: APP_PRODUCTIVITY_IMAGE,
    screenshots: [
      APP_PRODUCTIVITY_IMAGE
    ],
    rating: 4.9,
    ratingCount: 5120,
    downloadCount: 210000,
    sizeMb: 18.4,
    version: '3.1.2',
    minAndroid: 'Android 7.0+',
    platforms: ['android', 'web', 'ios', 'windows'],
    releaseDate: '2025-11-10',
    updatedDate: '2026-10-28',
    changelog: 'دعم النسخ الاحتياطي المحلي، ومؤقت تركيز قابل للتخصيص وميزة المزامنة السريعة.',
    price: 0,
    ageRating: '3+',
    downloadUrl: '#download-focus-hub',
    packageFileName: 'focus_calm_hub_v3.1.2.apk',
    hasPlayableDemo: true,
    playableGameType: 'code-runner',
    reviews: [
      {
        id: 'rev-4',
        userName: 'مها الأحمد',
        rating: 5,
        date: 'منذ 3 أيام',
        comment: 'أفضل تطبيق استخدمته لتنظيم مهام العمل. الواجهة هادئة وبدون تعقيد.',
        helpfulCount: 54
      }
    ]
  },
  {
    id: 'app-smart-tasbeeh',
    title: 'المسبحة الرقمية الذكية والأذكار',
    titleEn: 'Smart Tasbeeh & Dhikr Pro',
    tagline: 'عداد أذكار وتسبيح ذكي مع اهتزاز لمسي، وسجل يومي، ومجموعة أذكار مأثورة',
    description: 'تطبيق مسبحة رقمية بتصميم هادئ ومريح للعين. يدعم التسبيح باللمس في أي مكان على الشاشة مع اهتزاز خفيف وتنبيهات صوتية هادئة عند إتمام كل دورة (33 أو 100). يشمل أذكار الصباح والمساء والاستغفار وحفظ الورد اليومي تلقائياً.',
    type: 'app',
    category: 'Utility',
    developer: {
      name: 'نور للحلول الإسلامية',
      email: 'nour.islamic@example.com',
      verified: true,
      avatar: '📿'
    },
    iconUrl: 'https://images.unsplash.com/photo-1591604129939-f1efa4d9f7fa?w=300&auto=format&fit=crop&q=80',
    coverUrl: 'https://images.unsplash.com/photo-1591604129939-f1efa4d9f7fa?w=900&auto=format&fit=crop&q=80',
    screenshots: [
      'https://images.unsplash.com/photo-1591604129939-f1efa4d9f7fa?w=600&auto=format&fit=crop&q=80'
    ],
    rating: 5.0,
    ratingCount: 8430,
    downloadCount: 380000,
    sizeMb: 8.6,
    version: '2.0.4',
    minAndroid: 'Android 6.0+',
    platforms: ['android', 'web'],
    releaseDate: '2025-08-01',
    updatedDate: '2026-10-15',
    changelog: 'إضافة وضع توفير الطاقة الليلي الفائق، وإمكانية تخصيص الذكر والهدف اليومي.',
    price: 0,
    ageRating: '3+',
    downloadUrl: '#download-smart-tasbeeh',
    packageFileName: 'smart_tasbeeh_pro_v2.0.4.apk',
    hasPlayableDemo: true,
    playableGameType: 'smart-tasbeeh',
    reviews: [
      {
        id: 'rev-5',
        userName: 'عبدالله الشمري',
        rating: 5,
        date: 'منذ يوم',
        comment: 'جزاكم الله خيراً، تطبيق نقي ورائع والتجربة التفاعلية المباشرة مفيدة جداً.',
        helpfulCount: 78
      }
    ]
  },
  {
    id: 'app-quick-code',
    title: 'محرر الأكواد السريع ومختبر الويب',
    titleEn: 'QuickCode Sandbox IDE',
    tagline: 'محرر ومترجم فوري لأكواد HTML و CSS و JavaScript بدون إنترنت',
    description: 'بيئة تطوير برمجية مصغرة وسريعة لتجربة وتجريب الأفكار والأكواد البرمجية مباشرة على هاتفك أو حاسوبك. يدعم تمييز الصياغة، والمعاينة الحية الفورية للنتائج بنقرة واحدة، وتصدير المشاريع كملفات جاهزة.',
    type: 'app',
    category: 'Tools',
    developer: {
      name: 'كودكرافت ستوديو',
      email: 'codecraft@example.com',
      verified: true,
      avatar: '💻'
    },
    iconUrl: 'https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=300&auto=format&fit=crop&q=80',
    coverUrl: 'https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=900&auto=format&fit=crop&q=80',
    screenshots: [
      'https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=600&auto=format&fit=crop&q=80'
    ],
    rating: 4.7,
    ratingCount: 1620,
    downloadCount: 74000,
    sizeMb: 14.2,
    version: '1.4.0',
    minAndroid: 'Android 8.0+',
    platforms: ['android', 'web', 'windows'],
    releaseDate: '2026-02-14',
    updatedDate: '2026-09-30',
    changelog: 'دعم المعاينة اللحظية داخل إطار معزول، وتنسيق الأكواد التلقائي.',
    price: 0,
    ageRating: '3+',
    downloadUrl: '#download-quickcode',
    packageFileName: 'quickcode_sandbox_v1.4.0.apk',
    hasPlayableDemo: true,
    playableGameType: 'code-runner',
    reviews: [
      {
        id: 'rev-6',
        userName: 'عمر التميمي',
        rating: 5,
        date: 'منذ أسبوعين',
        comment: 'أداة ممتازة لاختبار الأفكار السريعة بدون الحاجة لفتح بيئات برمجية ثقيلة.',
        helpfulCount: 23
      }
    ]
  },
  {
    id: 'game-brain-duel',
    title: 'تحدي العباقرة وسرعة الحساب',
    titleEn: 'Brain Math Blitz',
    tagline: 'لعبة ذكاء سريعة لتنشيط العقل وتحدي سرعة البديهة والعمليات الحسابية',
    description: 'اختبر مهاراتك الذهنية وسرعة رد فعلك في حل المعادلات والألغاز الرياضية قبل انتهاء الوقت. تقدم اللعبة مراحل متدرجة الصعوبة، ونظام نقاط، وتحديات يومية تضمن لك تدريباً ممتعاً لعقلك كل يوم.',
    type: 'game',
    category: 'Puzzle',
    developer: {
      name: 'عقول ذكية ستوديو',
      email: 'smartminds@example.com',
      verified: true,
      avatar: '🧠'
    },
    iconUrl: 'https://images.unsplash.com/photo-1606326608606-aa0b62935f2b?w=300&auto=format&fit=crop&q=80',
    coverUrl: 'https://images.unsplash.com/photo-1606326608606-aa0b62935f2b?w=900&auto=format&fit=crop&q=80',
    screenshots: [
      'https://images.unsplash.com/photo-1606326608606-aa0b62935f2b?w=600&auto=format&fit=crop&q=80'
    ],
    rating: 4.8,
    ratingCount: 3100,
    downloadCount: 115000,
    sizeMb: 24.0,
    version: '2.1.0',
    minAndroid: 'Android 7.0+',
    platforms: ['android', 'web'],
    releaseDate: '2026-04-10',
    updatedDate: '2026-10-12',
    changelog: 'إضافة وضع المبارزة السريعة 60 ثانية، ومؤثرات صوتية محفزة للتركيز.',
    price: 0,
    ageRating: '3+',
    downloadUrl: '#download-brain-duel',
    packageFileName: 'brain_math_blitz_v2.1.0.apk',
    hasPlayableDemo: true,
    playableGameType: 'desert-rally',
    reviews: [
      {
        id: 'rev-7',
        userName: 'ريم السديري',
        rating: 5,
        date: 'منذ 4 أيام',
        comment: 'لعبة إدمانية ومفيدة جداً للأطفال والكبار على حد سواء.',
        helpfulCount: 37
      }
    ]
  }
];

export const CATEGORIES_LIST: { id: AppCategory | 'All'; labelAr: string; labelEn: string; icon: string }[] = [
  { id: 'All', labelAr: 'جميع التصنيفات', labelEn: 'All Categories', icon: '✨' },
  { id: 'Arcade', labelAr: 'أركيد وكلاسيك', labelEn: 'Arcade', icon: '👾' },
  { id: 'Racing', labelAr: 'سباقات وسرعة', labelEn: 'Racing', icon: '🏎️' },
  { id: 'Productivity', labelAr: 'إنتاجية وتنظيم', labelEn: 'Productivity', icon: '⚡' },
  { id: 'Tools', labelAr: 'أدوات ومطورين', labelEn: 'Tools', icon: '🛠️' },
  { id: 'Utility', labelAr: 'خدمات وأدوات عامة', labelEn: 'Utilities', icon: '📱' },
  { id: 'Puzzle', labelAr: 'ألغاز وتحدي', labelEn: 'Puzzles', icon: '🧩' },
  { id: 'Action', labelAr: 'أكشن ومغامرات', labelEn: 'Action', icon: '⚔️' },
  { id: 'Education', labelAr: 'تعليم وتدريب', labelEn: 'Education', icon: '🎓' },
];
