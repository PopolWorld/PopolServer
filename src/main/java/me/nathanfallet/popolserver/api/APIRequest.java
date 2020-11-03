package me.nathanfallet.popolserver.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class APIRequest<T> {

    private String method;
    private String host;
    private String path;
    private HashMap<String, String> headers;
    private HashMap<String, Object> queryItems;
    private String body;
    private int httpResult;

    public APIRequest(String method, String host, String path) {
        // Get request parameters
        this.method = method;
        this.host = host;
        this.path = path;
        this.headers = new HashMap<>();
        this.queryItems = new HashMap<>();
    }

    public APIRequest<T> with(String name, Object value) {
        queryItems.put(name, value);
        return this;
    }

    public APIRequest<T> withBody(String body) {
        this.body = body;
        return this;
    }

    public APIRequest<T> withHeader(String name, String value) {
        this.headers.put(name, value);
        return this;
    }

    public URL getURL() {
        try {
            URI uri = new URI("https", null, host, 443, path, getQuery(), null);

            return uri.toURL();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getQuery() {
        StringBuilder sb = new StringBuilder();

        if (!queryItems.isEmpty()) {
            for (String key : queryItems.keySet()) {
                if (!sb.toString().isEmpty()) {
                    sb.append("&");
                }
                sb.append(key);
                sb.append("=");
                sb.append(queryItems.get(key));
            }
        }

        return sb.toString();
    }

    public void execute(Class<T> classOfT, CompletionHandler<T> completionHandler) {
        try {
            // Create the request based on give parameters
            HttpsURLConnection con = (HttpsURLConnection) getURL().openConnection();
            con.setRequestMethod(method);
            for (String key : headers.keySet()) {
                con.setRequestProperty(key, headers.get(key));
            }

            // Set body
            if (body != null) {
                con.setDoOutput(true);
                con.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                try (OutputStream os = con.getOutputStream()) {
                    os.write(body.getBytes("utf-8"));
                }
            }

            // Launch the request to server
            con.connect();

            // Get data and response
            StringBuilder sb = new StringBuilder();
            httpResult = con.getResponseCode();

            // Parse JSON data
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();

            String json = sb.toString().trim();
            Gson gson = new GsonBuilder().create();

            // Return object
            completionHandler.completionHandler(gson.fromJson(json, classOfT), statusForCode(httpResult));
            return;
        } catch (Exception e) {
            // If it fails, return a null object
            completionHandler.completionHandler(null, statusForCode(httpResult));
        }
    }

    public APIResponseStatus statusForCode(int code) {
        switch (code) {
            case 200:
                return APIResponseStatus.ok;
            case 201:
                return APIResponseStatus.created;
            case 400:
                return APIResponseStatus.badRequest;
            case 401:
                return APIResponseStatus.unauthorized;
            case 403:
                return APIResponseStatus.forbidden;
            case 404:
                return APIResponseStatus.notFound;
            case 521:
                return APIResponseStatus.originDown;
            default:
                return APIResponseStatus.offline;
        }
    }

    public interface CompletionHandler<T> {

        void completionHandler(T object, APIResponseStatus status);

    }

}
