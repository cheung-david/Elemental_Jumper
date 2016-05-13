package com.dc.ElementalJumper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.*;
import com.dc.ElementalJumper.screens.Map;

import java.util.ArrayList;

/**
 * Created by David on 04/09/2015.
 */
public class HorizontalMove {
    public static final float PLATFORM_WIDTH = 2;
    public static final float PLATFORM_HEIGHT = 0.5f;
    public static final float PLATFORM_VELOCITY = 1;
    public static final float PLATFORM_MOVING = 0;
    private float type = PLATFORM_MOVING;
    private Body platforms;
    private BodyDef bodyDef;
    private ArrayList<Body> bodyList;
    private FixtureDef fixtureDef;


    public HorizontalMove ()
    {
        //this.bodyList = new ArrayList<Body>();
        this.bodyDef = new BodyDef();
        this.fixtureDef = new FixtureDef();
    }


    public void generateMovingPlat(Shape shape, short category, Sprite sprite, float posX, float posY, World world)
    {

            bodyDef.type = BodyDef.BodyType.KinematicBody;

            // Spawn bombs above viewport
            bodyDef.position.set(posX, posY);
            bodyDef.fixedRotation = true;


            //fixture definition
            fixtureDef.shape = shape;
            fixtureDef.filter.categoryBits = category;

            platforms = world.createBody(bodyDef);
            //super.addToWorld(platforms, bodyDef);

            platforms.setUserData(sprite);
            platforms.setLinearVelocity(1, 0);
            platforms.createFixture(fixtureDef);

           // bodyList.add(platforms);

    }


   /* public void update (float deltaTime) {
        if (type == PLATFORM_MOVING) {

            if (platforms.getPosition().x < boundsXL + PLATFORM_WIDTH)
            {
                platforms.setLinearVelocity(1,0);
            }
            if (platforms.getPosition().x >  boundsXR - PLATFORM_WIDTH)
            {
                platforms.setLinearVelocity(-1,0);
            }
        }

    }*/
    public ArrayList<Body> getBodyList() {
        return bodyList;
    }
}
