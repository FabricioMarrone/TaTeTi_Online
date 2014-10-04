package shuken.Engine.SimpleGUI;

import shuken.Engine.Resources.ResourceManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class TimeLabel extends ClickableArea{

	
	/** Fuente con la que se grafica el label. */
	protected BitmapFont font;
	
	protected TextureRegion skin;
	
	protected float labelLength;
	
	protected float alpha;
	protected float visibleTime;
	private float elapsedTime;
	
	public TimeLabel(String label, float posX, float posY, BitmapFont font, float visibleTime) {
		this(label, posX, posY, font, visibleTime, null);
	}
	public TimeLabel(String label, float posX, float posY, BitmapFont font, float visibleTime, TextureRegion skin) {
		super(new Rectangle(posX, posY, 5, 5));

		this.setLabel(label, font);
		this.skin= skin;
		
		this.visibleTime= visibleTime;
		alpha= 1;
		elapsedTime= 0;
		
		consumeClick= false;
	}

	@Override
	public void render(SpriteBatch batch) {
		
		if(skin != null) {
			batch.setColor(1, 1, 1, alpha);
			batch.draw(skin, zone.x, zone.y, zone.width, zone.height);
			batch.setColor(1, 1, 1, 1);
		}
		
		font.setColor(1, 1, 1, alpha);
		font.draw(batch, label, zone.x + (zone.width - labelLength)/2, zone.y + font.getXHeight() + zone.height/2);
		font.setColor(1, 1, 1, 1);
		
	}

	@Override
	public void update(float delta) {
		elapsedTime+= delta;
		if(elapsedTime > visibleTime * 0.35f) {
			//alpha-= 0.008f;
			alpha-= (0.04f/visibleTime);
			if(alpha < 0) alpha= 0;
		}
		
		if(elapsedTime > visibleTime){
			reset();
			SimpleGUI.getInstance().turnAreaOFF(this);
		}
	}

	
	@Override
	public void setLabel(String lbl){
		this.setLabel(lbl, font);
	}
	
	public void setLabel(String label, BitmapFont font){
		this.label= label;
		this.font= font;
		labelLength= ResourceManager.fonts.calculateTextWidth(font, label);
		zone.set(zone.x, zone.y, labelLength + 20, font.getLineHeight() + 10);
		
		//Lo centramos en x
		zone.setX((Gdx.graphics.getWidth()/2) - (zone.width/2));
	}
	
	
	public void setVisibleTime(float time){
		visibleTime= time;
	}
	
	public void reset(){
		elapsedTime= 0;
		alpha= 1;
	}
}//fin clase
