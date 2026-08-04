// Talks to the same getLabels.pub.srvc endpoint AngularJS's systemMessagesLoader uses
// (see src/main/webapp/js/app.js) rather than reaching into Angular's $translate
// service, so this works standalone on pages the AngularJS shell hasn't rendered yet.
// Labels are cached in-memory per page load; there is no cross-bundle cache with the
// AngularJS app, so a page that has both an Angular-rendered shell and a React-mounted
// body currently fetches labels twice. Worth collapsing later (e.g. by exposing
// util.systemMessages on window) but not needed for a single migrated page.

import { apiRequest } from './httpClient';

export const DEFAULT_LOCALE = 'en_us';

let labelsPromise = null;

function fetchLabels(tenantId) {
  if (!labelsPromise) {
    labelsPromise = apiRequest('getLabels.pub.srvc', tenantId != null ? tenantId : null).then((labels) => {
      const byCode = {};
      (labels || []).forEach((label) => {
        byCode[label.code] = label.description || {};
      });
      return byCode;
    });
  }
  return labelsPromise;
}

export async function loadTranslator(tenantId, locale) {
  const byCode = await fetchLabels(tenantId);
  const resolvedLocale = locale || DEFAULT_LOCALE;
  return function translate(code) {
    return (byCode[code] && byCode[code][resolvedLocale]) || code;
  };
}
