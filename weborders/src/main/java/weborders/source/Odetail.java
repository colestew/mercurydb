/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package weborders.source;

import org.mercurydb.HgIndex;

/**
 *
 * @author dsb
 */
public class Odetail extends Common {
	@HgIndex
    public int ono;
    public Part pno;
    public int qty;
    
    private Odetail() {}
    
    public Odetail( Order o, Part p, int q){
        ono = o.ono;
        pno = p;
        qty = q;
    }
    
    public String toString() {
        return print(""+ono, ""+pno.pno, ""+qty);
    }
    
}
