import JSZip from 'jszip';

export interface ExtractedApkData {
  title?: string;
  packageId?: string;
  version?: string;
  iconUrl?: string;
  screenshots: string[];
  sizeMb: number;
  fileName: string;
}

/**
 * Automatically inspects an uploaded APK or ZIP file,
 * extracts actual app launcher icons and preview images directly from the archive!
 */
export async function extractDataFromApkFile(file: File): Promise<ExtractedApkData> {
  const sizeMb = Number((file.size / (1024 * 1024)).toFixed(1));
  const fileName = file.name;

  // Clean filename to deduce title and package
  const cleanName = fileName.replace(/\.(apk|zip|xapk|aab)$/i, '');
  let guessedTitle = cleanName
    .replace(/[_-]/g, ' ')
    .replace(/\b(v?\d+(\.\d+)*)\b/gi, '')
    .trim();

  // If camelCase or dot-separated, clean up
  if (guessedTitle.includes('.')) {
    const parts = guessedTitle.split('.');
    guessedTitle = parts[parts.length - 1];
  }
  guessedTitle = guessedTitle.charAt(0).toUpperCase() + guessedTitle.slice(1);

  const guessedPackage = cleanName.toLowerCase().replace(/[^a-z0-9.]/g, '.');

  let extractedIcon: string | undefined;
  const extractedScreenshots: string[] = [];

  try {
    const zip = await JSZip.loadAsync(file);
    const imageFiles: { path: string; file: JSZip.JSZipObject }[] = [];

    // Scan all files in the APK
    zip.forEach((relativePath, zipEntry) => {
      if (!zipEntry.dir && /\.(png|jpg|jpeg|webp)$/i.test(relativePath)) {
        imageFiles.push({ path: relativePath, file: zipEntry });
      }
    });

    if (imageFiles.length > 0) {
      // Prioritize launcher icons: xxxhdpi > xxhdpi > xhdpi > hdpi > any icon
      const iconCandidates = imageFiles.filter((item) =>
        /ic_launcher|app_icon|icon|logo/i.test(item.path)
      );

      let bestIcon = iconCandidates.find((i) => /xxxhdpi|480dpi|640dpi/i.test(i.path));
      if (!bestIcon) bestIcon = iconCandidates.find((i) => /xxhdpi|320dpi/i.test(i.path));
      if (!bestIcon) bestIcon = iconCandidates.find((i) => /xhdpi|240dpi/i.test(i.path));
      if (!bestIcon) bestIcon = iconCandidates[0] || imageFiles[0];

      if (bestIcon) {
        const blob = await bestIcon.file.async('blob');
        extractedIcon = await blobToDataUrl(blob);
      }

      // Collect other images as screenshots / promo banners
      const remaining = imageFiles.filter((i) => i.path !== bestIcon?.path);
      for (const item of remaining.slice(0, 4)) {
        try {
          const blob = await item.file.async('blob');
          const dataUrl = await blobToDataUrl(blob);
          extractedScreenshots.push(dataUrl);
        } catch {
          // Continue
        }
      }
    }
  } catch (err) {
    console.warn('Could not read as zip archive (normal for raw non-zip files):', err);
  }

  // If no launcher icon found inside zip, generate a crisp Google Play style badge icon
  if (!extractedIcon) {
    extractedIcon = createGeneratedIcon(guessedTitle);
  }

  // If no screenshots found, generate Google Play style device mockup screenshots
  if (extractedScreenshots.length === 0) {
    extractedScreenshots.push(
      createMockupScreenshot(guessedTitle, 'تجربة أداء فائقة وواجهة عصرية'),
      createMockupScreenshot(guessedTitle, 'حماية وأمان معتمد من متجر Play')
    );
  }

  return {
    title: guessedTitle,
    packageId: guessedPackage || `com.play.${cleanName.toLowerCase()}`,
    version: '1.0.0',
    iconUrl: extractedIcon,
    screenshots: extractedScreenshots,
    sizeMb,
    fileName,
  };
}

function blobToDataUrl(blob: Blob): Promise<string> {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = () => resolve(reader.result as string);
    reader.onerror = reject;
    reader.readAsDataURL(blob);
  });
}

function createGeneratedIcon(title: string): string {
  const char = (title && title.charAt(0)) ? title.charAt(0).toUpperCase() : 'P';
  const colors = [
    ['#0086F8', '#00A3FF'],
    ['#00C853', '#69F0AE'],
    ['#FF6D00', '#FFD600'],
    ['#7C4DFF', '#B388FF'],
  ];
  const color = colors[Math.abs(title.length) % colors.length];

  return `data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="160" height="160" viewBox="0 0 160 160"><defs><linearGradient id="g" x1="0" y1="0" x2="1" y2="1"><stop stop-color="${encodeURIComponent(color[0])}"/><stop offset="1" stop-color="${encodeURIComponent(color[1])}"/></linearGradient></defs><rect width="160" height="160" rx="36" fill="url(%23g)"/><text x="50%" y="56%" dominant-baseline="middle" text-anchor="middle" font-family="sans-serif" font-weight="900" font-size="76" fill="white">${char}</text></svg>`;
}

function createMockupScreenshot(title: string, sub: string): string {
  return `data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="600" height="380" viewBox="0 0 600 380"><rect width="600" height="380" fill="%230f172a"/><rect x="40" y="30" width="520" height="320" rx="20" fill="%231e293b" stroke="%23334155" stroke-width="2"/><circle cx="80" cy="70" r="14" fill="%2300c853"/><text x="110" y="76" font-family="sans-serif" font-size="20" font-weight="bold" fill="white">${encodeURIComponent(title)}</text><text x="110" y="110" font-family="sans-serif" font-size="14" fill="%2394a3b8">${encodeURIComponent(sub)}</text><rect x="80" y="140" width="440" height="150" rx="14" fill="%230f172a" stroke="%23475569" stroke-dasharray="4 4"/><text x="300" y="220" font-family="sans-serif" font-size="16" font-weight="bold" fill="%2338bdf8" text-anchor="middle">معاينة واجهة التطبيق داخل متجر Play</text></svg>`;
}
