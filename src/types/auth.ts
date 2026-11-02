export interface UserProfile {
  name: string;
  email: string;
  isVerifiedDeveloper: boolean;
  joinedDate: string;
}

const AUTH_KEY = 'play_store_dev_auth_v2';

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
