package ecse429.group7.performance;

import org.junit.Test;

import kong.unirest.Unirest;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;

public class TestCategoryPerformance extends BasePerformanceTest {
    
    public void addRandomCategory()
    {
        HttpResponse<JsonNode> response = Unirest.post("/categories").header("Content-Type", "application/json").body(
                "{\n   \"title\":\"" + getRandomString() + "\",\n \"description\":\"" + getRandomString() + "\"\n}\n")
                .asJson();

        category_id_list.add(response.getBody().getObject().getInt("id"));
    }
    
    public void removeLastCategory()
    {
        Unirest.delete("/categories/" + category_id_list.removeLast()).header("Content-Type", "application/json").asJson();
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

    @Test
    public void test10CategoriesAdd()
    {
        testManyCategoriesAddTime(10);
    }

    @Test
    public void test50CategoriesAdd()
    {
        testManyCategoriesAddTime(50);
    }

    @Test
    public void test100CategoriesAdd()
    {
        testManyCategoriesAddTime(100);
    }

    @Test
    public void test250CategoriesAdd()
    {
        testManyCategoriesAddTime(250);
    }

    @Test
    public void test500CategoriesAdd()
    {
        testManyCategoriesAddTime(500);
    }

    @Test
    public void test1000CategoriesAdd()
    {
        testManyCategoriesAddTime(1000);
    }

    @Test
    public void test10CategoriesRemove()
    {
        testManyCategoriesDeleteTime(10);
    }

    @Test
    public void test50CategoriesRemove()
    {
        testManyCategoriesDeleteTime(50);
    }

    @Test
    public void test100CategoriesRemove()
    {
        testManyCategoriesDeleteTime(100);
    }

    @Test
    public void test250CategoriesRemove()
    {
        testManyCategoriesDeleteTime(250);
    }

    @Test
    public void test500CategoriesRemove()
    {
        testManyCategoriesDeleteTime(500);
    }

    @Test
    public void test1000CategoriesRemove()
    {
        testManyCategoriesDeleteTime(1000);
    }

    @Test
    public void test10CategoriesChange()
    {
        testManyCategoriesChangeTime(10);
    }

    @Test
    public void test50CategoriesChange()
    {
        testManyCategoriesChangeTime(50);
    }

    @Test
    public void test100CategoriesChange()
    {
        testManyCategoriesChangeTime(100);
    }

    @Test
    public void test250CategoriesChange()
    {
        testManyCategoriesChangeTime(250);
    }

    @Test
    public void test500CategoriesChange()
    {
        testManyCategoriesChangeTime(500);
    }

    @Test
    public void test1000CategoriesChange()
    {
        testManyCategoriesChangeTime(1000);
    }
}
