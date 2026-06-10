package board;

public class Settlement {

    private boolean city = false;

    public void upgrade() {
        city = true;
    }

    public boolean isCity() {
        return city;
    }

    public int getVictoryPoints() {
        return 1;
    }
}