import bagel.*;
import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

/**
 * The control for player entity: movement, shooting bullets, stats, keys, skills
 */
public class Player {
    private Point prevPosition;
    private Point position;
    private Image currImage;

    // stats player
    private double health;
    private double speed;
    private double coins = 0;
    private boolean faceLeft = false;
    private int keys = 0;
    private int shootCoolDown = 0;
    private final int bulletFreqFrame = parseIntProp("bulletFreq", 30);

    // character and player stats
    private static final Image RIGHT_IMAGE = new Image("res/player_right.png");
    private static final Image LEFT_IMAGE = new Image("res/player_left.png");
    private static final Image ROBOT_LEFT  = new Image("res/robot_left.png");
    private static final Image ROBOT_RIGHT = new Image("res/robot_right.png");
    private static final Image MARINE_LEFT  = new Image("res/marine_left.png");
    private static final Image MARINE_RIGHT = new Image("res/marine_right.png");

    private CharacterSkill skill = null;
    private enum Skin { DEFAULT, ROBOT, MARINE }
    private Skin skin = Skin.DEFAULT;

    /**
     * Creates a player the given position using app.properties
     * @param position initial of screen coordinates
     */
    public Player(Point position) {
        this.position = position;
        this.currImage = RIGHT_IMAGE;
        this.speed = Double.parseDouble(ShadowDungeon.getGameProps().getProperty("movingSpeed"));
        this.health = Double.parseDouble(ShadowDungeon.getGameProps().getProperty("initialHealth"));
        this.keys = 0;
    }

    /**
     * Updates movement and facing in non-combat rooms
     * @param input current keyboard/mouse input
     */
    public void update(Input input) {
        // check movement keys and mouse cursor
        double currX = position.x;
        double currY = position.y;

        if (input.isDown(Keys.A)) {
            currX -= speed;
        }
        if (input.isDown(Keys.D)) {
            currX += speed;
        }
        if (input.isDown(Keys.W)) {
            currY -= speed;
        }
        if (input.isDown(Keys.S)) {
            currY += speed;
        }

        faceLeft = input.getMouseX() < currX;

        // update the player position accordingly and ensure it can't move past the game window
        Rectangle rect = currImage.getBoundingBoxAt(new Point(currX, currY));
        Point topLeft = rect.topLeft();
        Point bottomRight = rect.bottomRight();
        if (topLeft.x >= 0 && bottomRight.x <= Window.getWidth() && topLeft.y >= 0 && bottomRight.y <= Window.getHeight()) {
            move(currX, currY);
        }
    }

    /**
     * Updates movement and facing in battle rooms
     * @param input current keyboard/mouse input
     * @param room the active battle room for spawning bullets from player
     */
    public void update(Input input, BattleRoom room) {
        // check movement keys and mouse cursor
        double currX = position.x;
        double currY = position.y;

        if (input.isDown(Keys.A)) {
            currX -= speed;
        }
        if (input.isDown(Keys.D)) {
            currX += speed;
        }
        if (input.isDown(Keys.W)) {
            currY -= speed;
        }
        if (input.isDown(Keys.S)) {
            currY += speed;
        }

        faceLeft = input.getMouseX() < currX;

        // update the player position accordingly and ensure it can't move past the game window
        Rectangle rect = currImage.getBoundingBoxAt(new Point(currX, currY));
        Point topLeft = rect.topLeft();
        Point bottomRight = rect.bottomRight();
        if (topLeft.x >= 0 && bottomRight.x <= Window.getWidth() && topLeft.y >= 0 && bottomRight.y <= Window.getHeight()) {
            move(currX, currY);
        }

        // shooting
        if (shootCoolDown > 0) shootCoolDown--;

        // character selection matters for shooting?

        // shooting
        if (canShoot() && input.wasPressed(MouseButtons.LEFT) && shootCoolDown == 0) {
            Point from = getPosition();
            Point click = input.getMousePosition();
            click = new Point(click.x, click.y);
            room.spawnBullet(from, click, getWeaponDamage());
            shootCoolDown = bulletFreqFrame;
        }

    }

    /**
     * Moves the player to a new position
     * @param x new coordinate
     * @param y new coordinate
     */
    public void move(double x, double y) {
        prevPosition = position;
        position = new Point(x, y);
    }

    /**
     * Draws the player within the correct skin and UI stats
     */
    public void draw() {
        switch (skin) {
            case ROBOT -> currImage = faceLeft ? ROBOT_LEFT : ROBOT_RIGHT;
            case MARINE -> currImage = faceLeft ? MARINE_LEFT : MARINE_RIGHT;
            default -> currImage = faceLeft ? LEFT_IMAGE : RIGHT_IMAGE;
        }
        currImage.draw(position.x, position.y);
        UserInterface.drawStats(health, coins, getKeys(), ShadowDungeon.getWeaponLevel());
    }

    /**
     * Adds coins to player
     * @param coins amount to add
     */
    public void earnCoins(double coins) {
        this.coins += coins;
    }

    /**
     * Applies damage from fireballs/river and can make game over if less than 0
     * @param damage amount of health to subtract
     */
    public void receiveDamage(double damage) {
        health -= damage;
        if (health <= 0) {
            ShadowDungeon.changeToGameOverRoom();
        }
    }

    /**
     * @return current position
     */
    public Point getPosition() {
        return position;
    }

    /**
     * @return current sprite image used
     */
    public Image getCurrImage() {
        return currImage;
    }

    /**
     * @return previous position (last frame)
     */
    public Point getPrevPosition() {
        return prevPosition;
    }

    /**
     * Increases the number of keys help
     * @param k number of keys to add
     */
    public void addKey(int k) {
        keys += k;
    }

    /**
     * Spends one key if there
     * @return true if a key was used, otherwise false
     */
    public boolean spendKey() {
        if (keys > 0) {
            keys--;
            return true;
        }
        return false;
    }

    /**
     * @return number of keys currently holding
     */
    public int getKeys() {
        return keys;
    }

    private static int parseIntProp(String k, int definition) {
        try {
            return Integer.parseInt(ShadowDungeon.getGameProps().getProperty(k));
        } catch (Exception e) {
            return definition;
        }
    }

    private boolean canShoot() {
        return ShadowDungeon.playerCanShoot();
    }

    /**
     * Current weapon damage from ShadowDungeon
     * @return bullet damage in hit points
     */
    public int getWeaponDamage() {
        try {
            return ShadowDungeon.getWeaponDamage();
        }
        catch (Throwable t) {
            return 30; // need to add proper method here
        }
    }

    /**
     * Attempts to spend coins
     * @param amount the coins to pay
     * @return true if payment went through, else false
     */
    public boolean spendCoins(int amount) {
        if (coins >= amount) {
            coins -= amount;
            //if (coins < 0) coins = 0;
            return true;
        }
        return false;
    }

    /**
     * heals the player by a given amount
     * @param amount health to restore
     */
    public void heal(double amount) {
        health += amount;
    }

    /**
     * Sets the active character skill (Robot or Marine)
     * @param s the skill to apply
     */
    public void setSkill(CharacterSkill s) {
        this.skill = s;
    }

    /**
     * Enemy was killed notification
     * @param e the enemy was killed
     */
    public void onEnemyKilled(Enemy e) {
        if (skill != null) {
            skill.onEnemyKilled(e, this);
        }
    }

    /**
     * If character takes river damage
     * @return true if river harms player, else false
     */
    public boolean takesRiverDamage() {
        return skill == null || skill.takesRiverDamage();
    }

    /**
     * Selects the robot character
     */
    public void selectRobot() {
        skin = Skin.ROBOT;
        setSkill(new RobotSkill());
    }

    /**
     * Selects the marine character
     */
    public void selectMarine() {
        skin = Skin.MARINE;
        setSkill(new MarineSkill());
    }

    /**
     * If some character has been chosen or default skin
     * @return true if chosen, or false if default skin
     */
    public boolean hasChosenChar() {
        return skin != Skin.DEFAULT;
    }



}
