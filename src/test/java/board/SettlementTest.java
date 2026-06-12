package board;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import domain.Player;
import domain.PlayerColor;
import org.junit.jupiter.api.Test;

class SettlementTest {

    private final Player player = new Player("Alice", PlayerColor.RED);

    // TC1 – fresh Settlement is not a city and yields 1 VP
    @Test
    void constructor_freshSettlement_isNotCityAndHasOneVp() {
        Settlement settlement = new Settlement(player);
        assertFalse(settlement.isCity());
        assertEquals(1, settlement.getVictoryPoints());
    }

    // TC2 – two constructions produce independent instances
    @Test
    void constructor_twoInstances_areIndependent() {
        Settlement s1 = new Settlement(player);
        Settlement s2 = new Settlement(player);
        assertNotSame(s1, s2);
    }

    // TC3 – isCity() returns false on a fresh Settlement
    @Test
    void isCity_freshSettlement_returnsFalse() {
        Settlement settlement = new Settlement(player);
        assertFalse(settlement.isCity());
    }

    // TC4 – isCity() returns true after upgrade()
    @Test
    void isCity_afterUpgrade_returnsTrue() {
        Settlement settlement = new Settlement(player);
        settlement.upgrade();
        assertTrue(settlement.isCity());
    }

    // TC5 – upgrade() on a non-city sets isCity to true
    @Test
    void upgrade_onNonCity_setsIsCity() {
        Settlement settlement = new Settlement(player);
        settlement.upgrade();
        assertTrue(settlement.isCity());
    }

    // TC6 – upgrade() on an already-upgraded city throws IllegalStateException
    @Test
    void upgrade_onCity_throwsIllegalStateException() {
        Settlement settlement = new Settlement(player);
        settlement.upgrade();
        assertThrows(IllegalStateException.class, settlement::upgrade);
    }

    // TC7 – getVictoryPoints() returns 1 before upgrade
    @Test
    void getVictoryPoints_notUpgraded_returnsOne() {
        Settlement settlement = new Settlement(player);
        assertEquals(1, settlement.getVictoryPoints());
    }

    // TC8 – getVictoryPoints() returns 2 after upgrade()
    @Test
    void getVictoryPoints_afterUpgrade_returnsTwo() {
        Settlement settlement = new Settlement(player);
        settlement.upgrade();
        assertEquals(2, settlement.getVictoryPoints());
    }

    // TC9 – getOwner() returns the player passed to the constructor
    @Test
    void getOwner_returnsConstructorOwner() {
        Settlement settlement = new Settlement(player);
        assertSame(player, settlement.getOwner());
    }
}