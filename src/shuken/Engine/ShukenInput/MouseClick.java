package shuken.Engine.ShukenInput;


/**
 * Representa un click de un mouse, guardando el boton (int de 0..2) y la posición del click. 
 */
public class MouseClick {

	/** Coordenada X del click */
	public int x;
	/** Coordenada Y del click */
	public int y;
	
	//public int realX; 	//coordenadas de la pantalla (utilizadas por la GUI)
	//public int realY;
	
	public boolean clicked;
	public int clickCount;
	public boolean pressed;
	public boolean released;
	
	
	//Constructor...
	public MouseClick(){
		this.x= 0;
		this.y= 0;
		//this.realX= 0;
		//this.realY= 0;
		this.clicked= false;
		this.pressed= false;
		this.released= false;
		this.clickCount= 0;
	}
	
	/**
	 * Guarda las coordenadas de un click.
	 * @param x
	 * @param y
	 */
	public void setCoords(int x, int y){
		this.x= x;
		this.y= y;
	}
	
	/**
	 * Limpia todo el click.
	 */
	public void cleanAll(){
		this.clicked= false;
		this.pressed= false;
		this.released= false;
		this.clickCount= 0;
		this.x= 0;
		this.y= 0;
		//this.realX= 0;
		//this.realY= 0;
	}
	

	public void cleanClick(){
		this.clicked= false;
		this.clickCount= 0;
	}
}
