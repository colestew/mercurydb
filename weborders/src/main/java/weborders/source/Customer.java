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
public class Customer extends Common {
    public int cno;
    @HgIndex
    public String cname;
    @HgIndex
    public String street;
    public Zipcode zip;
    public String phone;
    
    private Customer() {}
    
    public void setPhone(String x) {
    	this.phone = x;
    }
    
    public Customer( int c, String n, String s, Zipcode z, String p) {
        cno = c;
        cname = n;
        street = s;
        zip = z;
        phone = p;
    }
    
    public String toString() {
        return print(""+cno, cname, street, ""+zip.zip, phone );
    }
}
