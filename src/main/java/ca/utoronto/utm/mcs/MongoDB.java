package ca.utoronto.utm.mcs;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.bson.types.ObjectId;

import javax.inject.Inject;
import java.util.ArrayList;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.regex;

public class MongoDB {
    @Inject MongoCollection<Document> collection;


    public String createPost(Document post) {
        collection.insertOne(post);
        return post.getObjectId("_id").toString();
    }

    public ArrayList<String> getPost(String id, String title) {
        FindIterable<Document> iterDoc;
        ArrayList<String> posts = new ArrayList<>();

        if (id != null) {
            iterDoc = collection.find(eq("_id", new ObjectId(id)));
            for (Document post : iterDoc)
                posts.add(post.toJson());
        }
        if (posts.size() == 0 && title != null) {
            iterDoc = collection.find(regex("title", title));
            for (Document post : iterDoc)
                posts.add(post.toJson());
        }
        return posts;
    }

    public DeleteResult deletePost(ObjectId id) {
        return collection.deleteOne(eq("_id", id));
    }

}
