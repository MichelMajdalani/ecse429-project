package ecse429.group7;

import ecse429.group7.BaseTest;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.After;

import kong.unirest.Unirest;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;

public class TestShutdown extends BaseTest {
        @After
        public void restartServerAfterShutdown()
        {
            startServer();
        }

        // GET /shutdown
        @Test
        public void testGetShutdownStatusCode()
        {
            assertGetStatusCode("/shutdown", STATUS_CODE_OK);
        }

        @Test
        public void testGetShutdownServerDisabled()
        {
            try
            {
                HttpResponse<JsonNode> response = Unirest.get("/shutdown").asJson();
            } catch(kong.unirest.UnirestException e)
            {
                System.out.println(e);
            }
        }
}
