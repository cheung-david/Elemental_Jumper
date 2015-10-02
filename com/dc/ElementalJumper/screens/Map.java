package com.dc.ElementalJumper.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.RopeJoint;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.dc.ElementalJumper.Controls;
import com.dc.ElementalJumper.HorizontalMove;
import com.dc.ElementalJumper.MapGenerator;
import com.dc.ElementalJumper.objects.Player;
import com.dc.ElementalJumper.AssetLoader;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by David on 17/08/2015.
 */
public class Map implements Screen {
    private TextureAtlas textureAtlas;
    private Skin skin;
    private Sprite cloud;
    private Sprite cloud2;
    private Sprite bomb, weightChain, neutralPlat;
    private Sprite background;
    private Sprite stateIcon;
    private SpriteBatch spriteBatch;
    private BitmapFont font;
    private ShapeRenderer shapeRenderer;
    private Music music;

    protected World world;
    private Box2DDebugRenderer debugRenderer;
    private OrthographicCamera camera;

    private final float TIMESTEP = 1 / 60f;
    private final int VELOCITYITERATIONS = 8;
    private final int POSITIONITERATIONS = 3;

    private Body box, ball, groundBody;

    private Player player;
    //protected HorizontalMove horizontalMove;
    private Array<Body> tempBody = new Array<Body>();
    private Vector3 bottomLeft, bottomRight;
    private MapGenerator mapGenerator;
    private ArrayList<Fixture> platformList;
    private ArrayList<Body> bodyList;
    private final float MIN_HEIGHT = 3; // 4 & 7 more difficult
    private final float MAX_HEIGHT = 5.5f; // Dist between platforms
    private float RATIO = 70;
    private int heightScore = 0;
    private final int BOMBS_AT_HEIGHT = 0;
    private int BOMB_FREQUENCY = 200; // Lower value results in higher spawn rate
    private final short CATEGORY_KILL = 0x0080;
    private final short CATEGORY_NEUTRAL = 0x0008;
    private boolean stateChanged = false;
    private Stage stage;
    private TextButton continueButton, playAgainButton, menuButton;
    private Window pause, gameOver;
    private Table table;
    private Label score, pauseButton, highScore;
    private int highScoreValue;
    public boolean paused = false;
    public final int MOVING_PLAT = 0;

    // Generate different sized fonts on the spot
    private BitmapFont createFont(FreeTypeFontGenerator generator, float dp)
    {
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        int fontSize = (int)(dp * Gdx.graphics.getDensity());
        parameter.size = fontSize;

        return generator.generateFont(parameter);
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Game is paused
        if(paused == true)
        {
            // Display pause window
            pause.setVisible(true);
        }
        // Teleport to other side when at edge of screen
        if(player.getBody().getPosition().x < bottomLeft.x)
        {
            player.getBody().setTransform(bottomRight.x, player.getBody().getPosition().y, player.getBody().getAngle());
        }
        else if (player.getBody().getPosition().x > bottomRight.x)
        {
            player.getBody().setTransform(bottomLeft.x, player.getBody().getPosition().y, player.getBody().getAngle());
        }

        // Update player
        player.move(delta);

        // Game over
        if(player.getGameOver())
        {
            if (heightScore > AssetLoader.getHighScore())
            {
                AssetLoader.setHighScore(heightScore);
                highScore.setText(":D New High Score!: " + heightScore);
            }

            // Make Gameover window visible
            gameOver.setVisible(true);
        }

        // Game is not paused or game over, update world
        if(!paused && !player.getGameOver())
        {
            world.step(TIMESTEP, VELOCITYITERATIONS, POSITIONITERATIONS);
        }

        camera.position.y = player.getBody().getPosition().y > camera.position.y ? player.getBody().getPosition().y:camera.position.y;
        camera.update();

        // Player falls off screen
        if ((player.getBody().getPosition().y < camera.position.y - camera.viewportHeight / 2f) && player.getBody().getPosition().y > 3)
        {
            player.setGameOver(true);
        }

        // Get all list of platforms
        platformList = mapGenerator.getPlatformList();


        // Set blue background
        background.setPosition(camera.position.x - camera.viewportWidth / 2,  camera.position.y - camera.viewportHeight / 2);

        // Draw new clouds
        // Determine if player is above 2 times the screen
        if (((player.getBody().getPosition().y) - (camera.viewportHeight * 2)) > cloud2.getY())
        {
            cloud.setPosition(MathUtils.random(-camera.viewportWidth / 2f, camera.viewportWidth * 1.1f), camera.position.y + camera.viewportHeight);
            cloud.setSize(5.6f, 4);
            cloud2.setPosition(MathUtils.random(-camera.viewportWidth, camera.viewportWidth * 1.1f),MathUtils.random (camera.position.y + camera.viewportHeight * 1.7f, camera.position.y + camera.viewportHeight * 2.5f));
            cloud2.setSize(MathUtils.random(6,7), MathUtils.random(3,4));
        }


        // Set difficulty of the game as the player gets higher
        // Changes platform occurences frequency and type
        // Set bomb spawn rate depending on how high the player has reached
         if (player.getBody().getPosition().y > 2750)
         {
             mapGenerator.setDifferentPlatformFreq(5);
             mapGenerator.setMovingPlatformDifficulty(30);
         }
        else if (player.getBody().getPosition().y > 2500)
        {
            BOMB_FREQUENCY = 250;
            mapGenerator.setMinDist(6);
            mapGenerator.setDifferentPlatformFreq(20);
            mapGenerator.setPlatformTypeDifficulty(4);
            mapGenerator.setMovingPlatformDifficulty(40);
        }
        else if (player.getBody().getPosition().y > 2000)
        {
            mapGenerator.setMinDist(5);
            mapGenerator.setMaxDist(8);
            BOMB_FREQUENCY = 350;
            mapGenerator.setDifferentPlatformFreq(30);
            mapGenerator.setPlatformTypeDifficulty(3);
            mapGenerator.setMovingPlatformDifficulty(50);
        }
        else if(player.getBody().getPosition().y > 1500)
        {
            mapGenerator.setDifferentPlatformFreq(40);
            mapGenerator.setPlatformTypeDifficulty(2);
            mapGenerator.setMovingPlatformDifficulty(60);
        }
        else if(player.getBody().getPosition().y > 1000)
        {
            mapGenerator.setMinDist(4.5f);
            mapGenerator.setMaxDist(7);
            BOMB_FREQUENCY = 700;
        }
        else if(player.getBody().getPosition().y > 750)
        {
            mapGenerator.setDifferentPlatformFreq(50);
            mapGenerator.setMovingPlatformDifficulty(70);
        }
        else if(player.getBody().getPosition().y > 500) {
            mapGenerator.setMinDist(4.3f);
            mapGenerator.setMaxDist(6);
            BOMB_FREQUENCY = 800;
            mapGenerator.setDifferentPlatformFreq(65);
        }
         else if(player.getBody().getPosition().y > 250) {
             mapGenerator.setMinDist(3.8f);
             mapGenerator.setMaxDist(6);
             BOMB_FREQUENCY = 1000;
             mapGenerator.setDifferentPlatformFreq(70);
             mapGenerator.setMovingPlatformDifficulty(80);
         }
        else // Game starting difficulty
        {
            mapGenerator.setMovingPlatformDifficulty(90);
            mapGenerator.setMinDist(3.3f);
            mapGenerator.setDifferentPlatformFreq(75); // Lower number = increase in difficulty
            BOMB_FREQUENCY = 1500; // Starting mode
        }

        // Current player score
        heightScore = Math.max(heightScore, (int)player.getBody().getPosition().y);

        int numOfPlatforms = platformList.size();
        //int numOfHorizontalObstacle = bodyList.size();

        //System.out.println(player.getBody().getPosition().y);

        // Check if the element type has been changed recently
        stateChanged = player.getStateChanged();
        // Assign current element icon type
        if (stateChanged) {
            if (player.getState() == 0) {
                if (stateIcon.getTexture() != null) // Clear old sprite from memory
                    stateIcon.getTexture().dispose();
                stateIcon = new Sprite(new Texture("data/images/redIcon.png"));
            } else if (player.getState() == 1) {
                if (stateIcon.getTexture() != null)
                    stateIcon.getTexture().dispose();
                stateIcon = new Sprite(new Texture("data/images/blueIcon.png"));
                //System.out.println("changed");
            } else if (player.getState() == 2) {
                if (stateIcon.getTexture() != null)
                    stateIcon.getTexture().dispose();
                stateIcon = new Sprite(new Texture("data/images/greenIcon.png"));
            } else if (player.getState() == 3) {
                if (stateIcon.getTexture() != null)
                    stateIcon.getTexture().dispose();
                stateIcon = new Sprite(new Texture("data/images/greyIcon.png"));
            }
            player.setStateChanged(false);
        }




        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        background.draw(spriteBatch);
        cloud.draw(spriteBatch);
        cloud2.draw(spriteBatch);
        //font.draw(spriteBatch,"Max Height: " + heightScore , camera.position.x, camera.position.y + camera.viewportHeight / 3);


        // Top right corner for element type
        stateIcon.setPosition(camera.position.x + camera.viewportWidth / 2.9f, camera.position.y + camera.viewportHeight / 2.8f);
        stateIcon.setSize(1f, 1f);


        // Start spawning falling bombs when player reaches certain height
        if(player.getBody().getPosition().y > BOMBS_AT_HEIGHT)
        {
            // How often bombs appear randomly
            if(MathUtils.random(1, BOMB_FREQUENCY) < 2)
            {
                BodyDef bodyDef = new BodyDef();
                bodyDef.type = BodyDef.BodyType.KinematicBody;
                FixtureDef fixtureDef = new FixtureDef();

                // Spawn bombs above viewport
                bodyDef.position.set(player.getBody().getPosition().x, player.getBody().getPosition().y + camera.viewportHeight * 1.1f);
                bodyDef.fixedRotation = true;


                PolygonShape boxShape = new PolygonShape();
                boxShape.setAsBox(0.3f,0.3f);

                //fixture definition
                fixtureDef.shape = boxShape;
                fixtureDef.filter.categoryBits = CATEGORY_KILL;

                box = world.createBody(bodyDef);
                box.setUserData(bomb);
                box.setLinearVelocity(0, -0.85f);
                box.createFixture(fixtureDef);

                boxShape.dispose();
            }
        }


        // Draw all platforms in the world
        for (int i = 0; i < numOfPlatforms; i++)
        {
            if(platformList.get(i).getUserData() != null && platformList.get(i).getUserData() instanceof Sprite) {
                Sprite sprite = (Sprite) platformList.get(i).getUserData();
                //Gdx.app.log("X:" + platformList.get(i).getBody().getPosition().x,"Y:" + platformList.get(i).getBody().getPosition().y);
                sprite.draw(spriteBatch);
            }
        }



        // Draw all bodies in the world
        world.getBodies(tempBody);
            for(Body body : tempBody) {
                if (body.getPosition().y != 0 && body.getPosition().y < (player.getBody().getPosition().y - camera.viewportHeight)
                        && (body.getFixtureList()).size != 0) // Destroy bodies that are no longer in view
                {
                    body.destroyFixture(body.getFixtureList().first());
                    world.destroyBody(body);
                    body.setUserData(null);
                    body = null;
                }
                // Determine if there is a sprite to draw
                else if (body.getUserData() != null && body.getUserData() instanceof Sprite) {
                   if (body.getLinearVelocity().x != 0 && body.getLinearVelocity().y == 0) {

                       updateHorizontals(body, 0);
                       Sprite sprite = (Sprite) body.getUserData();
                       sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2, body.getPosition().y - sprite.getHeight() / 2);
                       sprite.draw(spriteBatch);
                    }
                    else
                    {
                        Sprite sprite = (Sprite) body.getUserData();
                        sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2, body.getPosition().y - sprite.getHeight() / 2);
                        sprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
                        sprite.draw(spriteBatch);
                    }
                }
            }

        stateIcon.draw(spriteBatch);
        spriteBatch.end();

       /*
        -May be of use later-
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0, 0, 0, 1);
        //shapeRenderer.line(-800, 0, 800, 0);
        //shapeRenderer.rect(0, 0, 1600, 10);
        shapeRenderer.end();*/

        // For debugging
       //debugRenderer.render(world, camera.combined);

        mapGenerator.generatePlat(camera.position.y + camera.viewportHeight / 2, player.getBody().getPosition().y - camera.viewportHeight / 2, camera);

        // Update current score
        score.setText("Max Height:" + heightScore);
        stage.act(delta);
        stage.draw();
    }


    @Override
    public void show() {
        // Load external file data
        AssetLoader.load();

        // Scale size to be proportional for different devices
        float unitScale = Gdx.graphics.getWidth() / 480f;

        // Sound enabled
        if (AssetLoader.getSound()) {
            music = Gdx.audio.newMusic(Gdx.files.internal("data/sound/HuSIMSkies.mp3"));
            music.setVolume(1f);
            music.setLooping(true);
            music.play();
        }


        // Set camera viewpoint ratio
        if(Gdx.app.getType() == Application.ApplicationType.Desktop) {
            Gdx.graphics.setDisplayMode((int) (Gdx.graphics.getHeight() / 1.5f), Gdx.graphics.getHeight(), false);
            RATIO = 30;

        }
        else if(Gdx.app.getType() == Application.ApplicationType.Android) {
            RATIO = 70;
            //Gdx.graphics.setDisplayMode((int) (Gdx.graphics.getHeight() / 1.5f), Gdx.graphics.getHeight(), false);
        }


        camera = new OrthographicCamera(Gdx.graphics.getWidth() / RATIO, Gdx.graphics.getHeight() / RATIO);
        //System.out.println(Gdx.graphics.getHeight());

        world = new World(new Vector2(0, -9.81f), true);
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        // Load all background sprites
        background = new Sprite(new Texture("data/images/bg.png"));
        stateIcon = new Sprite(new Texture("data/images/blueIcon.png"));
        cloud = new Sprite(new Texture("data/images/cloud1.png"));
        cloud2 = new Sprite(new Texture("data/images/cloud2.png"));
        bomb = new Sprite(new Texture("data/images/bomb.png"));
        weightChain = new Sprite(new Texture("data/images/weightChained.png"));
        neutralPlat = new Sprite(new Texture("data/images/boxplat.png"));
        bomb.setSize(1, 1);
        weightChain.setSize(1, 1);
        neutralPlat.setSize(1.2f, 0.2f);

        // Assign background clouds initial position and size
        cloud.setPosition(MathUtils.random(0, camera.position.x), MathUtils.random(0, camera.position.y + camera.viewportHeight / 4));
        cloud.setSize(5.6f, 4);
        cloud2.setPosition(MathUtils.random(0, camera.position.x), MathUtils.random(0, camera.position.y + camera.viewportHeight));
        cloud2.setSize(MathUtils.random(4, 6), MathUtils.random(3, 4));

        // Score font
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("data/fonts/zerothre.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 64;
        font = generator.generateFont(parameter);
        font.setColor(Color.BLACK);


        generator.dispose();

        // For debugging purposes
        debugRenderer = new Box2DDebugRenderer();

        // Construct main player
        player = new Player(world, 0, 1, 1, 1.5f);
        world.setContactFilter(player);
        world.setContactListener(player);

/*


*/

        // Create a stage to hold the pause window actor
        stage = new Stage(new StretchViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        //Gdx.input.setInputProcessor(stage);
        textureAtlas = new TextureAtlas("data/ui/Button.pack");
        skin = new Skin(Gdx.files.internal("data/ui/menuSkin.json"), textureAtlas);

        // Create pause window
        pause = new Window(" PAUSED",skin);
        pause.padTop(64);
        pause.pack();
        pause.setVisible(false);

        // Create continue button
        continueButton = new TextButton("CONTINUE", skin);


        // Create pause button
        pauseButton = new Label("   ~Pause ||",skin);
        pauseButton.setFontScale(unitScale * 0.7f);
        pauseButton.setColor(Color.BLACK);


        continueButton.pad(30);

        // Set input to button
        continueButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int buttons) {
                pause.setVisible(false);
                paused = false;
                player.setPaused(paused);
                return false;
            }
        });

        // Set input to the pause button at header bar.
        pauseButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int buttons) {
                if (!player.getGameOver()) {
                    pause.setVisible(true);
                    paused = true;
                    player.setPaused(paused);
                }
                return true;
            }
        });

        // Scaling button size relative to screen size
        float buttonWidth = Gdx.graphics.getWidth() * 0.75f; // Makes the button 75% of the screen width wide.
        float buttonHeight = buttonWidth / continueButton.getWidth() * continueButton.getHeight(); // Use the button image to calculate the correct height.

        // Add contents to window
        pause.add(continueButton).size(buttonWidth, buttonHeight).row();
        pause.setSize(stage.getWidth() / 1.5f, stage.getHeight() / 1.5f);
        pause.setPosition((stage.getWidth() / 2) - (pause.getWidth() / 2), (stage.getHeight() / 2) - (pause.getHeight() / 2));


        table = new Table(skin);
        table.align(10);
        table.setFillParent(true);
        //table.debug();

        // Add score to header bar
        score = new Label("Max Height:" + heightScore, skin);
        score.setFontScale(unitScale * 0.6f);
        score.setColor(Color.BLACK);

        table.padTop(3);
        table.padLeft(5);
        table.top();
        table.add(score);

        // Add pause button to header bar
        table.add(pauseButton);

        stage.addActor(table);
        // Load the window onto the stage
        stage.addActor(pause);

        gameOver = new Window(" GAME OVER",skin);
        //gameOver.setScale(unitScale);
        gameOver.padTop(64);
        gameOver.pack();
        gameOver.setVisible(false);


        highScoreValue = AssetLoader.getHighScore();
        highScore = new Label("High Score: " + highScoreValue, skin);
        highScore.setFontScale(unitScale * 0.5f);
        highScore.setColor(Color.BLACK);

        // Create play again button
        playAgainButton = new TextButton("PLAY AGAIN", skin);

        playAgainButton.pad(5);

        // Set input to button
        playAgainButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int buttons) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new Map());
                return false;
            }
        });

        // Create continue button
        menuButton = new TextButton("BACK TO MENU", skin);

        menuButton.pad(5);

        // Set input to button
        menuButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int buttons) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new StartMenu());
                return false;
            }
        });

        // Scaling button size relative to screen size
        buttonWidth = Gdx.graphics.getWidth() * 0.75f; // Makes the button 75% of the screen width wide.
        buttonHeight = buttonWidth / menuButton.getWidth() * menuButton.getHeight(); // Use the button image to calculate the correct height.

        // Add contents to window
        gameOver.add(highScore);
        gameOver.row();
        gameOver.add(playAgainButton).size(buttonWidth, buttonHeight).row();
        gameOver.add(menuButton).size(buttonWidth, buttonHeight).row();
        gameOver.setSize(stage.getWidth() / 1.4f, stage.getHeight() / 1.4f);
        gameOver.setPosition((stage.getWidth() / 2) - (gameOver.getWidth() / 2), (stage.getHeight() / 2) - (gameOver.getHeight() / 2));

        stage.addActor(gameOver);

        InputMultiplexer inputMultiplexer = new InputMultiplexer();

        // Main controls
        inputMultiplexer.addProcessor(new InputAdapter() {
            // Desktop controls
            @Override
            public boolean keyDown(int keycode) {
                switch (keycode) {
                    case Input.Keys.TAB:
                        ((Game) Gdx.app.getApplicationListener()).setScreen(new StartMenu());
                        break;
                    case Input.Keys.P:
                        paused = !paused;
                        player.setPaused(paused);
                        pause.setVisible(paused);
                        //((Game) Gdx.app.getApplicationListener()).setScreen(new Paused());
                        break;
                }
                return false;
            }

            // Zoom in and zoom out
            @Override
            public boolean scrolled(int amount) {
                camera.zoom += amount / 10f;
                return true;
            }


        });

        inputMultiplexer.addProcessor(player);
        inputMultiplexer.addProcessor(stage);

        // Allow input for all added processors
        Gdx.input.setInputProcessor(inputMultiplexer);


        // Body Definition
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        FixtureDef fixtureDef = new FixtureDef();


        // Ball
        bodyDef.position.set(10, 5);
        bodyDef.fixedRotation = true;

        // May be of use later
        //CircleShape circleShape = new CircleShape();
        //circleShape.setRadius(.5f);

        PolygonShape boxShape = new PolygonShape();
        boxShape.setAsBox(1.5f, 0.1f);

        // Fixtures
        fixtureDef.shape = boxShape;
        fixtureDef.density = 10.1f; // per sq meter
        fixtureDef.restitution = 0;
        fixtureDef.friction = 0;
        fixtureDef.filter.categoryBits = CATEGORY_NEUTRAL;

        //neutralPlat.setSize(1.6f,0.2f);
        ball = world.createBody(bodyDef);
        neutralPlat.setSize(3f, 0.2f);
        ball.setUserData(neutralPlat);
        ball.createFixture(fixtureDef);

        //circleShape.dispose();
        boxShape.dispose();

        // Ground
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(0, 3f);

        //Screen Coordinates
        ChainShape groundShape = new ChainShape();

        bottomLeft = new Vector3(0, Gdx.graphics.getHeight(), 0);
        bottomRight = new Vector3(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0);
        camera.unproject(bottomLeft);
        camera.unproject(bottomRight);


        groundShape.createChain(new float[]{bottomLeft.x, bottomLeft.y, bottomRight.x, bottomRight.y});

        fixtureDef.shape = groundShape;
        fixtureDef.friction = .4f;
        fixtureDef.restitution = 0;
        fixtureDef.filter.categoryBits = -1; // Collision with everything

        groundBody = world.createBody(bodyDef);
        groundBody.createFixture(fixtureDef);

        bodyDef.position.set(0,0);
        groundBody = world.createBody(bodyDef);
        groundBody.createFixture(fixtureDef);
        /*Body platformBody = world.createBody(bodyDef);
        platformBody.createFixture(fixtureDef);*/
        fixtureDef.restitution = 0;
        groundShape.dispose();

        // Initialize main map
        mapGenerator = new MapGenerator(groundBody, bottomLeft.x, bottomRight.x, MIN_HEIGHT, MAX_HEIGHT,
                player.WIDTH * 1.5f, player.WIDTH * 3, 0.001f, 0, Gdx.graphics.getHeight(), world);


        // Temp Box
        fixtureDef.filter.categoryBits = 0;
        bodyDef.position.y = 4;
        PolygonShape pivotBox = new PolygonShape();
        pivotBox.setAsBox(.50f, .25f);
        fixtureDef.restitution = 0f;

        fixtureDef.shape = pivotBox;
        //fixtureDef.filter.categoryBits = -1;

        Body newBox = world.createBody(bodyDef);
        newBox.createFixture(fixtureDef);

        pivotBox.dispose();

        // Distance joint between boxes
        DistanceJointDef distanceJointDef = new DistanceJointDef();

        distanceJointDef.bodyA = newBox;
        distanceJointDef.bodyB = ball;
        distanceJointDef.length = 2;

        world.createJoint(distanceJointDef);


        /* May be useful for future obstacles

        // Rope Joint Box to ground
        RopeJointDef ropeJointDef = new RopeJointDef();
        ropeJointDef.bodyA = groundBody;
        ropeJointDef.bodyB = ball;
        ropeJointDef.maxLength = 4;
        ropeJointDef.localAnchorA.set(0,0);
        ropeJointDef.localAnchorB.set(0,0);

        world.createJoint(ropeJointDef);
        */
    }

    @Override
    public void hide() {
        if (!player.getGameOver()) {
            paused = true;
            player.setPaused(paused);
        }
        else
        {
            dispose();
        }
    }



    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
        camera.viewportWidth = width / RATIO;
        camera.viewportHeight = height / RATIO;
        camera.update();
    }



    @Override
    public void dispose(){
        world.dispose();
        debugRenderer.dispose();
        textureAtlas.dispose();
        skin.dispose();
        background.getTexture().dispose();
        cloud.getTexture().dispose();
        cloud2.getTexture().dispose();
        bomb.getTexture().dispose();
        shapeRenderer.dispose();
        font.dispose();
        if(AssetLoader.getSound()) {
            music.dispose();
        }
    }

    @Override
    public void pause(){
        if (!player.getGameOver()) {
            paused = true;
            player.setPaused(paused);
        }
    }

    @Override
    public void resume(){

    }

    // Update the direction of the moving platforms
    public void updateHorizontals (Body platforms, float type) {
        // Type for moving platform
        if (type == MOVING_PLAT) {
            // Platform reached left edge of screen
            if (platforms.getPosition().x < camera.position.x - (camera.viewportWidth / 2f) + 1.5f)
            {
                platforms.setLinearVelocity(1,0);
            }
            // Platform reached right edge of screen
            if (platforms.getPosition().x >   camera.position.x + (camera.viewportWidth / 2f) - 1.5f)
            {
                platforms.setLinearVelocity(-1,0);
            }
        }
    }

    public World getWorld()
    {
        return world;
    }

    public void addToWorld(Body body, BodyDef bodyDef)
    {
        body = world.createBody(bodyDef);
    }
}
