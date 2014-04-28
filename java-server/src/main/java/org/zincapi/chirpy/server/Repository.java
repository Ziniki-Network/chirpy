package org.zincapi.chirpy.server;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.zincapi.MulticastResponse;
import org.zincapi.Requestor;
import org.zincapi.Zinc;

public class Repository {
	final Set<String> topics = new TreeSet<String>();
	final Map<String, Requestor> federatedNodes = new TreeMap<String, Requestor>();
	private final Zinc z;
	private final MulticastResponse mc;

	public Repository(Zinc z) {
		this.z = z;
		mc = z.getMulticastResponse("topics");
	}

	public TopicResourceHandler addTopic(String topic) throws JSONException {
		if (topics.contains(topic))
			return null;
		
		topics.add(topic);
		TopicResourceHandler handler = new TopicResourceHandler(z, this, topic);
		z.handleResource("topic/" + topic, handler);
		mc.send(new JSONObject("{\"topic\":{\"name\":\"" + topic + "\"}}"));
		return handler;
	}
}
