# Trellis Mainline

## Initiative

- Title: Maintain the Mikufans Android module for Bilibili International 3.20.4.
- Parent task: none.
- Objective: Continue maintaining the fork's support for the `com.bilibili.app.in` 3.20.4 host and adapt features as needed. The repository's `readme.md` describes this existing direction; the user requested Trellis framework completion on 2026-11-27.
- Bootstrap task: `.trellis/tasks/archive/2026-11/00-bootstrap-guidelines/` prepared shared development rules without implementing product features. The task was archived in commit `b3931f1f05280d20a3b5ca78ff759b0a46a45aba`, whose staged index and committed snapshot passed independent checks.

## Requirements and sources

- `readme.md` identifies BBZQ v1.0.3 as the upstream base, the host package/version, and the Android Docker build route. Existing source and Xposed metadata are evidence of implementation, not proof that every inherited feature works on a device.
- `AGENTS.md` identifies available Android device categories and the archived multi-APK location. Confirm actual availability and host version at task time.
- On 2026-11-27 the user requested that the unchecked sharing, update-detection, emoji, external-link, and telemetry items in `readme.md` be converted into `impl-bbzq-new-features`; they are recorded as a task tree. The user approved implementation of the update child task; the other items remain in planning.
- On 2026-11-27 the user initially set Android 8.1 / API 27 and then corrected the minimum to Android 8.0 / API 26, with libxposed API 102 or later; the lint task owns the corresponding build and policy changes. Preserve persisted preferences, host/module interoperability, and applicable third-party notices. No product-wide PRD beyond the planning task records or approved implementation order has been established.

## Continuation

- mode: guided
- serial authorization: none
- next pulse: after archive or on a project-relevant request

## Work and evidence

| Work | State | Readiness and evidence |
| --- | --- | --- |
| Bootstrap shared development policy | complete | The task record and evidence are in `.trellis/tasks/archive/2026-11/00-bootstrap-guidelines/`; archive commit `b3931f1f05280d20a3b5ca78ff759b0a46a45aba` contains exactly one Codex trailer. |
| Product feature ideas in `readme.md` | in progress | `.trellis/tasks/11-27-impl-bbzq-new-features/` maps all five items to child tasks. The update child was implemented in `ea4e37c` and archived at `.trellis/tasks/archive/2026-11/11-27-handle-forced-update/` after the user reviewed and accepted its recorded limitations; the other children remain in planning. |
| Android Lint backlog | in progress | `.trellis/tasks/11-27-fix-android-lint/` records the baseline of 79 errors and 48 warnings, the user's corrected Android 8.0 / API 26 and libxposed API 102+ minimums, a passing debug build and 18 tests, Lint with 0 errors and 12 documented warnings, and Android 11 regression evidence for the final APK. API 26–29, an Android 8.0 framework providing libxposed API 102, and authorized host Provider access remain unverified. Commit and archive remain pending. |

The bootstrap debug build, current-session policy loading, archive commit, and isolated delivery snapshot passed; CI behavior was not verified. The update task has a debug build, focused tests, and a controlled forced-update device comparison; its archived research record retains the Lint baseline, preference synchronization edge cases, and unverified host paths.

On task start or approved scope change, map the task to this record. After verification and archive, add only actual results, limitations, task location, and commit evidence. A project-relevant no-task pulse reads mainline, task records, Git state, and validation evidence; report the current objective, blockers, supported candidates, and next permitted action without creating work in guided mode. In paused mode make no changes. Advance serial work only through the normal Trellis lifecycle when the user has explicitly authorized its bounded ordered list and stop conditions; stop on ambiguity, risk, dirty state, or unmet dependencies.
