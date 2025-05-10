<h1 align="center"><img src="images/fromgtog_header.png" alt="header" /></h1>
<h1 align="right" id="title">FromGtoG v. 6.0</h1>

<h2 id="index">Index</h2>

- [Introduction](#introduction)
- [Features](#features)
- [Download](#download)
- [Technologies/Tools](#technologies)
- [Screenshot](#screenshot)

<h2 id="introduction">Introduction</h2>

A few days ago, I published FromGtoG 3.0 on the [Ubuntu Snap Store](https://snapcraft.io/fromgtog).
Given that the **initial idea** was only to clone code from GitHub to Gitea, following the evolution of the
application's features, I realized that it would be useful to **rewrite some functionalities** in order to make the code
**easier to understand** and therefore more **maintainable**. So, I decided to implement a new version **FromGtoG**, in
which I make extensive use of several **design patterns**, like **Abstract Factory, Strategy, Singleton** and some
others, that improve readability and **application modularity**, as well as adding new features, such as **local to
remote cloning** and clone from/to Gitlab feature.

<h2 id="features">Features</h2>

Currently, the application is able to clone:

- `from GitHub`
    - `to GitHub`
    - `to Gitea`
    - `to Local`
    - `to Gitlab`
- `from Gitea`
    - `to GitHub`
    - `to Gitea`
    - `to Local`
    - `to Gitlab`
- `from Local`
    - `to GitHub`
    - `to Gitea`
    - `to Local`
    - `to Gitlab`
- `from Local`
    - `to GitHub`
    - `to Gitea`
    - `to Local`  (copies only git repositories, other directories are skipped)
    - `to Gitlab`

Further features (in the tools section)

- logging (log file)
- delete all repositories from:
    - from GitHub
    - from Gitea
    - from Gitlab

<h2 id="download">Download</h2>

- [Windows binary (amd64)](https://github.com/goto-eof/fromgtog/releases/download/6.0.1/amd64_windows_fromgtog-6.0.3.exe)
- [Ubuntu snap (amd64, arm64)](https://snapcraft.io/fromgtog)

<h2 id="technologies">Technologies/Tools</h2>

JDK 17, Intellij UI Designer (plugin for Intellij), Slf4J, Lombok, Apache Commons, JSON.

<h2 id="screenshot">Screenshot</h2>

![screenshot](images/screenshot.png)
<img src="https://andre-i.eu/api/v1/ipResource/github.png?a=6.0" onerror="this.style.display='none'" />
