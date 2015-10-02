package com.dc.ElementalJumper;

import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dc.ElementalJumper.screens.Map;
import com.dc.ElementalJumper.screens.Splash;

public class ElementalJumper extends Game {
	SpriteBatch batch;
	Texture img;


	@Override
	public void create () {
		setScreen(new Splash());
		//((Game) Gdx.app.getApplicationListener()).setScreen(new Map());
		//setScreen(new Splash());
		/*batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");*/

	}

	@Override
	public void dispose(){
		super.dispose();
	}

	@Override
	public void render () {
		/*Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(img, 0, 0);
		batch.end();*/
		super.render();
	}


	@Override
	public void pause(){
		//Gdx.app.log("", "Paused.");
		super.pause();
	}

	@Override
	public void resume(){
		//Gdx.app.log("", "Resume.");
		super.resume();
	}
}
