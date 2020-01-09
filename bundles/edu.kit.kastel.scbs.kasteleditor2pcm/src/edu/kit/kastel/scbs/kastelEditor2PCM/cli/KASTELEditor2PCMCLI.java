package edu.kit.kastel.scbs.kastelEditor2PCM.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;



public class KASTELEditor2PCMCLI{

	KASTELEditor2PCMCLIHandler handler;
	
	public KASTELEditor2PCMCLI() {
		handler = new KASTELEditor2PCMCLIHandler();
	}

	private CommandLine parseInput(Options options, String[] args) throws ParseException {
		CommandLineParser parser = new DefaultParser();
		return parser.parse(options, args);
	}
	
	public KASTELEditor2PCMCommandLineParameters interrogateCommandLine(String[] args) {
		Options options = handler.getOptions();
		CommandLine cmd = null;
		try {
			cmd = parseInput(options, args);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return handler.interrogateCommandLine(cmd);
		
	}
}
