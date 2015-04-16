/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package weborders.source;

import org.mercurydb.annotations.HgIndexStyle;
import org.mercurydb.annotations.HgUpdate;
import org.mercurydb.annotations.HgValue;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dsb
 */
@SuppressWarnings("unused")
public class Odetail extends Common {
    private int ono;
    private List<Part> pnos = new ArrayList<>();
    private int qty;

    // TODO add multiple part constructor
    public Odetail(Order o, Part p, int q) {
        ono = o.getOno();
        pnos.add(p);
        qty = q;
    }

    @HgValue(value = "ono", index = HgIndexStyle.ORDERED)
    public int getOno() {
        return ono;
    }

    @HgValue(value = "pnos", index = HgIndexStyle.UNORDERED)
    public List<Part> getPnos() {
        return pnos;
    }

    @HgUpdate("pnos")
    public void addPart(Part p) {
        pnos.add(p);
    }

    @HgValue("qty")
    public int getQuantity() {
        return qty;
    }

    public String toString() {
        return print("" + ono, "" + pnos.get(0).getPno(), "" + qty);
    }
}
