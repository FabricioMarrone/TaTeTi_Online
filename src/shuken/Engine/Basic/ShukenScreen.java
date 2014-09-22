package shuken.Engine.Basic;


import shuken.Engine.SimpleGUI.ClickableArea;

import com.badlogic.gdx.Screen;

public abstract class ShukenScreen implements Screen{

	/** SimpleGUI event */
	public abstract void simpleGUI_Event(ClickableArea area); 
	
	/** This method is called automatically by the game after creating ALL the screens of the game. */
	public abstract void postCreate();
}
