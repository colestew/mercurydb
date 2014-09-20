/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tests.target;

import javadb.Index;

/**
 *
 * @author dsb
 */
public class odetail extends Common {
	@Index
    public int ono;
    public part pno;
    public int qty;
    
    private odetail() {}
    
    public odetail( order o, part p, int q){
        ono = o.ono;
        pno = p;
        qty = q;
    }
    
    public String toString() {
        return print(""+ono, ""+pno.pno, ""+qty);
    }
    
}
