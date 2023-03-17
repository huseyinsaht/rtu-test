package modbustest.util;

/**
 * Handle log and console output functionality.
 * TODO Add additional methods and colors where appropriate
 * 
 * @author stefan.feilmeier
 *
 */
public class Log {

	public static final String HIGH_INTENSITY = "\u001B[1m";

	public static final String BLACK = "\u001B[30m";
	public static final String RED = "\u001B[31m";
	public static final String GREEN = "\u001B[32m";
	public static final String YELLOW = "\u001B[33m";
	public static final String CYAN = "\u001B[36m";
	public static final String WHITE = "\u001B[37m";

	public static final String BACKGROUND_BLACK = "\u001B[40m";
	public static final String BACKGROUND_RED = "\u001B[41m";
	public static final String BACKGROUND_WHITE = "\u001B[47m";
	public static final String ANSI_RESET = "\u001B[0m";
	
	/**
	 * Handle info message
	 * 
	 * @param message
	 */
	public static void info(String message) {
		System.out.println(message);
	}
	
	/**
	 * Handle error message
	 * 
	 * @param message
	 */
	public static void error(String message) {
		System.out.println(RED + "Error: " + message + ANSI_RESET);
	}
	
	/**
	 * Handle exception
	 * 
	 * @param message
	 */
	public static void exception(Exception e) {
		Log.error(e.getMessage());
		e.printStackTrace();
	}
}
