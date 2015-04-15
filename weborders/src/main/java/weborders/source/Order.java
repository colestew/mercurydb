/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package weborders.source;

import org.mercurydb.annotations.HgIndexStyle;
import org.mercurydb.annotations.HgUpdate;
import org.mercurydb.annotations.HgValue;

/**
 * @author dsb
 */
@SuppressWarnings("unused")
public class Order extends Common {
    private int ono;
    private Customer cno;
    private Employee eno;

    private String received;
    private String shipped;

    @HgValue(value = "ono", index = HgIndexStyle.ORDERED)
    public int getOno() {
        return ono;
    }

    @HgUpdate("ono")
    public void setOno(int o) {
        if (o < 0) {
            this.ono = -1; // detect bad order numbers with a single error number
            return;
        }

        this.ono = o;
    }

    public Order(int o, Customer c, Employee e, String r, String s) {
        ono = o;
        cno = c;
        eno = e;
        received = r;
        shipped = s;
    }

    public String toString() {
        return print("" + ono, "" + cno.getCno(), "" + eno.getEno(), received, shipped);
    }
}
