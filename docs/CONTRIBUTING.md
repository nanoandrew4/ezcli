### Welcome!

Hey there! It's awesome to have you, thanks for stopping by! A lot of work goes into evolving this project, and therefore, to maximize time spent coding and minimize time spent administering this project please try to follow the guidelines laid out in this project. Though it might seem like there are a lot of rules to follow and you might be put off at first, rest assured that you likely already follow most of them. This document is just meant to keep everyone on the same page, and explain how the project is developed. If you don't have one of the skills outlined here, don't despair. Everything here can be learnt with a couple of minutes of Googling around, or with the resources provided in this document.

## About
---
In case you missed it while navigating your way here, ezcli is, in a nutshell, a program designed to maximize your efficiency while using your favorite terminal. The key goal is to save you key presses and mouse movements, so that you can quickly do what needs to be done. Check out the [README](../README.md) for more detailed information.

## Branches
---
This project has two main branches, *master* and *release*. The *master* branch contains the latest code, which although has been tested, can still contain undiscovered bugs, and likely is ahead of the *release* branch, with partially implemented or fully fleshed experimental features. The *release* branch contains the code from the latest minor or patch release. After the goals for a new release have been reached (read [roadmap](ROADMAP.md)), the code will be throughoutly tested for bugs, and if none are found, a new release will be published with all the new features and minor fixes in *master*. If a major bug is found in the program that affects *master* and/or *release*, the bug fix will be directly applied to the relevant branch, and a new patch release will be issued, if the bug affects *release*.

**You may not work on *master* or *release* directly. As specified in the [Workflow](CONTRIBUTING.md#workflow) section, you must create a new branch on your fork and work from there.**

## Versions
---
See [semantic versioning](http://semver.org/). Releases in this project are to follow semantic versioning.

## Code Style
---
For code style, see [Java Code Conventions](http://www.oracle.com/technetwork/java/codeconventions-150003.pdf). Sure, it is 20+ years old, but chances are if you develop in Java, you follow most of these conventions already. If you don't, it's not too late for you! It might seem like a hassle, but I can assure you it is well worth learning to write readable code. Your future co-developers (and current ones!) will appreciate it.

Additionally, please write descriptive code. Do not overload it with comments, but try to make it so that it is easy to follow. Use descriptive class and variable names, keep the code simple, and use 
comments where needed.

Sample JavaDoc block:
/**
	* This method takes in a number and a string, and appends the number to the end of the string.
	*
	* @param string String to be modified
	* @param num Number to be appended
	* @return String with num appended to string
	*/

## Workflow
---
If you are new to the project, your first step should be to fork the project and set up your remotes. Sounds like crazy talk? Check out [this](https://help.github.com/articles/fork-a-repo/) link for help setting yourself up.

As stated in the [Branches](CONTRIBUTING.md#branches) section of these guidelines, there are two branches for this project which hold all the working code. In order to contribute code to this repository, a certain workflow is required (don't worry, it's easy to grasp!). For a detailed explanation of the process, please see [this gist](https://gist.github.com/Chaser324/ce0505fbed06b947d962), which outlines all the steps and command line code required. 

Yes, I said command line code. In order to prevent single file commit madness, it is required that you learn to work with git in a command line environment. You're not learning a new language, don't worry, but a set of commands that get your code from your computer to GitHub. It might take a bit of trial and error, but it's easy to get a grasp of. This keeps the commits descriptive and meaningful, as well as neatly organized in case someone has to go back to it at some point. It is also good experience for your future contributions to this and other projects, as well as in your professional life (if you go on to develop software).

##### Here is a summary of the workflow:
1. Open an issue (if one is not currently open) specifying what you will be working on (feature or fix), so that multiple people don't end up working in parallel on the same issue. Please avoid picking large topics to work on individually, to improve coordination between contributors. 
2. Before starting work on a new feature or bug fix, make sure to update your local *master* branch with the remote *master* branch.
3. Set up a new branch on which you will work, and give it a descriptive name.
4. Work on your code on this branch, and commit + push frequently to your fork to keep your work safe (or if you need someone to test a code snippet, so they can access it).
5. Clean up your work. Merge with master (if master was changed since you created your new branch). Please ensure that prior to moving on, your code is properly styled and you are only pushing a stable feature.
6. Push your polished code to your fork, and submit a pull request. Title should be "short and sweet", attempting to encompass what changes this brings in a few words. A more expansive overview of the changes should be made in the comment as per the [pull request template](PULL_REQUEST_TEMPLATE.md), so that the repository maintainers can get a feel for what is happening under the hood before they look at the code.
7. If your pull request is accepted and merged, congrats! If it is not, a comment will be left on the pull request specifying why it was not merged. Make the necessary changes, and request a new review. If everything is in order and no new quirks are found, your code will be merged!

