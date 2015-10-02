package com.dc.ElementalJumper.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;

/**
 * Created by David on 20/08/2015.
 */
public class Paused implements Screen {
    private Stage stage;
    private Skin skin;
    private TextureAtlas textureAtlas;
    private Table table;
    private Label settings;
    private TextButton continueButton;

    @Override
    public void show() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        textureAtlas = new TextureAtlas("data/ui/Button.pack");
        skin = new Skin(Gdx.files.internal("data/ui/menuSkin.json"), textureAtlas);

        final Window pause = new Window("PAUSE",skin);
        pause.padTop(64);
        pause.pack();

        continueButton = new TextButton("CONTINUE", skin);

        continueButton.pad(30);
        continueButton.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int buttons) {
                pause.setVisible(false);

                return true;
            }
        });

        table = new Table(skin);
        table.setFillParent(true);
        //table.debug();
        settings = new Label("Score", skin);
        settings.setFontScale(0.7f);

        table.add(settings);
        table.row();


        stage.addActor(table);
        pause.add(continueButton).row();
        pause.setSize(stage.getWidth() / 1.2f, stage.getHeight() / 1.2f);
        pause.setPosition((stage.getWidth() / 2) - (pause.getWidth() / 2), (stage.getHeight() / 2) - (pause.getHeight() / 2));

        stage.addActor(pause);
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
        textureAtlas.dispose();
        skin.dispose();
        stage.dispose();
    }
}
