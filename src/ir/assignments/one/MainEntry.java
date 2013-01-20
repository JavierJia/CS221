package ir.assignments.one;

import ir.assignments.one.b.WordFrequencyCounter;
import ir.assignments.one.c.TwoGramFrequencyCounter;
import ir.assignments.one.d.PalindromeFrequencyCounter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class MainEntry {
	
	public class MyOptions {
	    public boolean RUN_WORD_FREQENT;
	    public boolean RUN_BIGRAM;
	    public boolean RUN_PALINDROME;
	    public String path_of_file;
	}
	
	public static void Help(Options options){
		HelpFormatter formatter = new HelpFormatter();
	    formatter.printHelp( "textprocess [options] <inputfile>", options );
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Options options = new Options();
		options.addOption("A", false, "print all below");
		options.addOption("f", false, "print the word count of the input file");
		options.addOption("b", false, "print the bigram count of the input file");
		options.addOption("p", false, "print the palindrome of the input file");
		
		CommandLineParser parser = new GnuParser();
		if (args.length < 1){
	    	Help(options);
		    return ;
		}
	    try {
	        // parse the command line arguments
	        CommandLine line = parser.parse( options, args );
	        if ( line.getArgs() == null || line.getArgs().length < 1){
	        	System.err.println("need input file");
	        	Help(options);
	        	return ;
	        }
	        String[] file = line.getArgs();
	        if ( line.hasOption("f") || line.hasOption("A")){
	        	System.out.println("Word Count:");
	        	WordFrequencyCounter.main(file);
	        }
	        if ( line.hasOption("b") || line.hasOption("A")){
	        	System.out.println("Bigram Count:");
	        	TwoGramFrequencyCounter.main(file);
	        }
	        if ( line.hasOption("p") || line.hasOption("A")){
	        	System.out.println("Palindrome Count:");
	        	PalindromeFrequencyCounter.main(file);
	        }
	    }
	    catch( ParseException exp ) {
	        System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
	    	Help(options);
		    return;
	    }
	    
	}

}
