package org.zincapi.chirpy.server;

import java.util.Set;
import java.util.TreeSet;

import org.zincapi.Zinc;

public class Repository {
	final Set<String> topics = new TreeSet<String>();
	private final Zinc z;

	public Repository(Zinc z) {
		this.z = z;
	}

	public void addTopic(String topic) {
		if (topics.contains(topic))
			return;
		
		topics.add(topic);
		z.handleResource("topic/" + topic, new TopicResourceHandler(z, topic));
	}
}
