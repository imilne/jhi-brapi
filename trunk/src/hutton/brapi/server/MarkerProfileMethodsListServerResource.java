package hutton.brapi.server;

import hutton.brapi.data.*;
import hutton.brapi.resource.*;

import com.google.inject.*;

import org.restlet.resource.*;

/**
 * Returns a list of all methods associated with a marker profile
 */
public class MarkerProfileMethodsListServerResource extends BaseBrapiServerResource
{
	@Inject
	private MarkerProfileMethodsDAO markerProfileMethodsDAO;

	@Get("json")
	public MarkerProfileMethodList getJson()
	{
		MarkerProfileMethodList methodList = markerProfileMethodsDAO.getAll();

		if (methodList != null)
			return methodList;

		throw new ResourceException(404);
	}
}