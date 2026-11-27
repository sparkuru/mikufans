# Trellis Plus Project Policy

- ownership: project-shared
- source: project-authored
- tracking: commit this file and its referenced project-owned detail files

## Reading and context

The main session reads this index and `.trellis/mainline.md` at task start, after a substantial interruption, and before commit or archive. Register this file and each applicable detail file separately in both task `implement` and `check` contexts with `task.py add-context`; the installed loader does not expand Markdown links. Validate those manifests before dispatch. The project-authored entry in root `AGENTS.md` makes this step discoverable; it is not proof that a worker received the files.

| File | When to load |
| --- | --- |
| [development-principles.md](development-principles.md) | Every project task. |
| [android-conventions.md](android-conventions.md) | Android application, hook, settings, compatibility, or related verification work. |
| [development.md](development.md) | Toolchain setup, Android builds, configuration, or device validation. |
| [ui-validation.md](ui-validation.md) | Native UI work or any future browser-accessible UI. |
| [review-and-archive.md](review-and-archive.md) | Planning checks, submit-ready review, archive, or attribution. |

## Continuity and ownership

Use `.trellis/mainline.md` for current direction, evidence, and continuation authority; keep detailed task evidence in the normal task directory. Default to guided continuation: recommend a supported next task and wait for the user to choose. A paused initiative makes no changes; serial continuation requires an explicit bounded list, order, and stop conditions. The main session owns task lifecycle, decisions, commits, and archival.

Do not customize Trellis runtime, generated platform files, or the managed section of `AGENTS.md`. Existing backend templates are upstream scaffolding, not Android conventions. Shared rules live here; local `.codex/`, `.agents/`, and similar assistant files remain untracked. Consult [third-party notices](../../../third_party/index.md) for retained upstream material and preserve its notices.
