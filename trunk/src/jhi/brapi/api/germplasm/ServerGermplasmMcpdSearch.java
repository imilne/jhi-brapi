package jhi.brapi.api.germplasm;

import java.util.*;

import jhi.brapi.api.*;

import org.restlet.resource.*;

/**
 * Given an id, returns the markerprofile ids which relate to the germplasm indicated by id.
 */
public class ServerGermplasmMcpdSearch extends BaseBrapiServerResource
{
	private GermplasmDAO germplasmDAO = new GermplasmDAO();

	private String pui;
	private String dbId;
	private String genus;
	private String species;
	private String name;
	private String subtaxa;
	private String panel;
	private String collection;

	@Override
	public void doInit()
	{
		super.doInit();
		this.pui = getQueryValue("germplasmPUI");
		this.dbId = getQueryValue("germplasmDbId");
		this.genus = getQueryValue("germplasmGenus");
		this.species = getQueryValue("germplasmSpecies");
		this.name = getQueryValue("germplasmName");
		this.subtaxa = getQueryValue("germplasmSubTaxa");
		this.panel = getQueryValue("panel");
		this.collection = getQueryValue("collection");
	}

	@Get("json")
	public BrapiListResource<BrapiGermplasmMcpd> getJson()
	{
		LinkedHashMap<String, String> parameters = new LinkedHashMap<>();
		addParameter(parameters, "germinatebase.general_identifier", pui);
		addParameter(parameters, "germinatebase.id", dbId);
		addParameter(parameters, "taxonomies.genus", genus);
		addParameter(parameters, "taxonomies.species", species);
		addParameter(parameters, "germinatebase.name", name);
		addParameter(parameters, "subtaxa.taxonomic_identifier", subtaxa);
		// TODO: panel, collection?

		// Provide a default for the WHERE clause which won't filter the results
		if(parameters.size() < 1)
			parameters.put("1", "1");

		return germplasmDAO.getMcpdForSearch(parameters, currentPage, pageSize);
	}

	private void addParameter(Map<String, String> map, String key, String value)
	{
		if (value != null && value.length() != 0)
			map.put(key, value);
	}

	@Post("json")
	public BrapiListResource<BrapiGermplasmMcpd> postJson(BrapiGermplasmPost params)
	{
		setFromPostBody(params);
		setParams(params);

		return getJson();
	}

	private void setFromPostBody(BrapiGermplasmPost params)
	{
		pui = params.getGermplasmPUI();
		dbId = params.getGermplasmDbId();
		genus = params.getGermplasmGenus();
		species = params.getGermplasmSpecies();
		name = params.getGermplasmName();
	}

	private void setParams(BrapiGermplasmPost params)
	{
		setPaginationParameters(params);
	}
}