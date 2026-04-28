import bagel.Input;
import bagel.util.Point;
import bagel.util.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Battle Rooms with doors that are locked until the player defeats all enemies
 * Manages enemies, fireballs, pickups for key, and obstacles that can be destroyed
 */
public class BattleRoom {
    private Player player;
    private Door primaryDoor;
    private Door secondaryDoor;
    private KeyBulletKin keyBulletKin;

    private final ArrayList<TreasureBox> treasureBoxes;
    private final ArrayList<Obstacle> obstacles = new ArrayList<>();
    private final ArrayList<River> rivers;
    private final List<Enemy> enemies = new ArrayList<>();
    private boolean keyDropped = false;

    private boolean stopCurrentUpdateCall = false; // this determines whether to prematurely stop the update execution
    private boolean isComplete = false;
    private final String nextRoomName;
    private final String roomName;

    private final List<Fireball> fireballs = new ArrayList<>();
    private final List<Bullet> bullets = new ArrayList<>();
    private final List<Key> keys = new ArrayList<>();

    /**
     * Builds a battle room with a name and link to next room
     * @param roomName rooms name, like A
     * @param nextRoomName next rooms name, like B
     */
    public BattleRoom(String roomName, String nextRoomName) {
        //walls = new ArrayList<>();
        rivers = new ArrayList<>();
        treasureBoxes = new ArrayList<>();
        this.roomName = roomName;
        this.nextRoomName = nextRoomName;
    }

    /**
     * Parses and spawns all entities for this room
     * @param gameProperties gets entity coordinates and values from
     */
    public void initEntities(Properties gameProperties) {
        // find the configuration of game objects for this room
        for (Map.Entry<Object, Object> entry: gameProperties.entrySet()) {
            String roomSuffix = String.format(".%s", roomName);

            if (entry.getKey().toString().contains(roomSuffix)) {
                String objectType = entry.getKey().toString()
                        .substring(0, entry.getKey().toString().length() - roomSuffix.length());
                String propertyValue = entry.getValue().toString();

                // ignore if the value is 0
                if (propertyValue.equals("0")) {
                    continue;
                }

                String[] coordinates;
                for (String coords: propertyValue.split(";")) {
                    switch (objectType) {
                        case "primarydoor":
                            coordinates = propertyValue.split(",");
                            primaryDoor = new Door(IOUtils.parseCoords(propertyValue), coordinates[2], this);
                            break;
                        case "secondarydoor":
                            coordinates = propertyValue.split(",");
                            secondaryDoor = new Door(IOUtils.parseCoords(propertyValue), coordinates[2], this);
                            break;
                        case "keyBulletKin":
                            List<Point> route = IOUtils.parseCoordsList(propertyValue);
                            if (route.isEmpty()) {
                                keyBulletKin = null;

                            } else {
                                keyBulletKin = new KeyBulletKin(route);
                                keyBulletKin.setActive(false);
                                //enemies.add(keyBulletKin);
                            }
                            break;
                        case "bulletKin": {
                            for (String s : propertyValue.split(";")) {
                                s = s.trim();
                                if (!s.isEmpty()) enemies.add(new BulletKin(IOUtils.parseCoords(s)));
                                }
                            break;
                            }
                        case "ashenBulletKin": {
                            for (String a : propertyValue.split(";")) {
                                a = a.trim();
                                if (!a.isEmpty()) enemies.add(new AshenBulletKin(IOUtils.parseCoords(a)));
                            }
                            break;
                        }
                        case "wall":
                            String[] itemsW = propertyValue.split(";");
                            for (String s : itemsW) {
                                s = s.trim();
                                if (!s.isEmpty()) {
                                    obstacles.add(new Wall(IOUtils.parseCoords(s)));
                                }
                            }
                            break;
                        case "table": {
                            String[] itemsT = propertyValue.split(";");
                            for (String s : itemsT) {
                                s = s.trim();
                                if (!s.isEmpty()) {
                                    obstacles.add(new Table(IOUtils.parseCoords(s)));
                                }
                            }
                            break;
                        }
                        case "basket": {
                            String[] itemsB = propertyValue.split(";");
                            for (String s : itemsB) {
                                s = s.trim();
                                if (!s.isEmpty()) {
                                    obstacles.add(new Basket(IOUtils.parseCoords(s)));
                                }
                            }
                            break;
                        }
                        case "treasurebox":
                            TreasureBox treasureBox = new TreasureBox(IOUtils.parseCoords(coords),
                                    Double.parseDouble(coords.split(",")[2]));
                            treasureBoxes.add(treasureBox);
                            break;
                        case "river":
                            River river = new River(IOUtils.parseCoords(coords));
                            rivers.add(river);
                            break;
                        default:
                    }
                }
            }
        }
    }

    /**
     * Per frame update for all active enemies in the room
     * @param input current keyboard/mouse input
     */
    public void update(Input input) {
        // draw doors
        primaryDoor.update(player);
        primaryDoor.draw();
        if (stopUpdatingEarlyIfNeeded()) {
            return;
        }

        secondaryDoor.update(player);
        secondaryDoor.draw();
        if (stopUpdatingEarlyIfNeeded()) {
            return;
        }

        // draw obstacles
        for (Obstacle ob : obstacles) {
            if(ob.isActive()) {
                ob.blockPlayerColliding(player);
                ob.draw();
            }
        }

        for (River river: rivers) {
            river.update(player);
            river.draw();
        }

        for (TreasureBox treasureBox: treasureBoxes) {
            if (treasureBox.isActive()) {
                treasureBox.update(input, player);
                treasureBox.draw();
            }
        }
        // draw player
        if (player != null) {
            player.update(input, this); // pass room to player?
            player.draw();
        }

        // enemies
        for (Enemy e : enemies) {
            if (e.isActive()) {
                e.update(player, this);
                e.draw();
            }
        }

        // keybulletkin enemy
        if (keyBulletKin != null && !keyDropped && keyBulletKin.isDead()) {
            keyDropped = true;
            keySpawning(keyBulletKin.getPosition());
        }

        if (keyBulletKin != null && keyBulletKin.isActive()) {
            keyBulletKin.update(player, this);
            keyBulletKin.draw();
        }

        enemyContactDamage();

        // bullet
        for (Bullet b : bullets) {
            b.update(this);
            b.draw();
        }
        bullets.removeIf(Bullet::isDead);

        // fireballs
        for (Fireball f : fireballs) {
            f.update(this, player);
            f.draw();
        }
        fireballs.removeIf(Fireball::isDead);

        // pickups
        for (Key k : keys) {
            k.update(player);
            k.draw();
        }
        keys.removeIf(Key::isCollected);

        // draw enemies if door unlocked
        if (noMoreEnemies() && !isComplete()) {
            setComplete(true);
            unlockAllDoors();
        }

        if (!isComplete && noMoreEnemies()) {
            isComplete = true;
            unlockAllDoors();
        }

    }

    /** Helper functions **/
    private boolean stopUpdatingEarlyIfNeeded() {
        if (stopCurrentUpdateCall) {
            player = null;
            stopCurrentUpdateCall = false;
            return true;
        }
        return false;
    }

    /**
     * Sets the controls for the player in this room
     * @param player instance
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * Finds the door that leads to a given room
     * @param roomName destination
     * @return the matching door
     */
    public Door findDoorByDestination(String roomName) {
        if (primaryDoor.toRoomName.equals(roomName)) {
            return primaryDoor;
        } else {
            return secondaryDoor;
        }
    }

    private void unlockAllDoors() {
        primaryDoor.unlock(false);
        secondaryDoor.unlock(false);
    }

    /**
     * Checks if room is complete (doors unlocked)
     * @return true if the room is complete, else false
     */
    public boolean isComplete() {
        return isComplete;
    }

    /**
     * sets the completion state to unlock doors in battleRoom
     * @param complete the new state
     */
    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    /**
     * If room's all enemies are dead
     * @return true if no enemies remain, else false
     */
    public boolean noMoreEnemies() {
        boolean kbkDead = (keyBulletKin == null) || keyBulletKin.isDead();
        boolean othersDead = true;
        for (Enemy e : enemies) {
            if (!e.isDead()) {
                othersDead = false;
                break;
            }
        }
        return kbkDead && othersDead;
    }

    /**
     * Spawns a fireball traveling from one point to another
     * @param from starting position (of the enemy)
     * @param to target position (of the player)
     */
    public void spawnFireball(Point from, Point to) {
        fireballs.add(new Fireball(from, to));
    }

    /**
     * Spawns a bullet traveling from one point to another
     * @param from starting position (of player)
     * @param to target position (of mouse click)
     * @param damage applied on hit
     */
    public void spawnBullet(Point from, Point to, int damage) {
        bullets.add(new Bullet(from, to, damage));
    }

    /**
     * Tests if a rectangle intersects any solid obstacle (including locked doors)
     * @param rect bounding rectangle box
     * @return true if blocked, else false
     */
    public boolean hitsObstacle(bagel.util.Rectangle rect) {
        for (Obstacle ob : obstacles) {
            if (ob.isActive() && rect.intersects(ob.getBounds())) {
                return true;
            }

            // adding doors as obstacle when locked
            if (primaryDoor != null && primaryDoor.behavesAsObstacle()
                && rect.intersects(primaryDoor.getBounds())) {
                return true;
            }

            if (secondaryDoor != null && secondaryDoor.behavesAsObstacle()
                    && rect.intersects(secondaryDoor.getBounds())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Activates all enemies
     * called when doors lock at the start
     */
    public void activateEnemies() {
        if (keyBulletKin != null) {
            keyBulletKin.setActive(true);
        }
        for (Enemy e : enemies) {
            e.setActive(true);
        }
    }

    /**
     * Spawns a key at given position (of keybulletkin death)
     * @param p world position for new key
     */
    public void keySpawning(Point p) {
        keys.add(new Key(new Point(p.x, p.y)));
    }

    /**
     * Applies damage to the first enemy intersected by given bullet
     * @param bulletBounds the bullet's bounding box at current position
     * @param damage to apply on hit
     * @return true if the bullet hit an enemy, false otherwise
     */
    public boolean hitAnyEnemy(Rectangle bulletBounds, int damage) {
        if (keyBulletKin != null && !keyBulletKin.isDead()
        && bulletBounds.intersects(keyBulletKin.getBounds())) {
            keyBulletKin.takeDamage(damage);
            if (keyBulletKin.isDead() && !keyDropped) {
                keyDropped = true;
                keySpawning(keyBulletKin.getPosition());
            }
            return true;
        }

        for (Enemy e : enemies) {
            if (!e.isDead() && bulletBounds.intersects(e.getBounds())) {
                e.takeDamage(damage);
                if (e.isDead() && !e.isRewardGiven()) {
                    if (player != null) {
                        player.earnCoins(e.getCoinReward());
                        player.onEnemyKilled(e);
                    }
                    e.setRewardGiven();
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Handles player bullets colldinig with obstacles (basket & tables)
     * @param bulletBounds the bullet's bounding box at current position
     * @return true if the bullet hit an enemy, otherwise, false
     */
    public boolean handleBullets(Rectangle bulletBounds) {
        for (Obstacle ob : obstacles) {
            if (ob.isActive() && bulletBounds.intersects(ob.getBounds())) {
                ob.onBulletHit(player);
                return true;
            }
        }

        if (primaryDoor != null && primaryDoor.behavesAsObstacle()
                && bulletBounds.intersects(primaryDoor.getBounds())) return true;

        if (secondaryDoor != null && secondaryDoor.behavesAsObstacle()
                && bulletBounds.intersects(secondaryDoor.getBounds())) return true;

        return false;
    }

    /**
     * Draws the room only when paused
     * so game play does not advance
     */
    public void drawOnly() {
        // if PAUSED
        if (ShadowDungeon.isPaused()) {
            if (primaryDoor != null) primaryDoor.draw();
            if (secondaryDoor != null) secondaryDoor.draw();

            for (Obstacle ob : obstacles) if (ob.isActive()) ob.draw();
            for (River river : rivers) river.draw();
            for (TreasureBox t : treasureBoxes) if (t.isActive()) t.draw();

            if (player != null) player.draw();

            for (Enemy e : enemies) if (e.isActive()) e.draw();
            if (keyBulletKin != null && keyBulletKin.isActive()) keyBulletKin.draw();

            for (Bullet b : bullets) b.draw();
            for (Fireball f :fireballs) f.draw();
            for (Key k : keys) k.draw();

        }
    }

    private void enemyContactDamage() {
        if (player == null) {
            return;
        }

        final double damage = propDouble("enemyContactDamage", 0.2);
        Rectangle pBounds = player.getCurrImage().getBoundingBoxAt(player.getPosition());

        if (keyBulletKin != null && keyBulletKin.isActive() && !keyBulletKin.isDead()) {
            if (pBounds.intersects(keyBulletKin.getBounds())) {
                player.receiveDamage(damage);
            }
        }

        for (Enemy e : enemies) {
            if (e.isActive() && !e.isDead() && pBounds.intersects(e.getBounds())) {
                player.receiveDamage(damage);
            }
        }
    }

    /**
     * Requests current update call to stop early
     */
    public void stopCurrentUpdateCall() {
        stopCurrentUpdateCall = true;
    }

    private static double propDouble(String key, double def) {
        try { return Double.parseDouble(ShadowDungeon.getGameProps().getProperty(key)); }
        catch (Exception e) { return def; }
    }


}
