import bagel.*;
import bagel.util.Point;
import bagel.Keys;

/**
 * In game pause store overlap
 * While visible, the world is paused
 * Player can purchase upgrades or restart game
 */
public class Store {
    private final Image storeImage;

    /** loads store overlay image */
    public Store() {
        storeImage = new Image("res/store.png");
    }
    /** If store overlay is currently visible */
    public boolean visible;

    /** @return if store overlay is visible */
    public boolean isVisible() { return visible; }

    /** Toggles store overlay visibility */
    public void toggle() { visible = !visible; }

    /** Hides the store overlay */
    public void hide() { visible = false; }

    /**
     * Handles store input when visible
     * @param input current input
     * @param player player making purchases
     */
    public void update(Input input, Player player) {
        if (!visible) return;

        // L: Weapon upgrade
        if (input.wasPressed(Keys.L)) {
            int cost = propInt("weaponPurchase", 50);
            if (player.spendCoins(cost)) {
                ShadowDungeon.upgradeWeaponLevelIfPossible();
            }
        }

        // E: buy health
        if (input.wasPressed(Keys.E)) {
            int cost = propInt("healthPurchase", 50);
            int bonus = propInt("healthBonus", 50);
            if (player.spendCoins(cost)) {
                player.heal(bonus);
            }
        }

        // P: restart game
        if (input.wasPressed(Keys.P)) {
            ShadowDungeon.resetGameState(ShadowDungeon.getGameProps());
            hide();
        }
    }

    /**
     * Draws the store overlay
     * @param player player kept for consistency
     */
    public void drawOverlay (Player player) {
        if (!visible) return;

        // where to draw store page
        Point center = IOUtils.parseCoords(ShadowDungeon.getGameProps().getProperty("store"));

        // store image
        storeImage.draw(center.x, center.y);

    }

    private static int propInt(String key, int def) {
        try {
            return Integer.parseInt(ShadowDungeon.getGameProps().getProperty(key));
        } catch (Exception e) {
            return def;
        }
    }
}
