package org.zincapi.chirpy.client;

import org.codehaus.jettison.json.JSONObject;
import org.zincapi.MakeRequest;

public class TopicHandler extends ChirpyHandler {

	@Override
	public void response(MakeRequest req, JSONObject payload) {
		System.out.println(payload);
		makeReady();
	}

}
