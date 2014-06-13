package javadb;

public class Utils {

	public static String upperFirst(String str) {
		char upperFirst = Character.toUpperCase(str.charAt(0));
        String ret = str.length() > 1
                ? upperFirst + str.substring(1)
                : ""+upperFirst;
        return ret;
	}
}
