import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

/**
 * Indestructible Obstacle that blocks the player from moving through it
 */
public class Wall extends Obstacle{

    /**
     * Creates a wall at a fixed position
     * @param position center position of wall sprite
     */
    public Wall(Point position) {
        super(position, new Image("res/wall.png"));
    }

    /**
     * per frame update to block the player on collision
     * @param player player to block
     */
    public void update(Player player) {
        blockPlayerColliding(player);

    }



}