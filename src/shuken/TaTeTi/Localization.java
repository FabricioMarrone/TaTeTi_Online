package shuken.TaTeTi;

import javax.swing.JOptionPane;

public class Localization {
	
	public static enum Languages{
		EN, ES
	}
	private static Languages language;
	
	//**********************************//
	public static String SePerdioComServer1;
	public static String SePerdioComServer2;
	public static String ServerNoEncontrado1;
	public static String ServerNoEncontrado2;
	public static String ServerNoEncontrado3;
	public static String ServerNoEncontrado4;
	//-Login screen
	public static String Ingresar;
	public static String CrearCuenta;
	public static String ActualizarEstado;
	public static String Usuario_dp;
	public static String Contraseña_dp;
	public static String RecordarNombreUsuario;
	public static String MusicaON;
	public static String EstadoDelServidor_dp;
	public static String ErrorMsg_CamposIncompletos;
	//-Create account screen
	public static String Crear;
	public static String Aceptar;
	public static String Cancelar;
	public static String ConfirmarContraseña_dp;
	public static String Cuenta;
	public static String Creada;
	public static String Exitosamente;
	public static String ErrorMSg_DebesCompletarTodosLosCampos;
	public static String ErrorMsg_NombreUsuarioDebeTenerCantCaracateres;
	public static String ErrorMsg_ContraseñasDebenCoincidir;
	public static String ErrorMsg_ContraseñasDebenCantCaracteres;
	//-Mainmenu
	public static String InvitarAjugar;
	public static String CerrarSesion;
	public static String Rechazar;
	public static String ElUsuario;
	public static String PerdioComunicacion;
	public static String SeleccioneEl;
	public static String Jugador;
	public static String ConElQueDeseajugar;
	public static String ObienIngrese;
	public static String Nombre;
	public static String DelJugadorCon;
	public static String DeseaJugar;
	public static String MejoresPuntajes;
	public static String JugadoresEnLinea;
	public static String NoHayJugadoresEnLinea;
	public static String QuiereJugarConVos;
	public static String EsperandoAque;
	public static String RespondaAsuSolicitud;
	public static String DemoroEnResponder;
	public static String RechazoOfertaDejugar;
	public static String CanceloSolicitud;
	public static String NoPuedesInvATiMismo;
	//-gameplay
	public static String VolverAlMenu;
	public static String Rendirse;
	public static String Juege;
	public static String SuTurno;
	public static String Espere;
	public static String PartidaFinalizada;
	public static String Empate;
	public static String Ganaste;
	public static String Perdiste;
	public static String ElContrincanteSeRindio;
	public static String TeHasRendido;
	public static String FallaEnPartida1;
	public static String FallaEnPartida2;
	//**********************************//
	
	public static Languages getCurrentLanguage(){
		return language;
	}
	
	private static void loadEN(){
		language= Languages.EN;
		
		SePerdioComServer1= "Communication with the server is lost.";
		SePerdioComServer2= "            (Click to hide message)";
		ServerNoEncontrado1= "Server not found. One of the following errors may have occurred:";
		ServerNoEncontrado2= "-Server is down.";
		ServerNoEncontrado3= "-The server IP address is incorrect.";
		ServerNoEncontrado4= "                                        (Click to hide message)";
		
		Ingresar= "Login";
		CrearCuenta= "Register";
		ActualizarEstado= "Update state";
		Usuario_dp= "     USER: ";
		Contraseña_dp= "  PASSWORD: ";
		RecordarNombreUsuario= "            Remember username";
		MusicaON= "  Music ON";
		EstadoDelServidor_dp= "Server state:";
		ErrorMsg_CamposIncompletos= "Incomplete fields.";
		
		Crear= "Create";
		Aceptar= "OK";
		Cancelar= "Cancel";
		ConfirmarContraseña_dp= "       CONFIRM PASSWORD: ";
		Cuenta= "Account";
		Creada= "created";
		Exitosamente= "successfully";
		ErrorMSg_DebesCompletarTodosLosCampos= "You must complete all the fields to create an account.";
		ErrorMsg_NombreUsuarioDebeTenerCantCaracateres= "Username must contain at least 4 characters.";
		ErrorMsg_ContraseñasDebenCoincidir= "Passwords must be equals.";
		ErrorMsg_ContraseñasDebenCantCaracteres= "Password must contain at least 6 characters.";
		
		InvitarAjugar= "Invite to play";
		CerrarSesion= "Sign off";
		Rechazar= "Refuse";
		ElUsuario= "The user ";
		PerdioComunicacion= " lost communication with server.";
		SeleccioneEl= "Select the";
		Jugador= "player";
		ConElQueDeseajugar= "you want to play.";
		ObienIngrese= "Or enter the ";
		Nombre= "name";
		DelJugadorCon= "of the player";
		DeseaJugar= "you want to play: ";
		MejoresPuntajes= "Best High Scores";
		JugadoresEnLinea= "Players Online";
		NoHayJugadoresEnLinea= "No players available";
		QuiereJugarConVos= "¡Wants to play with you!";
		EsperandoAque= "Waiting for ";
		RespondaAsuSolicitud= "...";
		DemoroEnResponder= " takes too long to answer.";
		RechazoOfertaDejugar= " refuses your solicitude to play.";
		CanceloSolicitud= " canceled the solicitude.";
		NoPuedesInvATiMismo= "You can't invite yourself.";
		
		VolverAlMenu= "Back to menu";
		Rendirse= "Surrender";
		Juege= "PLAY";
		SuTurno= "YOUR TURN";
		Espere= "WAIT";
		PartidaFinalizada= "MATCH END";
		Empate= "DRAW";
		Ganaste= "YOU WON";
		Perdiste= "YOU LOSE";
		ElContrincanteSeRindio= "The opponent SURRENDERED";
		TeHasRendido= "YOU SURRENDERED";
		FallaEnPartida1= "Has occurred a failure that caused an abrupt finish of the match.";
		FallaEnPartida2= "It will not be taken into account in the statistics.";
	}//end load EN
	
	private static void loadES(){
		language= Languages.ES;
		
		SePerdioComServer1= "Se perdió la comunicación con el servidor.";
		SePerdioComServer2= "            (Click para quitar mensaje)";
		ServerNoEncontrado1= "No se encuentra el servidor. Uno de los siguientes errores pudo haber ocurrido:";
		ServerNoEncontrado2= "-El servidor está caido.";
		ServerNoEncontrado3= "-La direccón IP del servidor es incorrecta.";
		ServerNoEncontrado4= "                                        (Click para quitar mensaje)";
		
		Ingresar= "Ingresar";
		CrearCuenta= "Crear cuenta";
		ActualizarEstado= "Actualizar estado";
		Usuario_dp= "USUARIO: ";
		Contraseña_dp= "CONTRASEÑA: ";
		RecordarNombreUsuario= "Recordar nombre de usuario";
		MusicaON= "Musica ON";
		EstadoDelServidor_dp= "Estado del servidor:";
		ErrorMsg_CamposIncompletos= "Campos incompletos.";
		
		Crear= "Crear";
		Aceptar= "Aceptar";
		Cancelar= "Cancelar";
		ConfirmarContraseña_dp= "CONFIRMAR CONTRASEÑA: ";
		Cuenta= "Cuenta";
		Creada= "creada";
		Exitosamente= "exitosamente";
		ErrorMSg_DebesCompletarTodosLosCampos= "Debes completar todos los campos para crearte una cuenta.";
		ErrorMsg_NombreUsuarioDebeTenerCantCaracateres= "El nombre de usuario debe contener como mínimo 4 caracteres.";
		ErrorMsg_ContraseñasDebenCoincidir= "Las contraseñas deben coincidir.";
		ErrorMsg_ContraseñasDebenCantCaracteres= "La contraseña debe contener como mínimo 6 caracteres.";
		
		InvitarAjugar= "Invitar a Jugar";
		CerrarSesion= "Cerrar Sesion";
		Rechazar= "Rechazar";
		ElUsuario= "El usuario ";
		PerdioComunicacion= " perdió comunicación con el servidor.";
		SeleccioneEl= "Seleccione el";
		Jugador= "jugador";
		ConElQueDeseajugar= "con el que desea jugar.";
		ObienIngrese= "O bien ingrese el";
		Nombre= "nombre";
		DelJugadorCon= "del jugador con";
		DeseaJugar= "el que desea jugar: ";
		MejoresPuntajes= "Mejores puntajes";
		JugadoresEnLinea= "Jugadores en línea";
		NoHayJugadoresEnLinea= "No hay jugadores disponibles";
		QuiereJugarConVos= "¡Quiere jugar con vos!";
		EsperandoAque= "Esperando a que ";
		RespondaAsuSolicitud= " responda a su solicitud...";
		DemoroEnResponder= " se ha demorado demasiado en responder.";
		RechazoOfertaDejugar= " ha rechazado tu oferta de jugar.";
		CanceloSolicitud= " ha cancelado la solicitud.";
		NoPuedesInvATiMismo= "No puedes invitarte a ti mismo.";
		
		VolverAlMenu= "Volver al menu";
		Rendirse= "Rendirse";
		Juege= "JUEGE";
		SuTurno= "SU TURNO";
		Espere= "ESPERE";
		PartidaFinalizada= "PARTIDA FINALIZADA";
		Empate= "EMPATE";
		Ganaste= "GANASTE";
		Perdiste= "PERDISTE";
		ElContrincanteSeRindio= "El contrincante se ha RENDIDO";
		TeHasRendido= "TE HAS RENDIDO";
		FallaEnPartida1= "Ha ocurrido una falla que provocó la finalizacion abrupta de la partida.";
		FallaEnPartida2= "La misma no será tenida en cuenta para las estadisticas.";
	}//end load ES
	
	public static void loadLanguage(String language){
		if(language.compareToIgnoreCase("EN")== 0) {
			loadEN();
			return;
		}
		if(language.compareToIgnoreCase("ES")== 0) {
			loadES();
			return;
		}
		
		JOptionPane.showMessageDialog(null, "Invalid language \"" + language + "\".\n Only EN (English) and ES (Español) are supported.");
		TaTeTi.app.exit();
	}//end load language
}//end class
