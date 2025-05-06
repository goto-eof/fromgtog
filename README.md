<h1 align="center"><img src="images/fromgtog_header.png" alt="header" /></h1>
<h1 align="right" id="title">FromGtoG v. 5.0.0 (WIP)</h1>

## Introduction

A few days ago, I published FromGtoG 3.0 on the [Ubuntu Snap Store](https://snapcraft.io/fromgtog).
Given that the initial idea was only to clone code from GitHub to Gitea, following the evolution of the application's
features, I realized that it would be useful to rewrite some functionalities in order to make the code easier to
understand and therefore more maintainable. So, I decided to implement version 5.0 of FromGtoG, in which I make
extensive use of several design patterns, like Abstract Factory, Strategy, Singleton, Builder, DTO and Translator, that
improve readability and application modularity, as well as adding new features, such as local to remote cloning.

# Work in progress | TODOs

- [ ] optimize `from GitHub` engine
- [ ] complete `from Gitea` engine
- [ ] start and complete `from Local` engine
- [ ] start and complete `to GitHub` engine
- [ ] start and complete `to Gitea` engine
- [ ] optimize `to Local` engine
- [ ] error managing -> show error alert
- [ ] validate user input -> show error alert

## Screenshot

![screenshot](images/screenshot.png)
<img src="https://andre-i.eu/api/v1/ipResource/github.png?a=5.0.0" onerror="this.style.display='none'" />
