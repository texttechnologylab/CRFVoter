package org.hucompute.crfvoter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.uima.UIMAException;

import com.github.jcrfsuite.CrfTagger;
import com.github.jcrfsuite.util.Pair;
public class CRFVoter {


	private final static String HELP_OPTION = "help";
	private final static String INPUT_TEXT_OPTION = "input-text";
	private final static String OUTPUT_OPTION = "output";
	
	private static Options createOptionsProcessSingle() {
		final Options options = new Options();

		options.addOption(Option.builder("h")
				.longOpt(HELP_OPTION)
				.required(false)
				.hasArg(false)
				.desc("Print this help.")
				.build());

		options.addOption(Option.builder("i")
				.longOpt(INPUT_TEXT_OPTION)
				.required(true)
				.hasArg(true)
				.argName("text")
				.desc("Input text to be processed.")
				.build());

		options.addOption(Option.builder("o")
				.longOpt(OUTPUT_OPTION)
				.required(false)
				.hasArg(true)
				.argName("path")
				.desc("Output file.")
				.build());

		return options;
	}
	
	private static void printHelp(final Options options) {
		final HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("CRFVoter GPRO", "Command Line Tool for CRFVoter with GPRO models.", options, "https://github.com/texttechnologylab/CRFVoter");
	}
	
	public static void main(String...args) throws IOException, UIMAException{
		final Options options = createOptionsProcessSingle();

		if (args.length < 1) {
			printHelp(options);
			System.exit(1);
		}
		
		CommandLine commandLine = null;
		try {
			CommandLineParser cmdLineParser = new DefaultParser();
			commandLine = cmdLineParser.parse(options, args);
		} catch (ParseException parseException) {
			System.err.println("error parsing arguments: " + parseException.getMessage());
			printHelp(options);
			System.exit(1);
		}
		if (commandLine == null) {
			System.err.println("unexpected error parsing arguments");
			printHelp(options);
			System.exit(1);
		}

		if (commandLine.hasOption(HELP_OPTION)) {
			printHelp(options);
			System.exit(0);
		}
		
		
		PreProcessor prepo = new PreProcessor();
		prepo.preprocess(commandLine.getOptionValue(INPUT_TEXT_OPTION));
		String modelFile = "models/CRFVoter/crfvoter.model";
		CrfTagger crfTagger = new CrfTagger(modelFile);
		List<List<Pair<String, Double>>> tagProbLists = crfTagger.tag(PreProcessor.crfvoterInputFile);
		List<List<String>>words = getWords(PreProcessor.crfvoterInputFile);
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < tagProbLists.size(); i++) {
			List<Pair<String, Double>> list = tagProbLists.get(i);
			for (int j = 0; j < list.size(); j++) {
				Pair<String, Double> pair = list.get(j);
				sb.append(words.get(i).get(j) + "\t" +pair.getFirst()).append(System.lineSeparator());
			}
			sb.append(System.lineSeparator());
		}
		
		if(!commandLine.hasOption(OUTPUT_OPTION))
			System.out.println(sb.toString());
		else
			FileUtils.writeStringToFile(new File(commandLine.getOptionValue(OUTPUT_OPTION)), sb.toString());
		
//		Paths.get(PreProcessor.outputPath).toFile().delete();
//		Paths.get(PreProcessor.crfvoterInputFile).toFile().delete();
	}
	
	public static List<List<String>> getWords(String file) throws IOException{
		List<List<String>> words = new ArrayList<>();
		List<String>fileWords = FileUtils.readLines(new File(file));
		List<String>sentence = new ArrayList<>();
		for (String string : fileWords) {
			if(string.length()==0){
				words.add(sentence);
				sentence = new ArrayList<>();
			}else{
				sentence.add(string.split("\t")[1].replace("w[0]=", ""));
			}
		}
		words.add(sentence);
		return words;
	}
}
