package searchengine.indri;

public class IndriResult 
{
	private String _docId;
	private Double _score;
	private Integer _queryId;
	private String _text;
	private Integer _resultNumber;
	private String _runName;
	
	public IndriResult(String docId, Double score, Integer queryId, Integer resultNumber, String runName) 
	{
		_docId = docId;
		_score = score;
		_queryId = queryId;
		_resultNumber = resultNumber;
		_runName = runName;
		_text = null;
	}

	public void setText(String text) {
		_text = text;
	}
	
	public String getText() {
		return _text;
	}

	public String getDocId() {
		return _docId;
	}

	public Double getScore() {
		return _score;
	}

	public Integer getQueryId() {
		return _queryId;
	}

	public Integer getResultNumber() {
		return _resultNumber;
	}

	public String getRunName() {
		return _runName;
	}
}
