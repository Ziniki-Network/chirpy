package org.zincapi.chirpy.server;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jettison.json.JSONObject;
import org.zincapi.HandleRequest;
import org.zincapi.MulticastResponse;
import org.zincapi.ResourceHandler;
import org.zincapi.Response;
import org.zincapi.Zinc;

public class TopicResourceHandler implements ResourceHandler {
	private final List<JSONObject> messages = new ArrayList<JSONObject>();
	private final MulticastResponse multi;

	public TopicResourceHandler(Zinc z, String name) {
		multi = z.getMulticastResponse(name);
	}

	@Override
	public void handle(HandleRequest hr, Response response) throws Exception {
		if (hr.isSubscribe()) {
			multi.attachResponse(response);
			for (JSONObject obj : messages)
				response.send(obj);
		} else if (hr.isCreate()) {
			JSONObject payload = hr.getPayload();
			messages.add(payload);
			multi.send(payload);
		} else
			System.out.println("Cannot handle request " + hr);
	}

}
