package org.zincapi.chirpy.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.zincapi.HandleRequest;
import org.zincapi.MakeRequest;
import org.zincapi.MulticastResponse;
import org.zincapi.Requestor;
import org.zincapi.ResourceHandler;
import org.zincapi.Response;
import org.zincapi.ResponseHandler;
import org.zincapi.Zinc;

public class TopicResourceHandler implements ResourceHandler, ResponseHandler {
	private final Repository repo;
	private final List<JSONObject> messages = new ArrayList<JSONObject>();
	private final MulticastResponse multi;
	private String resource;

	public TopicResourceHandler(Zinc z, Repository repo, String name) {
		this.repo = repo;
		this.resource = "topic/" + name;
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
			// forward the request to federation
			String uri = hr.getConnectionURI();
			for (Entry<String, Requestor> sRqr : repo.federatedNodes.entrySet()) {
				if (uri == null || !uri.equals(sRqr.getKey())) {
					System.out.println("Need to forward " + hr + " from " + uri + " to " + sRqr.getKey());
					MakeRequest create = sRqr.getValue().create(resource, null);
					create.setPayload(hr.getPayload());
					create.send();
				}
			}
			
			// distribute to listeners
			messages.add(payload);
			multi.send(payload);
		} else
			System.out.println("Cannot handle request " + hr);
	}

	// This is used to handle subscribing to federated nodes
	@Override
	public void response(MakeRequest req, JSONObject payload) {
		try {
			System.out.println(payload);
			messages.add(payload);
			multi.send(payload);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
