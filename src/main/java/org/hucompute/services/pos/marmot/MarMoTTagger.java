package org.hucompute.services.pos.marmot;

import static org.apache.uima.fit.util.JCasUtil.select;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.parameter.ComponentParameters;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import marmot.morph.MorphTagger;
import marmot.morph.Sentence;
import marmot.morph.Word;

public class MarMoTTagger extends JCasAnnotator_ImplBase {
	private MorphTagger tagger; 

	/**
	 * Location from which the model is read.
	 */
	public static final String PARAM_MODEL_LOCATION = ComponentParameters.PARAM_MODEL_LOCATION;
	@ConfigurationParameter(name = PARAM_MODEL_LOCATION, mandatory = false)
	protected String modelLocation;


	@Override
	public void initialize(UimaContext aContext)
			throws ResourceInitializationException {
		// TODO Auto-generated method stub
		super.initialize(aContext);


		try {
			tagger = loadFromStream(new FileInputStream(new File(modelLocation)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	public <E extends Serializable> E loadFromStream(InputStream istream) {
		try {
			ObjectInputStream stream = new ObjectInputStream(new GZIPInputStream(istream));

			Object object = stream.readObject();
			stream.close();

			if (object == null) {
				throw new RuntimeException("Object couldn't be deserialized: ");
			}

			E new_object;

			try {
				new_object = (E) object;
			} catch (ClassCastException e) {
				throw new RuntimeException(
						"Does not seem to be of right type a: " );
			}
			return new_object;
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		for (de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence sentence : select(aJCas, de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence.class)) {
			List<Word> words = new ArrayList<>();
			List<Token>tokens =  JCasUtil.selectCovered(Token.class, sentence);
			for (Token token : tokens) {
				words.add(new Word(token.getCoveredText()));
			}
			List<List<String>> tags = tagger.tag(new Sentence(words));
			IobDecoderModified decoder = new IobDecoderModified(aJCas.getCas());
			String[]pos_tags = new String[tags.size()];
			for (int i = 0; i < tags.size(); i++) {
				pos_tags[i]=tags.get(i).get(0);
			}
			decoder.decode( tokens, pos_tags);
		}
	}		
}
