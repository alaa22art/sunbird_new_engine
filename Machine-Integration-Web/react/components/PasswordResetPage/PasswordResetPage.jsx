import { useEffect, useMemo, useState } from 'react';
import jwtDecode from 'jwt-decode';
import { apiRequest } from '../../lib/httpClient';
import { loadTranslator, DEFAULT_LOCALE } from '../../lib/i18n';
import './PasswordResetPage.css';

const LOGIN_URL = '/miw/login';

// React port of the AngularJS passwordReset module (component/passwordReset/*).
// Reachable at /miw/password-reset?t=<jwt> -- see the 'password-reset' state in
// src/main/webapp/js/routes.js, which now renders <react-mount component="'PasswordResetPage'">
// instead of the old passwordResetCtrl/password-reset-view.html.
//
// Cross-framework navigation isn't wired up yet (Phase 1), so moving to/from the
// still-AngularJS login page is a full page load rather than an in-SPA transition.
export default function PasswordResetPage() {
  const token = useMemo(() => new URLSearchParams(window.location.search).get('t'), []);
  const [username, setUsername] = useState('');
  const [tenantId, setTenantId] = useState(null);
  const [translate, setTranslate] = useState(null);
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [submitted, setSubmitted] = useState(false);
  const [errorMessage, setErrorMessage] = useState(null);

  useEffect(() => {
    if (!token) {
      window.location.assign(LOGIN_URL);
      return;
    }
    const decoded = jwtDecode(token);
    setUsername(decoded.user.username);
    setTenantId(decoded.user.tenantId != null ? decoded.user.tenantId : null);
  }, [token]);

  useEffect(() => {
    if (!token) {
      return;
    }
    loadTranslator(tenantId, DEFAULT_LOCALE).then(setTranslate);
  }, [token, tenantId]);

  if (!token || !translate) {
    return null;
  }

  const passwordsMismatch = confirmPassword.length > 0 && password !== confirmPassword;
  const canSubmit = password.length > 0 && confirmPassword.length > 0 && !passwordsMismatch && !submitting;

  async function handleSubmit(event) {
    event.preventDefault();
    if (!canSubmit) {
      return;
    }
    setSubmitting(true);
    setErrorMessage(null);
    try {
      await apiRequest('changeForgottenPassword.pub.srvc', { password, token });
      setSubmitted(true);
      window.setTimeout(() => window.location.assign(LOGIN_URL), 1500);
    } catch (err) {
      setErrorMessage(translate('somethingWrong'));
      setSubmitting(false);
    }
  }

  return (
    <div className="password-reset-wrapper">
      <form className="password-reset-card" onSubmit={handleSubmit}>
        <h1 className="password-reset-title">{translate('passwordReset')}</h1>

        <label className="password-reset-field">
          <span>{translate('username')}</span>
          <input type="text" value={username} disabled />
        </label>

        <label className="password-reset-field">
          <span>{translate('passwordNew')}</span>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </label>

        <label className="password-reset-field">
          <span>{translate('passwordConfirm')}</span>
          <input
            type="password"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
            required
          />
          {passwordsMismatch && (
            <span className="password-reset-error">{translate('passwordNoMatch')}</span>
          )}
        </label>

        {errorMessage && <div className="password-reset-error">{errorMessage}</div>}
        {submitted && <div className="password-reset-success">{translate('success')}</div>}

        <button type="submit" className="password-reset-submit" disabled={!canSubmit}>
          {translate('submit')}
        </button>
      </form>
    </div>
  );
}
