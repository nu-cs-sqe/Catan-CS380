# Development Cards — Changes Needed

Tracks the code changes implied by the design in `development-card-bva.md`
(cards stored as a `Map<DevelopmentCard,Integer>` on `Player`; no hand class;
`knightsPlayed` stays on `Player`; VP-card score derived from the map;
buy-delay enforced by `Game`). **Nothing here is implemented yet — this is the to-do.**

Follow the TDD order from `CATAN_A_GRADE_GUIDE.md` §3: BVA row → `test:` (failing) → `feat:` (pass) → flip row to `yes`.

---

## 1. New file — `domain/DevelopmentCard.java`  (enum)  — BVA TC1–TC5
- Constants: `KNIGHT`, `ROAD_BUILDING`, `YEAR_OF_PLENTY`, `MONOPOLY`, `VICTORY_POINT`.
- Methods: `isPlayableAction()`, `isVictoryPoint()`, `isReturnableToBank()`
  (returnable == playable action; VP cards are never returned).
- Test file `DevelopmentCardTest.java` for TC1–TC5 already exists (currently red).

## 2. `domain/Player.java`  — BVA TC6–TC20
**Add:**
- Field `private final Map<DevelopmentCard,Integer> devCards;` (EnumMap), initialised to 0 for
  every value in **both** the normal constructor and the copy constructor (mirror `emptyResourceHand()`).
- `int getDevelopmentCardCount(DevelopmentCard card)` — null → NPE; else map count (TC6, TC7).
- `void buyDevelopmentCard(DevelopmentCard card)` — null → NPE; else `devCards.merge(card,1,Integer::sum)`.
  VP cards go in the map too (so they score). No buy-delay logic here (TC8–TC11).
- `DevelopmentCard playDevelopmentCard(DevelopmentCard card)` — null → NPE;
  `VICTORY_POINT` → IllegalStateException; count 0 → IllegalStateException; else decrement and return.
  When `card == KNIGHT`, also bump the army count by **reusing the existing `playKnight()`** (TC12–TC17).

**Change:**
- `getVictoryPoints()` — replace `+ victoryPointDevCards` with `+ getDevelopmentCardCount(VICTORY_POINT)`
  (TC18–TC20). This mirrors how settlement VP is already derived from the `settlements` list.
- Copy constructor — copy the new `devCards` map (`new EnumMap<>(other.devCards)`).

**Remove (now duplication):**
- `private int victoryPointDevCards;`
- `public void addVictoryPointDevCard()` and its copy-constructor line.

**Keep (NOT duplication):**
- `int knightsPlayed`, `playKnight()`, `getKnightsPlayed()`, `awardLargestArmy()`,
  `hasLongestRoad`/`hasLargestArmy` flags.

## 3. Callers & existing tests to update
- `grep -rn addVictoryPointDevCard src` → replace any caller with `buyDevelopmentCard(VICTORY_POINT)`.
  (Currently only referenced inside `Player`.)
- `docs/bva/player-bva.md` + `PlayerTest.java`: the "VP dev cards held" case (was `addVictoryPointDevCard`)
  must switch to `buyDevelopmentCard(VICTORY_POINT)`. Update that BVA row + test together.

## 4. `domain/Game.java` — buy-delay rule (Game's BVA, not this doc)
- Track which card(s) the current player bought **this turn** (e.g. a per-turn set on Game).
- Reject `playDevelopmentCard` for a card bought this turn until the player's next turn;
  clear/activate the set on turn advance.
- Add these boundary cases to **Game's** BVA (buy this turn → can't play; next turn → can play).

## 5. Bank (Friend B) — coordinate, do not duplicate
- Bank owns the **deck** (14 Knight / 5 VP / 2 Road Building / 2 Year of Plenty / 2 Monopoly).
- Deck as `Deque<DevelopmentCard>`; `drawDevelopmentCard()` / `returnDevelopmentCard(card)`.
- Use `card.isReturnableToBank()` for the "VP cards can't be returned" rule — import `domain.DevelopmentCard`,
  do **not** declare a second card enum.
- Buy flow (later, in Game): pay cost (ORE+SHEEP+WHEAT) → `bank.drawDevelopmentCard()` → `player.buyDevelopmentCard(card)`.

## 6. Effects (deferred) — BVA TC21–TC40, orchestrated by `Game`
Implement only after Bank/Robber/Game integrate. Each removes the card via
`player.playDevelopmentCard(card)`, then resolves against canonical `Player` objects (never board/robber copies):
- **Knight** (TC21–26): move Robber → steal 1 random resource → `playKnight()` already bumps army; Game recomputes Largest Army (≥3 and strictly greatest).
- **Monopoly** (TC27–31): take all of one resource from every opponent; reject `null`/`GENERIC`.
- **Year of Plenty** (TC32–36): draw 2 from Bank; reject `null`/`GENERIC`/insufficient stock.
- **Road Building** (TC37–40): place up to 2 free roads via Board; respect remaining-roads cap and edge legality.

---

## Victory-point sources (reference)
`Player.getVictoryPoints()` aggregates exactly four: (1) settlements/cities via `Settlement.getVictoryPoints()`,
(2) VP dev cards (→ moving to the `devCards` map), (3) Longest Road flag +2, (4) Largest Army flag +2.
