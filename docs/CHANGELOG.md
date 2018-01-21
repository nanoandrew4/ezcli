# Changelog

v0.3.0 Update:

+ Reworked how events are handled. Now they are implemented as lambda expressions, which allows them to be swapped out as needed.
+ Abstracted output system to allow easy implementation of substitute output systems.
+ Colored output module using ANSI codes added. For Unix systems only. Package name: color_output
+ Added direct interfacing with bash, which was previously not possible. All commands except "cd" are handled directly by bash.
+ Cleaned up code, separated FileAutocomplete portions to be implemented in next minor release.
+ Fixed visual bug caused by the program reacting too quickly to held keys. Set 40ms delay between each key press detection (normal is 30ms)
+ Fixed some bugs in Terminal module.
+ Improved documentation.

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

