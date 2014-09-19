package shuken.Engine.SimpleGUI;

import java.util.ArrayList;

import shuken.Engine.Resources.ResourceManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class SimpleErrorMessage extends ClickableArea {

	private BitmapFont font;
	private ArrayList<String> text;
	private TextureRegion skin;
	
	public SimpleErrorMessage(ArrayList<String> text, BitmapFont font, TextureRegion skin) {
		super(defineZone(text, font));
		
		this.text= text;
		this.font= font;
		this.skin= skin;
	}

	private static Rectangle defineZone(ArrayList<String> text, BitmapFont font){
		
		float width= 0;
		for(int i= 0; i < text.size(); i++){
			float w= ResourceManager.fonts.calculateTextWidth(font, text.get(i));
			if(w > width) width= w;
		}
		float height= text.size() * font.getLineHeight();
		
		float x= (Gdx.graphics.getWidth() - width)/2;
		float y= Gdx.graphics.getHeight() - height - 5;
		
		return new Rectangle(x, y, width, height);
	}
	
	
	@Override
	public void render(SpriteBatch batch) {
		if(skin != null) {
			batch.setColor(0, 0, 0.2f, 0.8f);
			batch.draw(skin, zone.x, zone.y, zone.width, zone.height + 3);
			batch.setColor(1, 1, 1, 1);
		}
			
		for(int i= 0; i < text.size(); i++){
			font.draw(batch, text.get(i), zone.x, (zone.y + zone.height) - (i*font.getLineHeight()));
		}
		
	}

	@Override
	public void update(float delta) { }

	public void setText(ArrayList<String> text){
		this.text= text;
		this.zone= defineZone(text, font);
	}
	
	
	@Override
	public void clickOn() {
		SimpleGUI.getInstance().turnAreaOFF(this);
	}

	@Override
	public void clickOff() {
		//SimpleGUI.getInstance().turnAreaOFF(this);
	}

	public void show(){
		SimpleGUI.getInstance().turnAreaON(this);
	}
}//end class
