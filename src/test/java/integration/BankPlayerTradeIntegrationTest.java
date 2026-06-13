package integration;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import domain.Bank;
import domain.Player;
import domain.PlayerColor;
import domain.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BankPlayerTradeIntegrationTest {

  private static final int FULL_STOCK = 19;
  private static final int TRADE_RATE = 4;

  private Bank bank;
  private Player player;

  @BeforeEach
  void setUp() {
    bank = new Bank(deck -> { });
    player = new Player("Alice", PlayerColor.RED);
  }

  @Test
  void shouldMoveResourcesFromBankToPlayer_whenBankDistributesWood() {
    bank.distributeResource(Resource.WOOD, TRADE_RATE);
    player.addResource(Resource.WOOD, TRADE_RATE);

    assertAll(
        () -> assertEquals(TRADE_RATE, player.getResourceCount(Resource.WOOD)),
        () -> assertEquals(FULL_STOCK - TRADE_RATE, bank.getStock(Resource.WOOD)),
        () -> assertEquals(FULL_STOCK,
            player.getResourceCount(Resource.WOOD) + bank.getStock(Resource.WOOD)));
  }

  @Test
  void shouldConserveResourcesAcrossBankAndPlayer_whenTradingFourWoodForOneBrick() {
    bank.distributeResource(Resource.WOOD, TRADE_RATE);
    player.addResource(Resource.WOOD, TRADE_RATE);

    player.removeResource(Resource.WOOD, TRADE_RATE);
    bank.maritimeTrade(Resource.WOOD, TRADE_RATE, Resource.BRICK);
    player.addResource(Resource.BRICK, 1);

    assertAll(
        () -> assertEquals(0, player.getResourceCount(Resource.WOOD)),
        () -> assertEquals(1, player.getResourceCount(Resource.BRICK)),
        () -> assertEquals(FULL_STOCK, bank.getStock(Resource.WOOD)),
        () -> assertEquals(FULL_STOCK - 1, bank.getStock(Resource.BRICK)),
        () -> assertEquals(FULL_STOCK,
            player.getResourceCount(Resource.WOOD) + bank.getStock(Resource.WOOD)),
        () -> assertEquals(FULL_STOCK,
            player.getResourceCount(Resource.BRICK) + bank.getStock(Resource.BRICK)));
  }
}
