[![Open in Codespaces](https://classroom.github.com/assets/launch-codespace-2972f46106e565e64193e422d61a12cf1da4916b45550586e14ef0a7c637dd04.svg)](https://classroom.github.com/open-in-codespaces?assignment_repo_id=23616217)
![Gradle Build](https://github.com/nu-cs-sqe/course-project-20252603-team-15-20252603/actions/workflows/main.yml/badge.svg)

# CATAN-2.0

A JavaFX implementation of Settlers of Catan, developed test-first for CS 380 (Software Quality
Engineering). The game logic lives in `domain` and `board`; the JavaFX presentation layer lives in
`ui`, `view`, and `controller`.

## Contributors

- Yusuf Ozdemir
- Adnan Alhabian
- Abdullah Itani

## Requirements

- **JDK 21** — the Gradle toolchain and `options.release` are both pinned to 21
- Gradle 8.10 (use the bundled `./gradlew` wrapper)
- JUnit 5.10, EasyMock 5.4, Cucumber 7.20 (resolved by Gradle)

> **Run the build on JDK 21.** PIT launches its mutation-analysis minion with the default `java` on
> your `PATH`, not the Gradle toolchain. On an older JDK the minion cannot load classes compiled for
> Java 21, silently runs zero tests, and the mutation report reads 0%. Because no mutation threshold
> is configured, the build still passes. If you see 0% mutation coverage, check `java -version` first.

## Building and running

```bash
./gradlew build   # compiles, tests, Checkstyle, SpotBugs, JaCoCo, PIT
./gradlew run     # launches the JavaFX game
```

## Quality pipeline

Every push and pull request against `main` runs `./gradlew build` on `ubuntu-latest`
(see [`.github/workflows/main.yml`](.github/workflows/main.yml)).

### What the build enforces

The pipeline is strict about *pass/fail*, and a single violation stops a merge:

| Gate | Configuration | Effect |
|---|---|---|
| Checkstyle | `isIgnoreFailures = false` | Any style violation fails the build |
| SpotBugs | `ignoreFailures = false` | Any SpotBugs finding fails the build |
| Tests | 298 JUnit 5 tests | Any failing test fails the build |
| Coverage + mutation | `tasks.build dependsOn("pitest")` | JaCoCo and PIT run on **every** build, not on demand |

### What the build does not enforce

There is **no `violationRules` block in the JaCoCo configuration and no `mutationThreshold` in the
pitest block**. The build does not fail on a coverage or mutation percentage. The numbers in
[Quality results](#quality-results) are results the project reached — not thresholds the pipeline
enforces.

### Branch protection

`main` is a protected branch:

- Changes must arrive through a pull request
- One approving review is required
- The `gradle (ubuntu-latest)` status check must pass
- Bypass is disabled for administrators

Combined with the gates above, a single style violation or SpotBugs finding blocks the merge.

## Quality results

Measured locally on JDK 21 with `./gradlew clean build` — 298 tests, all passing.

Core game logic, the packages under test:

| Package | Line | Branch | Instruction |
|---|---|---|---|
| `domain` | 99% | 90% | 99% |
| `board` | 98% | 84% | 97% |
| **Combined core** | **99%** (849/860 lines) | — | — |

Mutation testing (PIT, targeting `domain.*` and `ui.*`):

| Metric | Result |
|---|---|
| Mutation coverage | **90%** (368 of 411 mutants killed) |
| Test strength | 91% |
| Line coverage of mutated classes | 97% (655/674) |
| Tests executed | 915 (2.23 per mutation) |

Project-wide JaCoCo reports 46% line and 52% branch coverage. The gap is the JavaFX presentation
layer — `view` and `controller` are exercised manually rather than by automated tests, and they
account for 1,005 of the 1,026 uncovered lines. The tested logic is the game logic.

## Testing approach

Development followed Boundary Value Analysis before implementation. Each class has a BVA document in
[`docs/bva/`](docs/bva/) enumerating test cases with a unique ID, the method under test, the state of
the system, the expected output, and whether it has been implemented.

**The BVA documents were reviewed as their own pull requests**, separately from the code that
implemented them. Test cases were specified and approved first, then written, then made to pass — the
commit history shows the BVA specs landing ahead of their implementations, so the TDD process was
real rather than reconstructed after the fact.

Three integration tests exercise real collaborating classes with no mocks — bank/player trading,
setup-to-turn, and turn-to-win. See [`docs/integration-test-plan.md`](docs/integration-test-plan.md).

## Documentation

- [`docs/bva/`](docs/bva/) — Boundary Value Analysis per class
- [`docs/requirements/game-rules.md`](docs/requirements/game-rules.md) — game rules implemented
- [`docs/integration-test-plan.md`](docs/integration-test-plan.md) — integration test plan
- [`docs/weekly-reports/report.md`](docs/weekly-reports/report.md) — weekly progress

## Acknowledgements

Tile images sourced
from [this GitHub repository](https://github.com/BryantCabrera/Settlers-of-Catan/tree/master/resources/imgs).

Used [Atlanta-fx](https://github.com/mkpaz/atlantafx) for more modern UI design.
