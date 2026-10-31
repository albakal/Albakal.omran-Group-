import React from 'react';
import { useApp } from '../context/AppContext';
import { X, Play } from 'lucide-react';

export const VideoModal: React.FC = () => {
  const { activeVideoUrl, setActiveVideoUrl, config } = useApp();

  if (!activeVideoUrl) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-3 sm:p-6 bg-black/85 backdrop-blur-md animate-in fade-in duration-200">
      <div className="relative w-full max-w-4xl bg-slate-900 border border-slate-800 rounded-3xl overflow-hidden shadow-2xl flex flex-col">
        
        {/* Header */}
        <div className="p-4 bg-slate-950 flex items-center justify-between text-right border-b border-slate-800 text-white">
          <div className="flex items-center gap-2">
            <Play className="w-4 h-4 text-emerald-400 fill-current" />
            <h3 className="text-sm font-bold truncate max-w-md">
              {config.mainVideoTitle}
            </h3>
          </div>

          <button
            onClick={() => setActiveVideoUrl(null)}
            className="p-1.5 rounded-xl hover:bg-slate-800 text-slate-400 hover:text-white transition-colors cursor-pointer"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Video Player Frame */}
        <div className="aspect-video w-full bg-black flex items-center justify-center">
          {activeVideoUrl.includes('youtube') ? (
            <iframe
              src={activeVideoUrl}
              title={config.mainVideoTitle}
              className="w-full h-full border-0"
              allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
              allowFullScreen
            />
          ) : (
            <div className="flex flex-col items-center justify-center p-8 text-center text-slate-300 space-y-3">
              <div className="w-16 h-16 rounded-full bg-blue-600/30 border border-blue-500/40 flex items-center justify-center text-blue-400">
                <Play className="w-8 h-8 fill-current ml-1" />
              </div>
              <h4 className="text-base font-bold text-white">مشغل الوسائط الذكي لعمران جروب</h4>
              <p className="text-xs text-slate-400 max-w-md">
                يمكنك تحديث رابط الفيديو المباشر إلى أي رابط YouTube أو MP4 من خلال لوحة التحكم في أي وقت.
              </p>
            </div>
          )}
        </div>

      </div>
    </div>
  );
};
