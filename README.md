# Nutrition Guesser 🥑

A light-hearted Android nutrition quiz for building an intuitive feel for common foods. Every question compares two foods on an equal **100 g basis** and asks which has more or less of a nutrient.

## Gameplay

- Each level begins with ten questions.
- Correct answers advance immediately.
- Wrong answers move to the back of the round for another try.
- Five incorrect guesses end the run and reset the next game to level 1.
- Early levels compare prepared foods with natural foods; higher levels use closer comparisons within the same category.
- The scoreboard tracks correct and incorrect guesses across completed levels.

Questions cover calories, protein, saturated fat, trans fat, dietary fibre, and sodium. Animated cards, floating shapes, confetti, large emoji artwork, and responsive layouts keep the experience playful and fully offline.

## Nutrition data

The app bundles 66 common foods using representative, rounded values from USDA FoodData Central, primarily FNDDS 2021-2023 and SR Legacy. FoodData Central is public domain/CC0. See [`data/README.md`](data/README.md) for provenance.

This is an educational game, not medical advice or a nutrition tracker. Recipe and brand values vary.

## Architecture

- Kotlin and Jetpack Compose
- Pure `GameEngine` and deterministic `QuestionFactory` separated from UI
- Offline catalog; no API key, account, analytics, or network permission
- Unit tests for retry ordering, scoring, game over, level progression, data integrity, and difficulty rules

## Building the APK

GitHub Actions is the supported build path. The **Android CI** workflow installs pinned Java and Gradle versions, runs unit tests and Android lint, builds the debug APK, and uploads it as the `NutritionGuesser-debug-apk` artifact.

1. Open the repository's **Actions** tab.
2. Select **Android CI**.
3. Run the workflow or open a run triggered by a push.
4. Download the APK artifact from the run summary.

## License

The repository is dedicated under CC0 1.0. USDA FoodData Central data are also public domain/CC0.
