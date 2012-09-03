package relationExtraction;

/* For representing a sentence that is annotated with pos tags and np chunks.*/
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import util.Log;

import edu.washington.cs.knowitall.nlp.ChunkedSentence;
import edu.washington.cs.knowitall.nlp.ChunkedSentenceIterator;
import edu.washington.cs.knowitall.nlp.ChunkedSentenceReader;

/* The class that is responsible for extraction. */
import edu.washington.cs.knowitall.extractor.ReVerbExtractor;

/* The class that is responsible for assigning a confidence score to an
 * extraction.
 */
import edu.washington.cs.knowitall.extractor.conf.ConfidenceFunction;
import edu.washington.cs.knowitall.extractor.conf.ConfidenceFunctionException;
import edu.washington.cs.knowitall.extractor.conf.ReVerbOpenNlpConfFunction;

/* A class for holding a (arg1, rel, arg2) triple. */
import edu.washington.cs.knowitall.nlp.extraction.ChunkedBinaryExtraction;
import edu.washington.cs.knowitall.normalization.BinaryExtractionNormalizer;
import edu.washington.cs.knowitall.normalization.NormalizedBinaryExtraction;
import edu.washington.cs.knowitall.util.DefaultObjects;

public class ReVerbRelExtractor 
{
	private ReVerbExtractor _extractor;
	private ConfidenceFunction _confFunc;
	private BinaryExtractionNormalizer _normalizer;

	public ReVerbRelExtractor() throws ConfidenceFunctionException, IOException {
		_extractor = new ReVerbExtractor();
		_confFunc = new ReVerbOpenNlpConfFunction();
		DefaultObjects.initializeNlpTools();
		_normalizer = new BinaryExtractionNormalizer();
	}

	private ArrayList<NormalizedBinaryExtraction> extractFromSentReader(ChunkedSentenceReader reader)
	{
		ArrayList<NormalizedBinaryExtraction> extractions = new ArrayList<NormalizedBinaryExtraction>();
		ChunkedSentenceIterator sentenceIt = reader.iterator();

		try{
			while (sentenceIt.hasNext()) 
			{
				// get the next chunked sentence
				ChunkedSentence sent = sentenceIt.next();
				Iterable<ChunkedBinaryExtraction> chunkedExtractions = _extractor.extract(sent);

				for (ChunkedBinaryExtraction extr : chunkedExtractions) {
					// run the confidence function
					double conf = getConf(extr);
					extractions.add(_normalizer.normalize(extr, conf));
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			Log.getInstance().add(new Exception(Thread.currentThread().getName()+" "+e.getMessage()));
		}

		return extractions;
	}

	private double getConf(ChunkedBinaryExtraction extr) 
	{
		try {
			return _confFunc.getConf(extr);
		} catch (ConfidenceFunctionException e) {
			System.err.println("Could not compute confidence for " + extr
					+ ": " + e.getMessage());
			e.printStackTrace();
			Log.getInstance().add(e);
			return 0;
		}
	}

	public ArrayList<NormalizedBinaryExtraction> extract(File file)
	{
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			ChunkedSentenceReader reader = DefaultObjects.getDefaultSentenceReader(in);
			return extractFromSentReader(reader);
		} catch (IOException e) {
			e.printStackTrace();
			Log.getInstance().add(e);
		}

		return null;		
	}

	public ArrayList<NormalizedBinaryExtraction> extractFromHTML(File file)
	{
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			ChunkedSentenceReader reader = DefaultObjects.getDefaultSentenceReaderHtml(in);
			ArrayList<NormalizedBinaryExtraction> result = extractFromSentReader(reader);
			in.close();
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			Log.getInstance().add(e);
		}

		return null;
	}

	public ArrayList<NormalizedBinaryExtraction> extract(String text)
	{
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(text.getBytes("UTF-8"))));
			ArrayList<NormalizedBinaryExtraction> result = extractFromSentReader(DefaultObjects.getDefaultSentenceReader(in));
			in.close();
			return result;		
		} catch (ConfidenceFunctionException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public ArrayList<NormalizedBinaryExtraction> extractFromHTML(String text)
	{
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(text.getBytes("UTF-8"))));
			ChunkedSentenceReader reader = DefaultObjects.getDefaultSentenceReaderHtml(in);
			return extractFromSentReader(reader);
		} catch (IOException e) {
			e.printStackTrace();
			Log.getInstance().add(e);
		}

		return null;	
	}

	public static void main(String[] args) {
		try {
			ReVerbRelExtractor reverb = new ReVerbRelExtractor();
			//			ArrayList<NormalizedBinaryExtraction> extractions = reverb.extract(new File("/home/ludo/Desktop/TAC/KBP/corpus/Source_Text_Corpus/data/2010/nw/nyt_eng/20101011/NYT_ENG_20101011.0001.BLENDER.sgm"));
			ArrayList<NormalizedBinaryExtraction> extractions = reverb.extract("Mrs. Clinton tried to beat him . "); 
			for(NormalizedBinaryExtraction extraction : extractions)
				System.out.println(extraction.getConf()+" "+extraction.getArgument1Norm()+" | "+extraction.getRelationNorm()+" | "+extraction.getArgument2Norm());
		} catch (ConfidenceFunctionException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
