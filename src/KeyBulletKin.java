import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

import java.util.ArrayList;
import java.util.List;

/**
 * Non shooting enemy that follows a fixed route
 * On death, it drops a key
 * While active, colldiing with player damages the player
 */
public class KeyBulletKin extends Enemy {
    private final List<Point> route;
    private int index = 0;
    private final double speed;
    private boolean active = false;
    private boolean droppedKey = false;

    /**
     * Creates a Key Bullet Kin with a multi point route
     * @param route ordered list of points
     */
    public KeyBulletKin(List<Point> route) {
        super(
                route.get(0),
                parseIntProp("keyBulletKinHealth", 30),
                new Image("res/key_bullet_kin.png"),
                0
        );
        this.route = new ArrayList<>(route);
        this.active = false;
        this.speed = parseDoubleProp("keyBulletKinSpeed", 4.0);
    }

    /**
     * Constructor for a single point route
     * @param oneP single position
     */
    public KeyBulletKin(Point oneP) {
        super(
                oneP,
                parseDoubleProp("keyBulletKinHealth", 30),
                new Image("res/key_bullet_kin.png"),
                0
        );
        this.route = new ArrayList<>();
        this.route.add(new Point(oneP.x, oneP.y));
        this.speed = parseDoubleProp("keyBulletKinSpeed", 4.0);
        this.active = false;
    }

    /**
     * Updates movement along the route when active
     * and applies contact damange if intersect with player
     * @param player the player reference
     * @param room room the enemy is in
     */
    @Override
    public void update(Player player, BattleRoom room) {

        if (!active) {
            return;
        }

        if (!route.isEmpty()) {
            Point position = getPosition();
            Point target = route.get(index);
            double dx = target.x - getPosition().x, dy = target.y - getPosition().y;
            double dist = Math.hypot(dx, dy);

            if (dist <= speed) {
                setPosition(new Point(target.x, target.y));
                //moveTo(target.x, target.y);
                index = (index + 1) % route.size();
            } else {
                double nx = position.x + speed * dx / Math.max(1e-6, dist);
                double ny = position.y + speed*dy / Math.max(1e-6, dist);
                setPosition(new Point(nx, ny));
            }
            // contact damage to player (not kill keybulletkin
            Rectangle myBounds = getBounds();
            Rectangle playerBounds = player.getCurrImage().getBoundingBoxAt(player.getPosition());
            if (myBounds.intersects(playerBounds)) {
                player.receiveDamage(0.2);
            }
        }
    }

    /**
     * Enables and disables enemy
     * @param active if enemy is active
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @return if enemy is active
     */
    public boolean isActive() {
        return active;
    }

    /* private helpers */
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
