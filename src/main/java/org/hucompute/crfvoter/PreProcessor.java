package org.hucompute.crfvoter;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.hucompute.segmenter.BreakIteratorSegmenterModified;
import org.hucompute.services.pos.marmot.MarMoTTagger;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.io.conll.Conll2002Writer;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordNamedEntityRecognizer;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordSegmenter;

/**
 * Generates a file for CRFSuite given a Goldstandard file, and predicted files from 
 * other Taggers
 * @author ahemati
 *
 */
public class PreProcessor {
	public static String outputPath = System.getProperty("java.io.tmpdir")+"/output";
	public static String crfvoterInputFile = System.getProperty("java.io.tmpdir")+"/crfvoterFile";


	public void analyse1(String inputText,String outputPath) throws UIMAException{
		JCas cas = JCasFactory.createText(inputText, "en");

		DocumentMetaData.create(cas).setDocumentTitle("stanford");
		DocumentMetaData.get(cas).setDocumentId("stanford");

		AggregateBuilder builder = new AggregateBuilder();
		builder.add(createEngineDescription(StanfordSegmenter.class));
		builder.add(createEngineDescription(BreakIteratorSegmenterModified.class,BreakIteratorSegmenterModified.PARAM_SPLIT_AT_MINUS,true));
		builder.add(createEngineDescription(StanfordNamedEntityRecognizer.class,
				StanfordNamedEntityRecognizer.PARAM_MODEL_LOCATION,"models/stanford/model.ser.gz"
				));
		builder.add(createEngineDescription(Conll2002Writer.class,
				Conll2002Writer.PARAM_TARGET_LOCATION, outputPath,
				Conll2002Writer.PARAM_OVERWRITE,true));
		SimplePipeline.runPipeline(cas,builder.createAggregate());
	}

	public void analyse2(String inputText, String outputPath) throws UIMAException{
		JCas cas = JCasFactory.createText(inputText, "en");
		DocumentMetaData.create(cas).setDocumentTitle("marmot");
		DocumentMetaData.get(cas).setDocumentId("marmot");
		AggregateBuilder builder = new AggregateBuilder();
		builder.add(createEngineDescription(StanfordSegmenter.class));
		builder.add(createEngineDescription(BreakIteratorSegmenterModified.class,BreakIteratorSegmenterModified.PARAM_SPLIT_AT_MINUS,true));
		builder.add(createEngineDescription(MarMoTTagger.class,
				MarMoTTagger.PARAM_MODEL_LOCATION,"models/marmot/marmot.model"
				));
		builder.add(createEngineDescription(Conll2002Writer.class,
				Conll2002Writer.PARAM_TARGET_LOCATION, outputPath,
				Conll2002Writer.PARAM_OVERWRITE,true));
		SimplePipeline.runPipeline(cas,builder.createAggregate());
	}

	public void constructInput(Path predFilesPath, Path outputFile) throws IOException{
		//Test
		List<List<String>> preds = new ArrayList<List<String>>();
		List<String> predsFileNames = new ArrayList<String>();

		File[]predFiles = predFilesPath.toFile().listFiles();

		for (File file : predFiles) {
			preds.add(FileUtils.readLines(file));
			predsFileNames.add(file.getName().split("\\.")[0]);
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < preds.get(0).size()-1; i++) {
			String word = preds.get(0).get(i).split(" ")[0];
			if(word.length() > 0){
				sb.append("O").append("\t").append("w[0]="+word).append("\t");


				if(i>=1)
					sb.append("w[-1]="+preds.get(0).get(i-1).split(" ")[0]).append("\t");
				//			if(i>=2)
				//				sb.append("w[-2]="+preds.get(0).get(i-2).split(" ")[0]).append("\t");

				if(i<preds.get(0).size()-1)
					sb.append("w[1]="+preds.get(0).get(i+1).split(" ")[0]).append("\t");
				//			if(i<preds.get(0).size()-2)
				//				sb.append("w[2]="+preds.get(0).get(i+2).split(" ")[0]).append("\t");

				for (int j = 0; j < preds.size(); j++) {
					sb.append(predsFileNames.get(j)).append("=").append(getPos(preds.get(j).get(i))).append("\t");
				}
			}
			sb.append("\n");
		}
		//				
		FileUtils.write(outputFile.toFile(), sb.toString());
	}

	public void preprocess(String input) throws IOException, UIMAException{
		PreProcessor prepro = new PreProcessor();
		prepro.analyse1(input,outputPath);
		prepro.analyse2(input,outputPath);
		prepro.constructInput(Paths.get(outputPath), Paths.get(crfvoterInputFile));
	}

	public static String getPos(String input){
		return input.split(" ")[input.split(" ").length-1];
	}

	/**
	 * Generates Ngrams for a String
	 * @param input 
	 * @param gramsize
	 * @return
	 */
	public static String getNgram(String input, int gramsize){


		StringBuilder sb = new StringBuilder();

		if(input.length()< gramsize)
			return "#"+input+"#";


		for (int i = 0; i <= input.length()-gramsize; i++) {
			if(i == 0)
				sb.append("#"+input.substring(0,gramsize-1)).append("\t");

			sb.append(input.substring(i,i+gramsize)).append("\t");

			if(i == input.length()-gramsize)
				sb.append(input.substring(i+1,i+gramsize)+"#").append("\t");

		}

		return sb.toString();
	}

}
