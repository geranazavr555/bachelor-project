package com.codeforces.iomarkup.daemon;

import lombok.SneakyThrows;
import org.apache.xmlrpc.webserver.ServletWebServer;
import org.apache.xmlrpc.webserver.XmlRpcServlet;

public class IoMarkupDaemon implements Runnable {
    private final ServletWebServer server;

    @SneakyThrows
    public IoMarkupDaemon(int port) {
        XmlRpcServlet servlet = new XmlRpcServlet();
        server = new ServletWebServer(servlet, port);
    }

    @SneakyThrows
    @Override
    public void run() {
        server.start();
    }

    public void close() {
        server.shutdown();
    }
}
