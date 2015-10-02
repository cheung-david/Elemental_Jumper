package com.dc.ElementalJumper;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.Filter;
import com.dc.ElementalJumper.screens.*;
import com.dc.ElementalJumper.screens.Map;


import java.util.*;
import java.util.logging.*;

/**
 * Created by David on 28/08/2015.
 */
public class MapGenerator extends Map {
    private Body platforms;
    private BodyDef bodyDef;
    private Fixture fixture;
    private Sprite platformType;
    private SpriteBatch spriteBatch;
    private float leftBounds, rightBounds;
    private float minDist, maxDist, minWidth, maxWidth, platformY, angle, height;
    private ArrayList<Fixture> platformList;
    private float screenSize;
    private final short CATEGORY_RED = 0x0040;
    private final short CATEGORY_GREEN = 0x0002; // 0000000000000010 in binary
    private final short CATEGORY_BLUE = 0x0004; // 0000000000000100 in binary
    private final short CATEGORY_NEUTRAL = 0x0008;
    private final short CATEGORY_GREY = 0x0020;
    private int platformTypeDifficulty = 4;
    private int differentPlatformDifficulty = 20;
    private int movingPlatformDifficulty = 50;
    private World world;
    protected HorizontalMove horizontalMove;

    public MapGenerator(Body platforms, float leftBounds, float rightBounds, float minDist, float maxDist, float minWidth, float maxWidth, float height, float angle, float screenSize, World world) {
        this.platforms = platforms;
        this.leftBounds = leftBounds;
        this.rightBounds = rightBounds;
        this.minDist = minDist;
        this.maxDist = maxDist;
        this.minWidth = minWidth;
        this.maxWidth = maxWidth;
        this.height = height;
        this.angle = angle;
        this.platformList = new ArrayList<Fixture>();
        this.bodyDef = new BodyDef();
        this.screenSize = screenSize;
        this.spriteBatch = new SpriteBatch();
        this.horizontalMove = new HorizontalMove();
        this.world = world;
    }

    public void generatePlat(float topBounds, float bottomEdge, OrthographicCamera camera){
        // Not in current screen, do not generate platforms
        if(platformY + MathUtils.random(minDist, maxDist) > topBounds)
        {
            return;
        }



        // Assign random width and position
        float width = MathUtils.random(minWidth, maxWidth);
        float platformX = MathUtils.random(leftBounds, rightBounds - width);

        platformY = topBounds;
        //bodyDef.type = BodyDef.BodyType.StaticBody;
        //bodyDef.position.set(platformX + width / 2, platformY + height / 2);

        PolygonShape platformShape = new PolygonShape();


        //if(MathUtils.random(1,100) < movingPlatformDifficulty)

        Filter filter = new Filter();

        // Spawn different platforms at a certain frequency depending on the current difficulty
        if (MathUtils.random(1, 100) < differentPlatformDifficulty) {
            filter.categoryBits = CATEGORY_NEUTRAL;
            //platformList.add(fixtureDef);
        }
        else // Spawn different types of platforms depending on difficulty
        {
            switch (MathUtils.random(1,platformTypeDifficulty)) {
                case 1:
                    filter.categoryBits = CATEGORY_RED;
                    break;
                case 2:
                    filter.categoryBits = CATEGORY_BLUE;
                    break;
                case 3:
                    filter.categoryBits = CATEGORY_GREEN;
                    break;
                case 4:
                    filter.categoryBits = CATEGORY_GREY;
                    break;
            }
        }


        // Create new sprite depending on the type of platform created
        if(filter.categoryBits == CATEGORY_RED)
        {
            platformType = new Sprite(new Texture("data/images/redplat.png"));
        }
        else if(filter.categoryBits == CATEGORY_BLUE)
        {
            platformType = new Sprite(new Texture("data/images/blueplat.png"));
        }
        else if (filter.categoryBits == CATEGORY_GREEN)
        {
            platformType = new Sprite(new Texture("data/images/greenplat.png"));
        }
        else if(filter.categoryBits == CATEGORY_GREY)
        {
            platformType = new Sprite(new Texture("data/images/greyplat.png"));
        }
        else
        {
            platformType = new Sprite(new Texture("data/images/boxplat.png"));
        }

        // Resize platform image and attach it onto fixture
        platformType.setSize(width, 0.5f);
        platformType.setPosition(platformX + width / 2f - (platformType.getWidth() / 2f), platformY + height / 2f - (platformType.getHeight() / 2f));

        if(MathUtils.random(1,100) < movingPlatformDifficulty)
        {
            platformShape.setAsBox(width / 2, height / 2, new Vector2(platformX + width / 2f, platformY + height / 2f), MathUtils.random(-angle, angle));
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = platformShape;
            fixtureDef.density = 0;
            fixtureDef.restitution = 0;
            fixtureDef.filter.categoryBits = filter.categoryBits;
            fixture = platforms.createFixture(fixtureDef);
            fixture.setUserData(platformType);
            platformList.add(fixture);
        }
        else
        {
            platformShape.setAsBox(width / 2, height / 2);
            horizontalMove.generateMovingPlat(platformShape, filter.categoryBits, platformType, platformX + width / 2f, platformY + height / 2f, world);
        }
        update();

        platformShape.dispose();
    }

    // Destroy older platforms to save memory
    public void update(){
        int len = platformList.size();
        if (len > 15)
        {
            Fixture currentPlat = platformList.get(0);
            platforms.destroyFixture(currentPlat);
            platformList.remove(0);
        }
    /*        for (int i = 0; i < len; i++)
        {
            Fixture currentPlat = platformList.get(i);
            if (currentPlat.getBody().getPosition().y < bottomEdge)
            {
                platforms.destroyFixture(currentPlat);
                platformList.remove(i);
                len = platformList.size();
            }
            System.out.println(currentPlat.getBody().getPosition().y);
            System.out.println("N:" + bottomEdge);
        }*/
    }

    public void setPlatforms(Body platforms) {
        this.platforms = platforms;
    }

    public float getLeftBounds() {
        return leftBounds;
    }

    public float getRightBounds() {
        return rightBounds;
    }

    public float getMinDist() {
        return minDist;
    }

    public float getMaxDist() {
        return maxDist;
    }

    public float getMinWidth() {
        return minWidth;
    }

    public float getMaxWidth() {
        return maxWidth;
    }

    public float getHeight() {
        return height;
    }

    public void setMinDist(float minDist) {
        this.minDist = minDist;
    }

    public void setMaxDist(float maxDist) {
        this.maxDist = maxDist;
    }

    public void setMinWidth(float minWidth) {
        this.minWidth = minWidth;
    }

    public void setMaxWidth(float maxWidth) {
        this.maxWidth = maxWidth;
    }

    public void setPlatformTypeDifficulty(int numOccur) {
        this.platformTypeDifficulty = numOccur;
    }

    public void setDifferentPlatformFreq(int occurFreq) {
        this.differentPlatformDifficulty = occurFreq;
    }

    public void setMovingPlatformDifficulty(int occurFreq) { this.movingPlatformDifficulty = occurFreq; }

    public ArrayList<Fixture> getPlatformList() {
        return platformList;
    }

    public ArrayList<Body> getBodyList() {
        return horizontalMove.getBodyList();
    }
}
