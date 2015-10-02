package com.dc.ElementalJumper.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.dc.ElementalJumper.AssetLoader;

import java.awt.*;


/**
 * Created by David on 20/08/2015.
 */

public class Settings implements Screen {
    private Stage stage;
    private Table table;
    private Skin skin;
    private TextureAtlas textureAtlas;
    private TextButton backButton, soundButton;
    private Label settings, sound;
    private SpriteBatch spriteBatch;
    private Sprite sprite;
    private boolean soundState;


    @Override
    public void show() {
        AssetLoader.load();
        stage = new Stage();

        Gdx.input.setInputProcessor(stage);

        // Load button styles and skin
        textureAtlas = new TextureAtlas("data/ui/Button.pack");
        skin = new Skin(Gdx.files.internal("data/ui/menuSkin.json"), textureAtlas);

        soundButton = new TextButton("Toggle Sound", skin);
        soundButton.pad(50);
        soundState = AssetLoader.getSound();

        soundButton.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int buttons) {
                soundState = !AssetLoader.getSound();
                AssetLoader.setSound(soundState);
                AssetLoader.setSound(soundState);
                return true;
            }

        });

        backButton = new TextButton("Back", skin);
        backButton.pad(50);
        backButton.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int buttons) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new StartMenu());
                return true;
            }

        });

        spriteBatch = new SpriteBatch();
        Texture backgroundTexture = new Texture("data/images/greybg.png");
        sprite = new Sprite(backgroundTexture);
        sprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        backButton.setColor(Color.WHITE);
        backButton.getLabel().setColor(Color.WHITE);
        backButton.pad(20);

        settings = new Label("SETTINGS", skin);
        settings.setFontScale(0.7f);
        settings.setColor(Color.WHITE);

        sound = new Label("Sound:", skin);
        sound.setFontScale(0.55f);
        sound.setColor(Color.WHITE);

        table = new Table(skin);
        table.setFillParent(true);
        //table.debug();

        table.add(settings);
        table.pad(30f);
        table.row();
        table.add(sound);
        table.row();
        table.padBottom(50);
        table.add(soundButton);
        table.row();
        table.add(backButton);

        stage.addActor(table);

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        spriteBatch.begin();

        if(soundState)
        {
            sound.setText("Sound: On");
        }
        else
        {
            sound.setText("Sound: Off");
        }

        sprite.draw(spriteBatch);
        spriteBatch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);

        table.invalidateHierarchy();
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
        spriteBatch.dispose();
    }
}
