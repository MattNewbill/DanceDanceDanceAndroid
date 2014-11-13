package com.newbillity.main;

import com.newbillity.graphics.*;

import com.newbillity.main.Game;
import com.newbillity.util.Util;
import android.content.Context;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.util.Log;

/**
 * **HAPPY**
 */

public class RenderView extends SurfaceView implements Runnable {

	Game game;
	Bitmap frameBuffer;
	Thread renderThread;
	SurfaceHolder holder;
	Graphics2D drawingCanvas;
	volatile boolean isRunning = false;
	
	final int TARGET_FPS = 30;
	final int TARGET_UPDATES = 30;
	int targetFPSTime = 1000 / TARGET_FPS;
	int targetUpdateTime = 1000 / TARGET_UPDATES;
	
	Paint fpsPaint;
	
	public RenderView(Game game, Bitmap frameBuffer) {
		super(game);
		this.game = game;
		this.frameBuffer = frameBuffer;
		holder = getHolder();
		drawingCanvas = new GraphicsCPU(frameBuffer);
		//drawingCanvas = new GraphicsCPU();
		
		fpsPaint = new Paint();
		fpsPaint.setColor(Color.WHITE);
		fpsPaint.setTextSize(24);
	}

	public void run() {
		
		Rect destinationRect = new Rect();
		long accumulator = 0;
		long startTimeFrame = System.nanoTime();
		long startTimeSleep;
		long fps = 0;
		int frames = 0;
		
		//debug
		long startTimeUpdate;
		long startTimeRender;
		long drawTime = 0;
		
		while(isRunning) {
			//long startTime = System.currentTimeMillis();
			
			//make sure we have a surface to draw on
			if(!holder.getSurface().isValid()) {
				continue;
			}
			
			drawingCanvas.drawRGB(0, 0, 0); //clear the screen
			
			startTimeSleep = System.nanoTime();
			long delta = System.currentTimeMillis() - startTimeSleep;
			
			//update
			startTimeUpdate = System.currentTimeMillis();
			//while(accumulator >= targetUpdateTime) {
				game.getCurrentState().update(delta);
				accumulator -= targetUpdateTime;
			//}
			long endTimeUpdate = System.currentTimeMillis() - startTimeUpdate;
			
			//render
			startTimeRender = System.currentTimeMillis();
			game.getCurrentState().render(drawingCanvas, delta);
			drawingCanvas.drawText(Long.toString(fps), 30, 30, fpsPaint); //draw fps
			drawingCanvas.drawText("U: " + endTimeUpdate, 30, 50, fpsPaint); //draw time to update
			drawingCanvas.drawText("R: " + (System.currentTimeMillis() - startTimeRender), 30, 70, fpsPaint); //draw time to render
			drawingCanvas.drawText("D: " + drawTime, 30, 90, fpsPaint); //draw time to post to screen
			
			long startTimeCleanup = System.currentTimeMillis();
			Canvas canvas = holder.lockCanvas();
			canvas.getClipBounds(destinationRect);
			if(Game.isScaled())
				canvas.drawBitmap(frameBuffer, null, destinationRect, new Paint(Paint.LINEAR_TEXT_FLAG)); //scales and translate automatically to fit screen
			else
			//	canvas.drawBitmap(frameBuffer, null, new Rect(0, 0, Game.G_WIDTH, Game.G_HEIGHT), null); //TODO: perhaps change this to P_WIDTH, P_HEIGHT....test on larger devices to see for sure
				canvas.drawBitmap(frameBuffer, 0, 0, null);
			holder.unlockCanvasAndPost(canvas);
			drawTime = System.currentTimeMillis() - startTimeCleanup;
			
			//other fps stuff
			long elapsedTime = (System.nanoTime() - startTimeFrame) / 1000000;
			frames++;
			if(elapsedTime > 1000) {
				fps = frames;
				frames = 0;
				startTimeFrame = System.nanoTime();
			}
			
			//accumulator += System.currentTimeMillis() - startTime;
			
			
			//sleep if we've rendered faster than our target tick time
			elapsedTime = (System.nanoTime() - startTimeSleep) / 1000000;
			if(elapsedTime < targetFPSTime) {
				//Log.i("combatgame", "TTS: " + (targetFPSTime - elapsedTime));
				try {
					Thread.sleep(targetFPSTime - elapsedTime);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			
		}
	}
	
	public void resume() {
		isRunning = true;
		renderThread = new Thread(this);
		renderThread.start();
	}
	
	public void pause() {
		isRunning = false;
		while(true) {
			try {
				renderThread.join();
				return;
			} catch(InterruptedException e) {
				//do nothing
			}
		}
	}
}
