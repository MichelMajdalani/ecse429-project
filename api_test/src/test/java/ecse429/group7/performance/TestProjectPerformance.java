package ecse429.group7.performance;

import org.junit.Test;

import kong.unirest.Unirest;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;

public class TestProjectPerformance extends BasePerformanceTest {
    
    public void addRandomProject()
    {
        HttpResponse<JsonNode> response = Unirest.post("/projects")
                .body("{\n\"description\":\"" + getRandomString() + "\",\n    \"active\":" + getRandomBool()
                        + ",\n    \"completed\":" + getRandomBool() + ",\n    \"title\":\"" + getRandomString()
                        + "\"\n}")
                .asJson();

        project_id_list.add(response.getBody().getObject().getInt("id"));
    }

    public void addManyProjects(int number_projects)
    {
        for (int i = 0; i < number_projects; i++) {
            addRandomProject();
        }
    }

    public void removeLastProject()
    {
        Unirest.delete("/projects/" + project_id_list.getLast()).header("Content-Type", "application/json").asJson();
        project_id_list.removeLast();
    }

    public void changeLastProject()
    {
        Unirest.put("/projects/" + project_id_list.getLast())
        .body("{\n\"description\":\"" + getRandomString() + "\",\n    \"active\":" + getRandomBool()
                + ",\n    \"completed\":" + getRandomBool() + ",\n    \"title\":\"" + getRandomString()
                + "\"\n}")
        .asJson();
    }

    public void testManyProjectsAddTime(int number_projects)
    {
        long start_whole = System.nanoTime();

        // Setup
        addManyProjects(number_projects);

        // Add one project
        long start_single = System.nanoTime();
        addRandomProject();
        long finish_single = System.nanoTime();

        // Reset state
        resetState();

        long finish_whole = System.nanoTime();
        System.out.println("Test Add Project with " + number_projects + " Projects in Server:");
        System.out.println("\tTotal Test Time: " + (finish_whole - start_whole) + " ns");
        System.out.println("\tSingle Project Add Time: " + (finish_single - start_single) + " ns");
    }

    public void testManyProjectDeleteTime(int number_projects)
    {
        long start_whole = System.nanoTime();

        // Setup
        addManyProjects(number_projects);

        // Remove last project
        long start_single = System.nanoTime();
        removeLastProject();
        long finish_single = System.nanoTime();

        // Reset state
        resetState();

        long finish_whole = System.nanoTime();
        System.out.println("Test Remove Projects with " + number_projects + " Projects in Server:");
        System.out.println("\tTotal Test Time: " + (finish_whole - start_whole) + " ns");
        System.out.println("\tSingle Project Remove Time: " + (finish_single - start_single) + " ns");
    }

    public void testManyProjectChangeTime(int number_projects)
    {
        long start_whole = System.nanoTime();

        // Setup
        addManyProjects(number_projects);

        // Change last project
        long start_single = System.nanoTime();
        changeLastProject();
        long finish_single = System.nanoTime();

        // Reset state
        resetState();

        long finish_whole = System.nanoTime();
        System.out.println("Test Change Projects with " + number_projects + " Projects in Server:");
        System.out.println("\tTotal Test Time: " + (finish_whole - start_whole) + " ns");
        System.out.println("\tSingle Project Change Time: " + (finish_single - start_single) + " ns");
    }

    @Test
    public void test10ProjectsAdd()
    {
        testManyProjectsAddTime(10);
    }

    @Test
    public void test50ProjectsAdd()
    {
        testManyProjectsAddTime(50);
    }

    @Test
    public void test100ProjectsAdd()
    {
        testManyProjectsAddTime(100);
    }

    @Test
    public void test250ProjectsAdd()
    {
        testManyProjectsAddTime(250);
    }

    @Test
    public void test500ProjectsAdd()
    {
        testManyProjectsAddTime(500);
    }

    @Test
    public void test1000ProjectsAdd()
    {
        testManyProjectsAddTime(1000);
    }

    @Test
    public void test10ProjectsRemove()
    {
        testManyProjectDeleteTime(10);
    }

    @Test
    public void test50ProjectsRemove()
    {
        testManyProjectDeleteTime(50);
    }

    @Test
    public void test100ProjectsRemove()
    {
        testManyProjectDeleteTime(100);
    }

    @Test
    public void test250ProjectsRemove()
    {
        testManyProjectDeleteTime(250);
    }

    @Test
    public void test500ProjectsRemove()
    {
        testManyProjectDeleteTime(500);
    }

    @Test
    public void test1000ProjectsRemove()
    {
        testManyProjectDeleteTime(1000);
    }

    @Test
    public void test10ProjectsChange()
    {
        testManyProjectChangeTime(10);
    }

    @Test
    public void test50ProjectsChange()
    {
        testManyProjectChangeTime(50);
    }

    @Test
    public void test100ProjectsChange()
    {
        testManyProjectChangeTime(100);
    }

    @Test
    public void test250ProjectsChange()
    {
        testManyProjectChangeTime(250);
    }

    @Test
    public void test500ProjectsChange()
    {
        testManyProjectChangeTime(500);
    }

    @Test
    public void test1000ProjectsChange()
    {
        testManyProjectChangeTime(1000);
    }
}
