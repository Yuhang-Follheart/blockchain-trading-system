import java.util.regex.Pattern;

public class MatchInteger {
	public static Pattern pattern = Pattern.compile("[0-9][0-9]*");
	public static boolean isInteger(String n) {
		return pattern.matcher(n).matches(); 
	}

}
