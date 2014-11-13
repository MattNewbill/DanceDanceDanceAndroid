package com.newbillity.input;

import combatgame.input.Pool;
import combatgame.input.TouchEvent;
import combatgame.input.Pool.*;
import combatgame.main.Game;

import java.util.List;
import java.util.ArrayList;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.util.Log;

/**
 * **HAPPY**
 */

public class TouchHandler implements OnTouchListener {

	public static final int MAX_POOL_SIZE = 100;
	
	Game game;
	double scaleX;
	double scaleY;
	
	boolean isTouched;
	int touchX, touchY;
	
	Pool<TouchEvent> pool;
	List<TouchEvent> touchEvents = new ArrayList<TouchEvent>();
	List<TouchEvent> touchEventsBuffer = new ArrayList<TouchEvent>();
	
	public TouchHandler(Game game, View view, double scaleX, double scaleY) {
		PoolObjectFactory<TouchEvent> factory = new PoolObjectFactory<TouchEvent>() {
			@Override
			public TouchEvent createObject() {
				return new TouchEvent();
			}
		};
		pool = new Pool<TouchEvent>(factory, MAX_POOL_SIZE);
		view.setOnTouchListener(this);
		
		this.game = game;
		this.scaleX = scaleX;
		this.scaleY = scaleY;
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		synchronized(this) {
			TouchEvent touchEvent = pool.newObject();
			switch(event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					touchEvent.type = TouchEvent.TOUCH_DOWN;
					isTouched = true;
					break;
				case MotionEvent.ACTION_MOVE:
					touchEvent.type = TouchEvent.TOUCH_DRAGGED;
					isTouched = true;
					break;
				case MotionEvent.ACTION_CANCEL:
				case MotionEvent.ACTION_UP:
					touchEvent.type = TouchEvent.TOUCH_UP;
					isTouched = false;
					break;
			}
			if(game.isScaled()) {
				touchEvent.x = touchX = (int)(event.getX() * scaleX);
				touchEvent.y = touchY = (int)(event.getY() * scaleY);
			}
			else {
				touchEvent.x = touchX = (int)event.getX();
				touchEvent.y = touchY = (int)event.getY();
			}
			touchEventsBuffer.add(touchEvent);
		}
		return true;
	}
	
	public boolean isTouched() {
		synchronized(this) {
			return isTouched();
		}
	}
	
	public int touchX() {
		synchronized(this) {
			return touchX;
		}
	}
	
	public int touchY() {
		synchronized(this) {
			return touchY;
		}
	}
	
	public List<TouchEvent> getTouchEvents() {
		synchronized(this) {
			for(int i = 0; i < touchEvents.size(); i++) {
				pool.free(touchEvents.get(i));
			}
			touchEvents.clear();
			touchEvents.addAll(touchEventsBuffer);
			touchEventsBuffer.clear();
			return touchEvents;
		}
	}
	
}
