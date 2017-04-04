package com.covain.projects.emailer.utils;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Properties;

public class Oauth2Utils {

    public static String OAUTH2 = "oauth2";

    private static String TOKEN_URL = "https://www.googleapis.com/oauth2/v4/token";
    private static String SUFFIX = ".properties";
    private static String CLIENT_ID = "client_id";
    private static String CLIENT_SECRET = "client_secret";
    private static String REFRESH_TOKEN = "refresh_token";
    private static String ACCESS_TOKEN = "access_token";
    private static String EXPIRE_TIME = "token_expire_time";

    public static String getAccessToken(Properties oauth2Props) {
        String oauthClientId = oauth2Props.getProperty(CLIENT_ID);
        String oauthSecret = oauth2Props.getProperty(CLIENT_SECRET);
        String refreshToken = oauth2Props.getProperty(REFRESH_TOKEN);
        String accessToken = oauth2Props.getProperty(ACCESS_TOKEN);
        long tokenExpires = new Long(oauth2Props.getProperty(EXPIRE_TIME, "0"));

        if(System.currentTimeMillis() > tokenExpires) {
            try {
                String request = "client_id="+ URLEncoder.encode(oauthClientId, "UTF-8")
                        +"&client_secret="+URLEncoder.encode(oauthSecret, "UTF-8")
                        +"&refresh_token="+URLEncoder.encode(refreshToken, "UTF-8")
                        +"&grant_type=refresh_token";
                HttpURLConnection conn = (HttpURLConnection) new URL(TOKEN_URL).openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                PrintWriter out = new PrintWriter(conn.getOutputStream());
                out.print(request); // note: println causes error
                out.flush();
                out.close();
                conn.connect();
                try {
                    HashMap<String,Object> result;
                    result = new ObjectMapper().readValue(conn.getInputStream(), new TypeReference<HashMap<String,Object>>() {});
                    accessToken = (String) result.get("access_token");
                    tokenExpires = System.currentTimeMillis()+(((Number)result.get("expires_in")).intValue()*1000);
                } catch (IOException e) {
                    String line;
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    while((line = in.readLine()) != null) {
                        System.out.println(line);
                    }
                    System.out.flush();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        oauth2Props.setProperty(ACCESS_TOKEN, accessToken);
        oauth2Props.setProperty(EXPIRE_TIME, String.valueOf(tokenExpires));
        saveProperties(OAUTH2, oauth2Props);
        return accessToken;
    }

    public static Properties getProperties(String propertiesName) {
        Properties properties = null;
        try (InputStream propertiesInputStream = new FileInputStream(propertiesName + SUFFIX)) {
            properties =  new Properties();
            properties.load(propertiesInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public static boolean saveProperties(String propertiesName, Properties properties) {
        boolean success = true;
        try (OutputStream propertiesOutputStream = new FileOutputStream(propertiesName + SUFFIX)) {
            properties.store(propertiesOutputStream, null);
        } catch (IOException e) {
            e.printStackTrace();
            success = false;
        }
        return success;
    }
}
