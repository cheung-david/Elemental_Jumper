package com.dc.ElementalJumper.objects;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.dc.ElementalJumper.AssetLoader;
import com.dc.ElementalJumper.screens.Map;
import com.dc.ElementalJumper.screens.StartMenu;
import org.w3c.dom.UserDataHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by David on 23/08/2015.
 */
public class Player extends InputAdapter implements ContactFilter, ContactListener
{
    private Fixture fixture;
    private Fixture footSensor;
    private Body body;
    private Vector2 velocity = new Vector2();
    private Sprite playerSprite, playerIdle, playerJR, playerJL, playerDuck;
    private float moveForce = 0.032f, jumpHeight = 56;
    public final float HEIGHT, WIDTH;
    private final short CATEGORY_RED = 0x0040;
    private final short CATEGORY_GREEN = 0x0002; // 0000000000000010 in binary
    private final short CATEGORY_BLUE = 0x0004; // 0000000000000100 in binary
    private final short CATEGORY_NEUTRAL = 0x0008;
    private final short CATEGORY_PLAYER = 0x0010;
    private final short CATEGORY_GREY = 0x0020;
    private final short CATEGORY_KILL = 0x0080;
    private short state = 1;
    private boolean stateChanged = false;
    private boolean paused = false;
    private Sound sound;
    private boolean gameOver = false;

    private int numFootContacts = 0;


    public Player(World world, float x, float y, float width, float height) {
        AssetLoader.load();
        this.WIDTH = width;
        this.HEIGHT= height;
        sound = Gdx.audio.newSound(Gdx.files.internal("data/sound/jump.wav"));

        BodyDef playerBody = new BodyDef();
        playerBody.type = BodyDef.BodyType.DynamicBody;
        playerBody.position.set(x, y);
        playerBody.fixedRotation = true;

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(width / 2,HEIGHT/ 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.density = 5;
        fixtureDef.restitution = 0;
        fixtureDef.friction = 0.95f;
        fixtureDef.filter.categoryBits = CATEGORY_PLAYER;
        fixtureDef.filter.maskBits = CATEGORY_NEUTRAL | CATEGORY_BLUE;

        body = world.createBody(playerBody);
        fixture = body.createFixture(fixtureDef);

        // Foot Sensor for collision
        polygonShape.setAsBox(width / 2, HEIGHT / 50, new Vector2(0,-0.8f), 0);
        //fixtureDef.isSensor = true;
        fixtureDef.shape = polygonShape;
        footSensor = body.createFixture(fixtureDef);
        footSensor.setUserData(3);

        // Set player sprite
        playerSprite = new Sprite(new Texture("data/images/player.png"));
        playerSprite.setSize(1.3f, 1.8f);

        playerIdle = new Sprite(new Texture("data/images/player_idle.png"));
        playerIdle.setSize(1.3f, 1.8f);

        playerJR = new Sprite(new Texture("data/images/player_jump_r.png"));
        playerJR.setSize(1.3f, 1.8f);

        playerJL = new Sprite(new Texture("data/images/player_jump_l.png"));
        playerJL.setSize(1.3f, 1.8f);

        playerDuck = new Sprite(new Texture("data/images/player_duck.png"));
        playerDuck.setSize(1.3f, 1.8f);

        //TextureRegion[]  playerFrames = new TextureRegion[4];
        //playerFrames[0] = new Texture("data/images/player.png");

        //playerSprite.setOrigin(playerSprite.getWidth() / 2, playerSprite.getHeight() / 2);
        body.setUserData(playerIdle);
        polygonShape.dispose();

    }

    public void move(float delta) {
        if (paused == true || gameOver == true)
        {
            return;
        }
        float acceleration=Gdx.input.getAccelerometerX(); // you can change it later to Y or Z, depending of the axis you want.

        if (Gdx.app.getType() == Application.ApplicationType.Android)
        {


            if (Math.abs(acceleration) > 0.4f) // the accelerometer value is < -0.4 and > 0.4 , this means that is not really stable and the position should move
            {
                velocity.x -= acceleration / 12 * moveForce; // we move it
                // now check for the animations
                if (velocity.x > 0.15f)
                    velocity.x = 0.15f;

                if (velocity.x < -0.15f)
                    velocity.x = -0.15f;

                if (velocity.x < 0) { // if the acceleration is negative
                //currentFrame = animation.getKeyFrame(4 + (int) stateTime);
                    body.setUserData(playerJL);
                } else {
                    body.setUserData(playerJR);
                // currentFrame = animation.getKeyFrame(8 + (int) stateTime);
                // this might be exactly backwards
                }
            }
            else
            {
                body.setUserData(playerIdle);
            // the sensor has some small movements, probably the device is not moving so we want to put the idle animation
            //currentFrame = animation.getKeyFrame(12);
            }
        }
        else
        {
            moveForce = 0.13f;
        }
        // Old player physics
        //body.applyForceToCenter(velocity, true);

        // If player is not moving, set animation to idle.
        if (velocity.x == 0)
            body.setUserData(playerIdle);

        body.setTransform(new Vector2(body.getPosition().x + velocity.x, body.getPosition().y + velocity.y), 0);

    }



    @Override
    public  boolean keyDown(int keycode) {
        if(paused == true || gameOver == true) {
            return false;
        }
        switch (keycode) {
            case Input.Keys.ESCAPE:
                ((Game) Gdx.app.getApplicationListener()).setScreen(new StartMenu());
                break;
            case Input.Keys.W:
                velocity.y = moveForce * 5;
                break;
            case Input.Keys.S:
                velocity.y = -moveForce;
                break;
            case Input.Keys.A:
                velocity.x = -moveForce;
                body.setUserData(playerJL);
                break;
            case Input.Keys.D:
                velocity.x = moveForce;
                body.setUserData(playerJR);
                break;
        }
        return true;

    }

    @Override
    public  boolean keyUp(int keycode) {
        if(paused == true || gameOver == true) {
            return false;
        }
        switch (keycode) {
            case Input.Keys.W:
                velocity.y = 0;
                break;
            case Input.Keys.S:
                velocity.y = 0;
                break;
            case Input.Keys.A:
                velocity.x = 0;
                break;
            case Input.Keys.D:
                velocity.x = 0;
                break;
        }
        return true;

    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button)
    {
        if(paused == true || gameOver == true) {
            return false;
        }
        // Top panel bar
        if (screenY < 85)
        {
            return false;
        }
        //System.out.println(screenX + " " + screenY);

        state++;
        Filter filter = new Filter();
        if(state > 3)
        {
            state = 0;
        }
        if (state == 0)
        {
            filter.maskBits = CATEGORY_NEUTRAL | CATEGORY_RED;
            footSensor.setFilterData(filter);
        }
        else if(state == 1)
        {
            filter.maskBits = CATEGORY_NEUTRAL | CATEGORY_BLUE;
            footSensor.setFilterData(filter);
        }
        else if(state == 2)
        {
            filter.maskBits = CATEGORY_NEUTRAL | CATEGORY_GREEN;
            footSensor.setFilterData(filter);
        }
        else if(state == 3)
        {
            filter.maskBits = CATEGORY_NEUTRAL | CATEGORY_GREY;
            //footSensor.
            footSensor.setFilterData(filter);
        }

        // System.out.println(state);
        stateChanged = true;
        return true;
    }

    @Override
    public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB) {

        //Gdx.app.log("State","Height:" + footSensor.getBody().getPosition().y);

        if ((footSensor.getFilterData().maskBits & fixtureA.getFilterData().categoryBits) != 0
                || (footSensor.getFilterData().maskBits & fixtureB.getFilterData().categoryBits) != 0) {
            if (fixtureA == footSensor || fixtureB == footSensor) {
                //float y1 = fixtureA.getShape()
                // float y2 = ((CircleShape) fixtureB.getShape()).getPosition().y;
                return (body.getLinearVelocity().y < 0);// && (body.getPosition().y > y1 || body.getPosition().y > y2);
            }
        }else if (fixtureA == fixture || fixtureB == fixture) {
            if (fixtureA.getFilterData().categoryBits == CATEGORY_KILL || fixtureB.getFilterData().categoryBits == CATEGORY_KILL) {
                Gdx.app.log("State", "Hit");
                setGameOver(true);
                return true;
            } else if (fixtureA.getFilterData().categoryBits == -1 || fixtureB.getFilterData().categoryBits == -1) {
                return true;
            }
        }
            return false;
    }


    // Contact Listener
    @Override
    public void beginContact(Contact contact) {
        //check if fixture A was the foot sensor
        /*java.lang.Object fixtureUserData = contact.getFixtureA().getUserData();
        if ( (int)fixtureUserData == 3 )
            numFootContacts++;
        //check if fixture B was the foot sensor
        fixtureUserData = contact.getFixtureB().getUserData();
        if ( (int)fixtureUserData == 3 )
            numFootContacts++;*/
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        body.setUserData(playerDuck);
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        if(contact.getWorldManifold().getPoints()[0].y <= body.getPosition().y - HEIGHT / 2.3f)
        {
            // Sound enabled
            if(AssetLoader.getSound()) {
                sound.play(0.9f);
            }
            body.applyLinearImpulse(0, jumpHeight, body.getWorldCenter().x, body.getWorldCenter().y, true);
        }
        //Gdx.app.log("vY:",""+body.getLinearVelocity().y);
    }

    @Override
    public void endContact(Contact contact) {
     /*   //check if fixture A was the foot sensor
        java.lang.Object fixtureUserData = contact.getFixtureA().getUserData();
        if ( (int)fixtureUserData == 3 )
            numFootContacts--;
        //check if fixture B was the foot sensor
        fixtureUserData = contact.getFixtureB().getUserData();
        if ( (int)fixtureUserData == 3 )
            numFootContacts--;*/
    }



    public float getRestitution()
    {
        return fixture.getRestitution();
    }

    public void setRestitution(float newRestitution)
    {
        fixture.setRestitution(newRestitution);
    }

    public float getFriction()
    {
        return fixture.getFriction();
    }

    public int getState()
    {
        return state;
    }

    public void setFriction(float newFriction)
    {
        fixture.setFriction(newFriction);
    }

    public Body getBody()
    {
        return body;
    }

    public void setStateChanged(boolean newState)
    {
        this.stateChanged = newState;
    }

    public boolean getStateChanged()
    {
        return stateChanged;
    }

    public Fixture getFixture()
    {
        return fixture;
    }

    public void setPaused(boolean paused)
    {
        this.paused = paused;
    }

    public void setGameOver(boolean gameOver)
    {
        this.gameOver = gameOver;
    }

    public boolean getGameOver() { return this.gameOver; }

}


