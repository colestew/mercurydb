/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package weborders.source;

/**
 * @author dsb
 */
@SuppressWarnings("unused")
public class Zipcode extends Common {
    public int zip;
    public String city;

    private Zipcode() {
    }

    public Zipcode(int z, String c) {
        zip = z;
        city = c;
    }

    public String toString() {
        return print("" + zip, city);
    }
}
