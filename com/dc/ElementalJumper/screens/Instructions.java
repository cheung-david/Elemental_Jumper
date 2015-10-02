package com.dc.ElementalJumper.screens;

import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import java.awt.*;


/**
 * Created by David on 12/09/2015.
 */

public class Instructions implements Screen {
    private Stage stage;
    private Table table;
    private Skin skin;
    private TextureAtlas textureAtlas;
    private TextButton backButton;
    private Label instructions;
    private SpriteBatch spriteBatch;
    private Sprite sprite;
    private Camera camera;
    private int RATIO = 40;


    @Override
    public void show() {
        // Set camera viewpoint ratio
        if(Gdx.app.getType() == Application.ApplicationType.Desktop) {
            Gdx.graphics.setDisplayMode((int) (Gdx.graphics.getHeight() / 1.5f), Gdx.graphics.getHeight(), false);
            RATIO = 30;

        }
        else if(Gdx.app.getType() == Application.ApplicationType.Android) {
            RATIO = 70;
            //Gdx.graphics.setDisplayMode((int) (Gdx.graphics.getHeight() / 1.5f), Gdx.graphics.getHeight(), false);
        }

        stage = new Stage();

        spriteBatch = new SpriteBatch();
        Texture texture = new Texture("data/images/instruction.png");
        camera = new OrthographicCamera(Gdx.graphics.getWidth() / RATIO, Gdx.graphics.getHeight() / RATIO);

        sprite = new Sprite(texture);
        sprite.setPosition(camera.position.x + 0.5f - camera.viewportWidth / 2f, camera.position.y + 1 - camera.viewportHeight / 2f);
        sprite.setSize(camera.viewportWidth / 1.1f, camera.viewportHeight / 1.1f);


        // Load button styles and skin
        textureAtlas = new TextureAtlas("data/ui/Button.pack");
        skin = new Skin(Gdx.files.internal("data/ui/menuSkin.json"), textureAtlas);

        // Set input for screen
        Gdx.input.setInputProcessor(new InputAdapter() {
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
               ((Game) Gdx.app.getApplicationListener()).setScreen(new StartMenu());
               return true;
           }
        });

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        sprite.draw(spriteBatch);
        spriteBatch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
        camera.viewportWidth = width / RATIO;
        camera.viewportHeight = height / RATIO;
        camera.update();
        //table.invalidateHierarchy();
        //table.setSize(width,height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
        textureAtlas.dispose();
    }
}
