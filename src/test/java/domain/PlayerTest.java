package domain;

import board.Edge;
import board.Vertex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerTest {

  private static final String DEFAULT_NAME = "Alice";
  private static final PlayerColor DEFAULT_COLOR = PlayerColor.RED;
  private static final int STARTING_SETTLEMENTS = 5;
  private static final int STARTING_CITIES = 4;
  private static final int STARTING_ROADS = 15;
  private static final Map<Resource, Integer> SETTLEMENT_COST =
      Map.of(
          Resource.BRICK, 1,
          Resource.WOOD, 1,
          Resource.SHEEP, 1,
          Resource.WHEAT, 1);

  private Player player;
  private int vertexCounter;

  @BeforeEach
  void setUp() {
    player = new Player(DEFAULT_NAME, DEFAULT_COLOR);
    vertexCounter = 0;
  }

  // BVA TC1
  @Test
  void shouldExposeNameAndColor_whenConstructedWithValidArguments() {
    assertAll(
        () -> assertEquals(DEFAULT_NAME, player.getName()),
        () -> assertEquals(DEFAULT_COLOR, player.getColor()));
  }

  // BVA TC2
  @Test
  void shouldThrowNullPointer_whenNameIsNull() {
    assertThrows(NullPointerException.class, () -> new Player(null, DEFAULT_COLOR));
  }

  // BVA TC3
  @Test
  void shouldThrowIllegalArgument_whenNameIsEmpty() {
    assertThrows(IllegalArgumentException.class, () -> new Player("", DEFAULT_COLOR));
  }

  // BVA TC4
  @Test
  void shouldThrowIllegalArgument_whenNameIsBlank() {
    assertThrows(IllegalArgumentException.class, () -> new Player("   ", DEFAULT_COLOR));
  }

  // BVA TC5
  @Test
  void shouldThrowNullPointer_whenColorIsNull() {
    assertThrows(NullPointerException.class, () -> new Player(DEFAULT_NAME, null));
  }

  // BVA TC6
  @Test
  void shouldStartWithZeroVpAndZeroResourcesAndFullPieces_whenFreshlyConstructed() {
    assertAll(
        () -> assertEquals(0, player.getVictoryPoints()),
        () -> assertEquals(0, player.getResourceCount(Resource.BRICK)),
        () -> assertEquals(0, player.getResourceCount(Resource.WOOD)),
        () -> assertEquals(0, player.getResourceCount(Resource.SHEEP)),
        () -> assertEquals(0, player.getResourceCount(Resource.WHEAT)),
        () -> assertEquals(0, player.getResourceCount(Resource.ORE)),
        () -> assertEquals(STARTING_SETTLEMENTS, player.getRemainingSettlements()),
        () -> assertEquals(STARTING_CITIES, player.getRemainingCities()),
        () -> assertEquals(STARTING_ROADS, player.getRemainingRoads()));
  }

  // BVA TC7
  @Test
  void shouldIncreaseResourceCountToOne_whenAddingOneToEmptyHand() {
    player.addResource(Resource.BRICK, 1);
    assertEquals(1, player.getResourceCount(Resource.BRICK));
  }

  // BVA TC8
  @Test
  void shouldLeaveResourceCountUnchanged_whenAddingZero() {
    player.addResource(Resource.BRICK, 0);
    assertEquals(0, player.getResourceCount(Resource.BRICK));
  }

  // BVA TC9
  @Test
  void shouldThrowIllegalArgument_whenAddingNegativeAmount() {
    assertThrows(IllegalArgumentException.class, () -> player.addResource(Resource.BRICK, -1));
  }

  // BVA TC10
  @Test
  void shouldThrowNullPointer_whenAddingNullResource() {
    assertThrows(NullPointerException.class, () -> player.addResource(null, 1));
  }

  // BVA TC11
  @Test
  void shouldAccumulateResourceCount_whenAddingSameResourceMultipleTimes() {
    player.addResource(Resource.BRICK, 3);
    player.addResource(Resource.BRICK, 2);
    assertEquals(5, player.getResourceCount(Resource.BRICK));
  }

  // BVA TC12
  @Test
  void shouldReduceResourceCountToZero_whenRemovingExactHeldAmount() {
    player.addResource(Resource.BRICK, 2);
    player.removeResource(Resource.BRICK, 2);
    assertEquals(0, player.getResourceCount(Resource.BRICK));
  }

  // BVA TC13
  @Test
  void shouldThrowIllegalStateAndLeaveCountUnchanged_whenRemovingMoreThanHeld() {
    player.addResource(Resource.BRICK, 2);
    assertThrows(IllegalStateException.class, () -> player.removeResource(Resource.BRICK, 3));
    assertEquals(2, player.getResourceCount(Resource.BRICK));
  }

  // BVA TC14
  @Test
  void shouldThrowIllegalArgument_whenRemovingNegativeAmount() {
    assertThrows(IllegalArgumentException.class, () -> player.removeResource(Resource.BRICK, -1));
  }

  // BVA TC15
  @Test
  void shouldReturnTrue_whenHoldingExactSettlementCost() {
    player.addResource(Resource.BRICK, 1);
    player.addResource(Resource.WOOD, 1);
    player.addResource(Resource.SHEEP, 1);
    player.addResource(Resource.WHEAT, 1);
    assertTrue(player.hasResources(SETTLEMENT_COST));
  }

  // BVA TC16
  @Test
  void shouldReturnFalse_whenMissingOneGrainOfSettlementCost() {
    player.addResource(Resource.BRICK, 1);
    player.addResource(Resource.WOOD, 1);
    player.addResource(Resource.SHEEP, 1);
    assertFalse(player.hasResources(SETTLEMENT_COST));
  }

  // BVA TC17
  @Test
  void shouldDiscardZero_whenHandSizeIsBelowThreshold() {
    player.addResource(Resource.BRICK, 6);
    assertEquals(0, player.discardOnSevenCount());
  }

  // BVA TC18
  @Test
  void shouldDiscardZero_whenHandSizeEqualsThreshold() {
    player.addResource(Resource.BRICK, 7);
    assertEquals(0, player.discardOnSevenCount());
  }

  // BVA TC19
  @Test
  void shouldDiscardHalf_whenHandSizeIsJustAboveThreshold() {
    player.addResource(Resource.BRICK, 8);
    assertEquals(4, player.discardOnSevenCount());
  }

  // BVA TC20
  @Test
  void shouldDiscardHalfRoundedDown_whenHandSizeIsOdd() {
    player.addResource(Resource.BRICK, 9);
    assertEquals(4, player.discardOnSevenCount());
  }

  // BVA TC21
  @Test
  void shouldDiscardZero_whenHandIsEmpty() {
    assertEquals(0, player.discardOnSevenCount());
  }

  // BVA TC22
  @Test
  void shouldHaveZeroVpAndNotWin_whenFreshlyConstructed() {
    assertEquals(0, player.getVictoryPoints());
    assertFalse(player.hasWon());
  }

  // BVA TC23
  @Test
  void shouldHaveSixVp_whenHoldingFourSettlementsAndOneCity() {
    placeSettlementsAndCities(4, 1);
    assertEquals(6, player.getVictoryPoints());
  }

  // BVA TC24
  @Test
  void shouldHaveNineVpAndNotWin_whenHoldingThreeSettlementsAndThreeCities() {
    placeSettlementsAndCities(3, 3);
    assertEquals(9, player.getVictoryPoints());
    assertFalse(player.hasWon());
  }

  // BVA TC25
  @Test
  void shouldHaveTenVpAndWin_whenHoldingFourSettlementsAndThreeCities() {
    placeSettlementsAndCities(4, 3);
    assertEquals(10, player.getVictoryPoints());
    assertTrue(player.hasWon());
  }

  // BVA TC26
  @Test
  void shouldHaveElevenVpAndWin_whenHoldingFiveSettlementsAndThreeCities() {
    placeSettlementsAndCities(5, 3);
    assertEquals(11, player.getVictoryPoints());
    assertTrue(player.hasWon());
  }

  // BVA TC27
  @Test
  void shouldHaveNineVp_whenHoldingThreeSettlementsTwoCitiesAndLongestRoad() {
    placeSettlementsAndCities(3, 2);
    player.awardLongestRoad();
    assertEquals(9, player.getVictoryPoints());
  }

  // BVA TC28
  @Test
  void shouldHaveElevenVpAndWin_whenHoldingPiecesPlusLongestRoadAndLargestArmy() {
    placeSettlementsAndCities(3, 2);
    player.awardLongestRoad();
    player.awardLargestArmy();
    assertEquals(11, player.getVictoryPoints());
    assertTrue(player.hasWon());
  }

  // BVA TC29
  @Test
  void shouldRestoreVp_whenLongestRoadAwardedThenRevoked() {
    placeSettlementsAndCities(3, 2);
    int before = player.getVictoryPoints();
    player.awardLongestRoad();
    player.revokeLongestRoad();
    assertEquals(before, player.getVictoryPoints());
  }

  // BVA TC30
  @Test
  void shouldHaveFiveVp_whenHoldingFiveVictoryPointDevCards() {
    for (int i = 0; i < 5; i++) {
      player.addVictoryPointDevCard();
    }
    assertEquals(5, player.getVictoryPoints());
  }

  // BVA TC31
  @Test
  void shouldStartWithFullPieceInventory_whenFreshlyConstructed() {
    assertAll(
        () -> assertEquals(STARTING_SETTLEMENTS, player.getRemainingSettlements()),
        () -> assertEquals(STARTING_CITIES, player.getRemainingCities()),
        () -> assertEquals(STARTING_ROADS, player.getRemainingRoads()));
  }

  // BVA TC39
  @Test
  void shouldThrowNullPointer_whenPlaceSettlementVertexIsNull() {
    assertThrows(NullPointerException.class, () -> player.placeSettlement(null));
  }

  // BVA TC40
  @Test
  void shouldThrowIllegalState_whenPlacingSettlementOnOccupiedVertex() {
    Vertex v = nextVertex();
    player.placeSettlement(v);
    assertThrows(IllegalStateException.class, () -> player.placeSettlement(v));
  }

  // BVA TC32
  @Test
  void shouldThrowIllegalState_whenPlacingMoreSettlementsThanCap() {
    for (int i = 0; i < STARTING_SETTLEMENTS; i++) {
      player.placeSettlement(nextVertex());
    }
    assertThrows(IllegalStateException.class, () -> player.placeSettlement(nextVertex()));
  }

  // BVA TC42
  @Test
  void shouldThrowIllegalState_whenUpgradingSettlementThatIsAlreadyACity() {
    Vertex v = nextVertex();
    player.placeSettlement(v);
    player.upgradeSettlementToCity(v);
    assertThrows(IllegalStateException.class, () -> player.upgradeSettlementToCity(v));
  }

  // BVA TC35
  @Test
  void shouldUpgradeSettlementToCity_restoreSettlementPieceAndConsumeCity() {
    Vertex v = nextVertex();
    player.placeSettlement(v);
    int settlementsBefore = player.getRemainingSettlements();
    int citiesBefore = player.getRemainingCities();
    player.upgradeSettlementToCity(v);
    assertAll(
        () -> assertTrue(v.getSettlement().isCity()),
        () -> assertEquals(settlementsBefore + 1, player.getRemainingSettlements()),
        () -> assertEquals(citiesBefore - 1, player.getRemainingCities()));
  }

  // BVA TC33
  @Test
  void shouldThrowIllegalState_whenUpgradingMoreCitiesThanCap() {
    for (int i = 0; i < STARTING_CITIES; i++) {
      Vertex v = nextVertex();
      player.placeSettlement(v);
      player.upgradeSettlementToCity(v);
    }
    Vertex overflow = nextVertex();
    player.placeSettlement(overflow);
    assertThrows(IllegalStateException.class, () -> player.upgradeSettlementToCity(overflow));
  }

  // BVA TC36
  @Test
  void shouldThrowIllegalState_whenUpgradingVertexWithNoPlayerSettlement() {
    assertThrows(IllegalStateException.class, () -> player.upgradeSettlementToCity(nextVertex()));
  }

  // BVA TC43
  @Test
  void shouldThrowNullPointer_whenPlaceRoadEdgeIsNull() {
    assertThrows(NullPointerException.class, () -> player.placeRoad(null));
  }

  // BVA TC34
  @Test
  void shouldThrowIllegalState_whenPlacingMoreRoadsThanCap() {
    for (int i = 0; i < STARTING_ROADS; i++) {
      player.placeRoad(new Edge("e" + i));
    }
    assertThrows(IllegalStateException.class, () -> player.placeRoad(new Edge("overflow")));
  }

  // BVA TC41
  @Test
  void shouldThrowNullPointer_whenUpgradeSettlementToCityVertexIsNull() {
    assertThrows(NullPointerException.class, () -> player.upgradeSettlementToCity(null));
  }

  // BVA TC37
  @Test
  void shouldHaveOneKnightPlayed_whenPlayingFirstKnight() {
    player.playKnight();
    assertEquals(1, player.getKnightsPlayed());
  }

  // BVA TC38
  @Test
  void shouldHaveThreeKnightsPlayed_whenPlayingThirdKnight() {
    player.playKnight();
    player.playKnight();
    player.playKnight();
    assertEquals(3, player.getKnightsPlayed());
  }

  // BVA TC44
  @Test
  void shouldHaveEmptyDevelopmentCards_whenFreshlyConstructed() {
    assertTrue(player.getDevelopmentCards().isEmpty());
  }

  // BVA TC45
  @Test
  void shouldReturnCardsInOrder_whenHoldingDevelopmentCards() {
    player.addDevelopmentCard(DevelopmentCard.KNIGHT);
    player.addDevelopmentCard(DevelopmentCard.MONOPOLY);
    assertEquals(
        List.of(DevelopmentCard.KNIGHT, DevelopmentCard.MONOPOLY),
        player.getDevelopmentCards());
  }

  // BVA TC46
  @Test
  void shouldNotExposeInternalList_whenMutatingReturnedDevelopmentCards() {
    player.addDevelopmentCard(DevelopmentCard.KNIGHT);
    List<DevelopmentCard> returned = player.getDevelopmentCards();
    assertThrows(RuntimeException.class, () -> returned.add(DevelopmentCard.MONOPOLY));
    assertEquals(List.of(DevelopmentCard.KNIGHT), player.getDevelopmentCards());
  }

  // BVA TC47
  @Test
  void shouldAppendToEmptyHand_whenAddingSingleDevelopmentCard() {
    player.addDevelopmentCard(DevelopmentCard.KNIGHT);
    assertEquals(List.of(DevelopmentCard.KNIGHT), player.getDevelopmentCards());
  }

  // BVA TC48
  @Test
  void shouldAppendPreservingOrder_whenAddingSingleDevelopmentCardToExistingHand() {
    player.addDevelopmentCard(DevelopmentCard.KNIGHT);
    player.addDevelopmentCard(DevelopmentCard.MONOPOLY);
    assertEquals(
        List.of(DevelopmentCard.KNIGHT, DevelopmentCard.MONOPOLY),
        player.getDevelopmentCards());
  }

  // BVA TC49
  @Test
  void shouldThrowNullPointer_whenAddingNullSingleDevelopmentCard() {
    assertThrows(NullPointerException.class, () -> player.addDevelopmentCard(null));
  }

  // BVA TC50
  @Test
  void shouldRetainDuplicates_whenAddingSameSingleDevelopmentCardTwice() {
    player.addDevelopmentCard(DevelopmentCard.KNIGHT);
    player.addDevelopmentCard(DevelopmentCard.KNIGHT);
    assertEquals(
        List.of(DevelopmentCard.KNIGHT, DevelopmentCard.KNIGHT),
        player.getDevelopmentCards());
  }

  // BVA TC51
  @Test
  void shouldAppendToEmptyHand_whenAddingDevelopmentCards() {
    player.addDevelopmentCards(List.of(DevelopmentCard.KNIGHT, DevelopmentCard.ROAD_BUILDING));
    assertEquals(
        List.of(DevelopmentCard.KNIGHT, DevelopmentCard.ROAD_BUILDING),
        player.getDevelopmentCards());
  }

  // BVA TC52
  @Test
  void shouldConcatenatePreservingOrder_whenAddingDevelopmentCardsToExistingHand() {
    player.addDevelopmentCard(DevelopmentCard.KNIGHT);
    player.addDevelopmentCards(List.of(DevelopmentCard.MONOPOLY));
    assertEquals(
        List.of(DevelopmentCard.KNIGHT, DevelopmentCard.MONOPOLY),
        player.getDevelopmentCards());
  }

  // BVA TC53
  @Test
  void shouldLeaveHandUnchanged_whenAddingEmptyDevelopmentCardList() {
    player.addDevelopmentCard(DevelopmentCard.KNIGHT);
    player.addDevelopmentCards(List.of());
    assertEquals(List.of(DevelopmentCard.KNIGHT), player.getDevelopmentCards());
  }

  // BVA TC54
  @Test
  void shouldRetainDuplicates_whenAddingDevelopmentCardsWithDuplicates() {
    player.addDevelopmentCards(List.of(DevelopmentCard.KNIGHT, DevelopmentCard.KNIGHT));
    assertEquals(
        List.of(DevelopmentCard.KNIGHT, DevelopmentCard.KNIGHT),
        player.getDevelopmentCards());
  }

  // BVA TC55
  @Test
  void shouldThrowNullPointer_whenAddingNullDevelopmentCards() {
    assertThrows(NullPointerException.class, () -> player.addDevelopmentCards(null));
  }

  // BVA TC56
  @Test
  void shouldThrowNullPointer_whenAddingDevelopmentCardsContainingNull() {
    List<DevelopmentCard> withNull = Arrays.asList(DevelopmentCard.KNIGHT, null);
    assertThrows(NullPointerException.class, () -> player.addDevelopmentCards(withNull));
  }

  // BVA TC57
  @Test
  void shouldNotAliasSourceList_whenAddingDevelopmentCards() {
    List<DevelopmentCard> source = new ArrayList<>(List.of(DevelopmentCard.KNIGHT));
    player.addDevelopmentCards(source);
    source.add(DevelopmentCard.MONOPOLY);
    assertEquals(List.of(DevelopmentCard.KNIGHT), player.getDevelopmentCards());
  }

  private Vertex nextVertex() {
    return new Vertex("v" + vertexCounter++);
  }

  private void placeSettlementsAndCities(int numSettlements, int numCities) {
    for (int i = 0; i < numCities; i++) {
      Vertex v = nextVertex();
      player.placeSettlement(v);
      player.upgradeSettlementToCity(v);
    }
    for (int i = 0; i < numSettlements; i++) {
      player.placeSettlement(nextVertex());
    }
  }
}
