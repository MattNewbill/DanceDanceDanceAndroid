package com.newbillity.state;

import combatgame.widgets.*;
import combatgame.state.AboutState;
import combatgame.state.ConnectionState;
import combatgame.state.State;
import combatgame.util.Util;
import combatgame.input.*;
import combatgame.main.*;
import combatgame.graphics.*;
import combatgame.main.StateManager;
import java.util.List;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * **HAPPY**
 */

public class MainMenuState extends State {

	static final int V_BUTTON_MARGIN = 10;
	
	Button startButton;
	Button aboutButton;
	Button exitButton;
	
	public MainMenuState(StateManager stateManager) {
		super(stateManager);
		Game.shouldScale(true);
		
		AssetManager am = stateManager.getAssetManager();
		
		try {
			//load images
			Bitmap startButtonDisarmed = BitmapFactory.decodeStream(am.open("images/main_menu/start_button_original.png"));
			Bitmap aboutButtonDisarmed = BitmapFactory.decodeStream(am.open("images/main_menu/about_button_original.png"));
			Bitmap exitButtonDisarmed = BitmapFactory.decodeStream(am.open("images/main_menu/exit_button_original.png"));
			
			Bitmap startButtonArmed = BitmapFactory.decodeStream(am.open("images/main_menu/start_button_original_armed.png"));
			Bitmap aboutButtonArmed = BitmapFactory.decodeStream(am.open("images/main_menu/about_button_original_armed.png"));
			Bitmap exitButtonArmed = BitmapFactory.decodeStream(am.open("images/main_menu/exit_button_original_armed.png"));
			
			//determine button coordinates
			int startButtonX = (Game.G_WIDTH / 2) - startButtonDisarmed.getWidth() / 2;
			int startButtonY = (int) (Game.G_HEIGHT / 2 - (startButtonDisarmed.getHeight() * 1.5));
			int aboutButtonX = (Game.G_WIDTH / 2) - aboutButtonDisarmed.getWidth() / 2;
			int aboutButtonY = (int) (Game.G_HEIGHT / 2 - (aboutButtonDisarmed.getHeight() * 1.5) + V_BUTTON_MARGIN + startButtonDisarmed.getHeight());
			int exitButtonX = (Game.G_WIDTH / 2) - exitButtonDisarmed.getWidth() / 2;
			int exitButtonY = (int) (Game.G_HEIGHT / 2 - (exitButtonDisarmed.getHeight() * 1.5) + (V_BUTTON_MARGIN * 2) + startButtonDisarmed.getHeight() + aboutButtonDisarmed.getHeight());
			
			//create actual buttons
			startButton = new Button(startButtonDisarmed, startButtonArmed, startButtonX, startButtonY);
			aboutButton = new Button(aboutButtonDisarmed, aboutButtonArmed, aboutButtonX, aboutButtonY);
			exitButton = new Button(exitButtonDisarmed, exitButtonArmed, exitButtonX, exitButtonY);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update(float delta) {
		List<TouchEvent> events = stateManager.getTouchHandler().getTouchEvents();
		events = startButton.update(events);
		events = aboutButton.update(events);
		events = exitButton.update(events);
		
		if(startButton.state == Button.ACTIVATED) {
			Log.i("combatgame", "start button pressed");
			//bring up the next screen, whether that's the connection screen or straight to the game screen
			stateManager.setState(new ConnectionState(stateManager));
		}
		else if(aboutButton.state == Button.ACTIVATED) {
			Log.i("combatgame", "about button pressed");
			//spash screen shamelessly promoting the hard work we've all put in
			stateManager.setState(new AboutState(stateManager));
		}
		else if(exitButton.state == Button.ACTIVATED) {
			Log.i("combatgame", "exit button pressed");
			//exit the app
			//not sure if this is considered bad form or not....guess we'll find out
			System.exit(0);
		}
	}

	@Override
	public void render(Graphics2D g, float delta) {
		//draw start button
		startButton.render(g);
		
		//draw about button
		aboutButton.render(g);
		
		//draw exit button
		exitButton.render(g);
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void dispose() {
		if(startButton != null)
			startButton.recycle();
		if(aboutButton != null)
			aboutButton.recycle();
		if(exitButton != null)
			exitButton.recycle();
	}

}
