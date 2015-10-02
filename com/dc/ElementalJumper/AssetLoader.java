package com.dc.ElementalJumper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * Created by David on 9/29/2015.
 */
public class AssetLoader {
    private static Preferences prefs;


    public static void load()
    {
        // Create (or retrieve existing) preferences file
        prefs = Gdx.app.getPreferences("ElementalJumper");

        if (!prefs.contains("highScore"))
        {
            prefs.putInteger("highScore", 0);
        }
        if (!prefs.contains("sound"))
        {
            prefs.putBoolean("sound", true);
        }
    }

    public static void setHighScore(int val)
    {
        prefs.putInteger("highScore", val);
        prefs.flush();
    }

    public static int getHighScore()
    {
        return prefs.getInteger("highScore");
    }

    public static void setSound(boolean val)
    {
        prefs.putBoolean("sound", val);
        prefs.flush();
    }


    public static boolean getSound()
    {
        return prefs.getBoolean("sound");
    }

    public static void dispose()
    {

    }
}
