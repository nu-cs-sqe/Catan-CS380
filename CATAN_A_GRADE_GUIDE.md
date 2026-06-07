# CLI Catan — Team Guide to an A

Every section cites the week guidance that grades it, so you can trace each step back to the source.

---

## 1. The A-Grade Rubric (What "A" Requires)

| # | Criterion | What it Means for Us | Source |
|---|---|---|---|
| 1 | **BVA** | Boundary Value Analysis documented before tests | Week 4 §4.3 |
| 2 | **All tests pass** | `./gradlew test` green on main | Week 5 §3 |
| 3 | **TDD/BDD** | Git history proves tests written before code | Week 4 §4.3, Week 5 §3 |
| 4 | **Test Quality** | Coverage + mutation + meaningful asserts + mocking | Week 5 §3, Week 7 §3 |
| 5 | **All features done** | Setup → 1 turn → multi-turn → 1 win → multi-win | Week 5 §2 |
| 6 | **PM board & progress reports** | GitHub Project active; weekly report updated | Week 4 §4.1, Week 4 §5, Week 5 §1+§4, Week 6 §1+§4, Week 7 §1+§6 |
| 7 | **CI & branch protection** | Actions runs on every PR; main protected | (Already set up — keep enforcing per Week 5 §3) |
| 8 | **Team coding standard** | Checkstyle config tuned by us, enforced in CI | Week 6 §3 |
| 9 | **i18n (locale)** | All strings in `ResourceBundle`, ≥ 2 locales | Week 7 §5 |
| 10 | **Clean Code standards** | Small functions, few params, info hiding, MVC, SpotBugs clean | Week 5 §3, Week 6 §3 |

One red X drops us to B.

---

## 2. Repository Layout

```
catan/
├── .github/workflows/ci.yml          # CI (already required)
├── config/
│   ├── checkstyle/checkstyle.xml     # Week 6 §3
│   └── spotbugs/exclude.xml          # Week 6 §3
├── docs/
│   ├── design/
│   │   ├── architecture.md           # Week 4 §4.2
│   │   └── bva/                      # Week 4 §4.3 (one file per feature)
│   ├── i18n-plan.md                  # Week 7 §5
│   ├── integration-test-plan.md      # Week 7 §4
│   └── weekly-report/report.md       # Week 4 §1+§5, Week 5–7 §1+last item
├── src/main/java/edu/<school>/catan/
│   ├── model/        # Pure game logic — no I/O   (MVC: Week 5 §3)
│   ├── controller/   # Orchestrates model + view  (MVC: Week 5 §3)
│   ├── view/         # CLI I/O only               (MVC: Week 5 §3)
│   ├── i18n/Messages.java                         # Week 7 §5
│   └── Main.java
├── src/main/resources/
│   ├── messages.properties           # Week 7 §5
│   └── messages_<locale>.properties  # Week 7 §5
├── src/test/java/.../integration/    # Week 7 §4
└── build.gradle                      # Plugins: Week 6 §3, Week 7 §3
```

---

## 3. Git & Branch Workflow

The "TDD" column is graded by inspecting commit history (**Week 5 §3** — reviewers reject PRs that don't follow the order).

**Per-feature workflow** (from **Week 4 §4.3** and **Week 5 §3**):

1. Pull main: `git checkout main && git pull` — *Week 4 §4.3*
2. Create branch: `git checkout -b feature/<name>` — *Week 4 §4.3*
3. Write BVA in `docs/design/bva/<feature>-bva.md`. Commit. — *Week 4 §4.3*
4. Open a **draft PR** — *Week 5 §3*
5. Write a failing test. Commit (`test: ...`). — *Week 4 §4.3*
6. Write minimum code to pass. Commit (`feat: ...`). — *Week 4 §4.3*
7. Refactor. Commit (`refactor: ...`).
8. Repeat 5–7 per behavior.
9. `git pull origin main` into your branch frequently. — *Week 4 §4.3*
10. Mark PR ready. Require 1 review. — *Week 4 §4.3*
11. Merge after CI is green. — *Week 4 §4.3*

**Commit prefixes** (make TDD visible to grader): `test:`, `feat:`, `refactor:`, `docs:`, `chore:`.

---

## 4. PR Review Checklist

Paste into `.github/pull_request_template.md`. Every line cites the week that grades it.

```markdown
## PR Checklist

### TDD evidence — Week 5 §3
- [ ] BVA doc exists in `docs/design/bva/`         (Week 4 §4.3)
- [ ] Draft PR opened before code commits          (Week 5 §3)
- [ ] Commits show tests BEFORE matching code      (Week 5 §3 — reject otherwise)
- [ ] No "fix tests" commits that mutate tests to match code

### Test quality — Week 5 §3, Week 7 §3
- [ ] Meaningful test names
- [ ] Mocks for external collaborators only        (Week 5 §3)
- [ ] Each test asserts one behavior
- [ ] BVA boundary cases covered                   (Week 4 §4.3)
- [ ] JaCoCo coverage thresholds met               (Week 7 §3)
- [ ] PIT mutation score met                       (Week 7 §3)

### Code quality — Week 5 §3, Week 6 §3
- [ ] Small functions                              (Week 5 §3)
- [ ] Few parameters                               (Week 5 §3 — textbook rule)
- [ ] Information hiding / true OO                 (Week 5 §3)
- [ ] MVC respected: no business logic in controllers, no I/O in model  (Week 5 §3)
- [ ] No user-facing string literals — all via Messages.get()  (Week 7 §5)
- [ ] Checkstyle clean                             (Week 6 §3)
- [ ] SpotBugs clean                               (Week 6 §3)

### CI
- [ ] `./gradlew check` passes locally
```

---

## 5. Feature Implementation Order

Order from **Week 5 §2** (repeated in Week 6 §2 and Week 7 §2 — they emphasize sticking to it):

| Order | Feature | Definition of Done | Started in |
|---|---|---|---|
| 1 | **Game Setup** | 2–4 players, board built, snake-order placement, starting resources dealt | Week 4 §4 |
| 2 | **One turn** | Roll → resources → trade → build → end turn | Week 5 §2 |
| 3 | **Multiple turns** | Rotation + state persistence + 7-roll robber/discard | Week 5 §2 |
| 4 | **One win condition** | 10 VP from settlements + cities | Week 5 §2 |
| 5 | **Multiple win conditions** | + Longest Road, Largest Army, VP dev cards | Week 5 §2 |

Each feature gets a PM board column/milestone (**Week 4 §4.1**).

---

## 6. GitHub Project Board — **Week 4 §4.1**

The professor grades the PM board directly: it must exist, show every feature, and contain a **User Story + Acceptance Criteria + Use Cases** for each feature (Week 4 §4.1). The empty Backlog view they showed in class is the target template.

### One-time setup
1. Repo → **Projects** tab → **New project** → **Team planning** template.
2. Name it `team-<n>-<term>` (e.g. `team-15-20252603`).
3. Open the **Backlog** view. Confirm the fields match what the professor expects:
   `Title`, `Assignees`, `Status`, `Linked pull requests`, `Sub-issues progress`, `Iteration`, `Estimate`.
4. Status values: `Backlog`, `Ready`, `In Progress`, `In Review`, `Done`.
5. Create one **Iteration** per week (Week 4 – Week 7).

### Per-feature workflow
For each feature listed in §5:
1. Create a parent Issue titled `Feature: <name>` and add it to the Project.
2. Fill the Issue body using the format below (matches the Exploding Kittens example shown in class).
3. Create child Issues for sub-tasks (BVA doc, failing test, implementation, refactor) and link them as **sub-issues** so the `Sub-issues progress` field populates.
4. Assign the Iteration, an Assignee, and an Estimate.
5. Move the card through `Status` as work progresses; link the PR through `Linked pull requests`.
6. Close the parent Issue only when every acceptance-criteria checkbox is ticked.

### Issue body template (the format the professor expects)

```markdown
## User Story
As a <role>, I want <feature>, so that <benefit>.

### Acceptance Criteria
- [ ] <testable condition>
- [ ] <testable condition>

## Use Case <N>: <Name>
- **Actor:** <role>
- **Preconditions:**
  - <state that must hold before the flow>
- **Main Flow:**
  1. <step>
  2. <step>
- **Alternate Flows:**
  - <step#>.a <variation>
- **Postconditions:**
  - <state after success>
```

Keep one Use Case per scenario. Acceptance Criteria must be testable — each checkbox should map to at least one BVA row in [docs/bva/](docs/bva/) (Week 4 §4.3).

---

## 7. BVA Template

Required by **Week 4 §4.3**, checked in PR review per **Week 5 §3**. Save as `docs/design/bva/<feature>-bva.md`.

**Format rules (match this exactly so the grader can trace tests back to rows):**
- One table per method under test
- Every row gets a Test Case ID — use it as a comment in the matching test
- "Implemented?" column must be filled in — start as `no`, flip to `yes` when the test is written and passing
- Every boundary value from the BVA must map to at least one test case

---

### Example 1 — `docs/design/bva/game-setup-bva.md`

```markdown
# BVA: Game Setup — Player Count

### Method under test: `setup_OnValidPlayerCount_InitializesGame()`

|     | System under test | Expected output          | Implemented? |
|-----|-------------------|--------------------------|--------------|
| TC1 | 0 players         | IllegalArgumentException | no           |
| TC2 | 1 player          | IllegalArgumentException | no           |
| TC3 | 2 players (min)   | Game initializes         | no           |
| TC4 | 4 players (max)   | Game initializes         | no           |
| TC5 | 5 players         | IllegalArgumentException | no           |


### Method under test: `placeSettlement_OnOccupiedVertex_ThrowsException()`

|     | System under test                      | Expected output       | Implemented? |
|-----|----------------------------------------|-----------------------|--------------|
| TC6 | vertex already has a settlement        | IllegalStateException | no           |
| TC7 | vertex adjacent to existing settlement | IllegalStateException | no           |
| TC8 | vertex free, no adjacent settlements   | settlement placed     | no           |
```

---

### Example 2 — `docs/design/bva/dice-roll-bva.md`

```markdown
# BVA: Dice Roll & Resource Distribution

### Method under test: `rollDice_OnAnyRoll_ReturnsSumBetweenTwoAndTwelve()`

|     | System under test          | Expected output | Implemented? |
|-----|----------------------------|-----------------|--------------|
| TC1 | both dice roll minimum (1) | sum is 2        | no           |
| TC2 | both dice roll maximum (6) | sum is 12       | no           |


### Method under test: `rollSeven_OnPlayerHandOverLimit_ForcesDiscard()`

|     | System under test                     | Expected output                       | Implemented? |
|-----|---------------------------------------|---------------------------------------|--------------|
| TC3 | player has exactly 7 cards, roll is 7 | no discard required                   | no           |
| TC4 | player has 8 cards, roll is 7         | player discards 4 cards (half, floor) | no           |
| TC5 | player has 0 cards, roll is 7         | no discard required                   | no           |
```

---

Every test method should trace to a row here. Flip `no` → `yes` when the test is committed — the grader checks this matches your git history (Week 5 §3).

---

## 8. Test Quality Standards

### Coverage (configured in build.gradle — **Week 7 §3**)
- **JaCoCo:** line ≥ 90%, branch ≥ 85% (build fails below threshold) — *Week 7 §3*
- **PIT mutation:** ≥ 80% — *Week 7 §3*

### Mocking (graded in **Week 5 §3**)
- Mock **external collaborators** (`Dice`, `CliView`), not the class under test. — *Week 5 §3*
- Use **Mockito**, not hand-rolled mocks.
- Never mock value objects (`Resource`, `Hex`).

### Test naming
```java
@Test void shouldDistributeResources_whenSettlementBordersRolledHex() { }
@Test void shouldRejectSettlement_whenAdjacentVertexOccupied() { }
```

---

## 9. Clean Code Standards

From **Week 5 §3** (Software Design Principles + GUI Development section):

1. **Small functions** — *Week 5 §3*
2. **Few parameters** (≤ 3 ideal, 4 max) — *Week 5 §3* ("number of parameters... mentioned in the textbook")
3. **No flag parameters** — Clean Code, enforced by Checkstyle (Week 6 §3)
4. **Information hiding / true OO** — *Week 5 §3*
5. **Tell, don't ask** — Clean Code
6. **MVC discipline** — *Week 5 §3*:
   - Model never imports `Scanner`, `System.out`, or view classes
   - View never mutates game state
   - Controller does not host business rules ("is there any code in a controller class that doesn't fit the responsibilities of a controller?" — Week 5 §3)
7. **No magic numbers** — enforced by Checkstyle (Week 6 §3)

---

## 10. Build Setup (`build.gradle`)

Plugins required across weeks. Refer to the **Lab 5 Code Coverage repository** (linked in Week 6 §3 and Week 7 §3) for exact versions and config.

```groovy
plugins {
    id 'java'
    id 'application'
    id 'checkstyle'                                  // Week 6 §3
    id 'com.github.spotbugs' version '<latest>'     // Week 6 §3
    id 'jacoco'                                      // Week 7 §3
    id 'info.solidsoft.pitest' version '<latest>'   // Week 7 §3
}

dependencies {
    testImplementation 'org.junit.jupiter:junit-jupiter:<latest>'
    testImplementation 'org.mockito:mockito-core:<latest>'   // Week 5 §3 mocking
}

test { useJUnitPlatform(); finalizedBy jacocoTestReport }

jacocoTestCoverageVerification {                     // Week 7 §3
    violationRules {
        rule {
            limit { counter = 'LINE';   minimum = 0.90 }
            limit { counter = 'BRANCH'; minimum = 0.85 }
        }
    }
}
check.dependsOn jacocoTestCoverageVerification

pitest {                                             // Week 7 §3
    targetClasses = ['edu.<school>.catan.*']
    mutationThreshold = 80
    timestampedReports = false
}

checkstyle {                                         // Week 6 §3
    configFile = file('config/checkstyle/checkstyle.xml')
}

spotbugs {                                           // Week 6 §3
    excludeFilter = file('config/spotbugs/exclude.xml')
}
```

**Run before every push:** `./gradlew check pitest`

---

## 11. CI Workflow (`.github/workflows/ci.yml`)

```yaml
name: CI
on: { push: { branches: [main] }, pull_request: { branches: [main] } }
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with: { distribution: 'temurin', java-version: '17' }
      - run: ./gradlew check          # Week 6 §3 (checkstyle, spotbugs), Week 7 §3 (jacoco)
      - run: ./gradlew pitest         # Week 7 §3
      - uses: actions/upload-artifact@v4
        with: { name: jacoco-report, path: build/reports/jacoco/ }
      - uses: actions/upload-artifact@v4
        with: { name: pit-report, path: build/reports/pitest/ }
```

**Branch protection on main:** required PR + passing CI + 1 approval + up-to-date branches + no force-push.

---

## 12. Checkstyle Configuration — **Week 6 §3**

Week 6 §3 says: pick Google or Oracle style as a **starter**, then "remove, add, or change" rules to fit our team.

Start with **Google Java Style** (`google_checks.xml`) into `config/checkstyle/checkstyle.xml`.

**Relax:**
- `LineLength` → 120
- `MissingJavadocMethod` → public API only
- `JavadocPackage` → off

**Add (to enforce Clean Code from Week 5 §3):**
- `MethodLength` max 40
- `ParameterNumber` max 4
- `CyclomaticComplexity` max 10
- `MagicNumber` (allow -1, 0, 1, 2)

Document deviations in `docs/design/coding-standard.md`.

**After Checkstyle is set up, Week 6 §3 explicitly says:** *"all the teammates should pause their development work and refactor the codebase to comply with the linters."* Plan a refactor sprint.

---

## 13. i18n Plan — **Week 7 §5**

Required: discuss and document plan in the PM board (Week 7 §5).

**Decision:** `java.util.ResourceBundle` + `.properties` files.

`src/main/resources/messages.properties` (English, default):
```properties
welcome=Welcome to Catan!
prompt.players=How many players (2-4)?
prompt.player.name=Enter name for player {0}:
error.invalid.player.count=Player count must be between 2 and 4.
resource.brick=Brick
turn.roll=It is {0}'s turn. Press ENTER to roll.
turn.rolled=You rolled a {0}.
win.message={0} wins with {1} victory points!
```

`src/main/resources/messages_es.properties` (Spanish):
```properties
welcome=¡Bienvenido a Catán!
prompt.players=¿Cuántos jugadores (2-4)?
```

`Messages.java`:
```java
public final class Messages {
    private static ResourceBundle bundle =
        ResourceBundle.getBundle("messages", Locale.getDefault());
    public static void setLocale(Locale locale) {
        bundle = ResourceBundle.getBundle("messages", locale);
    }
    public static String get(String key, Object... args) {
        return MessageFormat.format(bundle.getString(key), args);
    }
}
```

**Hard rule:** zero user-facing string literals outside `messages*.properties`. Ship ≥ 2 locales. Document in `docs/i18n-plan.md` (Week 7 §5).

---

## 14. Integration Test Plan — **Week 7 §4**

Required: discuss and document in the PM board.

In `docs/integration-test-plan.md`:
1. Setup → Turn: complete setup, run one full turn, assert state consistency.
2. Turn → Win: scripted turns reaching 10 VP triggers game end.
3. Robber: roll 7 → discard → move → steal end-to-end.
4. Trade: domestic two-player trade, then maritime 4:1.

Place tests in `src/test/java/.../integration/`. Real classes, no mocks.

---

## 15. Weekly Report Template — **Week 4 §1+§5, Week 5–7 §1+last item**

`docs/weekly-report/report.md` — append a section each week.

```markdown
## Week N (YYYY-MM-DD)

### Planning                                # Week N §1
- Goal for the week:
- Task assignments:

### Progress                                # Week N final item
- Completed (PR #s):
- In progress:
- Blocked:

### Metrics
- Test count:
- Coverage: line %, branch %
- Mutation score:
- Open PRs:

### Retrospective
- What went well:
- To improve:
```

---

## 16. Catan Domain Quick Reference (Rulebook)

Source: `catanrulebook.pdf`. Each row is a candidate BVA case (Week 4 §4.3).

| Rule | Value |
|---|---|
| Players | 2–4 |
| Starting hand | 1 resource per adjacent hex of 2nd settlement |
| Settlement cost | brick + lumber + wool + grain |
| City cost | 3 ore + 2 grain |
| Road cost | brick + lumber |
| Dev card cost | ore + wool + grain |
| Hand > 7 on roll of 7 | discard half (round down) |
| 4:1 maritime | always available |
| 3:1 generic harbor | requires settlement on harbor |
| 2:1 specific harbor | requires settlement on matching harbor |
| Longest Road | 5+ continuous, 2 VP |
| Largest Army | 3+ played knights, 2 VP |
| Win | 10 VP on your own turn |
| Settlement VP | 1 |
| City VP | 2 |
| Distance rule | no settlement adjacent to another |

---

## 17. Per-Week Action Map

Pin this somewhere visible.

| Week | Action | Source |
|---|---|---|
| **4** | Planning in `report.md` | §1 |
| 4 | Decide CLI vs GUI (we chose CLI) | §3 |
| 4 | User Story + Use Cases for Setup on PM board | §4.1 |
| 4 | Design doc in `docs/design/architecture.md` | §4.2 |
| 4 | Feature branches, BVA docs, draft PRs, TDD commits | §4.3 |
| 4 | Weekly progress in `report.md` | §5 |
| **5** | Planning in `report.md` | §1 |
| 5 | Continue Setup → move to "One turn" if done | §2 |
| 5 | Apply PR review checklist (BVA, TDD history, mocking, MVC) | §3 |
| 5 | Weekly progress in `report.md` | §4 |
| **6** | Planning in `report.md` | §1 |
| 6 | Continue feature order | §2 |
| 6 | Set up Checkstyle + SpotBugs; refactor codebase to comply | §3 |
| 6 | Weekly progress in `report.md` | §4 |
| **7** | Planning in `report.md` | §1 |
| 7 | Continue feature order | §2 |
| 7 | Set up JaCoCo + PIT with thresholds | §3 |
| 7 | Integration test plan on PM board | §4 |
| 7 | i18n plan on PM board, implement ResourceBundle | §5 |
| 7 | Weekly progress in `report.md` | §6 |

---

## 18. Pre-Submission Checklist

- [ ] `./gradlew clean check pitest` passes from fresh checkout — *Weeks 6+7*
- [ ] Features 1–5 demoable — *Week 5 §2*
- [ ] ≥ 2 locales work — *Week 7 §5*
- [ ] BVA doc per feature — *Week 4 §4.3*
- [ ] `report.md` entry per week — *Weeks 4–7*
- [ ] PM board: all issues closed — *Week 4 §4.1*
- [ ] README has build/run instructions
- [ ] No TODO / FIXME / commented-out code — *Clean Code, Week 5 §3*
- [ ] Checkstyle: 0 violations — *Week 6 §3*
- [ ] SpotBugs: 0 violations — *Week 6 §3*
- [ ] JaCoCo: line ≥ 90%, branch ≥ 85% — *Week 7 §3*
- [ ] PIT: ≥ 80% — *Week 7 §3*
- [ ] Every PR reviewed by teammate (not self-merged) — *Week 5 §3*
