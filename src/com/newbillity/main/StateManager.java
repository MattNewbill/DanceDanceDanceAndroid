package com.newbillity.main;

import com.newbillity.input.TouchHandler;

import com.newbillity.main.*;
import com.newbillity.state.State;

/**
 * **HAPPY**
 */

public interface StateManager {

	public void setState(State state);
	public State getCurrentState();
	public State getInitialState();
	public android.content.res.AssetManager getAssetManager();
	public TouchHandler getTouchHandler();
	
}
