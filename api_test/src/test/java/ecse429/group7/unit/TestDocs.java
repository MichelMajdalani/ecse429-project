package ecse429.group7.unit;

import ecse429.group7.BaseTest;

import org.junit.Test;


public class TestDocs extends BaseTest {

    // GET /shutdown
    @Test
    public void testGetDocsStatusCode() {
        assertGetStatusCode("/docs", STATUS_CODE_OK);
    }

}