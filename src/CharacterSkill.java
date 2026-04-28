/**
 * Interface defines a character specific skill applied to player
 * Implementations include robotSkill, marineSkill
 */
public interface CharacterSkill {
    /**
     * Called when the player kills an enemy so skill can award bonus coins
     * @param e the enemy that was killed
     * @param p the player who did the kill
     */
    void onEnemyKilled(Enemy e, Player p);

    /**
     * If player takes damage while standing on river tiles or not
     * @return true if river tiles, cause damage to player, else false
     */
    boolean takesRiverDamage();
}
