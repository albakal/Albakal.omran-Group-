import React, { useState, useEffect, useRef } from 'react';
import { AppItem } from '../types/store';
import { X, Maximize2, Minimize2, Volume2, VolumeX, RotateCcw, Play, CheckCircle2 } from 'lucide-react';
import { playLaserSound, playChimeSound, playClickSound } from '../utils/audio';

interface PlayableModalProps {
  app: AppItem;
  onClose: () => void;
  isArabic: boolean;
}

export const PlayableModal: React.FC<PlayableModalProps> = ({ app, onClose, isArabic }) => {
  const [isFullscreen, setIsFullscreen] = useState(false);
  const [soundEnabled, setSoundEnabled] = useState(true);

  // Space Shooter Game State
  const canvasRef = useRef<HTMLCanvasElement | null>(null);
  const [gameScore, setGameScore] = useState(0);
  const [gameOver, setGameOver] = useState(false);
  const [gameStarted, setGameStarted] = useState(false);

  // Smart Tasbeeh State
  const [tasbeehCount, setTasbeehCount] = useState(0);
  const [tasbeehGoal, setTasbeehGoal] = useState(33);
  const [currentDhikrIndex, setCurrentDhikrIndex] = useState(0);

  const dhikrList = [
    { text: 'سُبْحَانَ اللَّهِ', count: 33 },
    { text: 'الْحَمْدُ لِلَّهِ', count: 33 },
    { text: 'اللَّهُ أَكْبَرُ', count: 34 },
    { text: 'أَسْتَغْفِرُ اللَّهَ وَأَتُوبُ إِلَيْهِ', count: 100 },
    { text: 'لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ', count: 10 },
  ];

  // Code Runner State
  const [codeHtml, setCodeHtml] = useState(`<!DOCTYPE html>
<html>
<head>
  <style>
    body { font-family: sans-serif; text-align: center; padding: 2rem; background: #0f172a; color: white; }
    .btn { background: #10b981; color: white; border: none; padding: 10px 20px; font-size: 16px; border-radius: 8px; cursor: pointer; }
    .card { background: #1e293b; padding: 20px; border-radius: 12px; max-width: 320px; margin: 0 auto; box-shadow: 0 4px 6px rgba(0,0,0,0.3); }
  </style>
</head>
<body>
  <div class="card">
    <h2>مرحباً من محرك QuickCode! ⚡</h2>
    <p>هذا تطبيق ويب حي يعمل مباشرة داخل المتجر.</p>
    <button class="btn" onclick="alert('يعمل بنجاح!')">اضغط للتجربة</button>
  </div>
</body>
</html>`);

  // Handle Space Shooter Game Engine
  useEffect(() => {
    if (app.playableGameType !== 'space-shooter' && app.playableGameType !== 'desert-rally') return;
    if (!gameStarted) return;

    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    let animId: number;
    let playerX = canvas.width / 2;
    const playerY = canvas.height - 45;
    const playerWidth = 36;
    const playerHeight = 36;

    let bullets: { x: number; y: number; speed: number }[] = [];
    let targets: { x: number; y: number; size: number; speed: number; hp: number; color: string }[] = [];
    let stars: { x: number; y: number; s: number; speed: number }[] = [];

    // Init background stars
    for (let i = 0; i < 45; i++) {
      stars.push({
        x: Math.random() * canvas.width,
        y: Math.random() * canvas.height,
        s: Math.random() * 2 + 1,
        speed: Math.random() * 2 + 1,
      });
    }

    let isMovingLeft = false;
    let isMovingRight = false;
    let isFiring = false;
    let lastFire = 0;
    let localScore = 0;

    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'ArrowLeft' || e.key === 'a' || e.key === 'A') isMovingLeft = true;
      if (e.key === 'ArrowRight' || e.key === 'd' || e.key === 'D') isMovingRight = true;
      if (e.key === ' ' || e.key === 'ArrowUp') isFiring = true;
    };

    const handleKeyUp = (e: KeyboardEvent) => {
      if (e.key === 'ArrowLeft' || e.key === 'a' || e.key === 'A') isMovingLeft = false;
      if (e.key === 'ArrowRight' || e.key === 'd' || e.key === 'D') isMovingRight = false;
      if (e.key === ' ' || e.key === 'ArrowUp') isFiring = false;
    };

    window.addEventListener('keydown', handleKeyDown);
    window.addEventListener('keyup', handleKeyUp);

    let spawnTimer = 0;

    const loop = (timestamp: number) => {
      ctx.fillStyle = '#090d16';
      ctx.fillRect(0, 0, canvas.width, canvas.height);

      // Render stars
      ctx.fillStyle = '#94a3b8';
      for (const star of stars) {
        star.y += star.speed;
        if (star.y > canvas.height) {
          star.y = 0;
          star.x = Math.random() * canvas.width;
        }
        ctx.fillRect(star.x, star.y, star.s, star.s);
      }

      // Player Movement
      if (isMovingLeft && playerX > playerWidth / 2) playerX -= 6;
      if (isMovingRight && playerX < canvas.width - playerWidth / 2) playerX += 6;

      // Auto / Keyboard Firing
      if ((isFiring || timestamp - lastFire > 380) && timestamp - lastFire > 200) {
        bullets.push({ x: playerX, y: playerY - 10, speed: 9 });
        lastFire = timestamp;
        if (soundEnabled) playLaserSound();
      }

      // Draw bullets
      ctx.fillStyle = '#34d399';
      ctx.shadowColor = '#10b981';
      ctx.shadowBlur = 8;
      for (let i = bullets.length - 1; i >= 0; i--) {
        const b = bullets[i];
        b.y -= b.speed;
        ctx.fillRect(b.x - 2, b.y, 4, 12);
        if (b.y < -15) {
          bullets.splice(i, 1);
        }
      }
      ctx.shadowBlur = 0;

      // Spawn Targets / Asteroids / Dune Obstacles
      spawnTimer++;
      if (spawnTimer % 45 === 0) {
        targets.push({
          x: Math.random() * (canvas.width - 40) + 20,
          y: -30,
          size: Math.random() * 18 + 14,
          speed: Math.random() * 2.2 + 2,
          hp: 1,
          color: app.playableGameType === 'desert-rally' ? '#f59e0b' : '#38bdf8'
        });
      }

      // Draw and update targets
      for (let i = targets.length - 1; i >= 0; i--) {
        const t = targets[i];
        t.y += t.speed;

        ctx.fillStyle = t.color;
        ctx.beginPath();
        ctx.arc(t.x, t.y, t.size, 0, Math.PI * 2);
        ctx.fill();

        // Check collision with bullets
        for (let j = bullets.length - 1; j >= 0; j--) {
          const b = bullets[j];
          const dist = Math.hypot(t.x - b.x, t.y - b.y);
          if (dist < t.size + 4) {
            bullets.splice(j, 1);
            targets.splice(i, 1);
            localScore += 10;
            setGameScore(localScore);
            if (soundEnabled) playChimeSound();
            break;
          }
        }

        // Check collision with player
        if (t.y + t.size > playerY - playerHeight / 2 && Math.abs(t.x - playerX) < playerWidth / 2 + t.size / 2) {
          setGameOver(true);
          return;
        }

        // Remove if off screen
        if (t.y > canvas.height + 40) {
          targets.splice(i, 1);
        }
      }

      // Draw Player Ship / Car
      ctx.fillStyle = '#10b981';
      ctx.beginPath();
      ctx.moveTo(playerX, playerY - 18);
      ctx.lineTo(playerX - 16, playerY + 16);
      ctx.lineTo(playerX, playerY + 8);
      ctx.lineTo(playerX + 16, playerY + 16);
      ctx.closePath();
      ctx.fill();

      // Jet Glow
      ctx.fillStyle = '#f59e0b';
      ctx.beginPath();
      ctx.arc(playerX, playerY + 14, 5 + Math.random() * 3, 0, Math.PI * 2);
      ctx.fill();

      animId = requestAnimationFrame(loop);
    };

    animId = requestAnimationFrame(loop);

    return () => {
      cancelAnimationFrame(animId);
      window.removeEventListener('keydown', handleKeyDown);
      window.removeEventListener('keyup', handleKeyUp);
    };
  }, [app.playableGameType, gameStarted, soundEnabled]);

  const resetGame = () => {
    setGameOver(false);
    setGameScore(0);
    setGameStarted(true);
  };

  const handleTasbeehClick = () => {
    playClickSound();
    const next = tasbeehCount + 1;
    setTasbeehCount(next);

    if (navigator.vibrate) {
      navigator.vibrate(25);
    }

    if (next % tasbeehGoal === 0 && soundEnabled) {
      playChimeSound();
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-3 sm:p-6 bg-slate-950/80 backdrop-blur-md">
      <div
        className={`relative w-full bg-slate-900 border border-slate-700/80 rounded-2xl shadow-2xl flex flex-col overflow-hidden transition-all ${
          isFullscreen ? 'h-full max-w-full rounded-none' : 'max-w-3xl max-h-[90vh]'
        }`}
      >
        {/* Top Control Bar */}
        <div className="flex items-center justify-between px-5 py-3.5 border-b border-slate-800 bg-slate-900/90 text-white shrink-0">
          <div className="flex items-center gap-3">
            <img
              src={app.iconUrl}
              alt={app.title}
              referrerPolicy="no-referrer"
              className="w-7 h-7 rounded-lg object-cover border border-slate-700"
            />
            <div>
              <div className="text-sm font-bold leading-tight">{app.title}</div>
              <div className="text-xs text-slate-400">
                {isArabic ? 'تشغيل وتجربة حية في المتصفح' : 'Live Browser Sandbox'}
              </div>
            </div>
          </div>

          <div className="flex items-center gap-2">
            <button
              onClick={() => setSoundEnabled(!soundEnabled)}
              className="p-1.5 text-slate-400 hover:text-white rounded-lg hover:bg-slate-800 transition-colors"
              title={soundEnabled ? 'كتم الصوت' : 'تشغيل الصوت'}
            >
              {soundEnabled ? <Volume2 className="w-4 h-4" /> : <VolumeX className="w-4 h-4" />}
            </button>

            <button
              onClick={() => setIsFullscreen(!isFullscreen)}
              className="p-1.5 text-slate-400 hover:text-white rounded-lg hover:bg-slate-800 transition-colors"
              title={isFullscreen ? 'تصغير' : 'ملء الشاشة'}
            >
              {isFullscreen ? <Minimize2 className="w-4 h-4" /> : <Maximize2 className="w-4 h-4" />}
            </button>

            <button
              onClick={onClose}
              className="p-1.5 text-slate-400 hover:text-rose-400 rounded-lg hover:bg-slate-800 transition-colors"
              title="إغلاق"
            >
              <X className="w-5 h-5" />
            </button>
          </div>
        </div>

        {/* Game / App Sandbox Content */}
        <div className="flex-1 flex flex-col items-center justify-center p-4 bg-slate-950 relative overflow-y-auto">
          {/* Option A: Space Shooter or Racing Game */}
          {(app.playableGameType === 'space-shooter' || app.playableGameType === 'desert-rally') && (
            <div className="flex flex-col items-center justify-center w-full max-w-lg">
              <div className="flex items-center justify-between w-full mb-2 text-xs font-mono text-slate-400 px-2">
                <div>
                  {isArabic ? 'النقاط: ' : 'Score: '}
                  <span className="text-emerald-400 font-bold text-sm tabular-nums">{gameScore}</span>
                </div>
                <div className="text-slate-400">
                  {isArabic ? 'التحكم: أسهم الكيبورد أو المس الأزرار' : 'Controls: Arrow Keys or Touch'}
                </div>
              </div>

              <div className="relative border border-slate-800 rounded-xl overflow-hidden shadow-2xl bg-slate-950">
                <canvas
                  ref={canvasRef}
                  width={460}
                  height={420}
                  className="w-full max-w-full aspect-[460/420] block bg-slate-950"
                />

                {!gameStarted && !gameOver && (
                  <div className="absolute inset-0 bg-slate-950/85 backdrop-blur-xs flex flex-col items-center justify-center p-6 text-center text-white space-y-4">
                    <div className="w-16 h-16 rounded-2xl bg-emerald-500/20 border border-emerald-500/40 flex items-center justify-center text-3xl">
                      🚀
                    </div>
                    <div>
                      <h3 className="text-xl font-bold">{app.title}</h3>
                      <p className="text-xs text-slate-400 mt-1 max-w-xs">{app.tagline}</p>
                    </div>
                    <button
                      onClick={() => setGameStarted(true)}
                      className="flex items-center gap-2 px-6 py-2.5 bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold rounded-xl text-sm shadow-lg shadow-emerald-500/20 transition-all cursor-pointer"
                    >
                      <Play className="w-4 h-4 fill-current" />
                      <span>{isArabic ? 'بدء اللعب الآن' : 'Start Game'}</span>
                    </button>
                  </div>
                )}

                {gameOver && (
                  <div className="absolute inset-0 bg-slate-950/90 backdrop-blur-xs flex flex-col items-center justify-center p-6 text-center text-white space-y-4">
                    <div className="text-3xl">💥</div>
                    <h3 className="text-xl font-bold text-rose-400">{isArabic ? 'انتهت المحاولة!' : 'Game Over'}</h3>
                    <div className="text-sm text-slate-300">
                      {isArabic ? 'مجموع نقاطك: ' : 'Final Score: '}
                      <span className="text-emerald-400 font-bold text-lg font-mono tabular-nums">{gameScore}</span>
                    </div>
                    <button
                      onClick={resetGame}
                      className="flex items-center gap-2 px-5 py-2.5 bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold rounded-xl text-sm shadow-lg transition-all cursor-pointer"
                    >
                      <RotateCcw className="w-4 h-4" />
                      <span>{isArabic ? 'إعادة المحاولة' : 'Play Again'}</span>
                    </button>
                  </div>
                )}
              </div>

              {/* On-screen touch buttons for mobile */}
              <div className="flex items-center justify-center gap-4 mt-3 w-full">
                <button
                  onMouseDown={() => {
                    window.dispatchEvent(new KeyboardEvent('keydown', { key: 'ArrowLeft' }));
                  }}
                  onMouseUp={() => {
                    window.dispatchEvent(new KeyboardEvent('keyup', { key: 'ArrowLeft' }));
                  }}
                  onTouchStart={() => {
                    window.dispatchEvent(new KeyboardEvent('keydown', { key: 'ArrowLeft' }));
                  }}
                  onTouchEnd={() => {
                    window.dispatchEvent(new KeyboardEvent('keyup', { key: 'ArrowLeft' }));
                  }}
                  className="px-6 py-2 bg-slate-800 hover:bg-slate-700 active:bg-slate-600 rounded-lg text-white font-bold text-sm cursor-pointer select-none"
                >
                  ◀
                </button>
                <button
                  onClick={() => {
                    if (soundEnabled) playLaserSound();
                    window.dispatchEvent(new KeyboardEvent('keydown', { key: ' ' }));
                    setTimeout(() => window.dispatchEvent(new KeyboardEvent('keyup', { key: ' ' })), 100);
                  }}
                  className="px-8 py-2 bg-emerald-600 hover:bg-emerald-500 active:bg-emerald-400 rounded-lg text-white font-bold text-sm shadow-md cursor-pointer select-none"
                >
                  ⚡ {isArabic ? 'إطلاق / توربو' : 'Fire'}
                </button>
                <button
                  onMouseDown={() => {
                    window.dispatchEvent(new KeyboardEvent('keydown', { key: 'ArrowRight' }));
                  }}
                  onMouseUp={() => {
                    window.dispatchEvent(new KeyboardEvent('keyup', { key: 'ArrowRight' }));
                  }}
                  onTouchStart={() => {
                    window.dispatchEvent(new KeyboardEvent('keydown', { key: 'ArrowRight' }));
                  }}
                  onTouchEnd={() => {
                    window.dispatchEvent(new KeyboardEvent('keyup', { key: 'ArrowRight' }));
                  }}
                  className="px-6 py-2 bg-slate-800 hover:bg-slate-700 active:bg-slate-600 rounded-lg text-white font-bold text-sm cursor-pointer select-none"
                >
                  ▶
                </button>
              </div>
            </div>
          )}

          {/* Option B: Smart Tasbeeh Interactive App */}
          {app.playableGameType === 'smart-tasbeeh' && (
            <div className="w-full max-w-md p-6 bg-slate-900 border border-slate-800 rounded-2xl shadow-xl flex flex-col items-center space-y-6">
              <div className="w-full flex items-center justify-between text-xs text-slate-400">
                <span>{isArabic ? 'الذكر الحالي:' : 'Current Dhikr:'}</span>
                <select
                  value={currentDhikrIndex}
                  onChange={(e) => {
                    setCurrentDhikrIndex(Number(e.target.value));
                    setTasbeehGoal(dhikrList[Number(e.target.value)].count);
                    setTasbeehCount(0);
                  }}
                  className="bg-slate-800 text-slate-200 border border-slate-700 rounded-md px-2 py-1 text-xs"
                >
                  {dhikrList.map((d, idx) => (
                    <option key={idx} value={idx}>
                      {d.text} ({d.count})
                    </option>
                  ))}
                </select>
              </div>

              <div className="text-center">
                <div className="text-xl sm:text-2xl font-black text-emerald-400 tracking-wide mb-1 font-serif">
                  {dhikrList[currentDhikrIndex].text}
                </div>
                <div className="text-xs text-slate-400">
                  {isArabic ? `الهدف: ${tasbeehGoal} تسبيحة` : `Goal: ${tasbeehGoal}`}
                </div>
              </div>

              {/* Big Tap Button */}
              <button
                onClick={handleTasbeehClick}
                className="w-48 h-48 rounded-full bg-gradient-to-br from-emerald-600 via-teal-700 to-emerald-900 shadow-2xl shadow-emerald-500/20 border-4 border-emerald-400/30 flex flex-col items-center justify-center text-white active:scale-95 transition-transform cursor-pointer select-none"
              >
                <div className="text-4xl sm:text-5xl font-black font-mono tabular-nums">
                  {tasbeehCount}
                </div>
                <div className="text-xs font-semibold text-emerald-200 mt-1">
                  {isArabic ? 'المس للتسبيح' : 'Tap to Count'}
                </div>
              </button>

              <div className="flex items-center gap-3 w-full justify-between">
                <button
                  onClick={() => setTasbeehCount(0)}
                  className="flex items-center gap-1 px-3 py-1.5 text-xs text-slate-400 hover:text-slate-200 bg-slate-800/80 rounded-lg transition-colors"
                >
                  <RotateCcw className="w-3.5 h-3.5" />
                  <span>{isArabic ? 'تصفير العداد' : 'Reset'}</span>
                </button>

                <div className="text-xs text-emerald-400 flex items-center gap-1 font-medium">
                  <CheckCircle2 className="w-3.5 h-3.5" />
                  <span>
                    {isArabic ? `الدورات المكتملة: ${Math.floor(tasbeehCount / tasbeehGoal)}` : `Completed: ${Math.floor(tasbeehCount / tasbeehGoal)}`}
                  </span>
                </div>
              </div>
            </div>
          )}

          {/* Option C: Code Runner / Custom App */}
          {app.playableGameType === 'code-runner' && (
            <div className="w-full max-w-2xl h-[420px] flex flex-col border border-slate-800 rounded-xl overflow-hidden bg-slate-900">
              <div className="flex border-b border-slate-800 bg-slate-950 px-3 py-2 text-xs font-mono text-slate-400 justify-between items-center">
                <span>sandbox_output.html</span>
                <span className="text-emerald-400">✓ Running Live</span>
              </div>
              <iframe
                title="sandbox"
                srcDoc={codeHtml}
                className="w-full flex-1 bg-slate-950 border-none"
                sandbox="allow-scripts allow-modals"
              />
            </div>
          )}

          {/* Option D: Custom Iframe / URL if provided by user */}
          {app.playableGameType === 'custom-iframe' && app.iframeUrl && (
            <div className="w-full h-[500px] border border-slate-800 rounded-xl overflow-hidden bg-slate-950">
              <iframe
                src={app.iframeUrl}
                title={app.title}
                className="w-full h-full border-none"
                sandbox="allow-scripts allow-same-origin allow-forms"
              />
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
