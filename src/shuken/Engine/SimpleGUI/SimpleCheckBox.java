package shuken.Engine.SimpleGUI;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class SimpleCheckBox extends ClickableArea{

	private boolean checked;
	private TextureRegion texChecked, texUnchecked;
	
	public SimpleCheckBox(float posX, float posY, float width, float height, TextureRegion checked, TextureRegion unChecked) {
		super(new Rectangle(posX, posY, width, height));
		
		this.checked= false;
		this.texChecked= checked;
		this.texUnchecked= unChecked;
	}
	
	public SimpleCheckBox(float posX, float posY, TextureRegion checked, TextureRegion unChecked) {
		super(new Rectangle(posX, posY, checked.getRegionWidth(), checked.getRegionHeight()));
		
		this.checked= false;
		this.texChecked= checked;
		this.texUnchecked= unChecked;
	}

	@Override
	public void render(SpriteBatch batch) {
		batch.draw(texUnchecked, zone.x, zone.y, zone.width, zone.height);
		
		if(isChecked()){
			batch.draw(texChecked, zone.x + 3, zone.y + 3, zone.width, zone.height);
		}
	}//end render

	@Override
	public void update(float delta) {
		//nothing
	}

	public boolean isChecked(){
		return checked;
	}
	
	public void setChecked(boolean checked){
		this.checked= checked;
	}

	@Override
	public void clickOn() {
		if(isChecked()) setChecked(false);
		else setChecked(true);
	}
	
	
}//end class
