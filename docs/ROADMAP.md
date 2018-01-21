# Roadmap

This document is an overview of the goals to be accomplished by v1.0.0. Because the project is under development and new features might come up that must be released with v1.0.0, expect this document to change.

Some definitions of terms used in this document:
* Interactive mode -> Mode in which the program processes your input, and does not relegate it to the system.
* Command mode -> Mode in which program relegates all input to the system, and returns all ouput from system to user.
* Module -> This program is formed by modules. Two types of modules exist. Independent modules, which the user interacts with directly (see Terminal module), and modules that provide functionality but are no use as standalone modules.

### Features warranting new minor release

Completion of any top level element on this list warrants a new minor release. Fundamental changes to the core portion of the program can also warrant a new minor release.

---

 
*Goals for v1.0.0 (in no particular order):*
* [x] Key parser.
  * [x] Perform specific actions depending on program module currently active (file explorer, text edit, reuse, etc)
  * [x] Allow user to drop to command prompt with single key-press while in "interactive" mode.
  * [x] Allow user to return to "interactive" mode for continued use of the program.
  * [x] Print help for each module that is developed.
* [x] Modular framework for all modules to operate under.
* [ ] Text based file explorer.
  * [ ] Rename, create and delete files.
  * [ ] Open files with default program assigned by OS, or with an included module such as the text editor.
  * [ ] Execute files with the press of a single button.
  * [ ] Iterate through all files and folders using arrow keys for easy access.
* [ ] Text editor.
  * [ ] Edit or create text files.
  * [ ] Find and highlight words.
* [ ] Reuse most accessed directories.
  * [ ] Print list of x most accessed directories, and allow user to move through and select one.
  * [ ] Print error at startup if override is invalid directory.
* [ ] Reuse most input command(s).
  * [ ] Track frequency and patterns of commands run while in command mode.
  * [ ] Generate most frequent commands for reuse.
* [ ] Configuration files for customizing program functions.
  * [ ] Generate default file if no configuration is detected.
  * [ ] Implement most accessed directories overriding(to make shortcuts to user specified directories).
  * [ ] Implement most frequently typed commands overriding, and add option to name and give short description (to create command shortcuts).
  * [ ] Add overrides to config files within program, using list sorted by frequency of previous inputs to build an override manually and save it.
* [ ] Program help (will not be checked until release).
  * [ ] Module that tours the program and shows the user how to use the basics of each module included, interactively.
* [ ] Implement own raw input processing class to replace Input, if possible.
* [ ] Windows support (:S)
