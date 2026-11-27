# Bootstrap Evidence

Scope: full Trellis Plus bootstrap on 2026-11-27, using the current `CHACKLIST.md` beside the user-supplied skill. The pre-existing state was a clean worktree, Trellis 0.6.17, no session task pointer, one `00-bootstrap-guidelines` task in progress, empty generic backend specs, no mainline or project-owned Trellis Plus policy, and no Codex-local UUPM. The user authorized the bootstrap and UUPM initialization. The existing task was resumed; no duplicate task was created.

`PASS` means evidence exists for the stated current scope; `GAP` means a missing current requirement; `UNKNOWN` means required evidence remains unavailable; `N/A` is conditional on the stated trigger. Future execution is not represented as already completed. The authoritative procedures and current checklist are supplied by the skill; this task record is evidence, not another project policy.

## Current checklist

| ID | Before → target, evidence and affected path | State |
| --- | --- | --- |
| 01 | No ownership classification → project-owned rules in `.trellis/spec/trellis-plus/`, task data here, local platform files excluded, upstream runtime read-only. | PASS |
| 02 | Existing upstream baseline → 35 tracked protected files and the managed `AGENTS.md` block match a clean 0.6.17 `--codex --opencode --no-monorepo -y` initialization; nine backend/guide scaffolds also match. No protected customization was made. | PASS |
| 03 | No shared Plus policy → `.trellis/spec/trellis-plus/index.md` identifies project ownership and links the detail files. | PASS |
| 04 | Generic backend placeholders → source-backed Android, development, UI and review actions with project paths, commands and explicit limits. | PASS |
| 05 | No portable Plus entry or context → the project-authored `AGENTS.md` extension requires mainline/policy reads and explicit context registration. Both eight-entry manifests validate; a real Codex check agent received their contents and the PRD. New machines must enable or use their own supported loading path. | PASS |
| 06 | Version 0.6.17, no backup found → `trellis update --dry-run` reported tracked templates unchanged and project data preserved. Rules remain outside template targets; an actual later update has not been exercised. | PASS |
| 07 | Local platform paths were ignored through this user's global Git excludes → they remain local; staged candidate paths must be explicit. A clone without those global excludes must configure its own local exclusions before staging. No files were staged for this bootstrap check. | PASS |
| 08 | No dedicated Trellis notice inventory → `third_party/index.md` identifies retained Trellis 0.6.17 and the exact Gradle Wrapper JAR. Their collected license texts match the installed distribution and JAR respectively; the root project license is unchanged. | PASS |
| 09 | Existing host compatibility and preferences lacked shared development exceptions → `development-principles.md` protects established contracts, user data and production security. | PASS |
| 10 | No project-specific scope policy → the shared principles require existing execution paths, bounded changes and justified dependencies. | PASS |
| 11 | No shared evidence discipline → rules distinguish actual behavior, hypotheses, build results and device results. The current task did not diagnose or alter a product defect. | PASS |
| 12 | Initially clean workspace → no unrelated source or README edits; task evidence stays under the normal task directory. | PASS |
| 13 | Docker command existed, no shared validation profile → `development.md` records exact build, task-context and device checks, lack of test suites, and the SDK 35/37 CI discrepancy. | PASS |
| 14 | No submit-ready gate → `review-and-archive.md` records blocking and optional review criteria before commit/archive. This documentation/bootstrap change has no product-facing UI change; actual snapshot and archive evidence remain separate delivery checks. | PASS |
| 15 | `hako` already existed → `./hako gradle --offline --no-daemon assembleDebug` succeeded with the existing cache and SDK 37 after Docker access was authorized. This is not a cold-start or device result. | PASS |
| 16 | Native APK, no previewable service → no service lifecycle or `preview.sh` is required. | N/A |
| 17 | No preview service or published listener → no preview network configuration applies. | N/A |
| 18 | No preview entry → no preview console renderer applies. | N/A |
| 19 | No preview listener or host publication → host-address discovery/readiness rules do not apply. | N/A |
| 20 | Existing wrapper and signing configuration, no dotenv consumer → `development.md` documents actual inputs, precedence, non-forwarded host signing environment and secret boundary. No unused `.env` was created. | PASS |
| 21 | No local configuration keys added → policy requires preserving values, appending missing keys once and reporting removals; no synchronization implementation was needed or exercised. | PASS |
| 22 | Native Views UI and no Codex-local UUPM → user accepted `uipro init --ai codex`; offline bundled initializer 2.10.2 created ignored local files. `search.py --help` and a domain search succeeded. | PASS |
| 23 | No UUPM workflow → `ui-validation.md` records planning, task research/design, both contexts, implementation, checks and stable spec updates, including decline and license branches. No UI design task occurred. | PASS |
| 24 | No browser-accessible product UI → Playwright is not a substitute for native Android device checks; future web work has an automation checkpoint. | N/A |
| 25 | No browser application or Playwright runner → a future browser task must establish its real profile; no commands or app were invented. | N/A |
| 26 | No UI behavior changed during bootstrap → native device and desktop/mobile browser execution are not triggered; applicable future native checks are documented. | N/A |
| 27 | No generic user smoke test requested → the build, context and policy checks were run by agents; future residual human-only risks feed the review gate. | PASS |
| 28 | No archive attribution policy → `review-and-archive.md` specifies the exact once-per-task trailer. The actual archive commit and trailer have not yet been verified. | UNKNOWN |
| 29 | Default archive could auto-commit a fixed message → installed 0.6.17 provides `--no-commit`; the command was used successfully after confirming no configured `after_archive` hook. The separate journal has its own `--no-commit` option. | PASS |
| 30 | No mainline → `.trellis/mainline.md` imports the existing host/version direction and labels unchecked README ideas as proposed; no product priority or approval was invented. | PASS |
| 31 | No continuity record → task start, build, loading and the no-commit archive are recorded; the archive commit and complete lifecycle remain unverified. | UNKNOWN |
| 32 | No explicit continuation control → guided mode, paused behavior, bounded serial authority and main-session ownership are documented. | PASS |
| 33 | No Plus self-check → the current skill-local checklist was read and every ID is reported here. Final archive and snapshot rows must be updated with actual evidence. | PASS |
| 34 | `00` in progress → source-backed rules supersede its generic backend template goals. `--no-commit` moved it to `.trellis/tasks/archive/2026-11/00-bootstrap-guidelines/` with status `completed` and date 2026-11-27; the required archive commit is pending. | UNKNOWN |
| 35 | Initialization delivery was incomplete → policy, build, local integration and task context are prepared; archive commit and final repository snapshot are still required. | UNKNOWN |
| 36 | Trellis runtime was already tracked → independent 0.6.17 baseline comparison and notice checks passed. An isolated candidate-only export passed task/context/link checks without local assistant files; an actual authorized index or delivery-commit snapshot has not yet been checked. | UNKNOWN |

## Verification detail

- The 0.6.17 baseline comparison used a fresh isolated initialization with `--codex --opencode --no-monorepo -y --user 9bishi`; protected tracked paths, generated backend/guide scaffolds and the managed `AGENTS.md` block were compared byte-for-byte. The CLI's dry-run update reported no template changes; this does not establish behavior after a future real update.
- `python3 .trellis/scripts/task.py validate .trellis/tasks/00-bootstrap-guidelines` passed with eight implement and eight check entries. Simulated native start events resolved all eight entries for both roles, and the real check agent reported native `SubagentStart` injection. This proves the current session's loading, not automatic setup on a new machine.
- The debug build completed successfully with `./hako gradle --offline --no-daemon assembleDebug`; it used existing image/SDK/dependency caches. AGP emitted deprecation warnings. No device, host feature, signed release, or CI run was performed; `.github/workflows/ci.yml` explicitly installs SDK 35 while the app requests SDK 37.
- `uipro init --ai codex --offline` generated ignored project-local skills after the user's explicit approval. The installed search entry, scripts and data exist; a help invocation and a read-only UX search succeeded. Another machine needs its own local integration and must respect existing authorization and files.
- The Trellis CLI and bundled core LICENSE files are byte-identical to `third_party/Trellis-0.6.17-LICENSE`. The wrapper JAR includes `META-INF/LICENSE`, retained exactly as `third_party/Gradle-wrapper-2db75c4-LICENSE`; its binary hash matches the public Gradle 8.10–8.12.1 wrapper checksums, while its original generating version among those matches is unknown.
- The independent policy reviewer found two issues: archive hooks can still execute with `--no-commit`, and the UUPM workflow needed an explicit decline/research branch. Both were corrected in the shared policy. Local Markdown links and whitespace were checked.
- Candidate-only exports of `HEAD` plus the explicit proposed project files passed read-only task listing, context validation, local link resolution, and exclusion of local assistant/cache files. The post-move export also verified the archived task and absence of the active task. The fresh temporary Git repository had no `master` ref and emitted the expected stale-branch warning. Neither export was a Git index or delivery commit, so it cannot prove the eventual snapshot.
- `python3 .trellis/scripts/task.py archive 00-bootstrap-guidelines --no-commit` moved the task to the archive and cleared the active pointer. `task.py list` reported zero active tasks; `task.py validate` also passed at the archived path. No commit was made.
- Review for the no-commit archive was `human-not-needed`: this task changed project documentation/configuration and notices, the relevant build and loading checks passed, and no product UI or behavior changed. The actual delivery snapshot is still pending and must be verified before the separately authorized commit.

## Delivery still pending

Before treating bootstrap as complete, obtain separately required commit authority. Inspect explicit candidate paths, validate the actual staged or committed snapshot in an isolated checkout, verify the archive commit and its single Codex trailer, and update mainline and this evidence. The no-commit archive has already happened; do not repeat it. Do not stage local assistant files or push.
