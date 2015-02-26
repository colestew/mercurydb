package org.mercurydb;

import java.net.MalformedURLException;

import javassist.CannotCompileException;
import javassist.NotFoundException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main {

	public static void main(String[] args) 
			throws NotFoundException, CannotCompileException, MalformedURLException {

		Options options = new Options();
		Option opt = new Option("src", "source-package", true, "The source package");
		opt.setRequired(true);
		options.addOption(opt);
		opt = new Option("db", "db-package", true, "The output database package");
		opt.setRequired(true);
		options.addOption(opt);
		opt = new Option("root", "java-root", true, "The root java directory");
		opt.setRequired(true);
		options.addOption(opt);
		opt = new Option("ih", "insert-hooks", false, "Insert db hooks into package. Can specify output class directory.");
		opt.setOptionalArg(true);
		options.addOption(opt);
		opt = new Option("sx", "suffix", true, "Specify output file suffix. Default is *Tbl");
		opt.setRequired(false);

		CommandLineParser parser = new GnuParser();

		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			printHelp(options);
		}
		
		String srcPkg = cmd.getOptionValue("src");
		String dbPkg = cmd.getOptionValue("db");
		String srcDir = cmd.getOptionValue("root");
		String suffix = cmd.getOptionValue("sfx");
		
		if (srcPkg != null && dbPkg != null && srcDir != null) {
			MercuryBootstrap bs = new MercuryBootstrap(srcPkg, dbPkg, srcDir) ;
			if (suffix != null) {
				bs.setTableSuffix(suffix);
			}
			if (cmd.hasOption("ih")) {
				bs.insertBytecodeHooks();
			} else {
				bs.generateTables();
			}
		}
	}

	public static void printHelp(Options options) {
		new HelpFormatter().printHelp("org.mercurydb.Main", options);
		System.exit(1);
	}
}
