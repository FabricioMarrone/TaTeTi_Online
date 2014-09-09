package shuken.TaTeTi.Transitions;

import shuken.Engine.Basic.ShukenScreen;
import shuken.Engine.Resources.ResourceManager;
import shuken.TaTeTi.Config;
import shuken.TaTeTi.Renderable;
import shuken.TaTeTi.TaTeTi;
import shuken.TaTeTi.Updateable;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * This class manage the transition effects between screens.
 * 
 * Life cycle: 
 * start() ---> stop(), if destiny != null ---> change screen
 * [......running....]finished
 * 
 * @author F.Marrone
 *
 */
public class Transition implements Updateable, Renderable{

	private boolean running, finished;
	
	private ShukenScreen destiny;
	private boolean automaticallyChangeScreen;
	
	private float transitionDuration;
	private float elapsedTime;
	
	
	/**
	 * Creates a new transition to the destiny screen. When the transition finish, the "change screen process" will be called automatically.
	 * @param duration (in seconds)
	 * @param destiny Can be null. In that case, the transition renders the effect but no change screen is made.
	 */
	public Transition(float duration, ShukenScreen destiny){
		this.running= false;
		this.finished= false;
		this.setDuration(duration);
		this.destiny= destiny;
		this.automaticallyChangeScreen= true;
	}
	
	
	@Override
	public void render(SpriteBatch batch, ShapeRenderer shapeRender) {
		ResourceManager.fonts.defaultFont.draw(batch, "Transition running " + elapsedTime + "/" + transitionDuration, 10, 300);
	}

	@Override
	public final void update(float delta) {
		if(!this.isRunning()) return;
		
		if(!Config.TRANSITIONS_ON) this.stop();
		
		elapsedTime+= delta;
		if(elapsedTime > transitionDuration){
			this.stop();
		}
		this.internalUpdate(delta);
	}

	/**
	 * This method is for sub-classes who wants to implement his own logic. Its called automatically by the base class.
	 * @param delta
	 */
	protected void internalUpdate(float delta){
		//Sub-class might override this method to implement his own logic
	}
	
	/**
	 * Starts the transition. It will run until reaches the transition time limit or stop() method is called.
	 */
	public void start(){
		this.running= true;
		this.finished= false;
	}
	
	/**
	 * Clears the transition (it make it stop and reset his state and times)
	 */
	public void clear(){
		this.running= false;
		this.finished= false;
		this.elapsedTime= 0;
	}
	
	/**
	 * Stops the transition. This will be called automatically after the transition time occurs. If automaticallyChangeScreen is active and there is
	 * a valid destiny, this method will call executeScreenChange().
	 */
	public void stop(){
		this.running= false;
		this.finished= true;
		this.elapsedTime= 0;
		if(automaticallyChangeScreen) executeScreenChange();
	}
	
	public void executeScreenChange(){
		if(destiny != null) {
			TaTeTi.getInstance().setScreen(destiny);
		}
	}
	
	/**
	 * Returns true if the transition is happening now.
	 */
	public boolean isRunning(){
		return running;
	}
	
	/**
	 * Returns true if the transition went from start to stop at least once. If you cast start() again, this state (finished) will be lost until
	 * stop() is called again.
	 * You can clear the "finish" state without starting the transition with the clear() method.
	 */
	public boolean isFinish(){
		return finished;
	}
	
	public float getDuration(){
		return transitionDuration;
	}
	
	/**
	 * Sets the duration of the transition (in seconds).
	 */
	public void setDuration(float duration){
		this.transitionDuration= duration;
	}
	
	public void setAutomaticallyChangeScreen(boolean b){
		this.automaticallyChangeScreen= b;
	}
	
	public void setDestiny(ShukenScreen screen){
		this.destiny= screen;
	}
}//end class
