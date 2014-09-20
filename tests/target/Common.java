/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tests.target;

/**
 *
 * @author dsb
 */
public class Common {
    protected String print(String... item ) {
        String result = "(";
        String next = ", ";
        for (String s : item) {
            result = result + s + next;
        }
        return result + ")";
    }
 
}
