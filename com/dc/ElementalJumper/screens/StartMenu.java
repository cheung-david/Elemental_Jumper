package com.dc.ElementalJumper.screens;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dc.ElementalJumper.AssetLoader;
import com.dc.ElementalJumper.com.dc.tween.ActorAccessor;


/**
 * Created by David on 16/08/2015.
 */
public class StartMenu implements Screen {

    private Stage stage;
    private Table table;
    private TextureAtlas textureAtlas;
    private TextureAtlas jumpAtlas;
    private Label menuTitle, menuTitle2, highScore;
    private Skin skin;
    private OrthographicCamera camera;
    //private BitmapFont whiteMenu;
    private TextButton startButton, exitButton, settingsButton, instructionButton;
    private TweenManager tweenManager;
    private SpriteBatch spriteBatch;
    private Sprite sprite;
    private TextureRegion playerFrames;
    private Array<TextureAtlas.AtlasRegion> textureRegion;
    private Animation animationUp;
    private float elapsedTime = 0;
    private float RATIO = 30;
    private Music music;

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        sprite.draw(spriteBatch);
        elapsedTime += Gdx.graphics.getDeltaTime();

        // Draw jumping animation older configurations
        //spriteBatch.draw(animationUp.getKeyFrame(elapsedTime, true), Gdx.graphics.getWidth() + 72, 150);
        //spriteBatch.draw(animationUp.getKeyFrame(elapsedTime, true), camera.position.x + camera.viewportWidth / 4f , camera.position.y - camera.viewportHeight / 4f, 2f, 3.25f);
        spriteBatch.draw(animationUp.getKeyFrame(elapsedTime, true), camera.position.x + camera.viewportWidth / 4f , camera.position.y - camera.viewportHeight / 4f, 2.65f, 4.6f);
        spriteBatch.end();

        stage.act(delta);
        stage.draw();
        tweenManager.update(delta);
    }

    @Override
    public void show() {
        // Load external saved data
        AssetLoader.load();

        // Sound is enabled
        if(AssetLoader.getSound()) {
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

        spriteBatch = new SpriteBatch();
        Texture backgroundTexture = new Texture("data/images/gamebgL.png");
        sprite = new Sprite(backgroundTexture);
        sprite.setPosition(camera.position.x - 1.5f - camera.viewportWidth / 2f, camera.position.y - camera.viewportHeight / 2f);
        sprite.setSize(camera.viewportWidth + 2f, camera.viewportHeight);
        // Screen is bigger than background
    /*    if (Gdx.graphics.getWidth() > sprite.getWidth()
                && Gdx.graphics.getHeight() > sprite.getHeight())
        {
            sprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }*/

        stage = new Stage(new StretchViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        Gdx.input.setInputProcessor(stage);



        textureAtlas = new TextureAtlas("data/ui/Button.pack");
        skin = new Skin(Gdx.files.internal("data/ui/menuSkin.json"), textureAtlas);
        jumpAtlas = new  TextureAtlas(Gdx.files.internal("data/images/animation/Jumper.pack"));
       // New font
        //whiteMenu = new BitmapFont(Gdx.files.internal("data/fonts/whiteninja.fnt"), false);


        // Button Style
        /*TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.getDrawable("buttonUp");
        textButtonStyle.down = skin.getDrawable("buttonDown");
        textButtonStyle.font = whiteMenu;
        */

      // textureRegion = new Array<TextureAtlas.AtlasRegion>();
        //textureRegion = textureAtlas.findRegions("player_00");
       /* animationUp = new Animation(0.1f,
                (textureAtlas.findRegion("player_00")),
                (textureAtlas.findRegion("player_01")),
                (textureAtlas.findRegion("player_02")),
                (textureAtlas.findRegion("player_03")),
                (textureAtlas.findRegion("player_04")));*/
        animationUp = new Animation(1/28f, jumpAtlas.getRegions());


        startButton = new TextButton("PLAY*", skin);
        startButton.padLeft(30);
        startButton.padRight(30);
        startButton.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int buttons) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new Map());
                return true;
            }

        });


        exitButton = new TextButton("EXIT", skin);
        exitButton.padLeft(30);
        exitButton.padRight(30);
        exitButton.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int buttons) {
                Gdx.app.exit();
                return true;
            }

        });



        settingsButton = new TextButton("Settings", skin);
        settingsButton.padLeft(30);
        settingsButton.padRight(30);
        settingsButton.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int buttons) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new Settings());
                return true;
            }

        });


        instructionButton = new TextButton("Instructions", skin);
        instructionButton.padLeft(30);
        instructionButton.padRight(30);
        instructionButton.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int buttons) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new Instructions());
                return true;
            }

        });

        //instructionButton.getSkin().getFont("default-font").setScale(0.33f, 0.33f);

        float unitScale = Gdx.graphics.getWidth() / 480f;

        //Label.LabelStyle title = new Label.LabelStyle(whiteMenu, Color.WHITE);
        menuTitle = new Label("Elemental", skin);
        menuTitle2 = new Label("Jumper", skin);
        highScore = new Label("High Score:" + AssetLoader.getHighScore(), skin);
        highScore.setFontScale(unitScale * 0.8f);
        menuTitle.setFontScale(unitScale * 1.3f);
        menuTitle2.setFontScale(unitScale * 1.3f);
        highScore.setColor(Color.WHITE);
        menuTitle.setColor(Color.WHITE);
        menuTitle2.setColor(Color.WHITE);



        // Scaling button size relative to screen size
        float buttonWidth = Gdx.graphics.getWidth() * 0.75f; // Makes the button 75% of the screen width wide.
        float buttonHeight = buttonWidth / instructionButton.getWidth() * instructionButton.getHeight(); // Use the button image to calculate the correct height.


        table = new Table(skin);
        table.setFillParent(true);
        table.add(menuTitle);
        table.row();

        table.add(menuTitle2);
        table.getCell(menuTitle2).spaceBottom(5);
        table.pad(25);
        table.row();
        table.add(highScore);
        table.row();

        table.add(startButton).size(buttonWidth, buttonHeight);
        table.row();
        table.add(settingsButton).size(buttonWidth, buttonHeight);
        table.row();
        table.add(instructionButton).size(buttonWidth, buttonHeight);
        table.row();
        table.add(exitButton).size(buttonWidth, buttonHeight);
        //table.debug();
        stage.addActor(table);

        // Animating buttons
        tweenManager = new TweenManager();
        Tween.registerAccessor(Actor.class, new ActorAccessor());

        Timeline.createParallel().beginParallel().
                push(Tween.set(startButton, ActorAccessor.OPACITY).target(0)).
                push(Tween.set(exitButton, ActorAccessor.OPACITY).target(0)).
                push(Tween.set(settingsButton, ActorAccessor.OPACITY).target(0)).
                push(Tween.to(startButton, ActorAccessor.OPACITY, 0.6f).target(1)).
                push(Tween.to(settingsButton, ActorAccessor.OPACITY, 0.6f).target(1)).
                push(Tween.to(exitButton, ActorAccessor.OPACITY, 0.6f).target(1)).end().
                start(tweenManager);
    }

    @Override
    public void hide() {
        // called when current screen changes from this to a different screen
        dispose();
    }


    public void resize(int width, int height) {
        //stage.getViewport().update(width, height);

        //table.invalidateHierarchy();
        camera.viewportWidth = width / RATIO;
        camera.viewportHeight = height / RATIO;
        camera.update();
        //table.setSize(width,height);
    }


    @Override
    public void dispose(){
        stage.dispose();
        textureAtlas.dispose();
        skin.dispose();
        spriteBatch.dispose();
        sprite.getTexture().dispose();

        if(AssetLoader.getSound()) {
            music.dispose();
        }
    }

    @Override
    public void pause(){

    }

    @Override
    public void resume(){

    }
}
