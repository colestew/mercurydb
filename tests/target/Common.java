/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tests.target;

import javadb.Index;

/**
 *
 * @author dsb
 */
public class Common {
	
	@Index
	public int commonId;
	
    protected String print(String... item ) {
        String result = "(";
        String next = ", ";
        for (String s : item) {
            result = result + s + next;
        }
        return result + ")";
    }
 
}
