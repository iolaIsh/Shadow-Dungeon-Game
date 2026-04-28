import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

/**
 * Collectible key dropped by KeyBulletKin
 * When player shoots with bullet and Keybutlletkin dies
 * then Key is marked and collected
 */
public class Key {
    private final Image image = new Image("res/key.png");
    private Point pos;
    private boolean collected = false;

    /**
     * Creates a key at given location
     * @param pos location of key center point
     */
    public Key(Point pos) {
        this.pos = pos;
    }

    /**
     * per frame update
     * @param player current player
     */
    public void update(Player player) {
        if (collected) return;
        Rectangle b = image.getBoundingBoxAt(pos);
        if (b.intersects(player.getCurrImage().getBoundingBoxAt(player.getPosition()))) {
            collected = true;
            player.addKey(1);
        }
    }

    /**
     * Draws the key if not collected yet
     */
    public void draw() {
        if (!collected) {
            image.draw(pos.x, pos.y);
        }
    }

    /**
     * @return if collected or not
     */
    public boolean isCollected() {
        return collected;
    }

}
