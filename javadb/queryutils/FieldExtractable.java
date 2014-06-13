package javadb.queryutils;

/**
 * This class defines two accessors for data
 * in a JoinStream. Note that JoinStream implements
 * JoinField. It provides a way for JoinStreams to
 * ask other JoinStreams how to extract a join key given
 * an instance of their class owner.
 * 
 * @author colestewart
 *
 */
public interface FieldExtractable {
	/**
	 * Returns the name of the class owner. Note
	 * that the parameter to extractJoinKey should
	 * always be of this type. Ex. For a JoinStream
	 * from ATable, this should return A.class
	 * @return a Class
	 */
	public Class<?> getContainerClass();
	
	/**
	 * Extracts a join key value from an instance of the
	 * type returned by getClassOwner()
	 * @param o
	 * @return
	 */
	public Object extractField(Object o);
	
	
	/**
	 * @return true if field is indexed, false otherwise
	 */
	public boolean isIndexed();
}
