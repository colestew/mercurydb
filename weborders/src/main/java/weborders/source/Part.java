/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package weborders.source;

/**
 * @author dsb
 */
public class Part extends Common {
    public int pno;
    public String pname;
    public int qoh;
    public double price;
    public int olevel;

    private Part() {
    }

    public Part(int p, String n, int q, double c, int o) {
        pno = p;
        pname = n;
        qoh = q;
        price = c;
        olevel = o;
    }

    public String toString() {
        return print("" + pno, pname, "" + qoh, "" + price, "" + olevel);
    }
}
