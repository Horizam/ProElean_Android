//package com.horizam.pro.elean;
//
//import android.widget.Toast;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.Iterator;
//
//public class okayakay {
//    JSONObject jsonObject;
//
//    {
//        try {
//            jsonObject = new JSONObject("response");
//            JSONObject mlObject = jsonObject.getJSONObject("ml");
//            for (Iterator<String> it = mlObject.keys(); it.hasNext(); ) {
//                String key = it.next();
//                JSONObject user = mlObject.getJSONObject(key);
//                String message = user.getString("message");
//                String uTime = user.getString("utime");
//                JSONObject op = user.getJSONObject("op");
//                String name = op.getString("name");
//                String avatar = op.getString("avatar");
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//}
