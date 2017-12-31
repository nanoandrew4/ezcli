# Changelog

v0.2.0 Update:

+ Re-worked module class and implementation.
+ Added support for "slave" modules. They are not their own separate entities but can react to keyboard events caught by other modules.
- Removed other WIP modules temporarily. They will be added in for next release, but a modular framwork rework requires its own release.
+ Ezcli now keeps a command history file, like bash, for use by other modules and itself from run to run.
+ Fixed some bugs in Terminal package.

v0.1.0 Update:

+ Implemented abstract classes to allow project to be modular. New modules should be pretty much plug and play.
+ Implemented Interactive and Terminal modules, which form the core of the ezcli_core module.
+ Terminal module can change directories, and othewise forwards commands to the system.
+ Terminal module can interact with other programs run from it, such as text editors.
+ Terminal module can autocomplete file names and folders, similar to how Unix systems autocomplete files and folders.
+ Terminal module can reuse commands, similar to how Unix systems do (through use of arrow keys).
+ Interactive module should be able to execute any module added to the program without any required modification.

