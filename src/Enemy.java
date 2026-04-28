import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

/**
 * Base class for all enemies
 * Tracks position, health and active state
 */
public abstract class Enemy {
    private Point position;
    private Image image;

    private double health;
    private boolean dead = false;
    private boolean active = false;

    private int coinReward = 0;
    private boolean rewardGiven = false;

    /**
     * Creates a new enemy
     * @param pos starting position of enemy
     * @param health initial health of enemy
     * @param img sprite image
     * @param coinReward coins awarded when this enemy dies
     */
    public Enemy(Point pos, double health, Image img, int coinReward) {
        this.position = pos;
        this.health = health;
        this.image = img;
        this.coinReward = coinReward;
    }

    /**
     * per frame update for enemy
     * @param player the player reference
     * @param room room the enemy is in
     */
    public abstract void update(Player player, BattleRoom room);

    /**
     * Draws the enemy, if alive
     */
    public void draw() {
        if (!dead) {
            image.draw(position.x, position.y);
        }

    }

    /**
     * @return the bounding box at current position
     */
    public Rectangle getBounds() {
        return image.getBoundingBoxAt(position);
    }

    /**
     * Applies damage to the enemy and marks it dead at health = 0
     * @param damage amount to subtract from health
     */
    public void takeDamage(double damage) {
        if (dead) return;
        health -= damage;
        if (health <= 0) {
            dead = true;
        }
    }

    /**
     * @return true if dead
     */
    public boolean isDead() {
        return dead;
    }

    /**
     * @return current enemy position
     */
    public Point getPosition() {
        return position;
    }

    /**
     * @param p sets position to p
     */
    public void setPosition(Point p) {
        this.position = p;
    }

    /**
     * Marks this enemy as active/inactive
     * @param on if enemy is active
     */
    public void setActive(boolean on) {
        this.active = on;
    }

    /**
     * @return true if enemy is active
     */
    public boolean isActive() {
        return this.active;
    }

    /**
     * @return sprite image used by bound queries
     */
    public Image getImage() { return image; }

    /**
     * @return coins awarded for killing enemy
     */
    public int getCoinReward() { return coinReward; }

    /**
     * @return whether the kill reward was already paid
     */
    public boolean isRewardGiven() { return rewardGiven; }

    /**
     * Marks the kill reward as paid
     */
    public void setRewardGiven() { this.rewardGiven = true; }

    /**
     * If enemy is killed by robot character
     * Extra coins are rewarded for the kill
     * @return true if robot bonus applies
     */
    public boolean robotBonus() { return false; }


}
