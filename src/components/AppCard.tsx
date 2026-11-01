import React from 'react';
import { AppItem } from '../types/store';
import { Star, Download, Play, Heart, ShieldCheck } from 'lucide-react';
import { playClickSound } from '../utils/audio';

interface AppCardProps {
  app: AppItem;
  onOpenDetails: (app: AppItem) => void;
  onPlayDemo: (app: AppItem) => void;
  isFavorite: boolean;
  onToggleFavorite: (id: string) => void;
  isArabic: boolean;
}

export const AppCard: React.FC<AppCardProps> = ({
  app,
  onOpenDetails,
  onPlayDemo,
  isFavorite,
  onToggleFavorite,
  isArabic
}) => {
  return (
    <div className="group relative flex flex-col justify-between rounded-2xl border border-slate-200 dark:border-slate-800/80 bg-white dark:bg-slate-900/90 overflow-hidden hover:border-slate-300 dark:hover:border-slate-700 hover:-translate-y-1 hover:shadow-xl transition-all duration-200">
      
      {/* Top Cover / Thumbnail */}
      <div
        onClick={() => onOpenDetails(app)}
        className="relative h-44 w-full bg-slate-950 overflow-hidden cursor-pointer"
      >
        <img
          src={app.coverUrl || app.iconUrl}
          alt={app.title}
          referrerPolicy="no-referrer"
          className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300 filter brightness-95"
        />
        <div className="absolute inset-0 bg-gradient-to-t from-slate-950/80 via-transparent to-black/20" />

        {/* Favorite Button */}
        <button
          onClick={(e) => {
            e.stopPropagation();
            playClickSound();
            onToggleFavorite(app.id);
          }}
          className="absolute top-3 left-3 p-2 rounded-full bg-slate-950/60 hover:bg-slate-900 text-white backdrop-blur-xs transition-colors cursor-pointer"
          title="المفضلة"
        >
          <Heart className={`w-4 h-4 ${isFavorite ? 'fill-rose-500 text-rose-500' : ''}`} />
        </button>

        {/* Playable badge if available */}
        {app.hasPlayableDemo && (
          <div className="absolute top-3 right-3 px-2 py-0.5 rounded-md bg-emerald-950/80 border border-emerald-500/40 text-[11px] font-bold text-emerald-300 flex items-center gap-1 backdrop-blur-xs">
            <Play className="w-3 h-3 fill-emerald-400 text-emerald-400" />
            <span>{isArabic ? 'تشغيل فوري' : 'Instant Play'}</span>
          </div>
        )}

        {/* Bottom icon + title overlay */}
        <div className="absolute bottom-3 right-3 left-3 flex items-center gap-2.5 text-white">
          <div className="w-10 h-10 rounded-xl overflow-hidden border border-white/20 shadow-md bg-slate-800 shrink-0">
            <img
              src={app.iconUrl}
              alt={app.title}
              referrerPolicy="no-referrer"
              className="w-full h-full object-cover"
            />
          </div>
          <div className="min-w-0 flex-1">
            <h3 className="text-sm font-bold text-white truncate leading-tight">
              {app.title}
            </h3>
            <div className="text-[11px] text-slate-300 flex items-center gap-1 truncate">
              <span>{app.developer.name}</span>
              {app.developer.verified && <ShieldCheck className="w-3 h-3 text-emerald-400 shrink-0" />}
            </div>
          </div>
        </div>
      </div>

      {/* Card Content & Zero-Pill Metadata */}
      <div className="p-4 flex-1 flex flex-col justify-between space-y-3">
        <p className="text-xs text-slate-600 dark:text-slate-400 line-clamp-2 leading-relaxed">
          {app.tagline}
        </p>

        {/* Clean unboxed metadata with bullet separators */}
        <div className="flex items-center gap-2 text-xs text-slate-500 dark:text-slate-400 pt-1 border-t border-slate-100 dark:border-slate-800/80">
          <div className="flex items-center gap-0.5 text-amber-500 font-bold font-mono">
            <span>{app.rating.toFixed(1)}</span>
            <Star className="w-3.5 h-3.5 fill-amber-400 text-amber-400" />
          </div>
          <span aria-hidden="true">·</span>
          <span className="font-mono tabular-nums">{app.sizeMb} MB</span>
          <span aria-hidden="true">·</span>
          <span>{app.category}</span>
          <span aria-hidden="true">·</span>
          <span className="font-mono tabular-nums">{app.downloadCount.toLocaleString()} {isArabic ? 'تنزيل' : 'DLs'}</span>
        </div>

        {/* Actions Button Row */}
        <div className="flex items-center gap-2 pt-1">
          <button
            onClick={() => onOpenDetails(app)}
            className="flex-1 flex items-center justify-center gap-1.5 py-2 px-3 bg-slate-100 hover:bg-slate-200 dark:bg-slate-800 dark:hover:bg-slate-700 text-slate-900 dark:text-white rounded-lg text-xs font-bold transition-colors cursor-pointer"
          >
            <Download className="w-3.5 h-3.5 text-emerald-500" />
            <span>{app.price === 0 ? (isArabic ? 'تثبيت / تحميل' : 'Install') : `$${app.price}`}</span>
          </button>

          {app.hasPlayableDemo && (
            <button
              onClick={() => {
                playClickSound();
                onPlayDemo(app);
              }}
              className="py-2 px-3 bg-emerald-600 hover:bg-emerald-500 text-white rounded-lg text-xs font-bold transition-colors flex items-center gap-1 cursor-pointer"
              title={isArabic ? 'تشغيل وتجربة في المتصفح' : 'Test Play in Browser'}
            >
              <Play className="w-3.5 h-3.5 fill-current" />
              <span>{isArabic ? 'تجربة' : 'Play'}</span>
            </button>
          )}
        </div>
      </div>
    </div>
  );
};
