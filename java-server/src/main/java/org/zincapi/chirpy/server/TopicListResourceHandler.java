package org.zincapi.chirpy.server;

import java.util.Map.Entry;

import org.codehaus.jettison.json.JSONObject;
import org.zincapi.HandleRequest;
import org.zincapi.MakeRequest;
import org.zincapi.MulticastResponse;
import org.zincapi.Requestor;
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
				String uri = hr.getConnectionURI();
				for (Entry<String, Requestor> sRqr : repo.federatedNodes.entrySet()) {
					if (uri == null || !uri.equals(sRqr.getKey())) {
						System.out.println("Need to forward " + hr + " from " + uri + " to " + sRqr.getKey());
						MakeRequest create = sRqr.getValue().create("topics", null);
						create.setPayload(hr.getPayload());
						create.send();
					}
				}
			} else
				throw new RuntimeException("Cannot handle " + hr);
		}
	}
}
