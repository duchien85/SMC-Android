package rs.pedjaapps.smc.object;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;
import java.util.*;

import rs.pedjaapps.smc.utility.TextUtils;
import rs.pedjaapps.smc.view.*;

/**
 * Created by pedja on 1/31/14.
 */
public class Level
{
    public float width;
    public float height;
    public List<GameObject> gameObjects;
    public Vector3 spanPosition;
	public Array<Background> backgrounds;
    public Array<String> music;
	public String levelName;
	public String particleEffect;

	public static final String LEVEL_EXT = ".smclvl";

	public Level(String levelName)
	{
		this.gameObjects = new ArrayList<>(5000);//set initial capacity so that we avoid alloc during game loop
		this.backgrounds = new Array<>(2);
		this.levelName = levelName;
	}

	public void dispose()
	{
		for(GameObject go : gameObjects)
		{
			go.dispose();
		}
		gameObjects = null;
		for(Background background : backgrounds)
		{
			background.dispose();
		}
		backgrounds = null;
	}

	public LevelEntry findEntry(String entry)
	{
		if(TextUtils.isEmpty(entry))
			return null;
		for(GameObject go : gameObjects)
		{
			if(go instanceof LevelEntry && entry.equals(((LevelEntry)go).name))
			{
				return (LevelEntry) go;
			}
		}
		return null;
	}

	public LevelEntry findEntryOrThrow(String entry)
	{
		if(TextUtils.isEmpty(entry))
			throw new GdxRuntimeException("Entry cannot be null");
		for(GameObject go : gameObjects)
		{
			if(go instanceof LevelEntry && entry.equals(((LevelEntry)go).name))
			{
				return (LevelEntry) go;
			}
		}
		throw new GdxRuntimeException("Entry " + entry + " not found in level " + levelName);
	}
}
