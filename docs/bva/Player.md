# BVA: Player

Boundary Value Analysis for the `domain.Player` class.
Each row corresponds to a test in `src/test/java/domain/PlayerTest.java`.

Rulebook references come from `docs/requirements/game-rules.md`.

### Method under test: `Player(String name, PlayerColor color)`

|      | System under test                | Expected output                        | Implemented? |
|------|----------------------------------|----------------------------------------|--------------|
| TC1  | name = "Alice", color = RED      | Player constructed, name/color set     | yes          |
| TC2  | name = null                      | NullPointerException                   | yes          |
| TC3  | name = ""                        | IllegalArgumentException               | yes          |
| TC4  | name = "   " (blank)             | IllegalArgumentException               | yes          |
| TC5  | color = null                     | NullPointerException                   | yes          |
| TC6  | freshly constructed              | 0 VP, 0 of every resource, full pieces | yes          |
