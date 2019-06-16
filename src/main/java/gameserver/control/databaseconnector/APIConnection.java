package gameserver.control.databaseconnector;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

/**
 * APIConnection class contains a constructor for the connection, and methods for managing that connection.
 * These methods are: getResponse from API(recieving bytes from InPutStream), send a message into OutputStream.
 *
 */
public class APIConnection {

    private static final String API_URL = "http://localhost:8080/rest/service";
    private HttpURLConnection connection;

    public APIConnection(String resourceUrl, String requestType){
        try {

            URL url = new URL(API_URL.concat(resourceUrl));
            connection = (HttpURLConnection) url.openConnection();

            //Designing the request
            connection.setRequestMethod(requestType);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            //Enable writing to outputStream
            connection.setDoOutput(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public JSONObject getResponse() {

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {


            StringBuilder response = new StringBuilder();
            String responseLine;


            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

            //Converting stringbuffer to JSON object
            return new JSONObject(response.toString());

        } catch (IOException e) {
            e.printStackTrace();

        }
        return null;
    }


    public void sendMessage(JSONObject jsonObject) {
        String jsonString = jsonObject.toString();

        //Sending JSONOBject
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close(){
        connection.disconnect();
    }


}

