/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javadb.tests.target;

/**
 *
 * @author dsb
 */
public class employee extends Common {
    public int eno;
    public String ename;
    public zipcode zip;
    public String date;
    
    private employee() {}
    
    public employee(int e, String n, zipcode z, String d) {
        eno = e;
        ename = n;
        zip = z;
        date = d;
    }
    
    public String toString() {
        return print(""+eno,ename,""+zip.zip,date);
    }
    
}
