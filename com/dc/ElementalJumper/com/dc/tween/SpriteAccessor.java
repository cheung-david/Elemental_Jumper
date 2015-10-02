package com.dc.ElementalJumper.com.dc.tween;

import aurelienribon.tweenengine.TweenAccessor;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by David on 15/08/2015.
 */
public class SpriteAccessor implements TweenAccessor<Sprite> {

    public static final int OPACITY = 0;

    @Override
    public int getValues(Sprite target, int tweenType, float[] returnValues) {
        switch(tweenType){
            case 0:
                returnValues[0] = target.getColor().a;
                return 1;
            default:
                assert false;
                return -1;
        }
    }

    @Override
    public void setValues(Sprite target, int tweenType, float[] newValues) {
        switch(tweenType){
            case 0:
                target.setColor(target.getColor().r, target.getColor().g, target.getColor().b, newValues[0]);
                break;
            default:
                assert false;
        }

    }

}
