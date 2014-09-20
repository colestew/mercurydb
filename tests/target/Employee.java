/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tests.target;

/**
 *
 * @author dsb
 */
public class Employee extends Common {
    public int eno;
    public String ename;
    public zipcode zip;
    public String date;
    
    private Employee() {}
    
    public Employee(int e, String n, zipcode z, String d) {
        eno = e;
        ename = n;
        zip = z;
        date = d;
    }
    
    public String toString() {
        return print(""+eno,ename,""+zip.zip,date);
    }
    
}
