package shuken.Engine.Resources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.FloatArray;

public class FontManager {

	private static FontManager instance= null;
	
	
	/** Fonts of the game */
	public BitmapFont defaultFont, UIlabelsFont, gameText;
	
	public static FontManager getInstance(){
		if(instance==null) instance= new FontManager();
		
		return instance;
	}
	
	/**
	 * Load all the fonts into memory.
	 */
	protected void loadFonts(){
		
		defaultFont= new BitmapFont(false);
		defaultFont.setColor(Color.WHITE);
		
		UIlabelsFont= new BitmapFont(Gdx.files.internal("assets/fonts/UIlabelsFont.fnt"), Gdx.files.internal("assets/fonts/UIlabelsFont_0.png"), false);
		UIlabelsFont.setColor(Color.WHITE);
		
		gameText= new BitmapFont(Gdx.files.internal("assets/fonts/GameText.fnt"), Gdx.files.internal("assets/fonts/GameText_0.png"), false);
		gameText.setColor(Color.WHITE);
		
	}
	
	
	
	public void disposeAll(){
		defaultFont.dispose();
		
	}
	
	
	
	/**
	 * Calculate the length of the text (in pixels) for the given font.
	 *
	 * @param font 
	 * @param text 
	 * @return length of the text (in pixels)
	 */
	public float calculateTextWidth(BitmapFont font, String text){
		FloatArray f1= new FloatArray();
		FloatArray f2= new FloatArray();
		font.computeGlyphAdvancesAndPositions(text, f1, f2);
		
		float max= 0f;
		for(int i=0; i < f1.size; i++){
			max+= f1.get(i);
		}
		
		return max;
	}
}//fin clase
