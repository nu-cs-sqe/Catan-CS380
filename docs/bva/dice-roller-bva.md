# BVA Analysis – `RandomDiceRoller`

## Method under test: `roll()`

Simulates rolling two six-sided dice. Returns the sum.

---

### TC1 – Roll result is at least 2
- **State of the system**: New RandomDiceRoller
- **Expected output**: `roll() >= 2` over many invocations
- **BVA note**: Minimum possible sum is 1+1=2. Boundary: 1 (invalid), 2 (valid minimum)
- **Implemented**: [x]

### TC2 – Roll result is at most 12
- **State of the system**: New RandomDiceRoller
- **Expected output**: `roll() <= 12` over many invocations
- **BVA note**: Maximum possible sum is 6+6=12. Boundary: 12 (valid maximum), 13 (invalid)
- **Implemented**: [x]