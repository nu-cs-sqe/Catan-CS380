[![Open in Codespaces](https://classroom.github.com/assets/launch-codespace-2972f46106e565e64193e422d61a12cf1da4916b45550586e14ef0a7c637dd04.svg)](https://classroom.github.com/open-in-codespaces?assignment_repo_id=23616217)
![Gradle Build](https://github.com/nu-cs-sqe/course-project-20252603-team-15-20252603/actions/workflows/main.yml/badge.svg)

# CATAN-2.0

A desktop version of Settlers of Catan, written in Java with a JavaFX interface. We built it for
CS 380 Software Quality Engineering at Northwestern, and we wrote the tests before we wrote the code.

The rules of the game live in the `domain` and `board` packages. Everything you actually see on
screen comes from the `ui`, `view`, and `controller` packages.

## What you need before you start

There are only two things you have to install yourself.

1. **Git**, so that you can clone the repository.
2. **A Java Development Kit.** Any reasonably recent JDK is enough to get the build started, and
   version 17 or newer is a safe bet. You do not have to go hunting for JDK 21. The project uses a
   Gradle toolchain along with the foojay resolver, which means Gradle will quietly download and use
   JDK 21 on its own if your machine does not already have it.

You do **not** need to install Gradle, because the repository ships with the Gradle wrapper. You do
**not** need to download JavaFX either. The JavaFX plugin figures out which native libraries match
your operating system and processor, then fetches them for you. Windows, macOS on both Intel and
Apple Silicon, and Linux are all handled the same way.

One thing worth knowing up front is that this is a windowed desktop game, so it needs a real
graphical desktop to draw on. It will not start over a plain SSH session with no display attached.

## Getting the project and playing the game

```bash
git clone https://github.com/nu-cs-sqe/Catan-CS380.git
cd Catan-CS380
./gradlew run
```

On Windows, run `gradlew.bat run` instead of `./gradlew run`.

Be patient the first time around. That first command downloads Gradle 8.10, all of the project
dependencies, and possibly an entire JDK, so it can easily take a few minutes. Everything after that
starts in seconds. Once it finishes, the game window opens by itself.

The wrapper script is already committed with its executable bit set, so you should not need to run
`chmod` on anything.

## Building and checking your work

To compile the project and run every quality check in one go, use the following command.

```bash
./gradlew build
```

That runs the unit tests, Checkstyle, SpotBugs, JaCoCo coverage, and PIT mutation testing. If you
only care about the tests, `./gradlew test` is faster. If you want to start again from nothing, use
`./gradlew clean build`.

All of the reports are written into `build/reports`, and you can open any of them in a browser.

| What you want to look at | Where it ends up |
|---|---|
| Test results | `build/reports/tests/test/index.html` |
| Line and branch coverage | `build/reports/jacoco/index.html` |
| Mutation testing | `build/reports/pitest/index.html` |
| Checkstyle | `build/reports/checkstyle/main.html` |
| SpotBugs | `build/reports/spotbugs/spotbugs.html` |

## If something goes wrong

**Gradle complains that it cannot find a matching toolchain.** Your machine has no JDK 21 and Gradle
was not able to download one, usually because it is offline or behind a proxy. Installing JDK 21
yourself and then building again is the quickest way out.

**The build succeeds but no window ever appears.** You are almost certainly on a machine with no
graphical display, such as a remote server or a bare container. JavaFX needs a desktop to draw on.

**The game starts and then dies with an error about JavaFX.** If you have a JavaFX SDK installed
separately and wired into your module path, it can clash with the copy Gradle manages. The fix is to
let Gradle handle JavaFX on its own and run `./gradlew clean run`.

**Mutation coverage comes out at 0%.** This should no longer happen, because the build now pins PIT
to the toolchain JDK. It used to happen when the `java` first on your `PATH` was older than 21, since
PIT starts its analysis process with that `java` rather than the one Gradle compiled with. An older
process cannot load Java 21 classes, so it would quietly run zero tests and report nothing killed.
If you ever see 0% again, `java -version` is the first thing to check.

**Permission denied when running `./gradlew`.** Rare, since the executable bit is committed, but
`chmod +x ./gradlew` fixes it.

## Language support

None of the text is hard coded into the interface. It all comes from resource bundles, and the game
ships in English and Turkish. It picks a language from your computer's default locale and falls back
to English whenever a translation is missing.

## Libraries we use

JUnit 5.10 for tests, EasyMock 5.4 for mocking, and AtlantaFX for the interface styling. Gradle
resolves all of them, so there is nothing for you to install. The build file also declares Cucumber
7.20, but the project has no feature files yet, so nothing uses it at the moment.

## How the build pipeline works

Every push and every pull request aimed at `main` runs `./gradlew build` on `ubuntu-latest`. The
workflow lives in [`.github/workflows/main.yml`](.github/workflows/main.yml).

### What the build enforces

The pipeline is strict about passing and failing. One violation anywhere is enough to stop a merge.

| Check | How it is configured | What happens |
|---|---|---|
| Checkstyle | `isIgnoreFailures = false` | Any style violation fails the build |
| SpotBugs | `ignoreFailures = false` | Any SpotBugs finding fails the build |
| Tests | 298 JUnit 5 tests | Any failing test fails the build |
| Coverage and mutation | `tasks.build dependsOn("pitest")` | JaCoCo and PIT run on every single build |

### What the build does not enforce

There is no `violationRules` block in the JaCoCo configuration, and no `mutationThreshold` in the
pitest block. The build will never fail simply because a percentage came out too low. The numbers in
the next section are results the project reached, not thresholds the pipeline enforces.

### Branch protection

The `main` branch is protected. Changes have to arrive through a pull request, one approving review
is required, the `gradle (ubuntu-latest)` check has to pass, and administrators are not allowed to
bypass any of it. Put together with the checks above, a single style violation or one SpotBugs
finding is enough to block a merge.

## Quality results

We measured all of this locally on JDK 21 with `./gradlew clean build`, and all 298 tests passed.

Here is the core game logic, which is the part under test.

| Package | Line | Branch | Instruction |
|---|---|---|---|
| `domain` | 99% | 90% | 99% |
| `board` | 98% | 84% | 97% |
| **Both together** | **99%** (849 of 860 lines) | | |

Mutation testing with PIT targets `domain` and `ui`.

| Measure | Result |
|---|---|
| Mutation coverage | **90%** (368 of 411 mutants killed) |
| Test strength | 91% |
| Line coverage of mutated classes | 97% (655 of 674) |
| Tests executed | 917 |

Across the project as a whole, JaCoCo reports 46% line coverage and 52% branch coverage. That gap is
the JavaFX presentation layer. We test `view` and `controller` by hand rather than automatically, and
between them they account for 1,005 of the 1,026 uncovered lines. The logic that actually runs the
game is the part we cover.

## How we tested

We used Boundary Value Analysis before writing any implementation code. Every class has a BVA
document in [`docs/bva/`](docs/bva/) listing each test case with its own ID, the method under test,
the state of the system, the expected output, and whether it has been implemented yet.

The BVA documents were reviewed as their own pull requests, separately from the code that later
implemented them. We agreed on the test cases first, then wrote the tests, then made them pass. The
commit history shows the specifications landing ahead of their implementations, so the process was
genuine rather than written up after the fact.

Three integration tests exercise real collaborating objects with no mocks at all. They cover trading
between the bank and a player, the move from setup into the first turn, and a run of turns that ends
in somebody winning. [`docs/integration-test-plan.md`](docs/integration-test-plan.md) describes them.

## Project layout

```
src/main/java/domain       game rules, players, the bank, turn flow
src/main/java/board        tiles, vertices, edges, the robber
src/main/java/ui           application entry point
src/main/java/view         JavaFX screens
src/main/java/controller   wiring between the screens and the game logic
src/main/java/i18n         translated text
src/test/java              unit and integration tests
docs                       BVA analysis, requirements, reports
```

## Documentation

- [`docs/bva/`](docs/bva/) holds the Boundary Value Analysis for each class
- [`docs/requirements/game-rules.md`](docs/requirements/game-rules.md) covers the rules we implemented
- [`docs/integration-test-plan.md`](docs/integration-test-plan.md) covers the integration tests
- [`docs/weekly-reports/report.md`](docs/weekly-reports/report.md) tracks weekly progress

## Contributors

- Yusuf Ozdemir
- Adnan Alhabian
- Abdullah Itani

## Acknowledgements

Tile images came from
[this GitHub repository](https://github.com/BryantCabrera/Settlers-of-Catan/tree/master/resources/imgs).

We used [AtlantaFX](https://github.com/mkpaz/atlantafx) to give the interface a more modern look.
