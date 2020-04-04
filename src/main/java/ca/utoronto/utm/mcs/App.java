package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpServer;
import dagger.ObjectGraph;

import javax.inject.Inject;
import java.net.URISyntaxException;

public class App implements Runnable
{
    @Inject HttpServer server;
    @Inject Config config;
    @Inject APIHandler httpHandler;

    public void run()
    {
        /* TODO: Add Working Context Here */
        server.createContext("/api/v1/post", httpHandler);
        server.setExecutor(null);
        server.start();
        System.out.printf("Server started on port %d...\n", config.port);
    }

    public static void main(String[] args) throws URISyntaxException
    {
        ObjectGraph objectGraph = ObjectGraph.create(new DaggerModule(new Config()));
        App app = objectGraph.get(App.class);
        app.run();
    }
}
