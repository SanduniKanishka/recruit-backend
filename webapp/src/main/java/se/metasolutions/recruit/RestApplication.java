package se.metasolutions.recruit;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import se.metasolutions.recruit.resources.DefaultResource;
import se.metasolutions.recruit.resources.EchoResource;
import se.metasolutions.recruit.resources.StatusResource;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.engine.io.IoUtils;
import org.restlet.routing.Router;
import org.restlet.routing.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Boilerplate for a REST API.
 */
public class RestApplication extends Application {

	static Logger log = LoggerFactory.getLogger(RestApplication.class);

	public static String KEY = RestApplication.class.getCanonicalName();

	public static String NAME = "REST";

	private static String VERSION = null;

	public RestApplication(Context parentContext) throws IOException, JSONException {
		this(parentContext, null);
	}

	public RestApplication(Context parentContext, URI configURI) throws IOException, JSONException {
		super(parentContext);
		getContext().getAttributes().put(KEY, this);
	}

	@Override
	public synchronized Restlet createInboundRoot() {
		getContext().getParameters().add("useForwardedForHeader", "true");

		Router router = new Router(getContext());
		router.setDefaultMatchingMode(Template.MODE_EQUALS);

		// global scope
		router.attach("/status", StatusResource.class);
		router.attach("/echo", EchoResource.class);
		router.attach("/", DefaultResource.class);

		return router;
	}

	public static String getVersion() {
		if (VERSION == null) {
			URI versionFile = getConfigurationURI("VERSION.txt");
			try {
				log.debug("Reading version number from " + versionFile);
				VERSION = readFirstLine(versionFile.toURL());
			} catch (IOException e) {
				log.error(e.getMessage());
			}
			if (VERSION == null) {
				VERSION = new SimpleDateFormat("yyyyMMdd").format(new Date());
			}
		}
		return VERSION;
	}

        public static URI getConfigurationURI(String fileName) {
                URL resURL = Thread.currentThread().getContextClassLoader().getResource(fileName);
                try {
                        if (resURL != null) {
                                return resURL.toURI();
                        }
                } catch (URISyntaxException e) {
                        log.error(e.getMessage());
                }

                String classPath = System.getProperty("java.class.path");
                String[] pathElements = classPath.split(System.getProperty("path.separator"));
                for (String element : pathElements)     {
                        File newFile = new File(element, fileName);
                        if (newFile.exists()) {
                                return newFile.toURI();
                        }
                }
                log.error("Unable to find " + fileName + " in classpath");
                return null;
        }

	private void setLogLevel(String logLevel) {
		Level l = Level.toLevel(logLevel, Level.INFO);
		Configurator.setRootLevel(l);
		log.info("Log level set to " + l);
	}

	private static String readFirstLine(URL url) {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(url.openStream()));
			return in.readLine();
		} catch (IOException ioe) {
			log.error(ioe.getMessage());
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					log.error(e.getMessage());
				}
			}
		}
		return null;
	}

	@Override
	public synchronized void stop() throws Exception {
		log.info("Shutting down");
		super.stop();
	}

}
