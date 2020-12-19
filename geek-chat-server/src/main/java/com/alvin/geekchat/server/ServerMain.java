package com.alvin.geekchat.server;

import com.alvin.geekchat.server.util.GlobalUtil;
import com.alvin.geekchat.server.daemon.MainListener;
import com.alvin.geekchat.server.daemon.OnlineChecker;
import com.alvin.geekchat.server.daemon.LoopListener;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;

@SpringBootApplication(scanBasePackages = {"com.alvin.geekchat.server"})
public class ServerMain {
	public static void main(String[] args) {
		ApplicationContext context = new SpringApplicationBuilder(ServerMain.class)
				.web(WebApplicationType.NONE)
				.run(args);
		GlobalUtil.context = context;
		new Thread(new OnlineChecker()).start();
		new Thread(new LoopListener(context)).start();
		new Thread(new MainListener(context)).start();
	}
}