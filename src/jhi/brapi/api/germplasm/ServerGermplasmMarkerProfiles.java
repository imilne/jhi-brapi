package jhi.brapi.api.germplasm;

import jhi.brapi.api.*;

import org.restlet.resource.*;

/**
 * Given an id, returns the markerprofile ids which relate to the germplasm indicated by id.
 */
public class ServerGermplasmMarkerProfiles extends BaseBrapiServerResource
{
	private GermplasmDAO germplasmDAO = new GermplasmDAO();

	private String id;

	@Override
	public void doInit()
	{
		super.doInit();
		this.id = getRequestAttributes().get("id").toString();
	}

	@Get("json")
	public BrapiBaseResource<BrapiGermplasmMarkerProfiles> getJson()
	{
		return germplasmDAO.getMarkerProfilesFor(getContext(), id, currentPage, pageSize);
	}
}