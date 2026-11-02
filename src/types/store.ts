export type AppType = 'app' | 'game';

export type AppCategory = 
  | 'Action'
  | 'Racing'
  | 'Arcade'
  | 'Productivity'
  | 'Tools'
  | 'Education'
  | 'Social'
  | 'Utility'
  | 'Puzzle';

export interface AppReview {
  id: string;
  userName: string;
  rating: number;
  date: string;
  comment: string;
  helpfulCount: number;
}

export interface AppItem {
  id: string;
  title: string;
  titleEn?: string;
  tagline: string;
  description: string;
  type: AppType;
  category: AppCategory;
  developer: {
    name: string;
    email?: string;
    verified: boolean;
    avatar?: string;
  };
  iconUrl: string;
  coverUrl?: string;
  screenshots: string[];
  rating: number;
  ratingCount: number;
  downloadCount: number;
  sizeMb: number;
  version: string;
  minAndroid?: string;
  platforms: ('android' | 'web' | 'windows' | 'ios')[];
  releaseDate: string;
  updatedDate: string;
  changelog: string;
  price: number; // 0 for free
  ageRating: '3+' | '7+' | '12+' | '16+' | '18+';
  downloadUrl?: string;
  packageFileName?: string;
  hasPlayableDemo: boolean;
  playableGameType?: 'space-shooter' | 'desert-rally' | 'smart-tasbeeh' | 'code-runner' | 'custom-iframe';
  iframeUrl?: string;
  isUserUploaded?: boolean;
  reviews: AppReview[];
}

export type StoreTab = 'all' | 'apps' | 'games' | 'my-apps' | 'favorites';

export type SortOption = 'popular' | 'rating' | 'newest' | 'size';
