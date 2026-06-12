package board;

import domain.Player;

public class Settlement {

    private final Player owner;
    private boolean city = false;

    public Settlement(Player owner) {
        this.owner = owner;
    }

    public Player getOwner() {
        return owner;
    }

    public void upgrade() {
        if (city) {
            throw new IllegalStateException("already a city");
        }
        city = true;
    }

    public boolean isCity() {
        return city;
    }

    public int getVictoryPoints() {
        return city ? 2 : 1;
    }
}