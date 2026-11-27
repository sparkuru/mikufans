# Development Principles

The repository is an actively developed fork of BBZQ targeting Bilibili International `com.bilibili.app.in` 3.20.4. Existing host compatibility, persisted settings, external module contracts, upstream attribution, and any real user data are not disposable merely because development is local. Treat unfinished ideas in `readme.md` as proposals until the user chooses a task.

## Scope and design

Inspect and reuse the current hook, settings, reflection, error-handling, and build paths before adding new ones. Solve the approved requirement directly; avoid speculative extension points, fallback chains, compatibility adapters, and dependencies without a demonstrated need. For unreleased behavior with no established external contract, change related callers together rather than retaining unused aliases. Preserve existing user data unless its disposability and the requested operation are established. Keep unrelated refactors, formatting, and dependency changes out of the task.

Before fixing a defect, identify the actual behavior and execution path, reproduce when practical, and distinguish evidence from hypotheses. Handle expected failures explicitly; do not hide a failed critical operation behind a success-looking default. Preserve intentional nullable behavior where an optional host API is absent, and do not claim a missing hook was installed.

## Validation and access

Use the smallest meaningful checks for the affected user behavior, then applicable build, device, or integration checks from [development.md](development.md). Do not weaken assertions, suppress failures, or treat a successful build as proof that a hook works inside the host. Record commands, results, fixtures, versions, and unverified paths; use real-device checks for native interactions when needed.

Development convenience may use explicit, separate test configuration where appropriate. Never expose production credentials, weaken production security defaults, disable the behavior being tested, open new ports, or destroy unknown data merely to make a test convenient. This project currently has no preview server or service binding.

## Workspace and documents

Preserve pre-existing changes and local values; clean up only artifacts known to belong to the current task. Do not edit or create README files unless the user explicitly requests it. Keep durable agent rules in this project-owned spec layer and task-specific decisions and evidence in the normal Trellis task. Before completion, compare the diff with acceptance criteria, record real checks and limitations, and apply the review gate in [review-and-archive.md](review-and-archive.md).
