# BVA: Settlement

## Solitary tests

### Method under test: `Settlement(Player owner)` (constructor)

A settlement is always created with the player that owns it; the owner is
exposed via `getOwner()` so a vertex can derive its owner from its settlement.

|     | System under test            | Expected output                                                              | Implemented? |
|-----|------------------------------|------------------------------------------------------------------------------|--------------|
| TC1 | construction with an owner   | Settlement created, `isCity()` returns false, `getVictoryPoints()` returns 1 | yes          |
| TC2 | two separate constructions   | each instance is independent (not same reference)                            | yes          |

### Method under test: `isCity()`

|     | System under test                   | Expected output | Implemented? |
|-----|-------------------------------------|-----------------|--------------|
| TC3 | freshly constructed Settlement      | false           | yes          |
| TC4 | Settlement after `upgrade()` called | true            | yes          |

### Method under test: `upgrade()`

|     | System under test                                  | Expected output                | Implemented? |
|-----|----------------------------------------------------|--------------------------------|--------------|
| TC5 | settlement not yet a city → call `upgrade()`       | `isCity()` becomes true        | yes          |
| TC6 | settlement already a city → call `upgrade()` again | `IllegalStateException` thrown | yes          |

### Method under test: `getVictoryPoints()`

|     | System under test                   | Expected output | Implemented? |
|-----|-------------------------------------|-----------------|--------------|
| TC7 | Settlement not yet upgraded         | 1               | yes          |
| TC8 | Settlement after `upgrade()` called | 2               | yes          |

### Method under test: `getOwner()`

|     | System under test                       | Expected output              | Implemented? |
|-----|-----------------------------------------|------------------------------|--------------|
| TC9 | `new Settlement(player)` → `getOwner()` | returns the player passed in | no           |
