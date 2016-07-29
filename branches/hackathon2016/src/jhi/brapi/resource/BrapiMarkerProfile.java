package jhi.brapi.resource;

/**
 * Created by gs40939 on 30/04/2015.
 */
public class BrapiMarkerProfile
{
	private String markerprofileDbId;
	private String germplasmDbId;
	private String uniqueDisplayName;
	private String extractDbId;
	private String analysisMethod;
	private int resultCount;
	private String sampleDbId;

	public String getMarkerprofileDbId()
	{
		return markerprofileDbId;
	}

	public void setMarkerprofileDbId(String markerprofileDbId)
	{
		this.markerprofileDbId = markerprofileDbId;
	}

	public String getGermplasmDbId()
	{
		return germplasmDbId;
	}

	public void setGermplasmDbId(String germplasmDbId)
	{
		this.germplasmDbId = germplasmDbId;
	}

	public String getUniqueDisplayName()
	{
		return uniqueDisplayName;
	}

	public void setUniqueDisplayName(String uniqueDisplayName)
	{
		this.uniqueDisplayName = uniqueDisplayName;
	}

	public String getExtractDbId()
	{
		return extractDbId;
	}

	public void setExtractDbId(String extractDbId)
	{
		this.extractDbId = extractDbId;
	}

	public String getAnalysisMethod()
	{
		return analysisMethod;
	}

	public void setAnalysisMethod(String analysisMethod)
	{
		this.analysisMethod = analysisMethod;
	}

	public int getResultCount()
	{
		return resultCount;
	}

	public void setResultCount(int resultCount)
	{
		this.resultCount = resultCount;
	}

	public String getSampleDbId()
	{
		return sampleDbId;
	}

	public void setSampleDbId(String sampleDbId)
	{
		this.sampleDbId = sampleDbId;
	}

	@Override
	public String toString()
	{
		return "MarkerProfile{" +
			"markerprofileDbId=" + markerprofileDbId +
			", germplasmDbId=" + germplasmDbId +
			", extractDbId=" + extractDbId +
			", analysisMethod='" + analysisMethod + '\'' +
			", resultCount='" + resultCount + '\'' +
			'}';
	}
}
