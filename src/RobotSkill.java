/**
 * Character skill for the Robot.
 * The Robot gets extra coins for killing an enemy.
 */
public class RobotSkill implements CharacterSkill {
    private final int extra = readInt("robotExtraCoin", 5);

    /**
     *Robot gains extra coins on kills by skill alone
     * @param e the enemy that was killed
     * @param p the player who did the kill
     */
    @Override
    public void onEnemyKilled(Enemy e, Player p) {
        if (e.robotBonus()) p.earnCoins(extra);
    }

    /**
     * Robot is not immune to river damage
     * @return true (river damage)
     */
    @Override
    public boolean takesRiverDamage() {
        return true;
    }

    private static int readInt(String k, int d) {
        try { return Integer.parseInt(ShadowDungeon.getGameProps().getProperty(k)); }
        catch (Exception x) { return d; }

    }


}
