import bagel.*;
import bagel.util.Point;

import java.util.Properties;

/**
 * Entry point for game and top level game control for Shadow Dungeon
 * This class owns the rooms, global properties, pause/store logic and weapon state
 */
public class ShadowDungeon extends AbstractGame {
    /** DESIGN FROM SOLUTIONS BY TEACHERS **/
    public static Properties gameProps;
    public static Properties messageProps;
    public static double screenWidth;
    public static double screenHeight;

    private static String currRoomName;
    private static PrepRoom prepRoom;
    private static BattleRoom battleRoomA;
    private static BattleRoom battleRoomB;
    private static EndRoom endRoom;
    private static Player player;
    private final Image background;

    public static final String PREP_ROOM_NAME = "prep";
    public static final String BATTLE_ROOM_A_NAME = "A";
    public static final String BATTLE_ROOM_B_NAME = "B";
    public static final String END_ROOM_NAME = "end";

    /** own implementation for 2b **/
    private static final int STANDARD = 0, ADVANCED = 1, ELITE = 2;
    private static int weaponLevel = STANDARD;
    private static int[] WEAPON_DAMAGE = new int[3];

    private final Store store;
    private static boolean paused = false;
    public static boolean isPaused() { return paused; }
    private static void setPaused(boolean p) { paused = p; }

    /**
     * Creates the game window and initialises gloval state
     * @param gameProps app properties (coordinates, health, damage, etc.)
     * @param messageProps text strings shown in game UI
     */
    public ShadowDungeon(Properties gameProps, Properties messageProps) {
        super(Integer.parseInt(gameProps.getProperty("window.width")),
                Integer.parseInt(gameProps.getProperty("window.height")),
                "Shadow Dungeon");

        ShadowDungeon.gameProps = gameProps;
        ShadowDungeon.messageProps = messageProps;
        screenWidth = Integer.parseInt(gameProps.getProperty("window.width"));
        screenHeight = Integer.parseInt(gameProps.getProperty("window.height"));
        this.background = new Image("res/background.png");

        store = new Store();

        resetGameState(gameProps);
    }

    /**
     * Resets all game state to a fresh start (rooms, player, weapons)
     * @param gameProps properties to re-initialise
     */
    public static void resetGameState(Properties gameProps) {
        initWeaponFromProps();
        weaponLevel = STANDARD;
        prepRoom = new PrepRoom();
        battleRoomA = new BattleRoom(BATTLE_ROOM_A_NAME, BATTLE_ROOM_B_NAME);
        battleRoomB = new BattleRoom(BATTLE_ROOM_B_NAME, END_ROOM_NAME);
        endRoom = new EndRoom();

        prepRoom.initEntities(gameProps);
        battleRoomA.initEntities(gameProps);
        battleRoomB.initEntities(gameProps);
        endRoom.initEntities(gameProps);

        currRoomName = PREP_ROOM_NAME;

        ShadowDungeon.player = new Player(IOUtils.parseCoords(gameProps.getProperty("player.start")));
        prepRoom.setPlayer(player);
    }

    /**
     * Main per frame update, handles the pause/store and forwards to current room
     * Render the relevant screen based on the keyboard input given by the user and the status of the gameplay.
     * @param input The current mouse/keyboard input.
     */
    @Override
    protected void update(Input input) {
        if (input.wasPressed(Keys.ESCAPE)) {
            Window.close();
        }

        background.draw((double) Window.getWidth() / 2, (double) Window.getHeight() / 2);

        if (input.wasPressed(Keys.SPACE)) {
            store.toggle();
            setPaused(store.isVisible());
        }

        if (store.isVisible()) {
            drawWorldOnly();
            store.update(input, player); // handles L, E, P
            store.drawOverlay(player);
            return;
        }

        switch (currRoomName) {
            case PREP_ROOM_NAME:
                prepRoom.update(input);
                return;
            case BATTLE_ROOM_A_NAME:
                battleRoomA.update(input);
                return;
            case BATTLE_ROOM_B_NAME:
                battleRoomB.update(input);
                return;
            default:
                endRoom.update(input);
        }

    }

    /**
     * Moves the player through a door into the named rooms and sets it up
     * @param roomName destination of room with a room key
     */
    public static void changeRoom(String roomName) {
        Door nextDoor;
        switch (roomName) {
            case PREP_ROOM_NAME:
                nextDoor = prepRoom.findDoorByDestination();

                // assume that prep room can only be entered through Battle Room A
                if (currRoomName.equals(BATTLE_ROOM_A_NAME)) {
                    battleRoomA.stopCurrentUpdateCall();
                }
                currRoomName = PREP_ROOM_NAME;

                // move the player to the center of the next room's door
                nextDoor.unlock(true);
                player.move(nextDoor.getPosition().x, nextDoor.getPosition().y);
                prepRoom.setPlayer(player);

                return;
            case BATTLE_ROOM_A_NAME:
                nextDoor = battleRoomA.findDoorByDestination(currRoomName);

                // assume that Battle Room A can only be entered through Prep Room or Battle Room B
                if (currRoomName.equals(BATTLE_ROOM_B_NAME)) {
                    battleRoomB.stopCurrentUpdateCall();
                } else if (currRoomName.equals(PREP_ROOM_NAME)) {
                    prepRoom.stopCurrentUpdateCall();
                }
                currRoomName = BATTLE_ROOM_A_NAME;

                // prepare the door to be able to activate the Battle Room
                if (!battleRoomA.isComplete()) {
                    nextDoor.setShouldLockAgain();
                }

                // move the player to the center of the next room's door
                nextDoor.unlock(true);
                player.move(nextDoor.getPosition().x, nextDoor.getPosition().y);
                battleRoomA.setPlayer(player);

                return;
            case BATTLE_ROOM_B_NAME:
                nextDoor = battleRoomB.findDoorByDestination(currRoomName);

                // assume that Battle Room B can only be entered through Battle Room A or End Room
                if (currRoomName.equals(BATTLE_ROOM_A_NAME)) {
                    battleRoomA.stopCurrentUpdateCall();
                } else if (currRoomName.equals(END_ROOM_NAME)) {
                    endRoom.stopCurrentUpdateCall();
                }
                currRoomName = BATTLE_ROOM_B_NAME;

                // prepare the door to be able to activate the Battle Room
                if (!battleRoomB.isComplete()) {
                    nextDoor.setShouldLockAgain();
                }

                // move the player to the center of the next room's door
                nextDoor.unlock(true);
                player.move(nextDoor.getPosition().x, nextDoor.getPosition().y);
                battleRoomB.setPlayer(player);

                return;
            default:
                nextDoor = endRoom.findDoorByDestination();

                // assume that end room can only be entered through Battle Room B
                if (currRoomName.equals(BATTLE_ROOM_B_NAME)) {
                    battleRoomB.stopCurrentUpdateCall();
                }
                currRoomName = END_ROOM_NAME;

                // move the player to the center of the next room's door
                nextDoor.unlock(true);
                player.move(nextDoor.getPosition().x, nextDoor.getPosition().y);
                endRoom.setPlayer(player);
        }
    }

    /**
     * Switches to the end room in a game-over state and repositions player
     */
    public static void changeToGameOverRoom() {
        switch (currRoomName) {
            case PREP_ROOM_NAME:
                prepRoom.stopCurrentUpdateCall();
            case BATTLE_ROOM_A_NAME:
                battleRoomA.stopCurrentUpdateCall();
            case BATTLE_ROOM_B_NAME:
                battleRoomB.stopCurrentUpdateCall();
            default:
        }

        endRoom.isGameOver();
        currRoomName = END_ROOM_NAME;

        Point startPos = IOUtils.parseCoords(ShadowDungeon.getGameProps().getProperty("player.start"));
        player.move(startPos.x, startPos.y);
        endRoom.setPlayer(player);
    }

    /**
     * global app properties accessor
     * @return the loaded app properties
     */
    public static Properties getGameProps() {
        return gameProps;
    }

    /**
     * global message properties accessor
     * @return the loaded message properties
     */
    public static Properties getMessageProps() {
        return messageProps;
    }

    /**
     * Program entry point is here
     * @param args
     */
    public static void main(String[] args) {
        Properties gameProps = IOUtils.readPropertiesFile("res/app.properties");
        Properties messageProps = IOUtils.readPropertiesFile("res/message.properties");
        ShadowDungeon game = new ShadowDungeon(gameProps, messageProps);
        game.run();
    }

    private static void initWeaponFromProps() {
        WEAPON_DAMAGE[STANDARD] = parseIntProp("weaponStandardDamage", 30);
        WEAPON_DAMAGE[ADVANCED] = parseIntProp("weaponAdvanceDamage", 50);
        WEAPON_DAMAGE[ELITE] = parseIntProp("weaponEliteDamage", 100);

    }

    /**
     * Damage given out by the current weapon level
     * @return weapon damage in points
     */
    public static int getWeaponDamage() {
        int wl = Math.max(0, Math.min(weaponLevel, WEAPON_DAMAGE.length - 1));
        return WEAPON_DAMAGE[wl];
    }

    /**
     * Current weapon tier (0, 1, or 2)
     * @return current weapon level
     */
    public static int getWeaponLevel() {
        return weaponLevel;
    }

    private static int parseIntProp(String k, int definition) {
        try {
            return Integer.parseInt(ShadowDungeon.getGameProps().getProperty(k));
        } catch (Exception e) {
            return definition;
        }
    }

    /**
     * If player can shoot after selecting character
     * @return true if character selected, else false
     */
    public static boolean playerCanShoot() {
        return !isPaused() && player != null && player.hasChosenChar();

    }

    /**
     * Increases weapon level
     */
    public static void upgradeWeaponLevelIfPossible() {
        if (weaponLevel < 2) {
            weaponLevel++;
        }
    }

    private void drawWorldOnly() {
        switch (currRoomName) {
            case PREP_ROOM_NAME: prepRoom.drawOnly(); break;
            case BATTLE_ROOM_A_NAME: battleRoomA.drawOnly(); break;
            case BATTLE_ROOM_B_NAME: battleRoomB.drawOnly(); break;
            default: endRoom.drawOnly(); break;
        }
    }

}
