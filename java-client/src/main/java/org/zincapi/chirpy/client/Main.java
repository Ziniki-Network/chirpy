package org.zincapi.chirpy.client;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.URI;

import org.codehaus.jettison.json.JSONObject;
import org.zincapi.MakeRequest;
import org.zincapi.Requestor;
import org.zincapi.Zinc;

public class Main {

	public static void main(String[] args) {
		try {
			Zinc zinc = new Zinc();
			Requestor requestor = zinc.newRequestor(URI.create("http://localhost:8380/chirpy"));
			TopicListHandler listHandler = new TopicListHandler();
			TopicHandler msgHandler = new TopicHandler();
			LineNumberReader lnr = new LineNumberReader(new InputStreamReader(System.in));
			String currTopic = null;
			String userName = "??";
			MakeRequest subscription = null;
			for (;;) {
				System.out.print("> ");
				System.out.flush();
				String s = lnr.readLine();
				if (s == null)
					break;
				
				String[] t = s.trim().split(" ");
				if (t.length == 0 || t[0].equals("#"))
					continue;
				MakeRequest cmd;
				if (t[0].equals("topics")) {
					cmd = requestor.subscribe("topics", listHandler);
				} else if (t[0].equals("newtopic")) {
					if (t.length != 2) {
						System.out.println("newtopic <topic>");
						continue;
					}
					cmd = requestor.create("topics", null);
					cmd.setPayload(new JSONObject("{\"topic\":\""+ t[1] + "\"}"));
				} else if (t[0].equals("topic")) {
					if (t.length != 2) {
						System.out.println("topic <topic>");
						continue;
					}
					if (subscription != null)
						subscription.unsubscribe();
					currTopic = "topic/"+t[1];
					subscription = cmd = requestor.subscribe(currTopic, msgHandler);
				} else if (t[0].equals("user")) {
					if (t.length != 2) {
						System.out.println("user <handle>");
					} else
						userName = t[1];
					continue;
				} else if (t[0].equals("quit")) {
					break;
				} else {
					if (currTopic == null) {
						System.out.println("cannot send message without setting a topic");
						continue;
					}
					cmd = requestor.create(currTopic, null);
					cmd.setPayload(new JSONObject("{\"message\":{\"text\":\"" + userName + ": " + s + "\"}}"));
				}
				cmd.send();
				if (cmd.getHandler() != null && cmd.getHandler() instanceof ChirpyHandler) {
					(((ChirpyHandler) cmd.getHandler())).waitForReady();
				}
			}
			zinc.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}

}
