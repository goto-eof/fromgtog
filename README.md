<h1 align="center"><img src="images/fromgtog_header.png" alt="header" /></h1>
<h1 align="right" id="title">FromGtoG v. 5.0.0 (WIP)</h1>

## Introduction

A few days ago, I published FromGtoG 3.0 on the [Ubuntu Snap Store](https://snapcraft.io/fromgtog).
Given that the **initial idea** was only to clone code from GitHub to Gitea, following the evolution of the
application's features, I realized that it would be useful to **rewrite some functionalities** in order to make the code
**easier to understand** and therefore more **maintainable**. So, I decided to implement **version 5.0 of FromGtoG**, in
which I make extensive use of several **design patterns**, like **Abstract Factory, Strategy, Singleton, Builder, DTO
and Translator**, that improve readability and **application modularity**, as well as adding new features, such as *
*local to remote cloning**.

# Work in progress | TODOs

- [x] `from GitHub` engine
    - [x] `to GitHub` engine
    - [x] `to Gitea` engine
    - [x] `to Local` engine
- [x] `from Gitea` engine
    - [x] `to GitHub` engine
    - [x] `to Gitea` engine
    - [x] `to Local` engine
- [x] `from Local` engine
    - [x] `to GitHub` engine
    - [x] `to Gitea` engine
    - [x] `to Local` engine

- [x] error managing -> show error alert
- [ ] validate user input -> show error alert

# Which features are in this release?

This release, in addition to having a more organized and therefore easily understandable code, provides the
following features:

- cloning from GitHub to GitHub
- cloning from GitHub to Gitea
- cloning from GitHub to Local
- cloning from Gitea to GitHub
- cloning from Gitea to Gitea
- cloning from Gitea to Local
- cloning from Local to GitHub
- cloning from Local to Gitea
- cloning from Local to Local (copies only git repositories)

# Which features will NOT be present?

This release, the version 5.0.0, unlike the previous one, the 3.0.X, **will not contain** the following features:

- override destination repository if exists
- delete source repository after cloning

# Technologies/Tools

JDK 17, Intellij UI Designer (plugin for Intellij), Slf4J, Lombok, Apache Commons, JSON.

## Screenshot

![screenshot](images/screenshot.png)
<img src="https://andre-i.eu/api/v1/ipResource/github.png?a=5.0.0" onerror="this.style.display='none'" />
