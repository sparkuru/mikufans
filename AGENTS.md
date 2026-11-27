<!-- TRELLIS:START -->
# Trellis Instructions

These instructions are for AI assistants working in this project.

This project is managed by Trellis. The working knowledge you need lives under `.trellis/`:

- `.trellis/workflow.md` — development phases, when to create tasks, skill routing
- `.trellis/spec/` — package- and layer-scoped coding guidelines (read before writing code in a given layer)
- `.trellis/workspace/` — per-developer journals and session traces
- `.trellis/tasks/` — active and archived tasks (PRDs, research, jsonl context)

If a Trellis command is available on your platform (e.g. `/trellis:finish-work`, `/trellis:continue`), prefer it over manual steps. Not every platform exposes every command.

If you're using Codex or another agent-capable tool, additional project-scoped helpers may live in:
- `.agents/skills/` — reusable Trellis skills
- `.codex/agents/` — optional custom subagents

Managed by Trellis. Edits outside this block are preserved; edits inside may be overwritten by a future `trellis update`.

<!-- TRELLIS:END -->

---

## APKs

check `archive/apks` for bilibili intl 3.20.4 multi-apks.

## refers

refer to upstream repo or third-party's forums/webs/repos/... for our impl.

## devices

### 可使用的 android 设备说明如下

- android 11 开发板, `product:rk3568_firefly_aioj`, root, 开发板密码为 `0000`, 属于测试密码，可以直接使用，不担心泄露. 尽可能无人值守地使用开发板, 可以通过 adb shell/screenshot 等方式, 确定开发板状态, 自行 `adb tap` 点击开发板屏幕
- android 13 手机, `product:lineage_kugo`, root
- android 15 手机, `product:xagapro`, root, 密码为 `0000`

### 通用要求

1. 可用的 android 设备, 见前文
2. 优先使用 `$browseros-neo` skill + 对应 mcp 展开网络检索, 若不可用再退选 agent 自身的 search 工具
