/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javadb.tests.target;

/**
 *
 * @author dsb
 */
public class Common {
    String print(String... item ) {
        String result = "(";
        String next = ", ";
        for (String s : item) {
            result = result + s + next;
        }
        return result + ")";
    }
 
}
