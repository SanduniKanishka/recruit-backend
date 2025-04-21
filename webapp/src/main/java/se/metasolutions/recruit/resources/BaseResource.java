package se.metasolutions.recruit.resources;

import se.metasolutions.recruit.RestApplication;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.ServerInfo;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;

/**
 * Base resource from which all other REST resources are subclassed. Handles basic functionality such as parsing of parameters.
 */
public class BaseResource extends ServerResource {

	private static ServerInfo serverInfo;

	protected MediaType format;

	protected HashMap<String,String> parameters;

	private static Logger log = LoggerFactory.getLogger(BaseResource.class);

	@Override
	public void init(Context c, Request request, Response response) {
		parameters = parseRequest(request.getResourceRef().getRemainingPart());
		super.init(c, request, response);

		if (parameters.containsKey("format")) {
			String format = parameters.get("format");
			if (format != null) {
				// workaround for URL-decoded pluses (space) in MIME-type names, e.g. ld+json
				format = format.replaceAll(" ", "+");
				this.format = new MediaType(format);
			}
		}

		// we set a custom Server header in the HTTP response
		setServerInfo(this.getServerInfo());
	}

	@Override
	protected void doRelease() {

	}

	static public HashMap<String, String> parseRequest(String request) {
		HashMap<String, String> argsAndVal = new HashMap<String, String>();

		int r = request.lastIndexOf("?");
		String req = request.substring(r + 1);
		String[] arguments = req.split("&");

		try {
			for (int i = 0; i < arguments.length; i++) {
				if (arguments[i].contains("=")) {
					String[] elements = arguments[i].split("=");
					String key = urlDecode(elements[0]).trim();
					String value = urlDecode(elements[1]).trim();
					if (key.length() > 0) {
						argsAndVal.put(key, value);
					}
				} else {
					String key = urlDecode(arguments[i]).trim();
					if (key.length() > 0) {
						argsAndVal.put(key, "");
					}
				}
			}
		} catch (IndexOutOfBoundsException e) {
			// special case!
			argsAndVal.put(req, "");
		}
		return argsAndVal;
	}

	private static String urlDecode(String input) {
		if (input != null) {
			try {
				return URLDecoder.decode(input, "UTF-8");
			} catch (UnsupportedEncodingException uee) {
				log.error(uee.getMessage());
			}
		}
		return null;
	}

	@Override
	public ServerInfo getServerInfo() {
		if (serverInfo == null) {
			ServerInfo si = super.getServerInfo();
			si.setAgent(RestApplication.NAME + "/" + RestApplication.getVersion());
			serverInfo = si;
		}
		return serverInfo;
	}

}
