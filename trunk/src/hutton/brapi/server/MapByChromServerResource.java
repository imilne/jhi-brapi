package hutton.brapi.server;

import hutton.brapi.data.*;
import hutton.brapi.resource.*;

import com.google.inject.*;

import org.restlet.resource.*;

/**
 * Created by gs40939 on 27/05/2015.
 */
public class MapByChromServerResource extends BaseBrapiServerResource<MapDetail>
{
	@Inject
	private MapDAO mapDAO;

	private String id;
	private String chrom;

	public void doInit()
	{
		super.doInit();
		this.id = (String)getRequestAttributes().get("id");
		this.chrom = (String)getRequestAttributes().get("chrom");
	}

	@Get("json")
	public MapDetail getJson()
	{
		MapDetail mapDetail = mapDAO.getByIdAndChromosome(Integer.parseInt(id), chrom);

		if (mapDetail != null)
			return mapDetail;

		throw new ResourceException(404);
	}
}
