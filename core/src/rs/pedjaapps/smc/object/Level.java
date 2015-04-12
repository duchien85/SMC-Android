package rs.pedjaapps.smc.object;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pedja on 1/31/14.
 */
public class Level
{
    public float width;
    public float height;
    public List<GameObject> gameObjects;
    public Vector3 spanPosition;
	public Background bg1;
	public Background bg2;
    public BackgroundColor bgColor;
    public Array<String> music;
	public String levelName;

	public static final String LEVEL_EXT = ".smclvl";
	public static final String LEVEL_DATA_EXT = ".data";

	public static final String[] levels = {"lvl_1", "lvl_2"};

	public Level(String levelName)
	{
		this.gameObjects = new ArrayList<>();
		this.levelName = levelName;
	}
}
