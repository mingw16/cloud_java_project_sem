import org.json.simple.JSONObject;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONHandler {

    public static JSONObject convertHashMapToJson(HashMap<String, String> map) {
        JSONObject jsonObject = new JSONObject();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            jsonObject.put(entry.getKey(), entry.getValue());
        }
        return jsonObject;
    }

    public static JSONObject convertStringListToJson(List<String> list) {
        JSONObject obj = new JSONObject();
        obj.put(list.get(0), list.get(1));
        return obj;
    }




//        public HashMap<String, Object> jsonToHashMap(JSONObject json) {
//            HashMap<String, Object> map = new HashMap<String, Object>();
//            Iterator<String> keysItr = json.keys();
//            while(keysItr.hasNext()) {
//                String key = keysItr.next();
//                Object value = json.get(key);
//
//                if(value instanceof JSONArray) {
//                    value = toList((JSONArray) value);
//                }
//
//                else if(value instanceof JSONObject) {
//                    value = jsonToHashMap((JSONObject) value);
//                }
//                map.put(key, value);
//            }
//            return map;
//        }


}

