package se.metasolutions.recruit.resources;

import se.metasolutions.recruit.RestApplication;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;

public class StatusResource extends BaseResource {

	private final static Logger log = LoggerFactory.getLogger(StatusResource.class);

	@Get("json")
	public Representation getJvmStatus() throws JSONException {
		JSONObject result = new JSONObject();
		result.put("version", RestApplication.getVersion());
		result.put("totalMemory", Runtime.getRuntime().totalMemory());
		result.put("freeMemory", Runtime.getRuntime().freeMemory());
		result.put("maxMemory", Runtime.getRuntime().maxMemory());
		result.put("availableProcessors", Runtime.getRuntime().availableProcessors());
		result.put("totalCommittedMemory", getTotalCommittedMemory());
		result.put("committedHeap", ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getCommitted());
		result.put("totalUsedMemory", getTotalUsedMemory());
		result.put("usedHeap", ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed());
		return new JsonRepresentation(result);
	}

	long getTotalCommittedMemory() {
		return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getCommitted() +
				ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getCommitted();
	}

	long getTotalUsedMemory() {
		return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() +
				ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getUsed();
	}

}
