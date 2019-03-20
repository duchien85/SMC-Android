package rs.pedjaapps.smc.object.items.mushroom;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import rs.pedjaapps.smc.object.World;
import rs.pedjaapps.smc.object.maryo.Maryo;
import rs.pedjaapps.smc.utility.GameSave;

/**
 * Created by pedja on 29.3.15..
 * <p/>
 * This file is part of SMC-Android
 * Copyright Predrag Čokulov 2015
 */
public class MushroomBlue extends Mushroom
{
    public MushroomBlue(World world, Vector2 size, Vector3 position)
    {
        super(world, size, position);
        textureName = "game_items_mushroom_blue";
        mPickPoints = 700;
    }

    @Override
    public int getType() {
        return TYPE_MUSHROOM_BLUE;
    }

    @Override
    protected void performCollisionAction()
    {
        playerHit = true;
        world.maryo.upgrade(Maryo.MaryoState.ice, this, false);
        world.trashObjects.add(this);
        GameSave.save.points += mPickPoints;
    }
}
