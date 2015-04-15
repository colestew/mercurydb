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
public class Part extends Common {
    private int pno;
    private String pname;
    private int qoh;
    private double price;
    private int olevel;

    public Part(int p, String n, int q, double c, int o) {
        pno = p;
        pname = n;
        qoh = q;
        price = c;
        olevel = o;
    }

    @HgValue("pno")
    public int getPno() {
        return pno;
    }

    @HgValue("pname")
    public String getPname() {
        return pname;
    }

    @HgValue("qoh")
    public int getQoh() {
        return qoh;
    }

    @HgValue("price")
    public double getPrice() {
        return price;
    }

    @HgValue("olevel")
    public int getOlevel() {
        return olevel;
    }

    public String toString() {
        return print("" + pno, pname, "" + qoh, "" + price, "" + olevel);
    }
}
