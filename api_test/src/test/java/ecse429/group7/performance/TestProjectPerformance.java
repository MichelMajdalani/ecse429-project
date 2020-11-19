package ecse429.group7.performance;

import org.junit.Test;

import kong.unirest.Unirest;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class TestProjectPerformance extends BasePerformanceTest {

    private final int num_projects;

    @Parameterized.Parameters(name = "{0} projects")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {10}, {50}, {100}, {250}, {500}, {1000}
        });
    }

    public TestProjectPerformance(int num) {
        num_projects = num;
    }

    @Test
    public void testProjectsAdd() {
        testManyProjectsAddTime(num_projects);
    }

    @Test
    public void testProjectsRemove() {
        testManyProjectDeleteTime(num_projects);
    }

    @Test
    public void testProjectsChange() {
        testManyProjectChangeTime(num_projects);
    }



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
}
