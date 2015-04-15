/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package weborders.source;

import org.mercurydb.annotations.HgValue;

/**
 * @author dsb
 */
@SuppressWarnings("unused")
public class Zipcode extends Common {
    private int zip;
    private String city;

    public Zipcode(int z, String c) {
        zip = z;
        city = c;
    }

    @HgValue("zip")
    public int getZip() {
        return zip;
    }

    @HgValue("city")
    public String getCity() {
        return city;
    }

    public String toString() {
        return print("" + zip, city);
    }
}
