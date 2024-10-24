# A detailed changelog over v2.

## Updated the main config screen
- Updated the list of elements to use `ElementListWidget` instead of `ScrollableWidget`.
  - Making the list no longer have a black background
  - Made the selecting improved, no longer renders outside of screen, better narrator.
- Added a search bar for elements, (searches after 800ms without typing)
- Added the ability to drop files, the file will then be validated before all changes are saved, and then everything reloaded.
- Replaced the "enable / disable" button with a smaller checkbox.
- Added red color to the delete button

### Updated the element screen
- Now uses tabs instead of the previous headlines. This makes it much more organized.
- Better validation
  - Prevents you from exiting if an error is there
  - Gives proper error messages and highlights it with red

### Attributes
- Brings a new feature, attributes, class-based.
- These can be used in this syntax: `{player.off_hand.count}`, break down:
  - `player` returns a custom class called `Player`
  - The `.off_hand` returns an instance of another custom class called `Item`
  - The `.count` returns the amount of the item, as a number.

### Developer Support
- Version 2 also brings developer support, introducing a registry.
  - `AdaptiveHudRegistry`
  - Variables:
    - `void registerVariable(String name, Method method, boolean overwrite`
    - `boolean unregisterVariable(String name)`
    - `boolean hasVariable(String name)`
    - `Set<String> variableList()`
    - `Method loadVariable(String name)`
  - Global Flags:
    - `boolean registerFlag(String name, Method method, boolean overwrite)`
    - `boolean unregisterFlag(String name)`
    - `boolean hasFlag(String name)`
    - `Set<String> flagList()`
    - `Method loadFlag(String name)`
  - Attribute Class:
    - `void registerAttribute(Class<?> requiredClass, Class<?> attributeClass)`