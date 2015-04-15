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
public class Employee extends Common {
    private int eno;
    private String ename;
    private Zipcode zip;
    private String date;

    public Employee(int e, String n, Zipcode z, String d) {
        eno = e;
        ename = n;
        zip = z;
        date = d;
    }

    @HgValue("eno")
    public int getEno() {
        return eno;
    }

    @HgValue("name")
    public String getName() {
        return ename;
    }

    @HgValue("zipcode")
    public Zipcode getZipcode() {
        return zip;
    }

    @HgValue("date")
    public String getDate() {
        return date;
    }

    public String toString() {
        return print("" + eno, ename, "" + zip.zip, date);
    }
}
