# Contributing to EinkLab

[English](#english) | [中文](#中文)

---

<a id="中文"></a>
## 中文

欢迎提 Issue / PR。几点约定：

1. **依赖红线**：只接受 AOSP / Apache-2.0 等开源许可的依赖。
   不接受 Firebase、广告、统计/追踪 SDK、任何闭源库。
   图片相关需求优先用系统 API（`ImageDecoder` / `BitmapFactory`）自己实现。
2. **设备预设**：新增分辨率预设必须以厂商公开规格为准，并在 PR 中注明来源；
   拿不准的一律不写，不要猜。
3. **代码风格**：Kotlin，注释用中文；Compose 界面保持大字、高对比，
   照顾墨水屏阅读体验。
4. **权限**：新增权限必须在 Manifest 注释和 README 权限表里说明用途，
   遵循最小权限原则。
5. **提交前**：确认 GitHub Actions 的 `assembleDebug` 通过。

---

<a id="english"></a>
## English

Issues and PRs are welcome. A few ground rules:

1. **Dependency red line**: only dependencies under open-source licenses
   (AOSP / Apache-2.0 etc.). No Firebase, no ads, no analytics/tracking SDKs,
   no closed-source libraries. Prefer platform APIs (`ImageDecoder` /
   `BitmapFactory`) for image work.
2. **Device presets**: new resolution presets must cite vendor-published specs
   in the PR. When in doubt, leave it out — never guess.
3. **Code style**: Kotlin with Chinese comments; keep Compose UI large-type
   and high-contrast for e-ink readability.
4. **Permissions**: any new permission must be documented with its purpose in
   both the Manifest comment and the README permission table. Least privilege.
5. **Before submitting**: make sure the GitHub Actions `assembleDebug` passes.
