package rs.pedjaapps.smc.utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;

import java.util.HashSet;
import java.util.Set;

import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.audio.SoundManager;
import rs.pedjaapps.smc.object.maryo.Maryo;
import rs.pedjaapps.smc.screen.AbstractScreen;

public class GameSave {
    // der aktuelle Stand, der gerade gespielt wird
    private static int levelScore;
    private static long levelPlaytime;
    private static int maryoState;
    private static int persistentMaryoState;
    private static int lifes;
    private static long totalPlaytime;
    private static Set<String> unlockedLevels;
    private static int coins;
    private static int item;
    private static int persistentItem;
    private static int totalScore;

    public static long getLevelPlaytime() {
        return levelPlaytime;
    }

    public static void addLevelPlaytime(long timeToAdd) {
        levelPlaytime += timeToAdd;
        totalPlaytime += timeToAdd;
    }

    public static void init() {
        unlockedLevels = new HashSet<>();

        // gibt es bereits einen gespeicherten Stand?
        boolean didRead = false;
        try {
            String savedGame = PrefsManager.getSaveGame();
            if (savedGame != null) {
                JsonValue savegame = new JsonReader().parse(savedGame);
                readFromJson(savegame);
                didRead = true;
            }
        } catch (Throwable t) {
            Gdx.app.error("GameSave", "Error loading saved state", t);
        }

        if (!didRead || lifes <= 0)
            resetGameOver();
    }

    private static void readFromJson(JsonValue savegame) {
        lifes = savegame.getInt("lifes");
        coins = savegame.getInt("coins");
        persistentItem = savegame.getInt("item");
        persistentMaryoState = savegame.getInt("state");
        totalPlaytime = savegame.getLong("playtime");

        JsonValue levelList = savegame.get("levels");
        unlockedLevels.clear();
        for (JsonValue jsonlevel = levelList.child; jsonlevel != null; jsonlevel = jsonlevel.next) {
            String levelId = jsonlevel.getString("id");
            Level level = Level.getLevel(levelId);
            if (level != null) {
                level.currentScore = jsonlevel.getInt("score");
                level.bestScore = Math.max(level.bestScore, jsonlevel.getInt("best"));
                if (jsonlevel.getBoolean("unlocked", false) || level.bestScore > 0)
                    unlockedLevels.add(levelId);
            }
        }

        item = persistentItem;
        maryoState = persistentMaryoState;
        recalcTotalScore();
    }

    private static JsonValue toJson() {
        JsonValue json = new JsonValue(JsonValue.ValueType.object);
        json.addChild("lifes", new JsonValue(lifes));
        json.addChild("coins", new JsonValue(coins));
        json.addChild("item", new JsonValue(persistentItem));
        json.addChild("state", new JsonValue(persistentMaryoState));
        json.addChild("playtime", new JsonValue(totalPlaytime));

        JsonValue levelArray = new JsonValue(JsonValue.ValueType.array);
        for (String levelId : Level.getLevelList()) {
            Level level = Level.getLevel(levelId);

            if (level.bestScore > 0 || isUnlocked(levelId)) {
                JsonValue levelJson = new JsonValue(JsonValue.ValueType.object);
                levelJson.addChild("id", new JsonValue(levelId));
                levelJson.addChild("score", new JsonValue(level.currentScore));
                levelJson.addChild("best", new JsonValue(level.bestScore));
                levelJson.addChild("unlocked", new JsonValue(isUnlocked(levelId)));
                levelArray.addChild(levelJson);
            }
        }
        json.addChild("levels", levelArray);

        return json;
    }

    public static void resetGameOver() {
        lifes = 4;
        item = 0;
        coins = 0;
        persistentMaryoState = 0;
        maryoState = 0;
        totalPlaytime = 0;
        totalScore = 0;

        for (String levelId : Level.getLevelList()) {
            Level level = Level.getLevel(levelId);

            level.currentScore = 0;
        }
        save();
    }

    private static void save() {
        JsonValue json = toJson();
        PrefsManager.setSaveGame(json.toJson(JsonWriter.OutputType.json));
    }

    public static boolean isUnlocked(String levelName) {
        return unlockedLevels.contains(levelName);
        //return true;
    }

    public static void unlockLevel(String levelName) {
        if (levelName != null && !unlockedLevels.contains(levelName)) {
            unlockedLevels.add(levelName);

            //das erste unlocked level ist level 1, das muss nicht gespeichert werden
            //um unnötiges save während init zu vermeiden
            if (unlockedLevels.size() > 1)
                save();
        }
    }

    public static void addCoins(AbstractScreen screen, int addCoins) {
        coins += addCoins;
        if (coins >= 100) {
            coins -= 100;
            lifes++;
            AssetManager manager = screen.game.assets.manager;
            if (manager.isLoaded(Assets.SOUND_ITEM_LIVE_UP_2))
                SoundManager.play(manager.get(Assets.SOUND_ITEM_LIVE_UP_2, Sound.class));
        }
    }

    public static int getCoins() {
        return coins;
    }

    public static int getItem() {
        return item;
    }

    public static void setItem(int itemType) {
        item = itemType;
    }

    public static Maryo.MaryoState getMaryoState() {
        return Maryo.MaryoState.fromInt(maryoState);
    }

    public static void setMaryoState(Maryo.MaryoState newState) {
        maryoState = Maryo.MaryoState.toInt(newState);
    }

    public static int getLifes() {
        return lifes;
    }

    public static void addScore(int score) {
        levelScore += score;
    }

    public static void addLifes(int addedLifes) {
        lifes += addedLifes;
    }

    public static int getScore() {
        return levelScore;
    }

    public static int getTotalScore() {
        return totalScore;
    }

    /**
     * Starten eines Spiels aus dem Menü heraus
     */
    public static boolean startLevelFresh() {
        item = persistentItem;
        maryoState = persistentMaryoState;
        persistentMaryoState = 0;
        persistentItem = 0;
        levelPlaytime = 0;
        lifes--;
        levelScore = 0;
        save();
        return lifes >= 0;
    }

    /**
     * Level erfolgreich beendet => Punkte etc übernehmen
     */
    public static void levelCleared(String levelName) {
        GameSave.unlockLevel(Level.getNextLevel(levelName));
        persistentItem = item;
        persistentMaryoState = maryoState;
        lifes++;

        Level level = Level.getLevel(levelName);
        level.currentScore = levelScore;
        if (level.currentScore > level.bestScore)
            level.bestScore = levelScore;

        save();

        recalcTotalScore();
    }

    private static void recalcTotalScore() {
        totalScore = 0;

        for (String levelId : Level.getLevelList()) {
            Level level = Level.getLevel(levelId);

            totalScore += level.currentScore;
        }

    }
}
