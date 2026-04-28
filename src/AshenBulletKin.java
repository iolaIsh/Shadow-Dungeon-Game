import bagel.Image;
import bagel.util.Point;

/**
 * Stronger stationary enemy that fires fireballs towards player
 * Awards coins on death based on properties
 */
public class AshenBulletKin extends Enemy {
    private final int shootEvery;
    private int cd = 0;

    /**
     * Creates an Ashen Bullet Kin at a position
     * loading health, coin, reward and fire rate from app.properties
     * @param p the spawn position (image's center)
     */
    public AshenBulletKin(Point p) {
        super(
                p,
                parseIntProp("ashenBulletKinHealth", 15),
                new Image("res/ashen_bullet_kin.png"),
                parseIntProp("ashenBulletKinCoin", 20)
        );
        shootEvery = parseIntProp("ashenBulletKinShootFrequency", 240);
    }

    /**
     * Updates each frame, if alive and cooldown is expired,
     * it fires a fireball towards player's current position
     * @param player the player target
     * @param room room to spawn projectiles in, in which enemy is in too
     */
    @Override
    public void update(Player player, BattleRoom room) {
        if (isDead()) return;
        if (cd-- <=0) {
            cd = shootEvery;
            room.spawnFireball(getPosition(), player.getPosition());
        }
    }

    public boolean robotBonus() { return true; }

    private static int parseIntProp(String k, int definition) {
        try {
            return Integer.parseInt(ShadowDungeon.getGameProps().getProperty(k));
        } catch (Exception e) {
            return definition;
        }
    }

}
