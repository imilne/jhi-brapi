package jhi.brapi.api.core.studies;

import java.sql.Date;
import java.time.*;
import java.util.*;

import jhi.brapi.api.core.locations.*;

public class Study
{
	private boolean active;
	private Map<String, Object> additionalInfo;
	private String commonCropName = "";
	private List<BrapiContact> contacts;
	private String culturalPractices = "";
	private List<BrapiDataLinks> dataLinks;
	private String documentationURL = "";
	private String endDate;
	private List<EnvironmentParameter> environmentParameters;
	private ExperimentalDesign experimentalDesign;
	private List<ExternalReference> externalReferences;
	private GrowthFacility growthFacility;
	private LastUpdate lastUpdate;
	private String license = "";
	private String locationDbId = "";
	private String locationName = "";
	private List<ObservationLevel> observationLevels;
	private String observationUnitDescritpion = "";
	private List<String> seasons;
	private String startDate;
	private String studyCode = "";
	private String studyDbId = "";
	private String studyDescription = "";
	private String studyName = "";
	private String studyType = "";
	private String trialDbID = "";
	private String trialName = "";

	public Study()
	{
	}

	public boolean isActive()
	{
		return active;
	}

	public void setActive(boolean active)
	{
		this.active = active;
	}

	public Map<String, Object> getAdditionalInfo()
	{
		return additionalInfo;
	}

	public void setAdditionalInfo(Map<String, Object> additionalInfo)
	{
		this.additionalInfo = additionalInfo;
	}

	public String getCommonCropName()
	{
		return commonCropName;
	}

	public void setCommonCropName(String commonCropName)
	{
		this.commonCropName = commonCropName;
	}

	public List<BrapiContact> getContacts()
	{
		return contacts;
	}

	public void setContacts(List<BrapiContact> contacts)
	{
		this.contacts = contacts;
	}

	public String getCulturalPractices()
	{
		return culturalPractices;
	}

	public void setCulturalPractices(String culturalPractices)
	{
		this.culturalPractices = culturalPractices;
	}

	public List<BrapiDataLinks> getDataLinks()
	{
		return dataLinks;
	}

	public void setDataLinks(List<BrapiDataLinks> dataLinks)
	{
		this.dataLinks = dataLinks;
	}

	public String getDocumentationURL()
	{
		return documentationURL;
	}

	public void setDocumentationURL(String documentationURL)
	{
		this.documentationURL = documentationURL;
	}

	public String getEndDate()
	{
		return endDate;
	}

	public void setEndDate(String endDate)
	{
		this.endDate = endDate;
	}

	public List<EnvironmentParameter> getEnvironmentParameters()
	{
		return environmentParameters;
	}

	public void setEnvironmentParameters(List<EnvironmentParameter> environmentParameters)
	{
		this.environmentParameters = environmentParameters;
	}

	public ExperimentalDesign getExperimentalDesign()
	{
		return experimentalDesign;
	}

	public void setExperimentalDesign(ExperimentalDesign experimentalDesign)
	{
		this.experimentalDesign = experimentalDesign;
	}

	public GrowthFacility getGrowthFacility()
	{
		return growthFacility;
	}

	public void setGrowthFacility(GrowthFacility growthFacility)
	{
		this.growthFacility = growthFacility;
	}

	public LastUpdate getLastUpdate()
	{
		return lastUpdate;
	}

	public void setLastUpdate(LastUpdate lastUpdate)
	{
		this.lastUpdate = lastUpdate;
	}

	public String getLicense()
	{
		return license;
	}

	public void setLicense(String license)
	{
		this.license = license;
	}

	public String getObservationUnitDescritpion()
	{
		return observationUnitDescritpion;
	}

	public void setObservationUnitDescritpion(String observationUnitDescritpion)
	{
		this.observationUnitDescritpion = observationUnitDescritpion;
	}

	public List<String> getSeasons()
	{
		return seasons;
	}

	public void setSeasons(List<String> seasons)
	{
		this.seasons = seasons;
	}

	public String getStartDate()
	{
		return startDate;
	}

	public void setStartDate(String startDate)
	{
		this.startDate = startDate;
	}

	public String getStudyDescription()
	{
		return studyDescription;
	}

	public void setStudyDescription(String studyDescription)
	{
		this.studyDescription = studyDescription;
	}

	public String getStudyName()
	{
		return studyName;
	}

	public void setStudyName(String studyName)
	{
		this.studyName = studyName;
	}

	public String getStudyType()
	{
		return studyType;
	}

	public void setStudyType(String studyType)
	{
		this.studyType = studyType;
	}

	public String getTrialDbID()
	{
		return trialDbID;
	}

	public void setTrialDbID(String trialDbID)
	{
		this.trialDbID = trialDbID;
	}

	public String getTrialName()
	{
		return trialName;
	}

	public void setTrialName(String trialName)
	{
		this.trialName = trialName;
	}

	public String getStudyDbId()
	{
		return studyDbId;
	}

	public void setStudyDbId(String studyDbId)
	{
		this.studyDbId = studyDbId;
	}

	public List<ExternalReference> getExternalReferences()
	{
		return externalReferences;
	}

	public void setExternalReferences(List<ExternalReference> externalReferences)
	{
		this.externalReferences = externalReferences;
	}

	public String getLocationDbId()
	{
		return locationDbId;
	}

	public void setLocationDbId(String locationDbId)
	{
		this.locationDbId = locationDbId;
	}

	public String getLocationName()
	{
		return locationName;
	}

	public void setLocationName(String locationName)
	{
		this.locationName = locationName;
	}

	public List<ObservationLevel> getObservationLevels()
	{
		return observationLevels;
	}

	public void setObservationLevels(List<ObservationLevel> observationLevels)
	{
		this.observationLevels = observationLevels;
	}

	public String getStudyCode()
	{
		return studyCode;
	}

	public void setStudyCode(String studyCode)
	{
		this.studyCode = studyCode;
	}
}