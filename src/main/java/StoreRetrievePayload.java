import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class StoreRetrievePayload {
    static ConcurrentHashMap<String, Object> mapp = new ConcurrentHashMap<>();
    static ConcurrentHashMap<String, Long> expiryTable = new ConcurrentHashMap<>();

    public static String storePayload(String arg){
        String[] payload = arg.split(":");
        if(payload.length >= 4){
            String time = payload[3];
            switch (payload[2]){
                case "EX" -> {
                    //set expiry secs to millisecs
                    expiryTable.put(payload[0], calculateExpiryTime(TimeUnit.SECONDS.toMillis(Long.valueOf(time))));
                }
                case "PX" -> {
                    //set milli secs expiry as it is
                    expiryTable.put(payload[0], calculateExpiryTime(Long.valueOf(time)));
                }
            }

        }
        mapp.put(payload[0], payload[1]);
        return "OK";
    }

    public static String retrievePayloadByKey(String key){
        if(!mapp.containsKey(key)){
            return "";
        }
       if(isKeyExpired(key)){
           //remove the keys as seperate task like future
           removeExpiredKeys(key);
           return "";
       }
       return mapp.get(key).toString();
    }

    public static Long calculateExpiryTime(Long time){
        //get current time in epoch
        return System.currentTimeMillis() +  time;
    }

    public static boolean isKeyExpired(String key) {
        Long expiryAt = expiryTable.get(key);
        if (expiryAt == null) {
            return false;
        }
        return expiryAt < System.currentTimeMillis();
    }

    private static void removeExpiredKeys(String key){
        expiryTable.remove(key);
        mapp.remove(key);
    }

}
