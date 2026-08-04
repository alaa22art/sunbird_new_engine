// Mirrors src/main/webapp/js/util.js#createApiRequest + APIInterceptorService.js so React
// modules hit the same REST endpoints, under the same base path, with the same bearer
// token the AngularJS shell already logged in with.

import { getToken } from './authClient';

const CONTEXT_ROOT = '/miw/';
const API_PATH = `${CONTEXT_ROOT}services/`;

export async function apiRequest(requestMapping, payload, options = {}) {
  const token = getToken();
  const headers = {
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
    ...options.headers,
  };

  const response = await fetch(`${window.location.origin}${API_PATH}${requestMapping}`, {
    method: options.method || 'POST',
    headers,
    body: payload != null ? (typeof payload === 'string' ? payload : JSON.stringify(payload)) : undefined,
  });

  if (!response.ok) {
    const error = new Error(`API request to ${requestMapping} failed with status ${response.status}`);
    error.status = response.status;
    error.response = response;
    throw error;
  }

  const contentType = response.headers.get('content-type') || '';
  return contentType.includes('application/json') ? response.json() : response.text();
}
