package ecse429.group7;

import kong.unirest.UnirestException;

import org.junit.Test;
import org.junit.After;

import kong.unirest.Unirest;

public class TestShutdown extends BaseTest {
        @After
        public void restartServerAfterShutdown()
        {
            System.out.println("Restarting server");
            startServer();
        }

        // GET /shutdown
        @Test(expected= UnirestException.class)
        public void testGetShutdownServerDisabled() {
            try {
                Unirest.get("/shutdown").asJson();
            } catch (Exception ignored) {}
            Unirest.get("/todos").asJson();
        }
}
