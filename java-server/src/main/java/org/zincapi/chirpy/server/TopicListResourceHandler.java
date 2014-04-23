package org.zincapi.chirpy.server;

import org.codehaus.jettison.json.JSONObject;
import org.zincapi.HandleRequest;
import org.zincapi.ResourceHandler;
import org.zincapi.Response;
import org.zincapi.Zinc;

public class TopicListResourceHandler implements ResourceHandler {

	public TopicListResourceHandler(Zinc z) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handle(HandleRequest hr, Response response) throws Exception {
		System.out.println("Hello");
		response.send(new JSONObject("{\"topic\":{\"name\":\"baseball\"}}"));
		response.send(new JSONObject("{\"topic\":{\"name\":\"football\"}}"));
	}

}
