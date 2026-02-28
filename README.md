📡 Vibe Radar
Privacy-First Social Discovery using Local AI & Hybrid Location Services.
 
> > [!IMPORTANT]
> **Work in Progress:** The `main` branch is reserved for stable production releases. For the latest architectural implementations, 512-dim vector matching logic, and active development, please switch to the [**`develop`** branch]

🛰️ The Mission

Vibe Radar (internally known as Project Beacon) is an open-source social discovery application designed to connect people with shared interests while maintaining absolute digital sovereignty.
Unlike traditional platforms that track and centralize your movements, Vibe Radar utilizes a "Blind Relay" architecture. The server acts as a zero-knowledge post office—facilitating connections without ever having the keys to decrypt your profile, your precise location, or your conversations.

🛡️ Privacy Pillars
* On-Device Semantic Matching: We use Google MediaPipe to process interests locally. Your "vibe" is converted into a vector on your phone, and matching happens via local math (Cosine Similarity), not on a server.
* Hardware-Level Location Privacy: The app is physically incapable of requesting GPS-level precision. We use coarse-only triangulation to ensure you stay "on the map" without being "on the radar".
* Ephemeral Interactions: All communications (Voice, Text, and Live Snap) are End-to-End Encrypted (E2EE) and operate on a strict "View Once" and Time-To-Live (TTL) policy.
* Anti-Forensic Security: Built-in protection against screenshots and unauthorized gallery access to prevent catfishing and data leaks.


### Build and Run Android Application

To build and run the development version of the Android app, use the run configuration from the run widget
in your IDE’s toolbar or build it directly from the terminal:
- on macOS/Linux
  ```shell
  ./gradlew :composeApp:assembleDebug
  ```
- on Windows
  ```shell
  .\gradlew.bat :composeApp:assembleDebug
  ```

### Build and Run iOS Application

To build and run the development version of the iOS app, use the run configuration from the run widget
in your IDE’s toolbar or open the [/iosApp](./iosApp) directory in Xcode and run it from there.

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…