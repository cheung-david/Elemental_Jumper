package com.dc.ElementalJumper.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

/**
 * Created by David on 9/17/2015.
 */
public class GameOver implements Screen {
    private Stage stage;
    private Table table;
    private Skin skin;
    private TextureAtlas textureAtlas;
    private TextButton retryButton, menuButton;
    private Label gameOver;

    @Override
    public void show() {
        stage = new Stage();

        Gdx.input.setInputProcessor(stage);

        // Load button styles and skin
        textureAtlas = new TextureAtlas("data/ui/Button.pack");
        skin = new Skin(Gdx.files.internal("data/ui/menuSkin.json"), textureAtlas);
        retryButton = new TextButton("PLAY AGAIN", skin);
        retryButton.pad(50);

        menuButton = new TextButton("RETURN TO MENU", skin);
        menuButton.pad(50);

        // Add input to button
        retryButton.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int buttons) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new Map());
                return true;
            }

        });

        // Add input to button
        menuButton.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int buttons) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new StartMenu());
                return true;
            }

        });

        retryButton.setColor(Color.WHITE);
        retryButton.pad(20);
        menuButton.setColor(Color.WHITE);
        menuButton.pad(20);

        gameOver = new Label("GAME OVER", skin);
        gameOver.setFontScale(0.7f);
        gameOver.setColor(Color.WHITE);

        // Make table to hold all the content
        table = new Table(skin);
        table.setFillParent(true);
        //table.debug();

        // Add contents to table
        table.add(gameOver);
        table.row();
        table.padBottom(50);
        table.add(retryButton);
        table.row();
        table.add(menuButton);

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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
    }
}
