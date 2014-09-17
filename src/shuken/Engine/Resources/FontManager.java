package shuken.Engine.Resources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.FloatArray;

public class FontManager {

	private static FontManager instance= null;
	
	
	/** Fuente utilizada con propositos generales. */
	public BitmapFont defaultFont, UIlabelsFont;
	
	//public static BitmapFont in_game_text;
	//public static BitmapFont harrington_22;
	
	
	
	
	
	public static FontManager getInstance(){
		if(instance==null) instance= new FontManager();
		
		return instance;
	}
	
	
	
	protected void loadFonts(){
		//Cargamos todas las fuentes para tenerlas disponibles cuando sean requeridas...
		defaultFont= new BitmapFont(false);
		defaultFont.setColor(Color.WHITE);
		
		UIlabelsFont= new BitmapFont(Gdx.files.internal("assets/fonts/UIlabelsFont.fnt"), Gdx.files.internal("assets/fonts/UIlabelsFont_0.png"), false);
		UIlabelsFont.setColor(Color.WHITE);
		
		//harrington_22= new BitmapFont(Gdx.files.internal("res/fonts/harrington_22.fnt"), Gdx.files.internal("res/fonts/harrington_22_0.png"), false);
	}
	
	
	
	public void disposeAll(){
		defaultFont.dispose();
		
	}
	
	
	
	/**
	 * Calcula la distancia en pixeles de un texto dado en una determinada fuente. </br>
	 *
	 * @param font tipo de letra
	 * @param text cadena de texto
	 * @return longitud del texto, en pixeles.
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
