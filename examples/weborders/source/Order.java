/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package examples.weborders.source;

import org.mercurydb.Index;

/**
 *
 * @author dsb
 */
public class Order extends Common {
	@Index
    public int ono;
	
    public customer cno;
	
    public Employee eno;
	
    public String received;
    public String shipped;
    
    public void setOno(int o) {
    	if (o < 0) {
    		this.ono = -1; // detect bad order numbers with a single error number
    		return;
    	} 
		this.ono = o;
		// implicit return 
	}
    
    private Order() {} 
    
    public Order(int o, customer c, Employee e, String r, String s){
        ono = o;
        cno = c;
        eno = e; 
        received = r;
        shipped = s;
    }
    
    public String toString() {
        return print(""+ono, ""+cno.cno, ""+eno.eno, received, shipped);
    }
    
}