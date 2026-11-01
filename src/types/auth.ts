export interface UserProfile {
  name: string;
  email: string;
  photoUrl?: string;
  isVerifiedDeveloper: boolean;
  joinedDate: string;
}

const AUTH_KEY = 'play_store_user_v1';

export const DEFAULT_USER_EMAIL = 'modm55085@gmail.com';

export function getStoredUser(): UserProfile | null {
  try {
    const raw = localStorage.getItem(AUTH_KEY);
    return raw ? JSON.parse(raw) : null;
  } catch {
    return null;
  }
}

export function saveStoredUser(user: UserProfile | null) {
  try {
    if (user) {
      localStorage.setItem(AUTH_KEY, JSON.stringify(user));
    } else {
      localStorage.removeItem(AUTH_KEY);
    }
  } catch {
    // Ignore
  }
}
