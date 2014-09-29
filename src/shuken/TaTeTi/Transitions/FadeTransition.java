package shuken.TaTeTi.Transitions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import shuken.Engine.Basic.ShukenScreen;
import shuken.Engine.Resources.ResourceManager;

/**
 * Simple Fade In/Out Transition.
 * @author F.Marrone
 *
 */
public class FadeTransition extends Transition{

	private float alpha;
	private boolean fadeIn;
	private float velocity;

	public FadeTransition(float duration, ShukenScreen destiny, boolean fadeIn) {
		super(duration, destiny);

		this.setFadeIn(fadeIn);
	}

	@Override
	public void render(SpriteBatch batch, ShapeRenderer shapeRender) {
		batch.setColor(1, 1, 1, alpha);
		batch.draw(ResourceManager.textures.transition, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());	
		batch.setColor(1, 1, 1, 1);
		
	}


	@Override
	protected void internalUpdate(float delta) {
		if(fadeIn){
			//FadeIn es "se aclara" (alpha= 1  --->  alpha= 0)
			alpha-= velocity * delta;
			if(alpha < 0){
				alpha= 0;
			}
		}else{
			//FadeOut es "se oscurece" (alpha= 0  --->  alpha= 1)
			alpha+= velocity * delta;
			if(alpha > 1){
				alpha= 1;
			}
		}
		
	}//end internal update

	

	@Override
	public void start() {
		super.start();
		this.setInitialAlpha();
	}


	
	
	@Override
	public void stop() {
		super.stop();
	}

	@Override
	public void setDuration(float duration) {
		super.setDuration(duration);
		this.velocity= 1/duration;
	}

	public void setFadeIn(boolean b){
		this.fadeIn= b;
		this.setInitialAlpha();
	}
	
	private void setInitialAlpha(){
		if(this.fadeIn) this.alpha= 1;
		else this.alpha= 0;
	}
	
}//end class
