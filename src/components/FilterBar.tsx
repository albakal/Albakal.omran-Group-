import React from 'react';
import { AppType, AppCategory, SortOption } from '../types/store';
import { CATEGORIES_LIST } from '../data/defaultCatalog';
import { Search, SlidersHorizontal, X } from 'lucide-react';
import { playClickSound } from '../utils/audio';

interface FilterBarProps {
  searchQuery: string;
  onSearchChange: (val: string) => void;
  selectedType: AppType | 'all';
  onSelectType: (type: AppType | 'all') => void;
  selectedCategory: AppCategory | 'All';
  onSelectCategory: (cat: AppCategory | 'All') => void;
  sortOption: SortOption;
  onSortChange: (sort: SortOption) => void;
  totalFilteredCount: number;
  isArabic: boolean;
}

export const FilterBar: React.FC<FilterBarProps> = ({
  searchQuery,
  onSearchChange,
  selectedType,
  onSelectType,
  selectedCategory,
  onSelectCategory,
  sortOption,
  onSortChange,
  totalFilteredCount,
  isArabic
}) => {
  const hasActiveFilters = searchQuery !== '' || selectedCategory !== 'All' || selectedType !== 'all';

  const clearAllFilters = () => {
    playClickSound();
    onSearchChange('');
    onSelectType('all');
    onSelectCategory('All');
  };

  return (
    <div className="space-y-4 my-6">
      {/* Search Lens & Query Input Box */}
      <div className="flex flex-col sm:flex-row items-stretch sm:items-center justify-between gap-3">
        {/* Prominent Search Lens (عدسة بحث على التطبيقات بالاسم) */}
        <div className="relative flex-1 group">
          <div className="absolute right-3.5 top-1/2 -translate-y-1/2 flex items-center justify-center pointer-events-none text-emerald-500">
            <Search className="w-5 h-5 group-focus-within:scale-110 transition-transform" />
          </div>

          <input
            type="text"
            value={searchQuery}
            onChange={(e) => onSearchChange(e.target.value)}
            placeholder={
              isArabic
                ? '🔍 ابحث عن التطبيقات والألعاب بالاسم...'
                : '🔍 Search apps and games by name...'
            }
            className="w-full pr-11 pl-10 py-3 rounded-xl border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900 text-slate-900 dark:text-white text-sm font-medium placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-transparent shadow-xs transition-all"
          />

          {searchQuery && (
            <button
              onClick={() => onSearchChange('')}
              className="absolute left-3.5 top-1/2 -translate-y-1/2 p-1 text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 rounded-full hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"
              title={isArabic ? 'مسح البحث' : 'Clear search'}
            >
              <X className="w-4 h-4" />
            </button>
          )}
        </div>

        {/* Segmented Type Control */}
        <div className="flex items-center gap-1 p-1 bg-slate-100 dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 shrink-0">
          <button
            onClick={() => {
              playClickSound();
              onSelectType('all');
            }}
            className={`px-3 py-2 text-xs font-semibold rounded-lg transition-colors cursor-pointer ${
              selectedType === 'all'
                ? 'bg-white dark:bg-slate-800 text-slate-900 dark:text-white shadow-xs'
                : 'text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white'
            }`}
          >
            {isArabic ? 'الكل' : 'All'}
          </button>

          <button
            onClick={() => {
              playClickSound();
              onSelectType('app');
            }}
            className={`px-3 py-2 text-xs font-semibold rounded-lg transition-colors cursor-pointer ${
              selectedType === 'app'
                ? 'bg-white dark:bg-slate-800 text-slate-900 dark:text-white shadow-xs'
                : 'text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white'
            }`}
          >
            {isArabic ? 'التطبيقات' : 'Apps'}
          </button>

          <button
            onClick={() => {
              playClickSound();
              onSelectType('game');
            }}
            className={`px-3 py-2 text-xs font-semibold rounded-lg transition-colors cursor-pointer ${
              selectedType === 'game'
                ? 'bg-white dark:bg-slate-800 text-slate-900 dark:text-white shadow-xs'
                : 'text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white'
            }`}
          >
            {isArabic ? 'الألعاب' : 'Games'}
          </button>
        </div>

        {/* Sort Select */}
        <div className="flex items-center gap-2 shrink-0">
          <SlidersHorizontal className="w-4 h-4 text-slate-400 hidden sm:block" />
          <select
            value={sortOption}
            onChange={(e) => {
              playClickSound();
              onSortChange(e.target.value as SortOption);
            }}
            className="px-3 py-2.5 rounded-xl border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900 text-xs font-medium text-slate-700 dark:text-slate-200 focus:outline-none focus:ring-2 focus:ring-emerald-500"
          >
            <option value="popular">{isArabic ? 'الأكثر تحميلاً' : 'Most Popular'}</option>
            <option value="rating">{isArabic ? 'الأعلى تقييماً' : 'Highest Rated'}</option>
            <option value="newest">{isArabic ? 'الأحدث إطلاقاً' : 'Newest'}</option>
            <option value="size">{isArabic ? 'الأصغر حجماً' : 'Smallest Size'}</option>
          </select>
        </div>
      </div>

      {/* Categories Horizontal Scroll Strip */}
      <div className="flex items-center gap-2 overflow-x-auto pb-1 text-xs">
        {CATEGORIES_LIST.map((cat) => (
          <button
            key={cat.id}
            onClick={() => {
              playClickSound();
              onSelectCategory(cat.id);
            }}
            className={`px-3 py-1.5 rounded-lg border whitespace-nowrap transition-colors cursor-pointer flex items-center gap-1.5 shrink-0 ${
              selectedCategory === cat.id
                ? 'border-emerald-500 bg-emerald-50 dark:bg-emerald-950/40 text-emerald-700 dark:text-emerald-300 font-bold'
                : 'border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900 text-slate-600 dark:text-slate-400 hover:border-slate-300 dark:hover:border-slate-700'
            }`}
          >
            <span>{cat.icon}</span>
            <span>{isArabic ? cat.labelAr : cat.labelEn}</span>
          </button>
        ))}
      </div>

      {/* Result counter & clear filter trigger */}
      <div className="flex items-center justify-between text-xs text-slate-500 pt-1">
        <div>
          {searchQuery ? (
            <span>
              {isArabic ? 'نتائج البحث عن بالاسم "' : 'Search results for "'}
              <span className="font-bold text-emerald-500">{searchQuery}</span>
              {'" : '}
            </span>
          ) : (
            <span>{isArabic ? 'المعروض حالياً: ' : 'Showing: '}</span>
          )}
          <span className="font-bold text-slate-900 dark:text-white font-mono tabular-nums">
            {totalFilteredCount}
          </span>
          {isArabic ? ' تطبيق ولعبة' : ' items'}
        </div>

        {hasActiveFilters && (
          <button
            onClick={clearAllFilters}
            className="text-xs text-rose-500 hover:text-rose-600 hover:underline flex items-center gap-1 cursor-pointer"
          >
            <X className="w-3.5 h-3.5" />
            <span>{isArabic ? 'إعادة ضبط الفلاتر' : 'Reset filters'}</span>
          </button>
        )}
      </div>
    </div>
  );
};
