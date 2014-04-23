package org.zincapi.chirpy.server;

import org.codehaus.jettison.json.JSONObject;
import org.zincapi.HandleRequest;
import org.zincapi.MulticastResponse;
import org.zincapi.ResourceHandler;
import org.zincapi.Response;
import org.zincapi.Zinc;

public class TopicListResourceHandler implements ResourceHandler {
	private final Repository repo;
	private final MulticastResponse mc;

	public TopicListResourceHandler(Zinc z, Repository repo) {
		this.repo = repo;
		mc = z.getMulticastResponse("topics");
	}

	@Override
	public void handle(HandleRequest hr, Response response) throws Exception {
		synchronized (repo) {
			if (hr.isSubscribe()) {
				for (String s : repo.topics)
					response.send(new JSONObject("{\"topic\":{\"name\":\"" + s + "\"}}"));
				mc.attachResponse(response);
			} else if (hr.isCreate()) {
				repo.addTopic(hr.getPayload().getString("topic"));
			} else
				throw new RuntimeException("Cannot handle " + hr);
		}
	}
}
