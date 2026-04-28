/**
 * Character skill for the Marine.
 * The Marine takes no damage from the river
 */
public class MarineSkill implements CharacterSkill {
    /**
     * Marine does not gain extra coins on kills by skill alone
     * @param e the enemy that was killed
     * @param p the player who did the kill
     */
    @Override
    public void onEnemyKilled(Enemy e, Player p) {
    }

    /**
     * Marine is immune to river damage
     * @return false (no river damage)
     */
    @Override
    public boolean takesRiverDamage() {
        return false;
    }
}
