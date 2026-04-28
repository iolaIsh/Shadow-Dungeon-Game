import bagel.*;

import java.util.Map;
import java.util.Properties;

/**
 * Final room of the game
 * Displays end message and has a door and restart area
 * if reached due to player death, door is locked
 */
public class EndRoom {
    /** Player currently in this room */
    private Player player;

    /** Exit door */
    private Door door;

    /** Restart area to reset game */
    private RestartArea restartArea;

    /** True if this room was entered by death */
    private boolean isGameOver = false;

    /** Used to stop the current update call when switching rooms */
    private boolean stopCurrentUpdateCall = false;

    /**
     * Creates child entities: door, restart area
     * @param gameProperties global game properties
     */
    public void initEntities(Properties gameProperties) {
        // find the configuration of game objects for this room
        for (Map.Entry<Object, Object> entry: gameProperties.entrySet()) {
            String roomSuffix = String.format(".%s", ShadowDungeon.END_ROOM_NAME);
            if (entry.getKey().toString().contains(roomSuffix)) {
                String objectType = entry.getKey().toString().substring(0, entry.getKey().toString().length() - roomSuffix.length());
                String propertyValue = entry.getValue().toString();

                switch (objectType) {
                    case "door":
                        String[] coordinates = propertyValue.split(",");
                        door = new Door(IOUtils.parseCoords(propertyValue), coordinates[2]);
                        break;
                    case "restartarea":
                        restartArea = new RestartArea(IOUtils.parseCoords(propertyValue));
                        break;
                    default:
                }
            }
        }
    }

    /**
     * Per frame update and draw for the end room
     * shows win/lose message
     * updates the door and restart area, draws the player
     * @param input current input state
     */
    public void update(Input input) {
        UserInterface.drawEndMessage(!isGameOver);

        // door should be locked if player got to this room by dying
        if (isGameOver) {
            findDoor().lock();
        }

        // update and draw all game objects in this room
        door.update(player);
        door.draw();
        if (stopUpdatingEarlyIfNeeded()) {
            return;
        }

        restartArea.update(input, player);
        restartArea.draw();

        if (player != null) {
            player.update(input);
            player.draw();
        }
    }

    /**
     * Gives player instance for this room
     * @param player
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * Requests current update call to stop early
     */
    public void stopCurrentUpdateCall() {
        stopCurrentUpdateCall = true;
    }

    /**
     * @return this room's door
     */
    public Door findDoor() {
        return door;
    }

    /**
     * Returns the next door reference
     * @return this room's door
     */
    public Door findDoorByDestination() {
        return door;
    }

    /**
     * Marks that the room was reached by Game over
     */
    public void isGameOver() {
        isGameOver = true;
    }

    /**
     * Draw only for when game is paused
     */
    public void drawOnly() {
        // if PAUSED
        if (ShadowDungeon.isPaused()) {
            if (door != null) door.draw();
            if (player != null) player.draw();

        }
    }

    /* Internal helper functions */
    private boolean stopUpdatingEarlyIfNeeded() {
        if (stopCurrentUpdateCall) {
            player = null;
            stopCurrentUpdateCall = false;
            return true;
        }
        return false;
    }

}