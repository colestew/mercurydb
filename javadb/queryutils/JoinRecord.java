package javadb.queryutils;

import java.util.HashMap;

/**
 * This class is essentially an alias
 * for HashMap<Class<?>, Object> currently. This
 * should make client code a bit cleaner, as well as
 * our JoinDriver class which also uses this.
 * 
 * @author colestewart
 *
 */
public class JoinRecord extends HashMap<Class<?>, Object> {
	private static final long serialVersionUID = 3954994964527336275L;
	
	private JoinRecord() {
		super();
	}
	public JoinRecord(JoinStream<?> s1, final Object o1, JoinStream<?> s2, final Object o2) {
		JoinRecord jr1 = wrapObject(s1, o1);
		JoinRecord jr2 = wrapObject(s2, o2);
		putAll(jr1);
		putAll(jr2);
	}
	
	public static JoinRecord wrapObject(JoinStream<?> s, Object o) {
		JoinRecord jr;
		if (o instanceof JoinRecord) {
			jr = (JoinRecord)o;
		} else {
			jr = new JoinRecord();
			jr.put(s.joinKey.getContainerClass(), o);
		}
		
		return jr;
	}
}
