package board;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SettlementTest {

    // TC1 – fresh Settlement is not a city and yields 1 VP
    @Test
    void constructor_freshSettlement_isNotCityAndHasOneVp() {
        Settlement settlement = new Settlement();
        assertFalse(settlement.isCity());
        assertEquals(1, settlement.getVictoryPoints());
    }

    // TC2 – two constructions produce independent instances
    @Test
    void constructor_twoInstances_areIndependent() {
        Settlement s1 = new Settlement();
        Settlement s2 = new Settlement();
        assertNotSame(s1, s2);
    }

    // TC3 – isCity() returns false on a fresh Settlement
    @Test
    void isCity_freshSettlement_returnsFalse() {
        Settlement settlement = new Settlement();
        assertFalse(settlement.isCity());
    }
}