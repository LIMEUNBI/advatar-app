package com.epopcon.advatar.network;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class NaverProfile {

    private static String token;
    private static String header = "Bearer " ;

    private static String apiUrl = "https://openapi.naver.com/v1/nid/me";

    private static Map<String, String> requestHeaders = new HashMap<>();

    public NaverProfile(String token) {
        this.token = token;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getProfile() {

        requestHeaders.put("Authorization", header + token);
        String responseBody = get();

        return responseBody;

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static String get(){
        HttpURLConnection con = connect(apiUrl);
        try {
            con.setRequestMethod("GET");
            for(Map.Entry<String, String> header : requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                return readBody(con.getInputStream());
            } else { // 에러 발생
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }

    private static HttpURLConnection connect(String apiUrl){
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection)url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static String readBody(InputStream body){
        InputStreamReader streamReader = new InputStreamReader(body);

        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();

            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }

            return decode(responseBody.toString());
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
        }
    }

    public static String decode(String unicode) throws Exception {
        StringBuffer str = new StringBuffer();

        char ch = 0;
        for( int i= unicode.indexOf("\\u"); i > -1; i = unicode.indexOf("\\u") ){
            ch = (char)Integer.parseInt( unicode.substring( i + 2, i + 6 ) ,16);
            str.append( unicode.substring(0, i) );
            str.append( String.valueOf(ch) );
            unicode = unicode.substring(i + 6);
        }
        str.append( unicode );

        return str.toString();
    }

}
