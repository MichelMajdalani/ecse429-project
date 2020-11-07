package ecse429.group7;
import org.junit.BeforeClass;
import org.junit.AfterClass;


import static org.junit.Assert.assertEquals;

import kong.unirest.Unirest;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class BaseTest 
{
    private static final String BASE_URL = "http://localhost:4567";
    
    protected static final int STATUS_CODE_OK = 200;
    protected static final int STATUS_CODE_CREATED = 201;
    protected static final int STATUS_CODE_BAD_REQUEST = 400;
    protected static final int STATUS_CODE_NOT_FOUND = 404;
    protected static final int STATUS_CODE_METHOD_NOT_ALLOWED = 405;

    @BeforeClass
    public static void setupForAllTests()
    {
        Unirest.config().defaultBaseUrl(BASE_URL);

        startServer();
        try{

            Thread.sleep(500);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static boolean startServer() {
        try {
            final Runtime re = Runtime.getRuntime();
            ProcessBuilder pb = new ProcessBuilder("java", "-jar", "../runTodoManagerRestAPI-1.5.5.jar");
            Process ps = pb.start();
            final InputStream is = ps.getInputStream();
            final BufferedReader output = new BufferedReader(new InputStreamReader(is));
            while (true) {
                String line = output.readLine();
                if (line!=null && line.contains("Running on 4567"))
                {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void assertGetStatusCode(String url, int status_code)
    {
        HttpResponse<JsonNode> response = Unirest.get(url).asJson();
        assertEquals(response.getStatus(), status_code);
    }

    public static void assertHeadStatusCode(String url, int status_code)
    {
        HttpResponse<JsonNode> response = Unirest.head(url).asJson();
        assertEquals(response.getStatus(), status_code);
    }

    public static void assertGetErrorMessage(String url, String expected_message, int index)
    {
        HttpResponse<JsonNode> response = Unirest.get(url).asJson();
        assertEquals(response.getBody().getObject().getJSONArray("errorMessages").getString(index), expected_message);
    }

    public static int findIdFromTodoName(String todo_name)
    {
        HttpResponse<JsonNode> response = Unirest.get("/todos").asJson();
        int id = -1;

        for(int i = 0; i < response.getBody().getObject().getJSONArray("todos").length(); i++)
        {
            if(response.getBody().getObject().getJSONArray("todos").getJSONObject(i).getString("title").equals(todo_name))
            {
                id = response.getBody().getObject().getJSONArray("todos").getJSONObject(i).getInt("id");
                break;
            }
        }

        return id;
    }

    public static int findIdFromTodoCategoryName(String category_name, String todo_name)
    {
        HttpResponse<JsonNode> response = Unirest.get("/todos").asJson();

        int id = -1;

        for(int i = 0; i < response.getBody().getObject().getJSONArray("todos").length(); i++)
        {
            if(response.getBody().getObject().getJSONArray("todos").getJSONObject(i).getString("title").equals(todo_name))
            {
                int todo_id = response.getBody().getObject().getJSONArray("todos").getJSONObject(i).getInt("id");
                HttpResponse<JsonNode> response_cat = Unirest.get("/todos/" + String.valueOf(todo_id) + "/categories").asJson();
                System.out.println(todo_id + " " + response_cat.getBody().toString());

                for(int j = 0; j < response_cat.getBody().getObject().getJSONArray("categories").length(); j++)
                {
                    System.out.println(response_cat.getBody().getObject().getJSONArray("categories").getJSONObject(j).getString("title"));
                    if(response_cat.getBody().getObject().getJSONArray("categories").getJSONObject(j).getString("title").equals(category_name))
                    {
                        id = response_cat.getBody().getObject().getJSONArray("categories").getJSONObject(j).getInt("id");
                        break;
                    }
                }
            }
        }
    
        return id;
    }
}
