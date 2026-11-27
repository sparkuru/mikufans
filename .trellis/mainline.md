# Trellis Mainline

## Initiative

- Title: Maintain the Mikufans Android module for Bilibili International 3.20.4.
- Parent task: none.
- Objective: Continue maintaining the fork's support for the `com.bilibili.app.in` 3.20.4 host and adapt features as needed. The repository's `readme.md` describes this existing direction; the user requested Trellis framework completion on 2026-11-27.
- Bootstrap task: `.trellis/tasks/archive/2026-11/00-bootstrap-guidelines/` prepared shared development rules without implementing product features. The task was archived in commit `b3931f1f05280d20a3b5ca78ff759b0a46a45aba`, whose staged index and committed snapshot passed independent checks.

## Requirements and sources

- `readme.md` identifies BBZQ v1.0.3 as the upstream base, the host package/version, and the Android Docker build route. Existing source and Xposed metadata are evidence of implementation, not proof that every inherited feature works on a device.
- `AGENTS.md` identifies available Android device categories and the archived multi-APK location. Confirm actual availability and host version at task time.
- The unchecked sharing, update-detection, emoji, external-link, and telemetry items in `readme.md` are proposed or unfinished ideas. Their order and acceptance are not approved by this bootstrap request.
- Preserve persisted preferences, host/module interoperability, and applicable third-party notices. No separate product PRD or approved implementation order was found in the current project sources.

## Continuation

- mode: guided
- serial authorization: none
- next pulse: after archive or on a project-relevant request

## Work and evidence

| Work | State | Readiness and evidence |
| --- | --- | --- |
| Bootstrap shared development policy | complete | The task record and evidence are in `.trellis/tasks/archive/2026-11/00-bootstrap-guidelines/`; archive commit `b3931f1f05280d20a3b5ca78ff759b0a46a45aba` contains exactly one Codex trailer. |
| Product feature ideas in `readme.md` | proposed | A user choice and a normal task with acceptance criteria are needed before implementation. |

The debug build, current-session policy loading, archive commit, and isolated delivery snapshot passed; no device behavior or CI build was verified. The next product task requires the user's choice among the proposed ideas.

On task start or approved scope change, map the task to this record. After verification and archive, add only actual results, limitations, task location, and commit evidence. A project-relevant no-task pulse reads mainline, task records, Git state, and validation evidence; report the current objective, blockers, supported candidates, and next permitted action without creating work in guided mode. In paused mode make no changes. Advance serial work only through the normal Trellis lifecycle when the user has explicitly authorized its bounded ordered list and stop conditions; stop on ambiguity, risk, dirty state, or unmet dependencies.
