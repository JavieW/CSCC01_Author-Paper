package ca.utoronto.utm.mcs;

import com.mongodb.client.result.DeleteResult;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;


public class APIHandler implements HttpHandler{
    @Inject MongoDB db;

    public void handle(HttpExchange r) {
        try {
            if (r.getRequestMethod().equals("PUT")) {
                handlePut(r);
            } else if (r.getRequestMethod().equals("GET")) {
                handleGet(r);
            } else if (r.getRequestMethod().equals("DELETE")) {
                handleDelete(r);
            } else {
                r.sendResponseHeaders(405, -1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handlePut(HttpExchange r) throws IOException {
        try {
            String body = Utils.convert(r.getRequestBody());

            JSONObject deserialized = new JSONObject(body);

            if (!(deserialized.has("title") && deserialized.has("author")
                    && deserialized.has("content") && deserialized.has("tags"))) {
                r.sendResponseHeaders(400, -1);
                return;
            }

            String title = deserialized.getString("title");
            String author = deserialized.getString("author");
            String content = deserialized.getString("content");
            JSONArray tags = deserialized.getJSONArray("tags");

            ArrayList<String> list_tags = new ArrayList<>();
            int len = tags.length();
            for (int i=0;i<len;i++){
                list_tags.add(tags.get(i).toString());
            }

            Document post = new Document("title", title)
                    .append("author", author)
                    .append("content", content)
                    .append("tags", list_tags);

            String _id = db.createPost(post);
            String response = "{\"_id\": \"" + _id +"\"}";
            r.sendResponseHeaders(200, response.length());
            OutputStream os = r.getResponseBody();
            os.write(response.getBytes());
            os.close();
        } catch (JSONException e) {
            r.sendResponseHeaders(400, -1);
        } catch (IOException e) {
            r.sendResponseHeaders(500, -1);
        }
    }

    private void handleGet(HttpExchange r) throws IOException {
        try {
            String body = Utils.convert(r.getRequestBody());

            JSONObject deserialized = new JSONObject(body);

            String _id = null;
            String title = null;

            if (deserialized.has("_id"))
                _id = deserialized.getString("_id");

            if (deserialized.has("title"))
                title = deserialized.getString("title");

            if (_id == null && title == null) {
                r.sendResponseHeaders(400, -1);
                return;
            }
            ArrayList<String> posts = db.getPost(_id, title);

            if (posts.size() == 0) {
                r.sendResponseHeaders(404, -1);
                return;
            }

            String response = "{ \"_id\":[" + String.join(",", posts) + "]}";

            byte[] bs = response.getBytes("UTF-8");
            r.sendResponseHeaders(200, bs.length);
            OutputStream os = r.getResponseBody();
            os.write(bs);
            os.close();
        } catch (JSONException e) {
            r.sendResponseHeaders(400, -1);
        } catch (IOException e) {
            r.sendResponseHeaders(500, -1);
        }
    }

    private void handleDelete(HttpExchange r) throws IOException {
        try {
            String body = Utils.convert(r.getRequestBody());

            JSONObject deserialized = new JSONObject(body);

            if (!(deserialized.has("_id"))) {
                r.sendResponseHeaders(400, -1);
                return;
            }

            String id = deserialized.getString("_id");
            DeleteResult result = db.deletePost(new ObjectId(id));
            if (result.getDeletedCount() > 0){
                r.sendResponseHeaders(200, -1);
            } else {
                r.sendResponseHeaders(404, -1);
            }
        } catch (JSONException e) {
            r.sendResponseHeaders(400, -1);
        } catch (IllegalArgumentException e) {
            r.sendResponseHeaders(404, -1); // _id not in required length
        } catch (IOException e) {
            r.sendResponseHeaders(500, -1);
        }
    }
}
