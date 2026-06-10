package domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DevelopmentCardTest {

    // BVA TC1
    @Test
    void shouldBeReturnableAction_whenKnight() {
        assertAll(
                () -> assertTrue(DevelopmentCard.KNIGHT.isPlayableAction()),
                () -> assertFalse(DevelopmentCard.KNIGHT.isVictoryPoint()),
                () -> assertTrue(DevelopmentCard.KNIGHT.isReturnableToBank())
        );
    }

    // BVA TC2
    @Test
    void shouldBeReturnableAction_whenRoadBuilding() {
        assertAll(
                () -> assertTrue(DevelopmentCard.ROAD_BUILDING.isPlayableAction()),
                () -> assertFalse(DevelopmentCard.ROAD_BUILDING.isVictoryPoint()),
                () -> assertTrue(DevelopmentCard.ROAD_BUILDING.isReturnableToBank())
        );
    }

    // BVA TC3
    @Test
    void shouldBeReturnableAction_whenYearOfPlenty() {
        assertAll(
                () -> assertTrue(DevelopmentCard.YEAR_OF_PLENTY.isPlayableAction()),
                () -> assertFalse(DevelopmentCard.YEAR_OF_PLENTY.isVictoryPoint()),
                () -> assertTrue(DevelopmentCard.YEAR_OF_PLENTY.isReturnableToBank())
        );
    }

    // BVA TC4
    @Test
    void shouldBeReturnableAction_whenMonopoly() {
        assertAll(
                () -> assertTrue(DevelopmentCard.MONOPOLY.isPlayableAction()),
                () -> assertFalse(DevelopmentCard.MONOPOLY.isVictoryPoint()),
                () -> assertTrue(DevelopmentCard.MONOPOLY.isReturnableToBank())
        );
    }

    // BVA TC5
    @Test
    void shouldBeNonReturnableVictoryPoint_whenVictoryPoint() {
        assertAll(
                () -> assertFalse(DevelopmentCard.VICTORY_POINT.isPlayableAction()),
                () -> assertTrue(DevelopmentCard.VICTORY_POINT.isVictoryPoint()),
                () -> assertFalse(DevelopmentCard.VICTORY_POINT.isReturnableToBank())
        );
    }
}
