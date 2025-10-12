<h1 align="center"><img src="images/fromgtog_header.png" alt="header" /></h1>
<h1 align="right" id="title">FromGtoG v. 8.1.16</h1>

<h2 id="index">Index</h2>

- [Introduction](#introduction)
- [Features](#features)
- [Download (Stable)](#download)
    - [MacOS](#download-macos)
    - [Windows](#download-windows)
    - [Linux](#download-linux)
- [Screenshot](#screenshot)
- [Logs from the developer](#news)
- [For developers](#for-developers)
- [Tested on](#tested-on)
- [Support me](#support-me)

<h2 id="introduction">Introduction</h2>

# FromGtoG 8.1.16: Advanced Git Repository Backup and Migration Utility

I'm excited to announce the release of **FromGtoG 8.1.16**! This application is an essential **cross-platform desktop
utility** for developers.

The **initial idea** focused only on cloning from GitHub to Gitea. Following a rapid evolution of features, FromGtoG is
now a powerful, full-fledged tool for **batch cloning** and **secure migration** between multiple platforms.

The application currently supports robust two-way cloning and backup across:

* **GitHub**
* **Gitea**
* **GitLab**
* **Local** file systems (supporting Local ↔ Remote cloning).

## Intelligent Cloning, Granular Control & Performance

FromGtoG goes beyond simple batch operations, offering advanced filtering and performance capabilities:

* **Detailed Logging:** Produces a comprehensive **log file** that allows you to analyze the application's work in
  detail, ensuring that every necessary repository was successfully cloned and verified.
* **Rate Limit Prevention:** Features an optional setting to define a **time interval between cloning calls** to prevent
  being banned or rate-limited by the remote server. This is especially useful for sequential (non-multi-threaded)
  operations.
* **Multi-Threading:** Utilizes **multi-threading** to **parallelize** the cloning process across multiple CPU cores,
  maximizing **speed** and **efficiency** on modern hardware.
* **Precise Repository Filtering:** Select exactly which repositories to clone based on their status or type:
    * **Private Repos**
    * **Public Repos**
    * **Organization Repos**
    * **Starred Repos**
    * **Forked Repos**
    * **Archived Repos** (Clones repositories archived on platforms like GitHub or Gitea, crucial for complete backups
      and long-term storage.)
* **Granular Control:**
    * Easily **filter out** specific repositories you do not wish to clone from the batch operation.
    * Clone **only** the repositories listed in a file, allowing developers to manage migration pipelines using external
      lists.

## Universal Compatibility

FromGtoG is built for maximum accessibility and stability, with dedicated support for major operating systems and
architectures:

* **Operating Systems:** **Windows**, **macOS**, and **Linux** (available as Snap and .deb package).
* **Architectures:** **amd64** (standard x86/Intel/AMD) and **arm64** (Apple Silicon/ARM Linux).

Since publishing version 3.0 just a few months ago, I realized a significant **code rewrite** was necessary to ensure
the application remains **maintainable** and **easy to understand** as it grows. This rewrite led us to the current,
stable version, **8.1.16**.

## Architectural Improvements: the evolution

To achieve the best possible structural integrity and to allow for future feature expansion, I implemented extensive use
of several **Software Design Patterns**. These patterns ensure better **modularity** and long-term stability:

* **Composite Pattern:** Used for robust and flexible **user input validation**, ensuring data integrity across all
  platforms.
* **Abstract Factory:** Manages flexible connections with different Git platform APIs (GitHub, Gitea, GitLab).
* **Strategy:** Enables dynamic switching of the cloning and migration logic (e.g., handling Local-to-Remote
  operations).
* **Singleton:** Ensures efficient, centralized resource management.

<h2 id="features">Features</h2>

Currently, the application is able to clone:

- from GitHub to GitHub\Gitea\Local\Gitlab
- from Gitea to GitHub\Gitea\Local\Gitlab
- from Gitlab to GitHub\Gitea\Local\Gitlab
- from Local to GitHub\Gitea\Local\Gitlab (copies only git repositories, other directories are skipped)

Further features

- granular cloning filter
- multi-threading
- logging (log file)
- customizable waiting time between 2 cloning processes
- delete all repositories from:
    - from GitHub
    - from Gitea
    - from Gitlab

<h2 id="download">Download (Stable)</h2>

<h2 id="download-macos">MacOS</h2>

- [Download MacOS AMD64 installer](https://github.com/goto-eof/fromgtog/releases/download/8.1.16/fromgtog-MacOS-8.1.16-amd64-Installer.zip) -
  just install the .pkg file (allow third party execution before). Note: for copy/paste actions, please use
  `Control + C` and `Control + V` (I will enable `Command + C` and `Command + V` in the future.)
- [Download MacOS ARM64 Installer](https://github.com/goto-eof/fromgtog/releases/download/8.1.16/fromgtog-MacOS-8.1.16-arm64-Installer.zip)

<h2 id="download-windows">Windows</h2>

- [Download Windows AMD64 installer](https://github.com/goto-eof/fromgtog/releases/download/8.1.16/fromgtog-Windows-8.1.16-amd64-Installer.zip) -
  just install the .exe file and start cloning.
  ~~- [Download Windows ARM64 installer](https://github.com/goto-eof/fromgtog/releases/download/8.1.16/fromgtog-Windows-8.1.16-arm64-Installer.zip) -
  just install the .exe file and start cloning.~~

<h2 id="download-linux">Linux</h2>

- [Install from Ubutnu AMD64/ARM64 Snapstore](https://snapcraft.io/fromgtog) - or execute `sudo snap install fromgtog`
  in order
  to install the application.
- [Download Ubuntu AMD64 package](https://github.com/goto-eof/fromgtog/releases/download/8.1.16/fromgtog-Linux-8.1.16-amd64-Installer.zip) -
  in order to install the .deb package execute `sudo dpkg -i fromgtog_8.1.16_amd64.deb`
  ~~- [Download Ubuntu ARM64 package](https://github.com/goto-eof/fromgtog/releases/download/8.1.16/fromgtog-Linux-8.1.16-arm64-Installer.zip) -
  in order to install the .deb package execute `sudo dpkg -i fromgtog_8.1.16_arm64.deb`~~

<h2 id="download-jar">Jar file</h2>

- [Jar file](https://github.com/goto-eof/fromgtog/releases/download/8.1.16/fromgtog.jar) - run it by executing
  `java -jar fromgtog.jar`.

<h2 id="technologies">Technologies/Tools</h2>

JDK 21, Intellij UI Designer (plugin for Intellij), Slf4J, Lombok, Apache Commons, JSON.

<h2 id="screenshot">Screenshot</h2>

![screenshot](images/FromGtoG.png)

<img src="https://andre-i.eu/api/v1/ipResource/custom.png?host=https://github.com/goto-eof/fromgtog" onerror="this.style.display='none'" />


<h2 id="news">News</h2>

- 2025/10/12
    - added the following GitHub Workflows in order to build executables of FromGtoG for each platform:
        - Linux (.deb)
            - amd64
            - arm64
        - MacOS (.pkg)
            - amd64
            - arm64
        - Windows (.msi)
            - amd64
            - arm64
- 2025/10/10
    - user input validation feature (composite pattern + factory pattern in order to make the code more readable) -> "
      Fianlly!" (:
    - currently validation feature is compatible only with Linux paths (not tested on other Operative Systems) -> I
      still need to complete it in order to make it cross platform
    - view tokens as password (****)
    - update log level to info -> "Fianlly!" (:
    - exclude repo names feature
    - clone only repo names stored in a file feature
- 2025/10/07
    - refactor: remove useless code - now we have auto-closable resource so it is not necessary to close them manually
    - show version number on the title bar
- 2025/10/06 - Today I applied few small improvements to the application. In particular:
    - I fixed an issue related to the Executor Service
    - I updated those parts of the code that allow to keep the total number of cloned repositories at the end of cloning
      process.
    - I fixed the "final status message". Now, if at least one git clone operation failed, FromGtoG will show a final
      error message.

      Because the improvements are not super important and because the release process on all platforms requires some
      time, I will release the new version of FromGtoG only on the Ubuntu App Center.

      About 2-3 days ago I played with Java Virtual Threads. I wanted to decrease the cloning process time. I succeeded
      to get a few seconds less than Platform Threads. Because the improvement was not significant (there is a
      bottleneck in an external library), I decided to avoid
      to include this new feature into FromGtoG application.
- 2025/06/23 - I discovered that FromGtoG `.deb` package was not working on Debian 10. I rebuilt the package on Debian
    10. So, now it should work on both Ubuntu latest and Debian 10. I apologize for the inconvenience. Please ping me if
        you find a bug (
        open an issue on [GitHub](https://github.com/goto-eof/fromgtog/issues)
        or [contact me](https://andre-i.eu/#contactme)). The v. 7.0.0 should work now also on Debian 10.
        You can download it
        from [here](https://github.com/goto-eof/fromgtog/releases/download/7.0.0/fromgtog_7.0.0_amd64.deb).
- fixed the windows/linux and macOS packaging - now it is possible to clone from GitHub. The absence of the
  `jdk.crypto.ec` and `java.security.sasl` imports in the `--add-modules` option, prevented the SSL connections, so the
  clone process failed, in particular for GitHub (which uses SSL).

<h2 id="for-developers">For developers</h2>

## Retrieve dependency modules of the jar

```bash
jdeps -s fromgtog.jar
```

## Generate a standalone for MacOS

It is necessary to install Xcode Command Line Tools before.

```bash
jpackage --type pkg --name "FromGtoG" --vendor "Andrei Dodu" --app-version "8.1.16" --input "target" --main-jar "fromgtog.jar" --icon "resources/icon.icns" --main-class "com.andreidodu.fromgtog.Main" --dest "executable" --add-modules java.base,java.desktop,java.net.http,jdk.crypto.ec,java.security.sasl,java.naming,java.sql,java.management,java.security.jgss,java.xml,java.logging --verbose --java-options "-Dawt.useSystemAAFontSettings=on -Dswing.aatext=true -Dsun.java2d.uiScale=true"
```

## Generate a standalone for Windows

It is necessary to install Wix Toolset before.

```bash
jpackage -t exe --name "FromGtoG" --vendor "Andrei Dodu" --app-version "8.1.16" --input "target" --dest "executable" --main-jar "fromgtog.jar" --icon "resources\icon.ico" --resource-dir resources --add-modules java.base,java.desktop,java.net.http,java.naming,java.sql,java.management,java.security.jgss,java.xml,java.logging,jdk.crypto.ec,java.security.sasl --win-shortcut --win-menu --main-class com.andreidodu.fromgtog.Main
```

## Generate a standalone for Linux

```bash
jpackage --type deb --name "FromGtoG" --vendor "Andrei Dodu" --app-version "8.1.16" --input "target" --main-jar "fromgtog.jar" --icon "resources/icon.png" --main-class "com.andreidodu.fromgtog.Main" --dest "executable" --add-modules java.base,java.desktop,java.net.http,java.naming,java.sql,java.management,java.security.jgss,java.xml,java.logging,jdk.crypto.ec,java.security.sasl --linux-shortcut --verbose --linux-package-deps "libasound2, libpulse0"
```

## Usefully commands

```bash
jar tvf fromgtog.jar 
```

### Generate and upload snap file - useful especially for me (:

```bash
sudo snap remove fromgtog && snapcraft clean && snapcraft && sudo snap install fromgtog_8.1.16_amd64.snap --dangerous && fromgtog

snapcraft upload --release=edge fromgtog_8.1.16_amd64.snap
```

<h2 id="tested-on">Tested on</h2>

- Ubuntu 25.04
- Debian 10
- Windows 11
- MacOS

<h2 id="support-me">Support Me</h2>

If you find this project helpful, consider [sponsoring me](https://github.com/sponsors/goto-eof) 💚

[![Sponsor](https://img.shields.io/badge/Sponsor-❤️-brightgreen)](https://github.com/sponsors/goto-eof)


