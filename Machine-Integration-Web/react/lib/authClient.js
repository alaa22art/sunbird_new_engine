// Reads the same session the legacy AngularJS app writes (see src/main/webapp/js/util.js
// setUserData/getStorageByName). The app currently always ends up writing to
// sessionStorage regardless of "remember me" (a pre-existing quirk in getStorageByName),
// so we check sessionStorage first and fall back to localStorage to stay correct either way.

function readStorageProperty(storage, key) {
  const raw = storage.getItem(key);
  if (raw == null) {
    return null;
  }
  try {
    return JSON.parse(raw);
  } catch (e) {
    return raw;
  }
}

function readFromEitherStorage(key) {
  const fromSession = readStorageProperty(window.sessionStorage, key);
  if (fromSession != null) {
    return fromSession;
  }
  return readStorageProperty(window.localStorage, key);
}

export function getToken() {
  return readFromEitherStorage('token');
}

export function getCurrentUser() {
  return readFromEitherStorage('user');
}

export function getAuthorities() {
  return readFromEitherStorage('authorities') || [];
}

export function isAuthenticated() {
  return !!getToken();
}
