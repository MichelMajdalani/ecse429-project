package ecse429.group7;

import ecse429.group7.BaseTest;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.BeforeClass;

import kong.unirest.Unirest;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;


public class TestProjects extends BaseTest
{
    //GET /projects
    @Test
    public void testGetProjectStatusCode()
    {
        assertGetStatusCode("/projects", STATUS_CODE_OK);
    }

    @Test
    public void testGetProjectsResponseSize()
    {
        HttpResponse<JsonNode> response = Unirest.get("/projects").asJson();
        assertEquals(response.getBody().getObject().getJSONArray("projects").length(), 1);
    }

    @Test
    public void testGetProjectsResponseTitle()
    {
        HttpResponse<JsonNode> response = Unirest.get("/projects").asJson();
        String title = response.getBody().getObject().getJSONArray("projects").getJSONObject(0).getString("title");
        assertEquals("Office Work", title);
    }

    @Test
    public void testGetProjectsResponseCompleted()
    {
        HttpResponse<JsonNode> response = Unirest.get("/projects").asJson();
        String completed = response.getBody().getObject().getJSONArray("projects").getJSONObject(0).getString("completed");
        assertEquals("false", completed);
    }
}