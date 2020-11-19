package ecse429.group7.performance;

import java.util.LinkedList;
import java.util.Random;

import ecse429.group7.BaseTest;
import org.apache.commons.text.RandomStringGenerator;

import org.junit.Before;

import kong.unirest.Unirest;

public class BasePerformanceTest extends BaseTest {

    LinkedList<Integer> task_id_list;
    LinkedList<Integer> project_id_list;
    LinkedList<Integer> category_id_list;
    RandomStringGenerator string_generator;
    Random number_generator;

    @Before
    public void before() {
        task_id_list = new LinkedList<>();
        project_id_list = new LinkedList<>();
        category_id_list = new LinkedList<>();
        string_generator = new RandomStringGenerator.Builder()
                .withinRange('a', 'z').build();
        number_generator = new Random();
    }

    public String getRandomString()
    {
        return string_generator.generate(number_generator.nextInt(18) + 2);
    }

    public String getRandomBool()
    {
        return number_generator.nextInt(2) % 2 == 0 ? "true" : "false";
    }

    public void resetState()
    {
        for(Integer id: task_id_list)
        {
            Unirest.delete("/todos/" + id).header("Content-Type", "application/json")
            .asJson();
        }

        for(Integer id: project_id_list)
        {
            Unirest.delete("/projects/" + id).header("Content-Type", "application/json").asJson();
        }
        
        for(Integer id: category_id_list)
        {
            Unirest.delete("/categories/" + id).header("Content-Type", "application/json").asJson();
        }
        
        task_id_list.clear();
        project_id_list.clear();
        category_id_list.clear();
    }
}
