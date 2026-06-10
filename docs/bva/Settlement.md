# BVA: Settlement

## Solitary tests

### Method under test: `Settlement()` (constructor)

|     | System under test          | Expected output                                                              | Implemented? |
|-----|----------------------------|------------------------------------------------------------------------------|--------------|
| TC1 | default construction       | Settlement created, `isCity()` returns false, `getVictoryPoints()` returns 1 | yes          |
| TC2 | two separate constructions | each instance is independent (not same reference)                            | yes          |

### Method under test: `isCity()`

|     | System under test                   | Expected output | Implemented? |
|-----|-------------------------------------|-----------------|--------------|
| TC3 | freshly constructed Settlement      | false           | yes          |
| TC4 | Settlement after `upgrade()` called | true            | no           |

### Method under test: `upgrade()`

|     | System under test                                  | Expected output                | Implemented? |
|-----|----------------------------------------------------|--------------------------------|--------------|
| TC5 | settlement not yet a city → call `upgrade()`       | `isCity()` becomes true        | no           |
| TC6 | settlement already a city → call `upgrade()` again | `IllegalStateException` thrown | no           |

### Method under test: `getVictoryPoints()`

|     | System under test                   | Expected output | Implemented? |
|-----|-------------------------------------|-----------------|--------------|
| TC7 | Settlement not yet upgraded         | 1               | no           |
| TC8 | Settlement after `upgrade()` called | 2               | no           |
