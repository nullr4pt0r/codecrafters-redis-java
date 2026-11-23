import java.util.concurrent.ConcurrentHashMap;

public class StoreRetrievePayload {
    static ConcurrentHashMap<String, Object> mapp = new ConcurrentHashMap<>();

    public static String storePayload(String arg){
        String[] payload = arg.split(":", 2);
        mapp.put(payload[0], payload[1]);
        return "OK";
    }

    public static String retrievePayloadByKey(String key){
       return mapp.get(key).toString();
    }
}
