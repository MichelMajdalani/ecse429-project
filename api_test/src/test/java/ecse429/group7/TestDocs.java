package ecse429.group7;

import ecse429.group7.BaseTest;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.BeforeClass;

import kong.unirest.Unirest;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;

public class TestDocs extends BaseTest {

    // GET /shutdown
    @Test
    public void testGetDocsStatusCode() {
        assertGetStatusCode("/docs", STATUS_CODE_OK);
    }

}