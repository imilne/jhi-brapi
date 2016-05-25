package jhi.brapi.server;

import jhi.brapi.data.*;
import jhi.brapi.resource.*;

import org.restlet.resource.*;

/**
 * Queries the database for the (Genome) MapList then returns a JSON (Jackson)
 * representation of the MapList for API clients
 * to consume.
 */
public class GenomeMaps extends BaseBrapiServerResource
{
	private MapDAO mapDAO = new MapDAO();

	@Get("json")
	public BasicResource<DataResult<BrapiGenomeMap>> getJson()
	{
		return mapDAO.getAll(currentPage, pageSize);
	}
}