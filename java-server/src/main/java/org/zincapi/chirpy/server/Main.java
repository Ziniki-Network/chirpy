package org.zincapi.chirpy.server;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.zincapi.ConnectionHandler;
import org.zincapi.HandleRequest;
import org.zincapi.MakeRequest;
import org.zincapi.Requestor;
import org.zincapi.Zinc;
import org.zincapi.inline.server.ZincServlet;

import com.gmmapowell.http.GPServletDefn;
import com.gmmapowell.http.InlineServer;
import com.gmmapowell.http.NotifyOnServerReady;
import com.gmmapowell.serialization.Endpoint;

public class Main {

	public static void main(String[] args) {
		int port = 8380;
		List<String> statics = new ArrayList<String>();
		final Set<String> federation = new TreeSet<String>();
		for (int i=0;i<args.length;i++) {
			if (args[i].equals("--port")) {
				if (i+1 < args.length) {
					port = Integer.parseInt(args[++i]);
				} else {
					System.err.println("--port requires a port");
					System.exit(1);
				}
			} else if (args[i].equals("--federate")) {
				if (i+1 < args.length) {
					federation.add(args[++i]);
				} else {
					System.err.println("--federate requires an address");
					System.exit(1);
				}
			} else if (args[i].equals("--static")) {
				if (i+1 < args.length) {
					statics.add(args[++i]);
				} else {
					System.err.println("--static requires a path");
					System.exit(1);
				}
			} else {
				System.err.println("invalid argument: " + args[i]);
				System.exit(1);
			}
		}
		if (statics.isEmpty())
			statics.add(".");
		InlineServer server = new InlineServer(port, "org.zincapi.inline.server.ZincServlet");
		GPServletDefn servlet = server.getBaseServlet();
		servlet.initParam("org.atmosphere.cpr.sessionSupport", "true");
		servlet.initParam("org.zincapi.server.init", "org.zincapi.chirpy.server.Main");
		servlet.setServletPath("/chirpy");
		for (String s : statics)
			server.addStaticDir(new File(s));
		server.notify(new NotifyOnServerReady() {
			@Override
			public void serverReady(InlineServer server, Endpoint addr) {
				ZincServlet zs = (ZincServlet) server.servletFor("/chirpy").getImpl();
				Zinc zinc = zs.getZinc();
				final Repository repo = (Repository) zs.getState();
				zinc.addConnectionHandler(new ConnectionHandler() {
					@Override
					public void newConnection(HandleRequest request, String type, String uri) {
						if (!type.equals("server"))
							return;
						// Set up a bi-directional communication
						Requestor rqr = request.obtainRequestor();
						repo.federatedNodes.put(uri, rqr);
						System.out.println("Received connection from server " + uri);
					}
				});
				if (!federation.isEmpty()) {
					zinc.setIdentity("server", "http://" + addr + "/chirpy");
					for (String s : federation) {
						try {
							System.out.println("Attempting to connect to " + s);
							Requestor rqr = zinc.newRequestor(URI.create(s));
							repo.federatedNodes.put(s, rqr);
							MakeRequest subscribe = rqr.subscribe("topics", new FederatedTopicHandler(rqr, repo));
							subscribe.send();
						} catch (Exception ex) {
							System.err.println("Could not parse " + s);
							System.exit(1);
						}
					}
				}
			}
		});
		server.run();
	}
	
	public static Repository initZinc(Zinc z) {
		try {
			Repository repo = new Repository(z);
			z.handleResource("topics", new TopicListResourceHandler(z, repo));
			return repo;
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
			return null;
		}
	}
}
