package com.dc.ElementalJumper.screens;

import aurelienribon.tweenengine.*;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dc.ElementalJumper.AssetLoader;
import com.dc.ElementalJumper.com.dc.tween.SpriteAccessor;

/**
 * Created by David on 15/08/2015.
 */
public class Splash implements Screen {

    private SpriteBatch spriteBatch;
    private Sprite sprite;
    TweenManager tweenManager;

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        spriteBatch.begin();
        sprite.draw(spriteBatch);
        spriteBatch.end();

        tweenManager.update(delta);
    }

    @Override
    public void show()
    {
        spriteBatch = new SpriteBatch();
        Texture texture = new Texture("badlogic.jpg");

        tweenManager = new TweenManager();

        sprite = new Sprite(texture);
        sprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        Tween.registerAccessor(Sprite.class, new SpriteAccessor());

        // Splash Screen fade in and then fade out
        Tween.set(sprite, SpriteAccessor.OPACITY).target(0).start(tweenManager);
        Tween.to(sprite, SpriteAccessor.OPACITY, 1.2f).target(1).start(tweenManager);
        Tween.to(sprite, SpriteAccessor.OPACITY, 1.2f).target(0).delay(1f).setCallback(new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new StartMenu());
            }
        }).start(tweenManager);


    }

    @Override
    public void hide() {
        // called when current screen changes from this to a different screen
        dispose();
    }


    public void resize(int width, int height) {
    }


    @Override
    public void dispose(){
        spriteBatch.dispose();
        sprite.getTexture().dispose();
    }

    @Override
    public void pause(){

    }

    @Override
    public void resume(){

    }



}
