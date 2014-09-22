package shuken.Engine.ShukenInput;


/**
 * Represents a mouse click, saving the button number (0..2) and the click position.
 * 
 * @author F. Marrone
 */
public class MouseClick {

	/** X-Coord of the click */
	public int x;
	/** Y-Coord of the click */
	public int y;
	
	public boolean clicked;
	public int clickCount;
	public boolean pressed;
	public boolean released;
	
	public MouseClick(){
		this.x= 0;
		this.y= 0;

		this.clicked= false;
		this.pressed= false;
		this.released= false;
		this.clickCount= 0;
	}
	
	public void setCoords(int x, int y){
		this.x= x;
		this.y= y;
	}
	
	public void cleanAll(){
		this.clicked= false;
		this.pressed= false;
		this.released= false;
		this.clickCount= 0;
		this.x= 0;
		this.y= 0;
	}
	
	public void cleanClick(){
		this.clicked= false;
		this.clickCount= 0;
	}
}
