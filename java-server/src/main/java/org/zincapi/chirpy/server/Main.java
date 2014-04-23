package org.zincapi.chirpy.server;

import java.io.File;

import org.zincapi.Zinc;

import com.gmmapowell.http.GPServletDefn;
import com.gmmapowell.http.InlineServer;

public class Main {

	public static void main(String[] args) {
		InlineServer server = new InlineServer(8380, "org.zincapi.inline.server.ZincServlet");
		GPServletDefn servlet = server.getBaseServlet();
		servlet.initParam("org.atmosphere.cpr.sessionSupport", "true");
		servlet.initParam("org.zincapi.server.init", "org.zincapi.chirpy.server.Main");
		servlet.setServletPath("/chirpy");
		if (args.length == 0)
			server.addStaticDir(new File("."));
		else {
			for (String s : args)
				server.addStaticDir(new File(s));
		}
		server.run();
	}
	
	public static void initZinc(Zinc z) {
		z.handleResource("topics", new TopicListResourceHandler(z));
		z.handleResource("topic/baseball", new TopicResourceHandler(z, "baseball"));
		z.handleResource("topic/football", new TopicResourceHandler(z, "football"));
	}
}
