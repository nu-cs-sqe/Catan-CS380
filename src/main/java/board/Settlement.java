package board;

import domain.Player;

public class Settlement {

    private final Player owner;
    private boolean city = false;

    public Settlement(Player owner) {
        this.owner = (owner != null) ? new Player(owner) : null;
    }

    public Player getOwner() {
        return (owner != null) ? new Player(owner) : null;
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