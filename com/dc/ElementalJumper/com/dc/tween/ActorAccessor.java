package com.dc.ElementalJumper.com.dc.tween;

import aurelienribon.tweenengine.TweenAccessor;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by David on 18/08/2015.
 */
public class ActorAccessor implements TweenAccessor<Actor> {

    public static final int OPACITY = 0;

    @Override
    public int getValues(Actor target, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case 0:
                returnValues[0] = target.getColor().a;
                return 1;
            default:
                assert false;
                return -1;
        }
    }

    @Override
    public void setValues(Actor target, int tweenType, float[] newValues) {
        switch (tweenType) {
            case 0:
                target.setColor(target.getColor().r, target.getColor().g, target.getColor().b, newValues[0]);
            default:
                assert false;
        }
    }
}

