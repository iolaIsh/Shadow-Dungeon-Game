import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

/**
 * Straight line projectile fired by the player
 */
public class Bullet {
    private final Image image = new Image("res/bullet.png");
    private Point pos;
    private final double vx, vy;
    private final int damage;
    private boolean dead = false;

    /**
     * Creates a bullet traveling
     * @param from start position
     * @param to aim target
     * @param damage damage given on hit to enemy
     */
    public Bullet(Point from, Point to, int damage) {
        this.damage = damage;
        double speed = parseDoubleProp("bulletSpeed", 4.5);
        double dx = to.x - from.x, dy = to.y - from.y;
        double len = Math.max(1e-6, Math.hypot(dx, dy));
        vx = speed * dx/len;
        vy = speed * dy/len;
        pos = new Point(from.x, from.y);
    }

    /**
     * Moves the bullet, checks collisions, and marks dead if needed
     * @param room the active battle room for collision inquiries
     */
    public void update(BattleRoom room) {
        if (dead) return;

        pos = new Point(pos.x + vx, pos.y + vy);

        // if off-screen
        if (pos.x < 0 || pos.x > ShadowDungeon.screenWidth ||
                pos.y < 0 || pos.y > ShadowDungeon.screenHeight) {
            dead = true;
            return;
        }

        Rectangle bounds = image.getBoundingBoxAt(pos);

        // bullets vs obstacles
        if (room.handleBullets(bounds)) {
            dead = true;
            return;
        }

        // hit enemy
        if (room.hitAnyEnemy(bounds, damage)) {
            dead = true;
            return;
        }

        // hit obstacle
        if (room.hitsObstacle(bounds)) {
            dead = true;
            return;
        }
    }

    /**
     * Draws the bullet if still alive
     */
    public void draw() {
        if (!dead) {
            image.draw(pos.x, pos.y);
        }
    }

    /**
     * @return true if bullet needs to be removed
     */
    public boolean isDead() {
        return dead;
    }

    private static double parseDoubleProp(String k, double def) {
        try { return Double.parseDouble(ShadowDungeon.getGameProps().getProperty(k)); }
        catch (Exception e) { return def; }
    }
}
