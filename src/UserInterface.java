import bagel.Font;
import bagel.Window;
import bagel.util.Point;

/**
 * Helper methods to display information for the player
 */
public class UserInterface {
    /**
     * Draws player's health, coins, keys and weapon level
     * @param health current health
     * @param coins current coins
     * @param keys current keys
     * @param weaponLevel current weaponLevel
     */
    public static void drawStats(double health, double coins, int keys, int weaponLevel) {
        int fontSize = Integer.parseInt(ShadowDungeon.getGameProps().getProperty("playerStats.fontSize"));
        drawData(String.format("%s %.1f", ShadowDungeon.getMessageProps().getProperty("healthDisplay"), health), fontSize,
                IOUtils.parseCoords(ShadowDungeon.getGameProps().getProperty("healthStat")));
        drawData(String.format("%s %.0f", ShadowDungeon.getMessageProps().getProperty("coinDisplay"), coins), fontSize,
                IOUtils.parseCoords(ShadowDungeon.getGameProps().getProperty("coinStat")));
        drawData(String.format("%s %d", ShadowDungeon.getMessageProps().getProperty("keyDisplay"), keys), fontSize,
                IOUtils.parseCoords(ShadowDungeon.getGameProps().getProperty("keyStat")));
        drawData(String.format("%s %d", ShadowDungeon.getMessageProps().getProperty("weaponDisplay"), weaponLevel), fontSize,
                IOUtils.parseCoords(ShadowDungeon.getGameProps().getProperty("weaponStat")));
    }

    /**
     * Draws prep room tite, movement promp, character selection prompt and skill descriptions of characters
     */
    public static void drawStartMessages() {
        drawTextCentered("title", Integer.parseInt(ShadowDungeon.getGameProps().getProperty("title.fontSize")), Double.parseDouble(ShadowDungeon.getGameProps().getProperty("title.y")));

        int promptSize = Integer.parseInt(ShadowDungeon.getGameProps().getProperty("prompt.fontSize"));
        drawTextCentered("moveMessage", promptSize, Double.parseDouble(ShadowDungeon.getGameProps().getProperty("moveMessage.y")));

        double selectY = Double.parseDouble(ShadowDungeon.getGameProps().getProperty("selectMessage.y"));
        drawTextCentered("selectMessage", promptSize, selectY);

        // character captions besides sprite
        int characterDescSize = Integer.parseInt(ShadowDungeon.getGameProps().getProperty("playerStats.fontSize"));
        Point marinePt = IOUtils.parseCoords(ShadowDungeon.getGameProps().getProperty("marineMessage"));
        Point robotPt = IOUtils.parseCoords(ShadowDungeon.getGameProps().getProperty("robotMessage"));
        drawData(ShadowDungeon.getMessageProps().getProperty("marineDescription"), characterDescSize, marinePt);
        drawData(ShadowDungeon.getMessageProps().getProperty("robotDescription"), characterDescSize, robotPt);
    }

    /**
     * Draws the end message or win or lose
     * @param win true to show the win message, false for lose message
     */
    public static void drawEndMessage(boolean win) {
        drawTextCentered(win ? "gameEnd.won" : "gameEnd.lost", Integer.parseInt(ShadowDungeon.getGameProps().getProperty("title.fontSize")), Double.parseDouble(ShadowDungeon.getGameProps().getProperty("title.y")));
    }

    /**
     * draws a centered line of text
     * @param textPath text path
     * @param fontSize font size
     * @param posY position
     */
    public static void drawTextCentered(String textPath, int fontSize, double posY) {
        Font font = new Font("res/wheaton.otf", fontSize);
        String text = ShadowDungeon.getMessageProps().getProperty(textPath);
        double posX = (Window.getWidth() - font.getWidth(text)) / 2;
        font.drawString(text, posX, posY);
    }

    /**
     * Draws data at a specific location
     * @param data data
     * @param fontSize size of font
     * @param location location
     */
    public static void drawData(String data, int fontSize, Point location) {
        Font font = new Font("res/wheaton.otf", fontSize);
        font.drawString(data, location.x, location.y);
    }
}
