/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package weborders.source;

import org.mercurydb.HgIndex;

/**
 *
 * @author dsb
 */
public class Common {
	
	@HgIndex
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
