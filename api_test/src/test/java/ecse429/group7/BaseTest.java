package ecse429.group7;
import org.junit.BeforeClass;

import static org.junit.Assert.assertEquals;

import kong.unirest.Unirest;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;


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
}
