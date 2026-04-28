import bagel.Image;
import bagel.util.Point;

/**
 * Destructible obstacle that blocks the player
 * Destroyed when hit by a bullet
 */
public class Table extends Obstacle {
    /**
     * Creates a table at a given position
     * @param position center point
     */
    public Table(Point position) {
        super(position, new Image("res/table.png"));
    }

    /**
     * Per frame update for collision blocking
     * @param player
     */
    public void update(Player player) {
        blockPlayerColliding(player);
    }

    /**
     * Destroys the table when shot by a player's bullet
     * @param shooter the player who fired the bullet
     */
    @Override
    public void onBulletHit(Player shooter) {
        if (isActive()) destroy();
    }

}