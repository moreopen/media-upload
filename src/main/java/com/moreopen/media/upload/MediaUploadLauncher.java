package com.moreopen.media.upload;

import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;

import com.moreopen.media.upload.utils.Constants;

public class MediaUploadLauncher {
	
	private static  Logger logger = Logger.getLogger(MediaUploadLauncher.class);

	private static Server server = null;
	private int port;

	public MediaUploadLauncher(int port) {
		this.port = port;
	}

	public void run() throws Exception {
		if (server != null) {
			return;
		}
		server = new Server(this.port);
		server.setThreadPool(getThreadPool());
		server.setStopAtShutdown(true);
		server.setSendServerVersion(true);
		
		HandlerCollection handlers = new HandlerCollection();
		//can get static resource by nginx, not by ResoruceHandler
		handlers.addHandler(getResourceHandler());
		handlers.addHandler(getWebAppContext());
		handlers.addHandler(getRequestLogHandler());
		server.setHandler(handlers);
		server.start();
		logger.info("Start icollector server, done.port: "+ this.port);
	}

	public void stop() throws Exception {
		if (server != null) {
			server.stop();
			server = null;
		}
	}

	public void setPort(int port) {
		this.port = port;
	}

	private ThreadPool getThreadPool() {
		return new ExecutorThreadPool();
	}

	private WebAppContext getWebAppContext() {
		String path = MediaUploadLauncher.class.getResource("/").getFile().replaceAll("/target/(.*)", "")
				+ "/src/main/webapp";
		return new WebAppContext(path, "/media");
	}
	
	/**
	 * build static resource visit handler
	 */
	private ContextHandler getResourceHandler() {
		ResourceHandler resourceHandler = new ResourceHandler();  
        resourceHandler.setDirectoriesListed(false);
        resourceHandler.setResourceBase(Constants.UPLOAD_TOP_DIR);  
        resourceHandler.setStylesheet(""); 
        
        ContextHandler contextHandler = new ContextHandler(Constants.DOWNLOAD_CONTEXT);
        contextHandler.setHandler(resourceHandler);
        return contextHandler;
	}
	
	protected static RequestLogHandler getRequestLogHandler() {
		RequestLogHandler logHandler = new RequestLogHandler();
		NCSARequestLog requestLog = new NCSARequestLog("jetty-yyyy_MM_dd.access.log");
		requestLog.setAppend(true);
		requestLog.setLogServer(true);
		requestLog.setExtended(false);
		requestLog.setLogTimeZone(TimeZone.getDefault().getID());
		requestLog.setLogLatency(true);
		requestLog.setLogDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
		logHandler.setRequestLog(requestLog);
		return logHandler;
	}

	public static void main(String[] args) throws Exception {
		new MediaUploadLauncher(8081).run();
		server.join();
	}


}
