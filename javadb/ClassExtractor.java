package javadb;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.common.collect.Sets;

public class ClassExtractor {
    @Index
    public final Class c;
    
    // mustache formatter data
    public List<FieldData> fields;
    public String packageName;
    
    // powerset for fancy queries
    public List<QueryData> queries;

    public ClassExtractor(Class c) throws IOException {
        this.c = c;
        this.fields = new ArrayList<>();
        this.queries = new ArrayList<>();
        populateFieldsList();
        populateQueriesList(queries, fields);
    }
    
    private void populateFieldsList() {
        for (Field f : c.getFields()) {
            String type = f.getType().getName();
            String fieldName = f.getName();
            boolean hasIndex = hasIndexAnnotation(f);
            boolean isFinal = Modifier.isFinal(f.getModifiers());
            fields.add(new FieldData(type, fieldName, hasIndex, isFinal));
        }
    }
    
    private static void populateQueriesList(List<QueryData> queries, List<FieldData> fields) {
    	Set<FieldData> fieldSet = new HashSet<>(fields);
    	Set<Set<FieldData>> powerset = Sets.powerSet(fieldSet);
    	for (Set<FieldData> querySet : powerset) {
    		// TODO: don't calculate all sets and allow 5 to be a runtime parameter
    		if (querySet.size() > 1 && querySet.size() <= 5) {
    			queries.add(new QueryData(querySet));
    		}
    	}
    }
    
    @SuppressWarnings("unused")
    private static class QueryData {
    	TreeSet<FieldData> qFields;
    	
    	QueryData(Collection<FieldData> fields) {
    		this.qFields = new TreeSet<FieldData>(fields);
    	}
    	
    	String prototype() {
    		String result = "";
    		for (FieldData fd : qFields) {
    			result += fd.type() + " " + fd.name + ", ";
    		}
    		return result.substring(0, result.length()-2);
    	}
    	
    	boolean hasIndex() {
    		for (FieldData fd : qFields) {
    			if (fd.hasIndex) return true;
    		}
    		return false;
    	}
    }
    
    @SuppressWarnings("unused")
    private static class FieldData implements Comparable<FieldData> {
        String _type;
        String name;
        boolean hasIndex;
        boolean isFinal;
        
        FieldData(String type, String fieldName, boolean hasIndex, boolean isFinal) {
        	this._type = type;
            this.name = fieldName;
            this.hasIndex = hasIndex;
            this.isFinal = isFinal;
        }
        
        FieldData(String type, String fieldName, boolean hasIndex) {
            this(type, fieldName, hasIndex, false);
        }
        
        FieldData(String type, String fieldName) {
            this(type,fieldName,false);
        }
        
        String type() {
            switch (_type) {
            case "boolean": return "Boolean";
            case "byte": return "Byte";
            case "char": return "Character";
            case "short": return "Short";
            case "int": return "Integer";
            case "long": return "Long";
            case "float": return "Float";
            case "double": return "Double";
            default: return _type;
            }
        }
        
        String CCname() {
        	return Utils.upperFirst(name);
        }

		@Override
		public int compareTo(FieldData o) {
			return name.compareTo(o.name);
		}
    }
    
    public String fullSourceClass() {
        return c.getName();
    }
    
    public String sourceClass() {
        return c.getSimpleName();
    }

    public void extract(File outFile) throws IOException {
        outFile.getParentFile().mkdirs();
        PrintWriter w = new PrintWriter(new BufferedWriter(new FileWriter(outFile)));
        packageName = outFile.getParent().replace(File.separatorChar, '.');
        extract(w);
        w.close();
    }

    public void extract(PrintWriter w) throws IOException {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache template = mf.compile("resources/template.java.mustache");
        Writer execute = template.execute(w, this);
        execute.flush();
    }

    private boolean hasIndexAnnotation(Field f) {
        return f.getAnnotation(javadb.Index.class) != null;
    }
}
