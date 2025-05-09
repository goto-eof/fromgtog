<h1 align="center"><img src="images/fromgtog_header.png" alt="header" /></h1>
<h1 align="right" id="title">FromGtoG v. 6.0</h1>

## Introduction

A few days ago, I published FromGtoG 3.0 on the [Ubuntu Snap Store](https://snapcraft.io/fromgtog).
Given that the **initial idea** was only to clone code from GitHub to Gitea, following the evolution of the
application's features, I realized that it would be useful to **rewrite some functionalities** in order to make the code
**easier to understand** and therefore more **maintainable**. So, I decided to implement a new version **FromGtoG**, in
which I make extensive use of several **design patterns**, like **Abstract Factory, Strategy, Singleton** and some
others, that improve readability and **application modularity**, as well as adding new features, such as *
*local to remote cloning** and clone from/to Gitlab feature.

# Currently the application is able to clone:

- [x] `from GitHub` engine
    - [x] `to GitHub` engine
    - [x] `to Gitea` engine
    - [x] `to Local` engine
    - [x] `to Gitlab` engine
- [x] `from Gitea` engine
    - [x] `to GitHub` engine
    - [x] `to Gitea` engine
    - [x] `to Local` engine
    - [x] `to Gitlab` engine
- [x] `from Local` engine
    - [x] `to GitHub` engine
    - [x] `to Gitea` engine
    - [x] `to Local` engine
    - [x] `to Gitlab` engine
- [x] `from Local` engine
    - [x] `to GitHub` engine
    - [x] `to Gitea` engine
    - [x] `to Local` engine (copies only git repositories, other directories are skipped)
    - [x] `to Gitlab` engine

# Which features will NOT be present?

This release **does not contain** the following features:

- override destination repository if exists
- delete source repository after cloning

# Technologies/Tools

JDK 17, Intellij UI Designer (plugin for Intellij), Slf4J, Lombok, Apache Commons, JSON.

## Screenshot

![screenshot](images/screenshot.png)
<img src="https://andre-i.eu/api/v1/ipResource/github.png?a=5.0.0" onerror="this.style.display='none'" />
