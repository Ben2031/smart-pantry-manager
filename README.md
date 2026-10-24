# Smart Pantry Manager

A Java Android app that helps reduce food waste. The user records the ingredients they
already have at home (their "pantry") and the app suggests recipes they can cook using
**strictly** those ingredients - no shopping trip needed.

## The strict-matching rule
A recipe is only suggested if **every** ingredient it needs is in the pantry in at least the
required quantity. A recipe with 4 of 5 ingredients is never shown. Matching is tolerant of
simple differences: "Tomatoes" = "tomato", and 1 kg = 1000 g (see `Normalizer.java` and
`MatchingEngine.java`).

## Features
- Add, edit and delete pantry items (name, quantity, unit, optional expiry date) with input validation
- Pantry list (RecyclerView + custom adapter) backed by a Room database
- 18 recipes seeded into the database on first run
- Suggested Recipes screen (strict matching) with a friendly message when nothing matches
- Recipe detail screen (ingredients + method)
- Settings screen (expiring-soon highlight toggle, default unit)
- Bottom navigation bar between Pantry, Recipes and Settings
- No Google Maps, no GPS/location, no internet permission

## Database choice: SQLite via Room
Room was chosen because the app only needs the user's own data on their own device. It works
offline, persists automatically between sessions, needs no server or account, and gives
compile-time checked queries on top of SQLite.

Tables: `pantry_items`, `recipes`, `recipe_ingredients` (one recipe has many ingredients).

## Setup / run instructions
1. Install Android Studio (latest stable).
2. Clone this repository: `git clone https://github.com/Ben2031/smart-pantry-manager.git`
3. In Android Studio choose **File > Open** and select the project folder.
4. Wait for the Gradle sync to finish.
5. Create an emulator in **Device Manager** (e.g. Pixel 6, API 34) or plug in a phone with USB debugging.
6. Press **Run** (green triangle).

Unit tests for the matching rule: right-click `MatchingEngineTest` > **Run**.

## Project structure
- `model/` - Room entities (PantryItem, Recipe, RecipeIngredient)
- `database/` - DAOs, AppDatabase, RecipeSeeder
- `util/` - Normalizer, Format, ExpiryUtil, Prefs
- `adapter/` - PantryAdapter, RecipeAdapter
- `MatchingEngine.java` - the strict-matching logic
- Activities: MainActivity (pantry), AddEditItemActivity, SuggestedRecipesActivity, RecipeDetailActivity, SettingsActivity
