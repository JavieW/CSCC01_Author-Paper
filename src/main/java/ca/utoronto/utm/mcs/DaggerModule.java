package ca.utoronto.utm.mcs;


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.sun.net.httpserver.HttpServer;
import dagger.Module;
import dagger.Provides;
import org.bson.Document;

import java.io.IOException;
import java.net.InetSocketAddress;

@Module (injects = {App.class, APIHandler.class, MongoDB.class}, library = true)
class DaggerModule {
    Config config;

    DaggerModule(Config cfg) {
        config = cfg;
    }

    @Provides MongoClient provideMongoClient() {
        return MongoClients.create("mongodb://localhost:27017");
    }

    @Provides HttpServer provideHttpServer(){
        try{
            HttpServer server = HttpServer.create(new InetSocketAddress(config.ip, config.port), 0);
            return server;
        } catch (IOException e) {
            System.out.println("Failed");
            return null;
        }
    }

    @Provides MongoCollection<Document> provideMongoCollection(){
        return provideMongoClient().getDatabase("csc301a2").getCollection("posts");
    }
}
