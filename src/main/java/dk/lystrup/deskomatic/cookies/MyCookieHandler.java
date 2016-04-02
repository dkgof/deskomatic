package dk.lystrup.deskomatic.cookies;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.webkit.network.CookieManager;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.net.CookieHandler;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * Idea for persistent cookies inspired from https://github.com/psycowithespn/TwitchWidget
 * 
 * @author Gof
 */
public class MyCookieHandler extends CookieHandler {

    private CookieManager manager;

    private final String cookieFile;

    private boolean loading;
    
    private Class<?> cookieClass;
    private Field bucketsField;
    private Field totalCountField;
    private Field cookieStoreField;
    
    private Gson gson;
    private JsonParser parser;
    
    public MyCookieHandler(String cookieFile) {
        manager = new CookieManager();

        this.cookieFile = cookieFile;

        try {
            Class<?> storeClass = Class.forName("com.sun.webkit.network.CookieStore");
            cookieClass = Class.forName("com.sun.webkit.network.Cookie");
            bucketsField = storeClass.getDeclaredField("buckets");
            bucketsField.setAccessible(true);
            totalCountField = storeClass.getDeclaredField("totalCount");
            totalCountField.setAccessible(true);
            cookieStoreField = CookieManager.class.getDeclaredField("store");
            cookieStoreField.setAccessible(true);

            gson = new GsonBuilder().setPrettyPrinting().create();
            
            parser = new JsonParser();

        } catch (ClassNotFoundException | NoSuchFieldException | SecurityException ex) {
            Logger.getLogger(MyCookieHandler.class.getName()).log(Level.SEVERE, "Error getting reflection for CookieStore", ex);
        }

        load(cookieClass);
    }
    
    @Override
    public Map<String, List<String>> get(URI uri, Map<String, List<String>> requestHeaders) throws IOException {
        return manager.get(uri, requestHeaders);
    }

    @Override
    public void put(URI uri, Map<String, List<String>> responseHeaders) throws IOException {
        manager.put(uri, responseHeaders);
        
        save(cookieClass);
    }

    private <T> void load(Class<T> cookieClass) {
        loading = true;
        
        try {
            FileReader reader = new FileReader(new File(cookieFile));
            
            JsonObject json = (JsonObject) parser.parse(reader);
            
            int totalCount = json.get("totalCount").getAsInt();
            
            Map<String, Map<T, T>> buckets = new HashMap<>();

            JsonObject bucketsJson = json.getAsJsonObject("buckets");
            bucketsJson.entrySet().stream().forEach((entry) -> {
                HashMap<T,T> bucket = new HashMap<>();
                entry.getValue().getAsJsonArray().forEach((jsonElement) -> {
                    T cookie = gson.fromJson(jsonElement, cookieClass);
                    bucket.put(cookie, cookie);
                });
                buckets.put(entry.getKey(), bucket);
            });

            Object store = cookieStoreField.get(manager);
            
            bucketsField.set(store, buckets);
            totalCountField.set(store, totalCount);
            
            reader.close();
        } catch (Exception ex) {
            Logger.getLogger(MyCookieHandler.class.getName()).log(Level.SEVERE, "Error loading old cookies...", ex);
        }
        
        loading = false;
    }

    private <T> void save(Class<T> cookieClass) {
        if(loading) {
            return;
        }
        
        try {
            Object store = cookieStoreField.get(manager);
            
            int totalCount = totalCountField.getInt(store);
            
            Map<String, Map<T, T>> buckets = (Map<String, Map<T, T>>) bucketsField.get(store);

            JsonObject json = new JsonObject();
            json.addProperty("totalCount", totalCount);
            
            JsonObject bucketsJson = new JsonObject();
            json.add("buckets", bucketsJson);
            
            buckets.keySet().stream().forEach((key) -> {
                JsonArray cookieArray = new JsonArray();
                buckets.get(key).values().stream().forEach((cookie) -> {
                    cookieArray.add(gson.toJsonTree(cookie));
                });
                bucketsJson.add(key, cookieArray);
            });
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(new File(cookieFile)))) {
                writer.println(gson.toJson(json));
                writer.flush();
            }
            
        } catch (IllegalArgumentException | IllegalAccessException | IOException ex) {
            Logger.getLogger(MyCookieHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
