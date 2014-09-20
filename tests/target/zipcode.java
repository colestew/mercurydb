/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tests.target;

/**
 *
 * @author dsb
 */
public class zipcode extends Common{
    public int zip;
    public String city;
    
    private zipcode() {}
    
    public zipcode(int z, String c) {
        zip = z;
        city = c;
    }
    
    public String toString() {
        return print(""+zip,city);
    }
}
