import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

/**
 * Base class for blocking objects
 * Active obstacles block the player but not enemy
 * They block the bullets and fireballs
 * Subclasses might override
 */
public abstract class Obstacle {
    private final Point position;
    private final Image image;
    private boolean active = true;

    /**
     * Creates an obstacle with a sprite at a fixed position
     * @param position center position
     * @param image sprite image
     */
    public Obstacle(Point position, Image image) {
        this.position = position;
        this.image = image;
    }

    /**
     * Draws the obstacle if it is active
     */
    public void draw() {
        if (active) image.draw(position.x, position.y);
    }

    /**
     * @return obstacle's bounding box at its position
     */
    public Rectangle getBounds() {
        return image.getBoundingBoxAt(position);
    }

    /**
     * If player collides with this obstacle when active, push player back to their
     * previous position
     * @param player player to check and push back
     */
    public void blockPlayerColliding(Player player) {
        if (!active) return;
        if (getBounds().intersects(player.getCurrImage().getBoundingBoxAt(player.getPosition()))) {
            player.move(player.getPrevPosition().x, player.getPrevPosition().y);
        }
    }

    /**
     * @return if obstacle is currently active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Deactivates and destroys the obstacle
     */
    public void destroy() {
        active = false;
    }

    /**
     * Called when a player's bullets hits the obstacle
     * @param shooter the player who fired the bullet
     */
    public void onBulletHit(Player shooter) {
    }

}
