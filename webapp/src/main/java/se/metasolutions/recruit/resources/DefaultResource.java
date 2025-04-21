package se.metasolutions.recruit.resources;

import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;


/**
 * Fallback if no other REST resource matches. Returns 404.
 */
public class DefaultResource extends BaseResource {

	@Get
	public Representation represent() throws ResourceException {
		getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND);
		String msg = "You made a request against the REST API. There is no resource at this URL.";
		return new StringRepresentation(msg);
	}

}
