# BVA Analysis – `Bank`

The `Bank` holds all unowned resources (19 of each) and the shuffled development card deck (25 cards).
It is the authoritative source for resource distribution, resource returns, development card draws, and maritime trades.

---

## Method under test: `Bank(Shuffler)` – constructor / initial state

### TC1 – Initial resource stock is 19 per resource
- **State of the system**: Freshly constructed Bank
- **Expected output**: `getStock(r) == 19` for each of BRICK, LUMBER, WOOL, GRAIN, ORE
- **BVA note**: 19 is the fixed starting supply per resource per Catan rules
- **Implemented**: [x]

### TC2 – Initial development card count is 25
- **State of the system**: Freshly constructed Bank
- **Expected output**: `getDevCardCount() == 25`
- **BVA note**: 14 Knights + 5 VP + 2 Road Building + 2 Year of Plenty + 2 Monopoly = 25
- **Implemented**: [x]

### TC3 – Initial deck composition is correct
- **State of the system**: Freshly constructed Bank with no-op shuffler
- **Expected output**: Drawing all 25 cards yields exactly 14 KNIGHT, 5 VICTORY_POINT, 2 ROAD_BUILDING, 2 YEAR_OF_PLENTY, 2 MONOPOLY
- **BVA note**: Verifies each card type count; composition is independent of shuffle order
- **Implemented**: [x]

### TC4 – Null shuffler throws NullPointerException
- **State of the system**: Constructing Bank with null shuffler
- **Expected output**: `NullPointerException`
- **BVA note**: Null boundary for required parameter
- **Implemented**: [x]

---

## Method under test: `getStock(Resource)`

### TC5 – Null resource throws NullPointerException
- **State of the system**: Freshly constructed Bank
- **Expected output**: `NullPointerException`
- **BVA note**: Null boundary for required parameter
- **Implemented**: [x]

---

## Method under test: `distributeResource(Resource, int)`

### TC6 – Distributing 1 reduces stock by 1
- **State of the system**: Stock = 19, distribute 1
- **Expected output**: Stock becomes 18
- **BVA note**: Normal case; stock decreases
- **Implemented**: [x]

### TC7 – Distributing exactly all stock succeeds (boundary)
- **State of the system**: Stock = 19, distribute 19
- **Expected output**: Stock becomes 0
- **BVA note**: Exact boundary — last available card distributed
- **Implemented**: [x]

### TC8 – Distributing one more than stock throws IllegalStateException (boundary)
- **State of the system**: Stock = 19, distribute 20
- **Expected output**: `IllegalStateException`
- **BVA note**: One past the boundary — insufficient stock
- **Implemented**: [x]

### TC9 – Zero amount throws IllegalArgumentException
- **State of the system**: Any bank, amount = 0
- **Expected output**: `IllegalArgumentException`
- **BVA note**: Amount must be positive; 0 is the boundary below valid range
- **Implemented**: [x]

### TC10 – Negative amount throws IllegalArgumentException
- **State of the system**: Any bank, amount = -1
- **Expected output**: `IllegalArgumentException`
- **BVA note**: Negative boundary below valid range
- **Implemented**: [x]

### TC11 – Null resource throws NullPointerException
- **State of the system**: Any bank, null resource
- **Expected output**: `NullPointerException`
- **Implemented**: [x]

---

## Method under test: `canDistribute(Resource, int)`

### TC12 – Returns true when stock is sufficient
- **State of the system**: Stock = 19, amount = 1
- **Expected output**: `true`
- **BVA note**: Normal within-bounds case
- **Implemented**: [x]

### TC13 – Returns true when amount equals stock exactly (boundary)
- **State of the system**: Stock = 19, amount = 19
- **Expected output**: `true`
- **BVA note**: Exact boundary — just enough
- **Implemented**: [x]

### TC14 – Returns false when amount exceeds stock by 1 (boundary)
- **State of the system**: Stock = 19, amount = 20
- **Expected output**: `false`
- **BVA note**: One past boundary — not enough
- **Implemented**: [x]

### TC15 – Zero amount throws IllegalArgumentException
- **State of the system**: Any bank, amount = 0
- **Expected output**: `IllegalArgumentException`
- **BVA note**: Amount must be positive
- **Implemented**: [x]

---

## Method under test: `returnResource(Resource, int)`

### TC16 – Returning 1 increases stock by 1
- **State of the system**: Stock = 18 (after 1 distributed), return 1
- **Expected output**: Stock becomes 19
- **BVA note**: Normal return case; stock increases
- **Implemented**: [x]

### TC17 – Zero amount throws IllegalArgumentException
- **State of the system**: Any bank, amount = 0
- **Expected output**: `IllegalArgumentException`
- **BVA note**: Amount boundary below valid range
- **Implemented**: [x]

### TC18 – Negative amount throws IllegalArgumentException
- **State of the system**: Any bank, amount = -1
- **Expected output**: `IllegalArgumentException`
- **BVA note**: Negative boundary
- **Implemented**: [x]

---

## Method under test: `drawDevelopmentCard()`

### TC19 – Drawing reduces deck count by 1
- **State of the system**: Deck has 25 cards
- **Expected output**: `getDevCardCount() == 24` after one draw
- **BVA note**: Normal draw case
- **Implemented**: [x]

### TC20 – Drawing last card succeeds (boundary)
- **State of the system**: Deck has exactly 1 card remaining
- **Expected output**: Card returned, deck count becomes 0
- **BVA note**: Boundary — last valid draw
- **Implemented**: [x]

### TC21 – Drawing from empty deck throws IllegalStateException (boundary)
- **State of the system**: Deck has 0 cards
- **Expected output**: `IllegalStateException`
- **BVA note**: One past boundary — deck exhausted
- **Implemented**: [x]

---

## Method under test: `returnDevelopmentCard(DevelopmentCard)`

### TC22 – Returning a KNIGHT card increases deck count
- **State of the system**: Any bank
- **Expected output**: `getDevCardCount()` increases by 1
- **BVA note**: Action card — valid to return
- **Implemented**: [x]

### TC23 – Returning a ROAD_BUILDING card increases deck count
- **State of the system**: Any bank
- **Expected output**: `getDevCardCount()` increases by 1
- **Implemented**: [x]

### TC24 – Returning a YEAR_OF_PLENTY card increases deck count
- **State of the system**: Any bank
- **Expected output**: `getDevCardCount()` increases by 1
- **Implemented**: [x]

### TC25 – Returning a MONOPOLY card increases deck count
- **State of the system**: Any bank
- **Expected output**: `getDevCardCount()` increases by 1
- **Implemented**: [x]

### TC26 – Returning a VICTORY_POINT card throws IllegalArgumentException
- **State of the system**: Any bank
- **Expected output**: `IllegalArgumentException`
- **BVA note**: VP cards are kept by the player permanently — returning one is a programming error
- **Implemented**: [x]

### TC27 – Null card throws NullPointerException
- **State of the system**: Any bank
- **Expected output**: `NullPointerException`
- **Implemented**: [x]

---

## Method under test: `maritimeTrade(Resource give, int giveCount, Resource receive)`

### TC28 – Rate 1 throws IllegalArgumentException (boundary below valid)
- **State of the system**: Any bank with stock
- **Expected output**: `IllegalArgumentException`
- **BVA note**: 1 is one below minimum valid rate of 2
- **Implemented**: [x]

### TC29 – Rate 2 succeeds (boundary — minimum valid)
- **State of the system**: Bank has receive resource in stock
- **Expected output**: Bank stock of give increases by 2, receive decreases by 1
- **BVA note**: 2:1 specific harbor rate — minimum valid trade rate
- **Implemented**: [x]

### TC30 – Rate 3 succeeds
- **State of the system**: Bank has receive resource in stock
- **Expected output**: Bank stock of give increases by 3, receive decreases by 1
- **BVA note**: 3:1 generic harbor rate
- **Implemented**: [x]

### TC31 – Rate 4 succeeds (boundary — maximum valid)
- **State of the system**: Bank has receive resource in stock
- **Expected output**: Bank stock of give increases by 4, receive decreases by 1
- **BVA note**: 4:1 default rate — maximum valid trade rate
- **Implemented**: [ ]

### TC32 – Rate 5 throws IllegalArgumentException (boundary above valid)
- **State of the system**: Any bank
- **Expected output**: `IllegalArgumentException`
- **BVA note**: 5 is one above maximum valid rate of 4
- **Implemented**: [ ]

### TC33 – Same resource for give and receive throws IllegalArgumentException
- **State of the system**: Any bank
- **Expected output**: `IllegalArgumentException`
- **BVA note**: Cannot trade a resource for itself
- **Implemented**: [ ]

### TC34 – Bank out of receive resource throws IllegalStateException
- **State of the system**: Bank stock of receive = 0
- **Expected output**: `IllegalStateException`
- **BVA note**: Boundary — bank cannot fulfill the trade
- **Implemented**: [ ]

### TC35 – Null give resource throws NullPointerException
- **State of the system**: Any bank
- **Expected output**: `NullPointerException`
- **Implemented**: [ ]

### TC36 – Null receive resource throws NullPointerException
- **State of the system**: Any bank
- **Expected output**: `NullPointerException`
- **Implemented**: [ ]
