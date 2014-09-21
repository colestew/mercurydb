/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package examples.weborders.source;

/**
 *
 * @author dsb
 */
public class Employee extends Common {
    public int eno;
    public String ename;
    public Zipcode zip;
    public String date;
    
    private Employee() {}
    
    public Employee(int e, String n, Zipcode z, String d) {
        eno = e;
        ename = n;
        zip = z;
        date = d;
    }
    
    public String toString() {
        return print(""+eno,ename,""+zip.zip,date);
    }
    
}
