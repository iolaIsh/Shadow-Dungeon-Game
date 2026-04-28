import bagel.Image;
import bagel.util.Point;

/**
 * Stationary enemy that fire fireballs towards player
 * Award coins on death as given in properties
 */
public class BulletKin extends Enemy{
    private final int shootEvery; // number of frames
    private int cd = 0;

    /**
     * Creates Bullet kin at a position, loading health, coin reward, and fire rate
     * @param p the spawn position (image center)
     */
    public BulletKin(Point p) {
        super(
                p,
                parseIntProp("bulletKinHealth", 100),
                new Image("res/bullet_kin.png"),
                parseIntProp("bulletKinCoin", 10)
        );
        shootEvery = parseIntProp("bulletkinShootFreuqency", 360);
    }

    /**
     * Updates each frame, if alive and cooldown is expired,
     * it fires a fireball towards player's current position
     * @param player the player target
     * @param room room to spawn projectiles in, in which enemy is in too
     */
    @Override public void update(Player player, BattleRoom room) {
        if (isDead()) return;
        if (cd-- <= 0) {
            cd = shootEvery;
            room.spawnFireball(getPosition(), player.getPosition());
        }
    }

    /**
     * If this enemy qualifies for Robot character's extra coin bonus
     * @return true if it does
     */
    @Override
    public boolean robotBonus() {
        return true;
    }

    private static int parseIntProp(String k, int definition) {
        try {
            return Integer.parseInt(ShadowDungeon.getGameProps().getProperty(k));
        } catch (Exception e) {
            return definition;
        }
    }

}
