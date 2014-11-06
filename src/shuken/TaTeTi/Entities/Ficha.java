package shuken.TaTeTi.Entities;

public enum Ficha {

	CRUZ,				//ordinal 0
	CIRCULO;			//ordinal 1
	
	
	public static Ficha getFicha(int ordinal){
		if(ordinal == CRUZ.ordinal()) return CRUZ;
		if(ordinal == CIRCULO.ordinal()) return CIRCULO;
		
		return null;
	}
}
