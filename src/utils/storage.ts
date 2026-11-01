import { AppItem, AppReview } from '../types/store';
import { DEFAULT_CATALOG } from '../data/defaultCatalog';

const STORAGE_KEY = 'play_store_catalog_v2_clean';
const FAVORITES_KEY = 'play_store_favorites_v2';
const DOWNLOADS_KEY = 'play_store_downloads_v2';

export function getStoredCatalog(): AppItem[] {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(DEFAULT_CATALOG));
      return DEFAULT_CATALOG;
    }
    const parsed = JSON.parse(raw);
    return Array.isArray(parsed) && parsed.length > 0 ? parsed : DEFAULT_CATALOG;
  } catch (err) {
    console.error('Failed to read catalog from storage', err);
    return DEFAULT_CATALOG;
  }
}

export function saveCatalog(catalog: AppItem[]) {
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(catalog));
  } catch (err) {
    console.error('Failed to save catalog to storage', err);
  }
}

export function addAppToCatalog(newItem: AppItem): AppItem[] {
  const current = getStoredCatalog();
  const updated = [newItem, ...current];
  saveCatalog(updated);
  return updated;
}

export function updateAppInCatalog(updatedItem: AppItem): AppItem[] {
  const current = getStoredCatalog();
  const index = current.findIndex(item => item.id === updatedItem.id);
  if (index !== -1) {
    current[index] = updatedItem;
    saveCatalog(current);
  }
  return [...current];
}

export function deleteAppFromCatalog(id: string): AppItem[] {
  const current = getStoredCatalog();
  const filtered = current.filter(item => item.id !== id);
  saveCatalog(filtered);
  return filtered;
}

export function incrementDownloadCount(id: string): AppItem[] {
  const current = getStoredCatalog();
  const item = current.find(i => i.id === id);
  if (item) {
    item.downloadCount = (item.downloadCount || 0) + 1;
    saveCatalog(current);
    addDownloadedAppId(id);
  }
  return [...current];
}

export function addReviewToApp(appId: string, review: AppReview): AppItem[] {
  const current = getStoredCatalog();
  const item = current.find(i => i.id === appId);
  if (item) {
    item.reviews = [review, ...(item.reviews || [])];
    const totalScore = item.reviews.reduce((sum, r) => sum + r.rating, 0);
    item.ratingCount = item.reviews.length;
    item.rating = Number((totalScore / item.ratingCount).toFixed(1));
    saveCatalog(current);
  }
  return [...current];
}

export function getFavorites(): string[] {
  try {
    const raw = localStorage.getItem(FAVORITES_KEY);
    return raw ? JSON.parse(raw) : [];
  } catch {
    return [];
  }
}

export function toggleFavorite(id: string): string[] {
  const favs = getFavorites();
  const exists = favs.includes(id);
  const updated = exists ? favs.filter(f => f !== id) : [...favs, id];
  try {
    localStorage.setItem(FAVORITES_KEY, JSON.stringify(updated));
  } catch {
    // Ignore
  }
  return updated;
}

export function getDownloadedAppIds(): string[] {
  try {
    const raw = localStorage.getItem(DOWNLOADS_KEY);
    return raw ? JSON.parse(raw) : [];
  } catch {
    return [];
  }
}

export function addDownloadedAppId(id: string): string[] {
  const current = getDownloadedAppIds();
  if (!current.includes(id)) {
    const updated = [id, ...current];
    try {
      localStorage.setItem(DOWNLOADS_KEY, JSON.stringify(updated));
    } catch {
      // Ignore
    }
    return updated;
  }
  return current;
}

export function exportCatalogJson(catalog: AppItem[]) {
  const dataStr = 'data:text/json;charset=utf-8,' + encodeURIComponent(JSON.stringify(catalog, null, 2));
  const downloadAnchor = document.createElement('a');
  downloadAnchor.setAttribute('href', dataStr);
  downloadAnchor.setAttribute('download', `play_store_catalog_${Date.now()}.json`);
  document.body.appendChild(downloadAnchor);
  downloadAnchor.click();
  downloadAnchor.remove();
}
