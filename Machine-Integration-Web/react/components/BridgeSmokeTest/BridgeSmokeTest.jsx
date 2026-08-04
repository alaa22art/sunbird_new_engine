import { useEffect, useState } from 'react';
import { isAuthenticated, getCurrentUser } from '../../lib/authClient';

// Not wired into any route. Exists so Phase 0 (the react-mount bridge, the Vite
// build, and the shared auth client) can be verified end-to-end before Phase 1
// migrates a real module. Mount it manually via:
//   <react-mount component="'BridgeSmokeTest'"></react-mount>
export default function BridgeSmokeTest() {
  const [authed, setAuthed] = useState(false);

  useEffect(() => {
    setAuthed(isAuthenticated());
  }, []);

  const user = getCurrentUser();

  return (
    <div style={{ padding: '1rem', border: '1px solid var(--color-table-border)' }}>
      <strong>React bridge is mounted.</strong>
      <div>Session detected: {authed ? `yes (${user && user.username})` : 'no'}</div>
    </div>
  );
}
