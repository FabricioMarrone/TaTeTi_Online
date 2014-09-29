package shuken.Engine.SimpleGUI;


import shuken.Engine.Resources.ResourceManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

/**
 * Boton simple que funciona con la SimpleGUI.
 * 
 * @author Shuken
 *
 */
public class SimpleButton extends ClickableArea{

	/** Fuente con la que se grafica el label. */
	protected BitmapFont font;
	
	/** Aspecto del boton. */
	protected TextureRegion skin;
	
	protected float labelLength;
	
	
	public SimpleButton(String label, float posX, float posY, BitmapFont font, boolean adaptSizeToText){
		this(label, posX, posY, font, null, adaptSizeToText);
	}
	
	public SimpleButton(String label, float posX, float posY, BitmapFont font, TextureRegion skin, boolean adaptSizeToText) {
		super(new Rectangle());
		
		setFont(font);
		setLabel(label);
		
		this.skin= skin;
		
		//Recalculamos el tamaño de la zona en funcion del largo del texto...
		if(adaptSizeToText){
			this.zone.set(posX, posY, labelLength + 20, font.getLineHeight() + 10);
		}else{
			if(skin != null) this.zone.set(posX, posY, skin.getRegionWidth(), skin.getRegionHeight());
			else this.zone.set(posX, posY, 120, 40);
		}
	}

	@Override
	public void render(SpriteBatch batch){
		//Graficamos skin...
		if(skin != null){
			if(this.coordOverArea(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY())) batch.setColor(1f, 0.93f, 0.86f, 1);
			
			batch.draw(skin, zone.x, zone.y, zone.width, zone.height);
			batch.setColor(Color.WHITE);
		}
		
		//Graficamos el texto del boton...
		font.setColor(Color.DARK_GRAY);
		font.draw(batch, label, zone.x + (zone.width - labelLength)/2, zone.y + font.getXHeight() + zone.height/2);
		font.setColor(Color.WHITE);
	}
	
	
	@Override
	public void update(float delta) {
		
		
	}
	
	
	@Override
	public void setLabel(String lbl){
		this.label= lbl;
		this.labelLength= ResourceManager.fonts.calculateTextWidth(font, lbl);
	}
	
	public void setFont(BitmapFont font){
		this.font= font;
	}

	@Override
	public void clickOn() {
		if(ResourceManager.isAudioOn()) ResourceManager.audio.select.play(0.2f);
	}

	
	
}//fin clase
