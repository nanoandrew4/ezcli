# Roadmap

This document is an overview of the goals to be accomplished by v1.0.0. Because the project is under development and new features might come up that must be released with v1.0.0, expect this document to change.

Some definitions of terms used in this document:
* Interactive mode -> Mode in which the program processes your input, and does not relegate it to the system.
* Command mode -> Mode in which program relegates all input to the system, and returns all ouput from system to user.
* Module -> This program is built by modules that process input individually. Only module can be active at once. An example of a module could be the text editor. Modules can be accessed with a single key stroke within interactive mode.

### Features warranting new minor release

Completion of an top level element on this list warrants a new minor release. When a top level element in the v1.0.0 list is achieved, a new issue will be created with the remaining minor features and fixes needed before a new minor release, so they can be worked on.

*Goals for v0.1.0:*
* [ ] Interactive module.
  * [x] Accessibility to terminal module.
  * [x] Help output.
  * [ ] Remove spaces preceding command (if any exist)
  * [ ] Review classes for undocumented code, and clean up and optimize if possible.
  * [ ] Implement touring method.
* [ ] Terminal module.
  * [ ] Implement previous command modification.
  * [ ] Add harsher unit testing.
  * [ ] Review classes for undocumented code, clean up and optimize if possible.
  * [ ] Implement touring method.
  * [ ] Improve responsiveness of Ctrl+C and other signals.
  * [ ] Implement getting system commands help.
  * [ ] Throughout testing of arrow key processing, and compare to system terminal use of arrow keys.
  * [ ] Clean up file autocomplete method and implement unit tests for it.
  * [ ] Clean up Input class.

---

 
*Goals for v1.0.0 (in no particular order):*
* [x] Key parser.
  * [x] Perform specific actions depending on program module currently active (file explorer, text edit, reuse, etc)
  * [x] Allow user to drop to command prompt with single key-press while in "interactive" mode.
  * [x] Allow user to return to "interactive" mode for continued use of the program.
  * [x] Print help for each module that is developed.
  * [ ] v0.1.0 goals.
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
  * [ ] Include help method for each module.
  * [ ] Module that tours the program and shows the user how to use the basics of each module included, interactively.
* [ ] Implement own raw input processing class to replace Input, if possible.

