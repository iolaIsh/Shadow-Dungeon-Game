import bagel.*;
import bagel.util.Point;
import java.util.Properties;

import bagel.Input;
import bagel.Keys;

import java.util.Map;
import java.util.Properties;

public class PrepRoom {
    private Player player;
    private Door door;
    private RestartArea restartArea;
    private boolean stopCurrentUpdateCall = false; // this determines whether to prematurely stop the update execution

    private Image robotSprite, marineSprite;
    private Point robotPos, marinePos;

    public void initEntities(Properties gameProperties) {
        String robot = gameProperties.getProperty("Robot");
        if (robot != null && !robot.isEmpty()) {
            robotPos = IOUtils.parseCoords(robot);
        }
        String marine = gameProperties.getProperty("Marine");
        if (marine != null && !marine.isEmpty()) {
            marinePos = IOUtils.parseCoords(marine);
        }
        // find the configuration of game objects for this room
        for (Map.Entry<Object, Object> entry: gameProperties.entrySet()) {
            String roomSuffix = String.format(".%s", ShadowDungeon.PREP_ROOM_NAME);
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
                    case "Robot":
                        robotPos = IOUtils.parseCoords(propertyValue);
                        break;
                    case "Marine":
                        marinePos = IOUtils.parseCoords(propertyValue);
                        break;
                    default:
                }
            }
        }

        robotSprite = new Image("res/robot_sprite.png");
        marineSprite = new Image("res/marine_sprite.png");

    }

    public void update(Input input) {
        UserInterface.drawStartMessages();

        // character switching
        if (marineSprite != null && marinePos != null) {
            marineSprite.draw(marinePos.x, marinePos.y);
        }
        if (robotSprite != null && robotPos != null) {
            robotSprite.draw(robotPos.x, robotPos.y);
        }

        // character selection
        if (player != null) {
            if (input.wasPressed(Keys.R)) {
                player.selectRobot();
                if (!findDoor().isUnlocked()) findDoor().unlock(false);
            }
            if (input.wasPressed(Keys.M)) {
                player.selectMarine();
                if (!findDoor().isUnlocked()) findDoor().unlock(false);
            }
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

        // door unlock mechanism
        if ((input.wasPressed(Keys.R) || (input.wasPressed(Keys.M))) && !findDoor().isUnlocked()) {
            findDoor().unlock(false);
        }
    }

    private boolean stopUpdatingEarlyIfNeeded() {
        if (stopCurrentUpdateCall) {
            player = null;
            stopCurrentUpdateCall = false;
            return true;
        }
        return false;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void stopCurrentUpdateCall() {
        stopCurrentUpdateCall = true;
    }

    public Door findDoor() {
        return door;
    }

    public Door findDoorByDestination() {
        return door;
    }

    public void drawOnly() {
        // if PAUSED
        if (ShadowDungeon.isPaused()) {
            if (door != null) door.draw();
            if (marineSprite != null && marinePos != null) marineSprite.draw(marinePos.x, marinePos.y);
            if (robotSprite != null && robotPos != null) robotSprite.draw(robotPos.x, robotPos.y);
            if (player != null) player.draw();


        }
    }

}
