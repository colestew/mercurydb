/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package weborders.source;

import org.mercurydb.HgIndex;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dsb
 */
@SuppressWarnings("unused")
public class Odetail extends Common {
    @HgIndex
    public int ono;
    public List<Part> pnos = new ArrayList<Part>();
    public int qty;

    private Odetail() {
    }

    public Odetail(Order o, Part p, int q) {
        ono = o.ono;
        pnos.add(p);
        qty = q;
    }

    public String toString() {
        return print("" + ono, "" + pnos.get(0).pno, "" + qty);
    }
}
