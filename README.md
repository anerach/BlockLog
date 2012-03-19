Main
===========
Block Log is a small Anti-Grief plugin with rollback support.
This plugin is still in development and it constantly gains new features!

Features
-----------

* Tracks player placed blocks.
* Ability to rollback players, regions or the whole world
* Ability to undo the last rollback or others with the rollback id
* MySQL and SQLite support
* Manual save and auto save
* Configurable wand
* In game config manipulation command

Changelog
-----------

### v0.5.1 ###
* [*] Fixed blocks not saving bug
* [*] Fixed some command bugs
* [*] Some other minor bug fixes 

#### Commands ####
* [*] Fixed rollback commands
* [*] Fixed clear command not showing the correct message
* [*] Updated blhelp to match the latest commands
* [*] Autosave now also saves an higher amount of blocks than specified
* [-] Removed console message when someone enables autosave

### v0.5 ###
* [+] New commands
* [*] Fixed SQLite problems
* [*] Fixed lagg issues with rollback's and undo's
* [*] Fixed internal storage message spamming
* [*] Some minor bug fixes 

#### Commands ####
* [+] Added autosave command
* [*] Optimized block select query's
* [*] Fixed rollback's and undo's command

#### Permissions ####
* [+] blocklog.autosave

### v0.4.1 ###
* [*] Bug fixes

### v0.4 ###
* [+] Notifies when new version available!
* [+] New commands
* [*] Bug fixes

#### Commands ####
* [*] Fixed config command not saving changes
* [*] Fixed reload command not being able to execute from console
* [+] Added undo command
* [+] Added clear command

#### Permissions ####
* [+] blocklog.undo
* [+] blocklog.clear

### v0.3 ###
* [+] Multi world support
* [*] Changed almost all the commands

#### Commands ####
* [+] Config manipulate command

#### Permissions ####
* [+] blocklog.config

### v0.2 ###
* [-] It doesn't add the blocks instantly to the database any more (Caused lagg)
* [+] More config options
* [+] New commands

#### Config ####
* [+] Configurable wand
* [+] Delay between database actions
* [+] Warning when internal block list reaches ...

#### Commands ####
* [+] Help command
* [+] Save command
* [+] Full save command

#### Permissions ####
* [+] blocklog.help
* [+] blocklog.notices
* [+] blocklog.fullsave
* [+] blocklog.save