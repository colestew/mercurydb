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
public class Customer extends Common {
    private int cno;
    private String cname;
    private String street;
    private Zipcode zip;
    private String phone;

    public Customer(int c, String n, String s, Zipcode z, String p) {
        cno = c;
        cname = n;
        street = s;
        zip = z;
        phone = p;
    }

    public String toString() {
        return print("" + cno, cname, street, "" + zip.getZip(), phone);
    }

    @HgValue(value="cno")
    public int getCno() {
        return cno;
    }

    @HgValue(value="cname", index=HgIndexStyle.ORDERED)
    public String getName() {
        return cname;
    }

    @HgValue(value="street", index=HgIndexStyle.ORDERED)
    public String getStreet() {
        return street;
    }

    @HgValue(value="zipcode")
    public Zipcode getZipcode() {
        return zip;
    }

    @HgValue(value="phone")
    public String getPhone() {
        return phone;
    }

    @HgUpdate("cname")
    public void setName(String name) {
        this.cname = name;
    }
}
