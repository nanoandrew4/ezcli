# Changelog

v0.1.0 Update:

+ Implemented abstract classes to allow project to be modular. New modules should be pretty much plug and play.
+ Implemented Interactive and Terminal modules, which form the core of the ezcli_core module.
+ Terminal module can change directories, and othewise forwards commands to the system.
+ Terminal module can interact with other programs run from it, such as text editors.
+ Terminal module can autocomplete file names and folders, similar to how Unix systems autocomplete files and folders.
+ Terminal module can reuse commands, similar to how Unix systems do (through use of arrow keys).
+ Interactive module should be able to execute any module added to the program without any required modification.

