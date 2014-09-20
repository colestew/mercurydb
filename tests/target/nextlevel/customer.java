/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tests.target.nextlevel;

import javadb.Index;
import tests.target.Common;
import tests.target.zipcode;

/**
 *
 * @author dsb
 */
public class customer extends Common {
    public int cno;
    @Index
    public String cname;
    @Index
    public String street;
    public zipcode zip;
    public String phone;
    
    private customer() {}
    
    public void setPhone(String x) {
    	this.phone = x;
    }
    
    public customer( int c, String n, String s, zipcode z, String p) {
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
