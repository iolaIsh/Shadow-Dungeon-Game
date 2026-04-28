import bagel.Image;
import bagel.util.Point;

/**
 * Enemy's projectile attack that travels in straight line
 * Towards position at fire time
 */
public class Fireball {
    private final Image image = new Image("res/fireball.png");
    private Point pos;
    private final double vx, vy;
    private final int damage;
    private boolean dead = false;

    /**
     * Creates a fireball
     * @param from start position (enemy)
     * @param to aim target (player)
     */
    public Fireball(Point from, Point to) {
        double speed = parseDoubleProp("fireballSpeed", 4);
        damage = parseIntProp("fireballDamage", 40);
        double dx = to.x - from.x, dy = to.y - from.y;
        double len = Math.max(1e-6, Math.hypot(dx, dy));
        vx = speed * dx/len;
        vy = speed * dy/len;
        pos = new Point(from.x, from.y);
    }

    /**
     * Updates motion and applies damage if it hits the player
     * @param room the battle room for obstacle checks
     * @param player the player to test for hits
     */
    public void update(BattleRoom room, Player player) {
        if (dead) return;
        pos = new Point(pos.x + vx, pos.y + vy);

        // if off-screen
        if (pos.x < 0 || pos.x > ShadowDungeon.screenWidth||
                pos.y < 0 || pos.y > ShadowDungeon.screenHeight) {
            dead = true;
            return;
        }

        // hit player
        if (image.getBoundingBoxAt(pos).intersects(player.getCurrImage().getBoundingBoxAt(player.getPosition()))) {
            player.receiveDamage(damage);
            dead = true;
            return;
        }

        // hit obstacle
        if (room.hitsObstacle(image.getBoundingBoxAt(pos))) {
            dead = true;
        }

    }

    /**
     * Draws the fireball if alive
     */
    public void draw() {
        if (!dead) {
            image.draw(pos.x, pos.y);
        }
    }

    /**
     * @return true if fireball should be removed
     */
    public boolean isDead() {
        return dead;
    }

    /* Private helper functions */
    private static int parseIntProp(String k, int definition) {
        try {
            return Integer.parseInt(ShadowDungeon.getGameProps().getProperty(k));
        } catch (Exception e) {
            return definition;
        }
    }

    private static double parseDoubleProp(String k, double def) {
        try { return Double.parseDouble(ShadowDungeon.getGameProps().getProperty(k)); }
        catch (Exception e) { return def; }
    }

}
