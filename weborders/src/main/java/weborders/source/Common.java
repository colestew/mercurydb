/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package weborders.source;

import org.mercurydb.annotations.HgValue;

/**
 * @author dsb
 */
public class Common {
    private int commonId;
    private static int nextId;

    // don't need a value for common
    public Common() {
        commonId = nextId++;
    }

    @HgValue("commonId")
    public int getCommon() {
        return commonId;
    }

    protected String print(String... item) {
        String result = "(";
        String next = ", ";
        for (String s : item) {
            result = result + s + next;
        }
        return result + ")";
    }
}
