package org.zincapi.chirpy.server;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.zincapi.MakeRequest;
import org.zincapi.Requestor;
import org.zincapi.ResponseHandler;

public class FederatedTopicHandler implements ResponseHandler {
	private final Requestor rqr;
	private final Repository repo;

	public FederatedTopicHandler(Requestor rqr, Repository repo) {
		this.rqr = rqr;
		this.repo = repo;
	}

	@Override
	public void response(MakeRequest req, JSONObject payload) {
		try {
			String topic = payload.getJSONObject("topic").getString("name");
			TopicResourceHandler handler = repo.addTopic(topic);
			System.out.println(topic + " " + handler);
			if (handler != null)
				rqr.subscribe("topic/"+topic, handler).send();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
