import bagel.Image;
import bagel.Input;
import bagel.Keys;
import bagel.util.Point;

/**
 * Area in Prep or End Room where the player can trigger a game reset
 */
public class RestartArea {
    private final Point position;
    private final Image image;

    /**
     * Creates the restart area at a given position
     * @param position center of area
     */
    public RestartArea(Point position) {
        this.position = position;
        this.image = new Image("res/restart_area.png");
    }

    /**
     * If player overlaps, and presses ENTER, reset game state
     * @param input checking for input ENTER
     * @param player player to test for intersection
     */
    public void update(Input input, Player player) {
        if (hasCollidedWith(player) && input.wasPressed(Keys.ENTER)) {
            ShadowDungeon.resetGameState(ShadowDungeon.getGameProps());
        }
    }

    /**
     * Draws the restart area sprite
     * */
    public void draw() {
        image.draw(position.x, position.y);
    }

    /**
     * Tests overlap between area and player
     * @param player player
     * @return true if overlapping
     */
    public boolean hasCollidedWith(Player player) {
        return image.getBoundingBoxAt(position).intersects(player.getCurrImage().getBoundingBoxAt(player.getPosition()));
    }
}