package shuken.TaTeTi.Screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import shuken.Engine.Basic.ShukenScreen;
import shuken.Engine.Resources.ResourceManager;
import shuken.Engine.SimpleGUI.ClickableArea;
import shuken.Engine.SimpleGUI.SimpleGUI;
import shuken.TaTeTi.Localization;
import shuken.TaTeTi.TaTeTi;
import shuken.TaTeTi.Updateable;
import shuken.TaTeTi.Transitions.FadeTransition;
import shuken.TaTeTi.Transitions.Transition;

public class LoadingScreen extends ShukenScreen  implements Updateable{

	private SpriteBatch batch;
	private ShapeRenderer shapeRender;
	
	private Texture loadingTex;
	private BitmapFont font;
	
	private ArrayList<Transition> transitions;
	private Transition transitionToLogginScreen;
	
	public LoadingScreen(){
		batch= new SpriteBatch();
		shapeRender= new ShapeRenderer();
		
		//Cargamos los recursos necesarios para este screen
		loadingTex= new Texture("assets/images/loading"+Localization.getCurrentLanguage()+".png");
		font= new BitmapFont(Gdx.files.internal("assets/fonts/UIlabelsFont.fnt"), Gdx.files.internal("assets/fonts/UIlabelsFont_0.png"), false);
		font.setColor(Color.WHITE);
		
		//Comenzamos a cargar el resto de recursos que va a utilizar el juego
		ResourceManager.loadAllResources();
    	SimpleGUI.getInstance();
	}
	
	@Override
	public void postCreate() {
		transitions= new ArrayList<Transition>();
		
		transitionToLogginScreen= new FadeTransition(0.5f, TaTeTi.getInstance().loginScreen, false);
		transitions.add(transitionToLogginScreen);
	}
	
	@Override
	public void update(float delta) {
		
		if(ResourceManager.loadDone){
			if(!transitionToLogginScreen.isRunning()) transitionToLogginScreen.start();
		}
		
		//Si estamos en una transicion...
		for(int i= 0; i < transitions.size(); i++){
			if(transitions.get(i).isRunning()){
				transitions.get(i).update(delta);
				return;
			}
		}
	}//end update
	
	@Override
	public void render(float delta) {
		//Limpiamos screen...
		Gdx.gl.glClearColor(0f,0f, 0.15f,0f);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		batch.begin();
		batch.draw(loadingTex, 183, 200);
		font.draw(batch, ResourceManager.percentLoad + "%", 315, 50);
		
		//Graficamos transiciones (si hay)
		for(int i= 0; i < transitions.size(); i++){
			if(transitions.get(i).isRunning()) transitions.get(i).render(batch, shapeRender);
		}
				
		batch.end();
		
		//Realizamos update
		this.update(TaTeTi.confirmDelta(delta));
	}

	@Override
	public void simpleGUI_Event(ClickableArea area) {}

	@Override
	public void show() {}

	@Override
	public void hide() {
		//Reseteamos transiciones
		for(int i= 0; i < transitions.size(); i++){
			transitions.get(i).clear();
		}
	}
	
	@Override
	public void resize(int width, int height) {}

	@Override
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public void dispose() {
		System.out.println("Loading screen dispose...");
		batch.dispose();
		shapeRender.dispose();
		loadingTex.dispose();
		font.dispose();
	}
}//end class
