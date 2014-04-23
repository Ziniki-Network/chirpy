package org.zincapi.chirpy.server;

import java.util.Set;
import java.util.TreeSet;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.zincapi.MulticastResponse;
import org.zincapi.Zinc;

public class Repository {
	final Set<String> topics = new TreeSet<String>();
	private final Zinc z;
	private final MulticastResponse mc;

	public Repository(Zinc z) {
		this.z = z;
		mc = z.getMulticastResponse("topics");
	}

	public void addTopic(String topic) throws JSONException {
		if (topics.contains(topic))
			return;
		
		topics.add(topic);
		z.handleResource("topic/" + topic, new TopicResourceHandler(z, topic));
		mc.send(new JSONObject("{\"topic\":{\"name\":\"" + topic + "\"}}"));
	}
}
