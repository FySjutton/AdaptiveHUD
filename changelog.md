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