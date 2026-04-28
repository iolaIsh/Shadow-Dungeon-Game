import bagel.Image;
import bagel.Input;
import bagel.Keys;
import bagel.util.Point;

/**
 * Interchangable treasure box
 * When the player overlaps and presses K, a key is consumed
 * and coins are awarded and box disappears
 */
public class TreasureBox {
    private final Point position;
    private final Image image;
    private final double coinValue;
    private boolean active = true;

    /**
     * Creates a treasure box with coin reward
     * @param position center point
     * @param coinValue coin payout when opened
     */
    public TreasureBox(Point position, double coinValue) {
        this.position = position;
        this.coinValue = coinValue;
        this.image = new Image("res/treasure_box.png");
    }

    /**
     * If player overlaps and presses K, attempst to spend a key
     * awards coins
     * on success, the box is no longer active
     * @param input current input
     * @param player player interacting and intersecting
     */
    public void update(Input input, Player player) {

        if (hasCollidedWith(player) && input.wasPressed(Keys.K)) {
            if (player.spendKey()) {
                player.earnCoins(coinValue);
                active = false;
            }
        }
    }

    /**
     * draw treasure box
     */
    public void draw() {
        image.draw(position.x, position.y);
    }

    /** test collison between box and player */
    public boolean hasCollidedWith(Player player) {
        return image.getBoundingBoxAt(position).intersects(player.getCurrImage().getBoundingBoxAt(player.getPosition()));
    }

    /**
     * @return if box is still active (visible)
     */
    public boolean isActive() {
        return active;
    }
}
