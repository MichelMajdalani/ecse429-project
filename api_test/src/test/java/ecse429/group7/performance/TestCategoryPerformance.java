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
public class TestCategoryPerformance extends BasePerformanceTest {

    @Parameterized.Parameters(name = "{0} categories")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {10}, {50}, {100}, {250}, {500}, {1000}
        });
    }

    private final int num_categories;

    public TestCategoryPerformance(int num) {
        num_categories = num;
    }

    @Test
    public void testCategoriesAdd() {
        testManyCategoriesAddTime(num_categories);
    }

    @Test
    public void testCategoriesRemove()
    {
        testManyCategoriesDeleteTime(num_categories);
    }

    @Test
    public void testCategoriesChange() {
        testManyCategoriesChangeTime(num_categories);
    }




    public void addRandomCategory()
    {
        HttpResponse<JsonNode> response = Unirest.post("/categories").header("Content-Type", "application/json").body(
                "{\n   \"title\":\"" + getRandomString() + "\",\n \"description\":\"" + getRandomString() + "\"\n}\n")
                .asJson();

        category_id_list.add(response.getBody().getObject().getInt("id"));
    }
    
    public void removeLastCategory()
    {
        Unirest.delete("/categories/" + category_id_list.getLast()).header("Content-Type", "application/json").asJson();
        category_id_list.removeLast();
    }
    
    public void changeLastCategory()
    {
        Unirest.put("/categories/" + category_id_list.getLast()).header("Content-Type", "application/json").body(
                "{\n   \"title\":\"" + getRandomString() + "\",\n \"description\":\"" + getRandomString() + "\"\n}\n")
                .asJson();
    }

    public void addManyCategories(int number_categories)
    {
        for (int i = 0; i < number_categories; i++) {
            addRandomCategory();
        }
    }

    public void testManyCategoriesAddTime(int number_categories)
    {
        long start_whole = System.nanoTime();

        // Setup
        addManyCategories(number_categories);

        // Add one category
        long start_single = System.nanoTime();
        addRandomCategory();
        long finish_single = System.nanoTime();

        // Reset state
        resetState();

        long finish_whole = System.nanoTime();
        System.out.println("Test Add Category with " + number_categories + " Categories in Server:");
        System.out.println("\tTotal Test Time: " + (finish_whole - start_whole) + " ns");
        System.out.println("\tSingle Category Add Time: " + (finish_single - start_single) + " ns");
    }

    public void testManyCategoriesDeleteTime(int number_categories)
    {
        long start_whole = System.nanoTime();

        // Setup
        addManyCategories(number_categories);

        // Remove last category
        long start_single = System.nanoTime();
        removeLastCategory();
        long finish_single = System.nanoTime();

        // Reset state
        resetState();

        long finish_whole = System.nanoTime();
        System.out.println("Test Remove Category with " + number_categories + " Categories in Server:");
        System.out.println("\tTotal Test Time: " + (finish_whole - start_whole) + " ns");
        System.out.println("\tSingle Category Remove Time: " + (finish_single - start_single) + " ns");
    }

    public void testManyCategoriesChangeTime(int number_categories)
    {
        long start_whole = System.nanoTime();

        // Setup
        addManyCategories(number_categories);

        // Change last category
        long start_single = System.nanoTime();
        changeLastCategory();
        long finish_single = System.nanoTime();

        // Reset state
        resetState();

        long finish_whole = System.nanoTime();
        System.out.println("Test Change Category with " + number_categories + " Categories in Server:");
        System.out.println("\tTotal Test Time: " + (finish_whole - start_whole) + " ns");
        System.out.println("\tSingle Category Change Time: " + (finish_single - start_single) + " ns");
    }
}
