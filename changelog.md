### Beta Version: v2.0.0-beta.3+1.21 changelog
**Changes made since v2.0.0-beta.2+1.21**

- Improved the look of the main menu.
  - Added check boxes (for enabling / disabling)
  - Colored delete button
- Fixed the colors not stopping in the multiline editor
- Added ability to drop files on the main menu screen to add them.
- Added automatic regeneration of the config file, if corrupted
- Removed some unused code, and made some minor changes.
  - Removed "enabling" inside the config screen
  - Moved "renderRequirement" to advanced
  - Improved name validation
  - Made renderRequirement work with attributes
  - Other minor changes
- Started adding suggestions to variables. This does not work yet, but you can see it work-in-progress.

### Beta Version: v2.0.0-beta.2+1.21 changelog
**Changes made since v2.0.0-beta.1+1.21**

- New element list widget in the main config screen
- Added a search bar to the main config screen
- Added developer support
  - AdaptiveHudRegistry, variables, flags and attributes
- Remade the flag system; now so developers can add their own
- Added functionality for attributes
  - For example, "{player.off_hand.count}". 
- Added a multiline editor (beta, just to get some response)
  - Color syntax for variables and conditions
  - Utilities like copy, paste, move, ctrl+a, selections
  - Quick surround, select a text and press {, [, (, %
- Fixed delete-function bug when renaming elements
- Made minor changes to the variable patterns

### Beta Version: v2.0.0-beta.1+1.21 changelog
**Changes made since v1.0.1+XXX**

- Switched to tabs in the configuration menu
- Rewrote the configuration system (better error handling)
- Rewrote json validation system
- Parse conditions from the inside-out, nest-support
- Renamed "text align" to "self align"
- Added actual text align
- New option if everything should be rendered on F1

- **NOTE**: Your current config-file won't work, and will make the game crash! Delete "/minecraft/config/adaptivehud/config.json5" and let the game generate a new one!

## Release: v1.0.1+XXX changelog
**Changes made since v1.0.0+XXX**

- Changed to the new logo

- ## Release: v1.0.0+1.21 changelog
**Changes made since v1.0.0+1.20.6**

- Updated to 1.21-1.21.1

## Release: v1.0.0+1.20.6 changelog
**Changes made since v1.0.0+1.20.4**

- Updated to 1.20.5-1.20.6

## Release: v1.0.0+1.20.4 changelog
**Changes made since v1.0.0-beta.7+1.20.4**

- Added support for 1.20.3
- Released the mod!

## v1.0.0-beta.7+1.20.4 changelog
**Changes made since v1.0.0-beta.6+1.20.4**

- Fixed the tfz variable (default rounding)
- Fixed so you can have strings in conditions. Some characters can also be escaped using the backslash.
- Added multiline support. (\n)
- Increased max length of values.
- Added some more flags, like replace, split and more. 
- Added support for multiple arguments on flags, this using the ";" character.
- Fixed some other minor bugs.
- Improved the default elements, also a default information screen.

## v1.0.0-beta.6+1.20.4 changelog
**Changes made since v1.0.0-beta.5+1.20.4**

- Fixed major problem with flags
- Made conditions support strings to some limits.

## v1.0.0-beta.5+1.20.4 changelog
**Changes made since v1.0.0-beta.4+1.20.4**

- Remade the flag system, now have global and specific flags. Both can take value.
- Made a documentation (also a documentation button)
- Fixed the get help button
- Added a few more variables
- Added "loading requirement", a condition for rendering the element

## v1.0.0-beta.4+1.20.4 changelog
**Changes made since v1.0.0-beta.3+1.20.4**

- Fixed a critical bug which made it so your game crashed instantly when opening the mod for the first time. This because I accidentally left a trailing comma in the config file, haha. While the user could edit the file manually and remove it, I will release this hotfix instead. :)

## v1.0.0-beta.3+1.20.4 changelog
**Changes made since v1.0.0-beta.2+1.20.4**

- A lot more variables, all marked with "beta.3" in the variable document
- Improved performance in some variables by adding a cooldown
- Improved performance in some variables by combining the same data
- Added more configuration options, and improved some error messages

## v1.0.0-beta.2+1.20.4 changelog
**Changes made since v1.0.0-beta.1+1.20.4**

- A lot more variables, all marked with "beta.2" in the variable document
- Better code (file names and such)
- More configuration settings (json5 aswell)
- Better default elements and config file
- Improved overlap issue (still broken, but better)
- Added title case flag "-tc"