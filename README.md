# EinkLab 墨屏实验室

[English](#english) | [中文](#中文)

---

<a id="中文"></a>
## 中文

给墨水屏安卓设备（如文石 BOOX、Bigme 等）用的开源小工具 App：Kotlin + Jetpack Compose + Material3 实现，v0.1.0。

它是桌面版 [eink-toolkit](https://github.com/<you>/eink-toolkit)（Python 版墨水屏工具箱）的安卓延续，功能思路一脉相承，代码全部重写。

### 无商业关联声明

**本项目为独立开源项目，与文石（BOOX）、亚马逊（Kindle）、乐天（Kobo）、Bigme 等任何厂商均无任何商业关系**：不隶属任何公司，不代言、不合作、无赞助。

- 无广告、无统计、无追踪
- 不含任何第三方 SDK（更没有 Firebase / 广告 / 数据分析类 SDK）
- 不含任何闭源库；依赖清单见下表，全部为 AOSP / Apache-2.0 开源组件
- 所有图片处理均在本地完成，不上传、不外传任何数据

### 功能（v0.1.0）

| 功能 | 说明 |
|---|---|
| 壁纸工坊 | 相册选图 → 选目标分辨率 → 灰阶 + 抖动处理 → 预览 → 保存到相册 / 设为系统壁纸 |
| 清残影 | 全屏黑白交替闪烁（次数可调，默认 10 次），帮助清除墨水屏残影 |
| 关于 | 开源声明、版本信息、无商业关联说明 |

壁纸工坊细节：

- 目标分辨率：`本机屏幕（自动读取）`（默认，最稳妥）+ `BOOX Palma（824 × 1648）` + 手动输入宽×高
- 抖动算法（纯 Kotlin 实现，无第三方图像库）：Floyd–Steinberg、Atkinson、Ordered Bayer 4×4、简单阈值
- 灰阶：2 / 4 / 16 / 256 可选；缩放：居中裁切填满 / 完整显示留白
- 设壁纸通过系统 `WallpaperManager`

### 截图

（待补充真机截图，见 `docs/screenshots/`）

### 构建

需要：JDK 17、Android SDK（含 API 35 平台）、Gradle 8.9+。

```bash
git clone https://github.com/<you>/einklab-android.git
cd einklab-android
gradle assembleDebug
# APK 输出：app/build/outputs/apk/debug/app-debug.apk
```

不想本地装 SDK？每次 push / PR 都会触发 GitHub Actions 自动编译，
在 Actions 页的 Artifacts 里下载 `einklab-debug-apk` 即可试用。

### 依赖清单（全部开源）

| 依赖 | 版本 | 许可证 |
|---|---|---|
| `androidx.compose:compose-bom` | 2025.01.00 | Apache-2.0 |
| `androidx.compose.ui:ui` / `ui-graphics` / `foundation` | BOM 管理 | Apache-2.0 |
| `androidx.compose.material3:material3` | BOM 管理 | Apache-2.0 |
| `androidx.compose.ui:ui-tooling-preview` | BOM 管理 | Apache-2.0 |
| `androidx.activity:activity-compose` | 1.10.1 | Apache-2.0 |
| `androidx.core:core-ktx` | 1.15.0 | Apache-2.0 |
| `androidx.lifecycle:lifecycle-runtime-ktx` | 2.8.7 | Apache-2.0 |
| Android Gradle Plugin | 8.7.3 | Apache-2.0 |
| Kotlin | 2.0.21 | Apache-2.0 |

图片解码用系统 `ImageDecoder`，抖动算法纯 Kotlin 手写——
**没有 Coil / Glide，没有 Firebase，没有广告/统计，没有闭源库。**

### 权限说明（最小权限）

| 权限 | 用途 |
|---|---|
| `READ_MEDIA_IMAGES`（API 33+）/ `READ_EXTERNAL_STORAGE`（API 29–32） | 「壁纸工坊」读取你选择的图片 |
| `SET_WALLPAPER`（普通权限） | 「壁纸工坊 → 设为系统壁纸」 |

### 设备分辨率预设

目前只收录有把握的公开规格：BOOX Palma 824 × 1648（来自文石公开规格）。
默认推荐 `本机屏幕（自动读取）`，在任何设备上都不会错。
新增预设请以厂商公开规格为准，欢迎提 PR（注明来源）。

### 参与贡献

见 [CONTRIBUTING.md](CONTRIBUTING.md)。

### 许可证

MIT — 见 [LICENSE](LICENSE)。Copyright (c) 2026 EinkLab contributors。

---

<a id="english"></a>
## English

An open-source toolbox for e-ink Android devices (BOOX, Bigme, etc.):
Kotlin + Jetpack Compose + Material3, v0.1.0.

It continues the idea of the desktop [eink-toolkit](https://github.com/<you>/eink-toolkit)
(Python), rewritten from scratch for Android.

### No commercial affiliation

**This is an independent open-source project with no commercial relationship
— no ownership, endorsement, partnership or sponsorship — with BOOX, Amazon
(Kindle), Rakuten (Kobo), Bigme or any other vendor.**

- No ads, no analytics, no tracking
- No third-party SDKs of any kind (no Firebase / ads / analytics SDKs)
- No closed-source libraries; every dependency is an AOSP / Apache-2.0
  open-source component (see table below)
- All image processing happens on-device; nothing is uploaded or shared

### Features (v0.1.0)

| Feature | Description |
|---|---|
| Wallpaper Lab | Pick a photo → choose target resolution → grayscale + dithering → preview → save to gallery / set as system wallpaper |
| De-ghost | Full-screen black/white flashing (adjustable count, default 10) to clear e-ink ghosting |
| About | Open-source notice, version info, no-affiliation statement |

Wallpaper Lab details:

- Target resolution: `This device (auto-detected)` (default, safest) + `BOOX Palma (824 × 1648)` + custom width × height
- Dithering (hand-written in pure Kotlin, no third-party imaging libs): Floyd–Steinberg, Atkinson, Ordered Bayer 4×4, plain threshold
- Gray levels: 2 / 4 / 16 / 256; scaling: center-crop fill / fit with padding
- Wallpaper is set via the system `WallpaperManager`

### Screenshots

(To be added from real devices, see `docs/screenshots/`)

### Build

Requirements: JDK 17, Android SDK (API 35 platform), Gradle 8.9+.

```bash
git clone https://github.com/<you>/einklab-android.git
cd einklab-android
gradle assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

No local SDK? Every push / PR triggers GitHub Actions to build automatically —
download `einklab-debug-apk` from the Artifacts section to try it.

### Dependencies (all open-source)

| Dependency | Version | License |
|---|---|---|
| `androidx.compose:compose-bom` | 2025.01.00 | Apache-2.0 |
| `androidx.compose.ui:ui` / `ui-graphics` / `foundation` | managed by BOM | Apache-2.0 |
| `androidx.compose.material3:material3` | managed by BOM | Apache-2.0 |
| `androidx.compose.ui:ui-tooling-preview` | managed by BOM | Apache-2.0 |
| `androidx.activity:activity-compose` | 1.10.1 | Apache-2.0 |
| `androidx.core:core-ktx` | 1.15.0 | Apache-2.0 |
| `androidx.lifecycle:lifecycle-runtime-ktx` | 2.8.7 | Apache-2.0 |
| Android Gradle Plugin | 8.7.3 | Apache-2.0 |
| Kotlin | 2.0.21 | Apache-2.0 |

Image decoding uses the platform `ImageDecoder`; dithering is hand-written
pure Kotlin — **no Coil / Glide, no Firebase, no ads/analytics, no
closed-source libraries.**

### Permissions (least privilege)

| Permission | Purpose |
|---|---|
| `READ_MEDIA_IMAGES` (API 33+) / `READ_EXTERNAL_STORAGE` (API 29–32) | Read the image you pick in Wallpaper Lab |
| `SET_WALLPAPER` (normal permission) | "Set as wallpaper" in Wallpaper Lab |

### Device resolution presets

Only vendor-published specs we are confident about are included: BOOX Palma
824 × 1648. `This device (auto-detected)` is the default and always correct.
PRs adding presets must cite the vendor spec as source.

### Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md).

### License

MIT — see [LICENSE](LICENSE). Copyright (c) 2026 EinkLab contributors.
