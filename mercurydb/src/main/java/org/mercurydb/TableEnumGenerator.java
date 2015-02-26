package org.mercurydb;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

public class TableEnumGenerator {
    
    public List<EnumTemplateVar> classes;

    public String packageName;
    
    public TableEnumGenerator(Collection<Class<?>> classes)
    		throws IOException {
        this.classes = classes.stream()
        		.map(c -> new EnumTemplateVar(c)).collect(Collectors.toList());
    }
    
    private class EnumTemplateVar {
    	private Class<?> c;
    	
    	public EnumTemplateVar(Class<?> c) {
    		this.c = c;
    	}
    	
    	public String templateString() {
    		return toEnumVar(c.getSimpleName()) + 
    				(classes.indexOf(this) == classes.size()-1 ? ";" : ",");
    	}
    }
    
    public String toEnumVar(String src) {
    	StringBuilder sb = new StringBuilder();
    	if (src.length() > 0) {
    		sb.append(Character.toUpperCase(src.charAt(0)));
    	}
    	for (int i = 1; i < src.length(); ++i) {
    		if (Character.isUpperCase(src.charAt(i))) {
    			sb.append('_' + src.charAt(i));
    		} else {
    			sb.append(Character.toUpperCase(src.charAt(i)));
    		}
    	}
    	return sb.toString();
    }

    public void extract(String outPath, String packageName) throws IOException {
    	File outFile = new File(outPath);
        outFile.getParentFile().mkdirs();
        PrintWriter w = new PrintWriter(new BufferedWriter(new FileWriter(outFile)));
        this.packageName = packageName;
        extract(w);
        w.close();
    }

    private void extract(PrintWriter w) throws IOException {
        MustacheFactory mf = new DefaultMustacheFactory();
        String templateName = "enum_template.java.mustache";
        InputStream templateStream = getClass().getResourceAsStream('/'+ templateName);
        Mustache template = mf.compile(new InputStreamReader(templateStream), templateName);
        Writer execute = template.execute(w, this);
        execute.flush();
    }
}
