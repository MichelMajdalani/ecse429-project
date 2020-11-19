package ecse429.group7.performance;

import ecse429.group7.BaseTest;
import org.junit.Test;

import kong.unirest.Unirest;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;

@RunWith(Parameterized.class)
public class PerformanceTestRunner extends BaseTest {

    private static final GenericPerformanceTest CATEGORIES_TESTER = new GenericPerformanceTest("categories") {
        @Override
        public void addRandom() {
            HttpResponse<JsonNode> response = Unirest.post("/categories").header("Content-Type", "application/json").body(
                    "{\n   \"title\":\"" + getRandomString() + "\",\n \"description\":\"" + getRandomString() + "\"\n}\n")
                    .asJson();
            id_list.add(response.getBody().getObject().getInt("id"));
        }

        @Override
        public void changeLast() {
            Unirest.put("/categories/" + id_list.getLast()).header("Content-Type", "application/json").body(
                    "{\n   \"title\":\"" + getRandomString() + "\",\n \"description\":\"" + getRandomString() + "\"\n}\n")
                    .asJson();
        }
    };
    private static final GenericPerformanceTest PROJECTS_TESTER = new GenericPerformanceTest("projects") {
        @Override
        public void addRandom() {
            HttpResponse<JsonNode> response = Unirest.post("/projects")
                    .body("{\n\"description\":\"" + getRandomString() + "\",\n    \"active\":" + getRandomBool()
                            + ",\n    \"completed\":" + getRandomBool() + ",\n    \"title\":\"" + getRandomString()
                            + "\"\n}")
                    .asJson();

            id_list.add(response.getBody().getObject().getInt("id"));
        }

        @Override
        public void changeLast() {
            Unirest.put("/projects/" + id_list.getLast())
                    .body("{\n\"description\":\"" + getRandomString() + "\",\n    \"active\":" + getRandomBool()
                            + ",\n    \"completed\":" + getRandomBool() + ",\n    \"title\":\"" + getRandomString()
                            + "\"\n}")
                    .asJson();
        }
    };
    private static final GenericPerformanceTest TODOS_TESTER = new GenericPerformanceTest("todos") {
        @Override
        public void addRandom() {
            HttpResponse<JsonNode> response = Unirest.post("/todos").body("{\"title\":\"" + getRandomString()
                    + "\",\"doneStatus\":" + getRandomBool() + ",\"description\":\"" + getRandomString() + "\"}").asJson();

            id_list.add(response.getBody().getObject().getInt("id"));
        }

        @Override
        public void changeLast() {
            Unirest.put("/todos/" + id_list.getLast()).body("{\"title\":\"" + getRandomString()
                    + "\",\"doneStatus\":" + getRandomBool() + ",\"description\":\"" + getRandomString() + "\"}").asJson();
        }
    };

    private final int num;
    private final GenericPerformanceTest tester;

    @Parameterized.Parameters(name = "{0} {1}")
    public static Collection<Object[]> data() {
        int[] sizeOpts = {10, 50, 100, 250, 500, 1000};
        String[] nameOpts = {"categories", "projects", "todos"};
        GenericPerformanceTest[] testers = {CATEGORIES_TESTER, PROJECTS_TESTER, TODOS_TESTER};
        ArrayList<Object[]> l = new ArrayList<>();
        for (int n : sizeOpts) {
            for (int j = 0; j < nameOpts.length; j++) {
                l.add(new Object[]{n, nameOpts[j], testers[j]});
            }
        }
        return l;
    }

    public PerformanceTestRunner(int num, String name, GenericPerformanceTest tester) {
        this.num = num;
        this.tester = tester;
    }

    @Test
    public void testAdd() {
        tester.timeAdd(num);
    }

    @Test
    public void testRemove() {
        tester.timeRemove(num);
    }

    @Test
    public void testChange() {
        tester.timeAdd(num);
    }
}
