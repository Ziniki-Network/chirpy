package org.zincapi.chirpy.server;

import org.zincapi.HandleRequest;
import org.zincapi.MulticastResponse;
import org.zincapi.ResourceHandler;
import org.zincapi.Response;
import org.zincapi.Zinc;

public class TopicResourceHandler implements ResourceHandler {
	private final Zinc z;
	private final String name;

	public TopicResourceHandler(Zinc z, String name) {
		this.z = z;
		this.name = name;
	}

	@Override
	public void handle(HandleRequest hr, Response response) throws Exception {
		if (hr.isSubscribe()) {
			System.out.println("Request for topic " + name);
			MulticastResponse r = z.getMulticastResponse(name);
			r.attachResponse(response);
		} else if (hr.isCreate()) {
			System.out.println("Publishing to topic " + name);
			MulticastResponse r = z.getMulticastResponse(name);
			r.send(hr.getPayload());
		} else
			System.out.println("Cannot handle request " + hr);
//		response.send(new JSONObject("{\"topic\":{\"name\":\"baseball\"}}"));
//		response.send(new JSONObject("{\"topic\":{\"name\":\"football\"}}"));
	}

}
