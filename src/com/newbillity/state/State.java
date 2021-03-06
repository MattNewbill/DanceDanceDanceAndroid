package com.newbillity.state;

import com.newbillity.main.*;
import com.newbillity.graphics.*;

/**
 * **HAPPY**
 */

public abstract class State {

	StateManager stateManager;
	
	public State(StateManager stateManager) {
		this.stateManager = stateManager;
	}
	
	public abstract void update(float delta);
	public abstract void render(Graphics2D g, float delta);
	public abstract void pause();
	public abstract void resume();
	public abstract void dispose();
	
}
