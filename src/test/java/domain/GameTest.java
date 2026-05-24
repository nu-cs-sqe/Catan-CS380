package domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameTest {

    // TC1 – Game accepts exactly 3 players
    @Test
    public void testGameAccepts3Players() {
        Game game = new Game(3);
        assertEquals(3, game.getNumberOfPlayers());
    }

    // TC2 – Game accepts exactly 4 players
    @Test
    public void testGameAccepts4Players() {
        Game game = new Game(4);
        assertEquals(4, game.getNumberOfPlayers());
    }

    // TC3 – Game rejects 2 players
    @Test
    public void testGameRejects2Players() {
        assertThrows(IllegalArgumentException.class, () -> new Game(2));
    }

    // TC4 – Game rejects 5 players
    @Test
    public void testGameRejects5Players() {
        assertThrows(IllegalArgumentException.class, () -> new Game(5));
    }
}