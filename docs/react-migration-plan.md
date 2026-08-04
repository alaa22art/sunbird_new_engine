# eHOPE / Machine-Integration-Web: React Migration Plan

Strangler-fig, module-by-module migration of the `Machine-Integration-Web` frontend
from AngularJS 1.8 to React. This document is the plan only — no code changes are
included.

## 1. Current state

AngularJS 1.8 SPA, RequireJS/AMD module loading, ui-router (24 states), Kendo UI +
jQuery UI widgets, angular-material 1.1.4, d3/c3 charts, gridstack, OAuth2
password-grant auth (`oauth/token`), and STOMP-over-SockJS for real-time PCR data.
Served under base path `/miw/`, backed by the Spring REST APIs in
`Machine-Integration-Core` / `Certacure-Core`.

AngularJS 1.x reached end-of-life in December 2021 (no further security patches),
and several core dependencies (angular-material 1.1.4, d3 v4/c3 0.4) are similarly
unmaintained. This plan replaces the frontend incrementally rather than as a
big-bang rewrite, given this is a live clinical lab system.

## 2. Target architecture

- **Shell-and-satellites**: keep the existing AngularJS shell (header, nav, footer,
  auth guard) as the host. Each migrated feature becomes a standalone React bundle
  mounted into a `<div>` via a thin AngularJS directive wrapper (`react-mount`), one
  per route.
- **Routing split**: `$stateProvider` keeps owning top-level navigation. Migrated
  states point their `template` at the mount-point directive instead of an
  AngularJS controller/view; unmigrated states are untouched. This avoids a dual-router
  fight and lets modules flip over one at a time without touching global nav.
- **Shared auth/session**: the OAuth2 token is already stored client-side
  (localStorage/cookie via the existing `loginService`) — React modules read the
  same token and hit the same REST endpoints, so there is no dual-login problem
  during the transition.
- **Build tooling**: introduce Vite alongside the existing Gulp pipeline. Gulp keeps
  building the legacy AngularJS bundle; Vite builds the React bundles as separate
  chunks emitted into the same `webapp` output dir, referenced by the mount-point
  directives. Retire Gulp only after the last module is ported.
- **Styling**: new modules use CSS Modules or a component library (evaluate MUI or
  Ant Design) rather than fighting the existing Bourbon/SCSS partials — but the
  shared `_colors.scss` / `_fonts.scss` / RTL rules get extracted into design tokens
  both stacks can consume, since **Arabic RTL support is a hard requirement and must
  not regress**.
- **Real-time (STOMP/SockJS)**: extract the existing `ng-stomp` connection into a
  framework-agnostic client (plain JS module or a small React context) so both
  AngularJS and React screens can subscribe to the same socket without opening
  duplicate connections. This is the trickiest interop piece and should not be the
  pilot.

## 3. Module inventory & risk-based priority

| Phase | Modules | Why here |
|---|---|---|
| **0 — Foundation** | Build the `react-mount` bridge, Vite pipeline, shared auth/HTTP client, design tokens (colors/RTL) | No user-facing change; proves the interop pattern before touching real screens |
| **1 — Pilot (low risk, low traffic)** | `userProfile`, `passwordReset`, `accessDenied` | Simple forms, no Kendo grids, no real-time — cheapest way to validate the whole pipeline end-to-end |
| **2 — Admin CRUD** | `usersManagement`, `rolesManagement`, `groupsManagement`, `tenantManagement`, `tenantMessages`, `lkpManagement`, `mappingCodes`, `branch` | Standard list/edit/dialog patterns, low clinical risk, high repetition — good place to build a reusable data-table/form kit for later phases |
| **3 — Core lab config** | `devices`, `drivers`, `driverAssays`, `deviceDetails`, `deviceTestsMapping`, `machineSetup`, `machineTypeSetup`, `mappingDriverTests`, `testCatalog`, `testMapping`, `testPanel`, `machineAssignTests` | Heavier Kendo grid usage, more business logic, but not real-time and not directly patient-facing |
| **4 — Clinical/real-time (highest risk, last)** | `machineResults`, `patientSampleLookup`, `pcrRealTimePatients`, `realTimePCR`, `transactionLog`, `messegesTransactionLog` | Live patient/test data, STOMP sockets, d3/c3 charts — migrate only after the socket bridge and chart replacement are proven elsewhere |
| **5 — Shell cutover** | `login`, `header`/nav, `footer`, `languageSwitcher` | Migrate the shell last, once every state it routes to is already React — then delete AngularJS/RequireJS/Kendo/jQuery UI entirely |

## 4. Milestones

1. Phase 0 complete + Phase 1 pilot shipped and validated in production → confirms
   the strangler pattern actually works for this app.
2. Reusable data-table/dialog kit exists → unblocks Phases 2–3 to move quickly (most
   of those modules are structurally similar CRUD screens).
3. Socket bridge validated on one Phase 4 module → de-risks the rest of real-time
   before committing the whole phase.
4. Shell cutover → AngularJS, RequireJS, Kendo UI, jQuery UI, angular-material,
   d3 v4/c3 removed from `package.json` and the build.

## 5. Key risks to flag now

- **Kendo UI license**: confirm whether Kendo is still licensed/needed for any
  remaining grids before planning a replacement (grid library choice affects
  Phase 2–3 estimates significantly).
- **No test coverage today** — recommend adding at least smoke/E2E coverage
  (Playwright) per module *before* migrating it, so behavioral parity old vs. new
  can be verified.
- **RTL** is easy to silently regress in a new component library; bake it into the
  Phase 0 design-token work, not bolted on later.
