import bagel.Image;
import bagel.util.Point;

/**
 * Hazard that applies damage for as long as the player is on it
 */
public class River{
    private final Point position;
    private final Image image;
    private final double damagePerFrame;

    /**
     * Creates a river tile at a position
     * damage per frame is read from app.properties
     * @param position center position of tile
     */
    public River(Point position) {
        this.position = position;
        this.image = new Image("res/river.png");
        this.damagePerFrame = Double.parseDouble(ShadowDungeon.getGameProps().getProperty("riverDamagePerFrame"));
    }

    /**
     * Damages the player if intersecting
     * if their skill allows river damage
     * @param player current player
     */
    public void update(Player player) {
        if (player == null) return;

        if (!player.takesRiverDamage()) {
            return;
        }

        if (hasCollidedWith(player)) {
            player.receiveDamage(damagePerFrame);
        }

    }

    /**
     * Draws river tile
     */
    public void draw() {
        image.draw(position.x, position.y);
    }

    /**
     * @param player the player
     * @return true if player is intersecting with river
     */
    public boolean hasCollidedWith(Player player) {
        return image.getBoundingBoxAt(position).intersects(player.getCurrImage().getBoundingBoxAt(player.getPosition()));
    }

}