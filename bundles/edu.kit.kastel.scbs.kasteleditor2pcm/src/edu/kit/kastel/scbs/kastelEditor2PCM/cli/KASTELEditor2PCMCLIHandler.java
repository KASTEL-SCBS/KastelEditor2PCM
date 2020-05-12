package edu.kit.kastel.scbs.kastelEditor2PCM.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class KASTELEditor2PCMCLIHandler {

	
	public static final String EDITOR_FILE_PATH = "filePath";
	public static final String GENERATION_FOLDER_PATH = "generationPath";
	public static final String ADVERSARIES = "enableAdversaries";
	public static final String GENERATE_JOANA_FLOWS = "enableJoanaFlows";
	public static final String SIMPLE_JOANA_FLOW_IDs = "simplejoanaflowids";
	public static final String CONSIDER_ASSETS = "considerAssets";
	
	

	public Options getOptions() {
		Options options = new Options();
		Option editorFilePathOption = Option.builder(EDITOR_FILE_PATH).argName("file").hasArg().desc("File that contains the Editor model").required().build();
		Option generationPathOption = Option.builder(GENERATION_FOLDER_PATH).argName("generationPath").desc("The Path the Models shall be generated into").hasArg().required().build();
		Option useAdversariesOption = Option.builder(ADVERSARIES).argName("considerAdversaries").desc("Determines wether adversaries should be considered in input or output").hasArg(false).build();
		Option generateJOANAFlowModelOption = Option.builder(GENERATE_JOANA_FLOWS).argName("generateJOANAFlowModel").desc("Specifies that a JOANA Flow Model should be generated").hasArg(false).build();
		Option simpleJoanaFlowIdsOption = Option.builder(SIMPLE_JOANA_FLOW_IDs).argName("SimpleJoanaFlowIDs").desc("Signals if incremental ids should be used for the generated Joana Flows").hasArg(false).build();
		Option considerAssets = Option.builder(CONSIDER_ASSETS).argName(CONSIDER_ASSETS).desc("Signals wether assets should be considered in generating interfaces and operations signatures for BBM and functional requirements").hasArg(false).build();
		
		options.addOption(editorFilePathOption);
		options.addOption(generationPathOption);
		options.addOption(useAdversariesOption);
		options.addOption(generateJOANAFlowModelOption);
		options.addOption(simpleJoanaFlowIdsOption);
		options.addOption(considerAssets);
		
		return options;
	}
	
	public KASTELEditor2PCMCommandLineParameters interrogateCommandLine(CommandLine cmd) {
		
		if(cmd == null) {
			return new KASTELEditor2PCMCommandLineParameters();
		}
		
		String filePath = "";
		String generationPath = "";
		
		if(cmd.hasOption(EDITOR_FILE_PATH)) {
			filePath = cmd.getOptionValue(EDITOR_FILE_PATH);
		} else {
			System.out.println("Editor File Path not provided");
			return new KASTELEditor2PCMCommandLineParameters();
		}
		
		if(cmd.hasOption(GENERATION_FOLDER_PATH)) {
			generationPath = cmd.getOptionValue(GENERATION_FOLDER_PATH);
		} else {
			System.out.println("Generation Path not provided");
			return new KASTELEditor2PCMCommandLineParameters();
		}
		
		boolean useAdversaries = cmd.hasOption(ADVERSARIES);
		boolean generateJOANAFlowModel = cmd.hasOption(GENERATE_JOANA_FLOWS);
		boolean useSimpleJoanaflowIds = cmd.hasOption(SIMPLE_JOANA_FLOW_IDs);
		boolean considerAssets = cmd.hasOption(CONSIDER_ASSETS);
		
		
		return new KASTELEditor2PCMCommandLineParameters(filePath, generationPath, useAdversaries, generateJOANAFlowModel, useSimpleJoanaflowIds, considerAssets);
	}
	
}
