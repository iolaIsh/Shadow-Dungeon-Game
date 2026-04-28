import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

/**
 * Door between rooms that can be locked and unlocked
 */
public class Door {
    private final Point position;
    private Image image;
    public final String toRoomName;
    public BattleRoom battleRoom; // only set if this door is inside a Battle Room
    private boolean unlocked = false;
    private boolean justEntered = false; // when the player only just entered this door's room
    private boolean shouldLockAgain = false;

    private static final Image LOCKED = new Image("res/locked_door.png");
    private static final Image UNLOCKED = new Image("res/unlocked_door.png");

    /**
     * Creates a door at a position pointing to a destination room
     * @param position center position of door
     * @param toRoomName destination room name
     */
    public Door(Point position, String toRoomName) {
        this.position = position;
        this.image = LOCKED;
        this.toRoomName = toRoomName;
    }

    /**
     * Door for battle rooms
     * @param position center position of door
     * @param toRoomName estination room name
     * @param battleRoom battleRoom name currently in
     */
    public Door(Point position, String toRoomName, BattleRoom battleRoom) {
        this.position = position;
        this.image = LOCKED;
        this.toRoomName = toRoomName;
        this.battleRoom = battleRoom;
    }

    /**
     * Updates lock state based on player interaction
     * @param player whose interaction is assessed
     */
    public void update(Player player) {
        if (hasCollidedWith(player)) {
            onCollideWith(player);
        } else {
            onNoLongerCollide();
        }
    }

    /**
     * Draws the door
     */
    public void draw() {
        image.draw(position.x, position.y);
    }

    /**
     * Unlocks this door
     * @param justEntered oneWay entry, so player does not go in and out of the door
     */
    public void unlock(boolean justEntered) {
        unlocked = true;
        image = UNLOCKED;
        this.justEntered = justEntered;
    }

    /**
     * Tests if given player is colliding with the door sprite
     * @param player
     * @return
     */
    public boolean hasCollidedWith(Player player) {
        return image.getBoundingBoxAt(position).intersects(player.getCurrImage().getBoundingBoxAt(player.getPosition()));
    }

    /**
     * locks the door and switches sprite to locked image
     */
    public void lock() {
        unlocked = false;
        image = LOCKED;
    }

    /**
     * @return true if the door is currently unlocked, else false
     */
    public boolean isUnlocked() {
        return unlocked;
    }

    /**
     * Marks that this door should re-lock after player enters through it
     * For battle doors, active the enemy encounters
     */
    public void setShouldLockAgain() {
        this.shouldLockAgain = true;
    }

    /**
     * @return the center position of the door
     */
    public Point getPosition() {
        return position;
    }

    /**
     * @return the current bounding box of the door and its position
     */
    public Rectangle getBounds() {
        return image.getBoundingBoxAt(position);
    }

    /**
     * Checks if door should behave like an obstacle (when locked)
     * @return if the door is locked, else if it is unlocked
     */
    public boolean behavesAsObstacle() {
        return !unlocked;
    }

    /* Internal helpers */
    private void onCollideWith(Player player) {
        // when the player only just entered this door's room, overlapping with the unlocked door shouldn't trigger room transition
        if (unlocked && !justEntered) {
            ShadowDungeon.changeRoom(toRoomName);
        }
        if (!unlocked) {
            player.move(player.getPrevPosition().x, player.getPrevPosition().y);
        }
    }

    private void onNoLongerCollide() {
        // when the player only just moved away from the unlocked door after walking through it
        if (unlocked && justEntered) {
            justEntered = false;

            // Battle Room activation conditions
            if (shouldLockAgain && battleRoom != null && !battleRoom.isComplete()) {
                unlocked = false;
                image = LOCKED;
                battleRoom.activateEnemies();
            }
        }
    }


}

