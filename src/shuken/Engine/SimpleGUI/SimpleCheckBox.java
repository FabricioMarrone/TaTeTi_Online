package shuken.Engine.SimpleGUI;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class SimpleCheckBox extends ClickableArea{

	private boolean checked;
	private Texture texChecked, texUnchecked;
	
	public SimpleCheckBox(float posX, float posY, float width, float height, Texture checked, Texture unChecked) {
		super(new Rectangle(posX, posY, width, height));
		
		this.checked= false;
		this.texChecked= checked;
		this.texUnchecked= unChecked;
	}

	@Override
	public void render(SpriteBatch batch) {
		if(isChecked()){
			batch.draw(texChecked, zone.x, zone.y, zone.width, zone.height);
		}else{
			batch.draw(texUnchecked, zone.x, zone.y, zone.width, zone.height);
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