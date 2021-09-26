package util;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

public class JsonReader {
    public static String parseJson(String urlAddress, String fieldName){
        JSONObject json = null;
        try {
            json = new JSONObject(IOUtils.toString(new URL(urlAddress), Charset.forName("UTF-8")));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Can't get the URL");
        }
        return json.getJSONObject("result").getString(fieldName);
    }
}
