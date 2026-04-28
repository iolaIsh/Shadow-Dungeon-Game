import bagel.Image;
import bagel.util.Point;

/**
 * Destructible obstacle
 * when hit by a player's bullet, it is destroyed
 * The bullet is removed and player recieves coin reward
 * Basket blocks the player when active; enemies can pass through
 */
public class Basket extends Obstacle {
    private static int COINS;

    /**
     * Creates a basket at given position
     * @param position
     */
    public Basket(Point position) {
        super(position, new Image("res/basket.png"));
    }

    /**
     * Per frame update for player collision blocking
     * @param player the player needed to block
     */
    public void update(Player player) {
        blockPlayerColliding(player);
    }

    /**
     * Called when a player's bullet hits the obstacle
     * Destroys the basket and pays coins to shooter
     * @param shooter the player who fired the bullet
     */
    @Override
    public void onBulletHit(Player shooter) {
        if (!isActive()) return;
        destroy();
        if (shooter != null) shooter.earnCoins(20); // need to read from properties
    }

    /* private helpers */
    private static int getCoinsPerBasket() {
        COINS = parseIntProp("basketCoin", 20);
        return COINS;
    }

    private static int parseIntProp(String k, int definition) {
        try {
            return Integer.parseInt(ShadowDungeon.getGameProps().getProperty(k));
        } catch (Exception e) {
            return definition;
        }
    }
}
