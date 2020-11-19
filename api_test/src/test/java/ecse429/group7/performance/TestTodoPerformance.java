package ecse429.group7.performance;

import org.junit.Test;

import kong.unirest.Unirest;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;

public class TestTodoPerformance extends BasePerformanceTest {
    
    public void addRandomTodo()
    {
        HttpResponse<JsonNode> response = Unirest.post("/todos").body("{\"title\":\"" + getRandomString()
                + "\",\"doneStatus\":" + getRandomBool() + ",\"description\":\"" + getRandomString() + "\"}").asJson();

        task_id_list.add(response.getBody().getObject().getInt("id"));
    }

    public void addManyTodos(int number_todos)
    {
        for (int i = 0; i < number_todos; i++) {
            addRandomTodo();
        }
    }

    public void removeLastTodo()
    {
        Unirest.delete("/todos/" + task_id_list.getLast()).header("Content-Type", "application/json").asJson();
        task_id_list.removeLast();
    }

    public void changeLastTodo()
    {
        Unirest.put("/todos/" + task_id_list.getLast()).body("{\"title\":\"" + getRandomString()
                + "\",\"doneStatus\":" + getRandomBool() + ",\"description\":\"" + getRandomString() + "\"}").asJson();
    }

    public void testManyTodosAddTime(int number_todos)
    {
        long start_whole = System.nanoTime();

        // Setup
        addManyTodos(number_todos);

        // Add one todo
        long start_single = System.nanoTime();
        addRandomTodo();
        long finish_single = System.nanoTime();

        // Reset state
        resetState();

        long finish_whole = System.nanoTime();
        System.out.println("Test Add Todo with " + number_todos + " Todos in Server:");
        System.out.println("\tTotal Test Time: " + (finish_whole - start_whole) + " ns");
        System.out.println("\tSingle Todo Add Time: " + (finish_single - start_single) + " ns");
    }

    public void testManyTodosRemoveTime(int number_todos)
    {
        long start_whole = System.nanoTime();

        // Setup
        addManyTodos(number_todos);

        // Remove single
        long start_single = System.nanoTime();
        removeLastTodo();
        long finish_single = System.nanoTime();

        // Reset state
        resetState();

        long finish_whole = System.nanoTime();
        System.out.println("Test Remove Todo with " + number_todos + " Todos in Server:");
        System.out.println("\tTotal Test Time: " + (finish_whole - start_whole) + " ns");
        System.out.println("\tSingle Todo Remove Time: " + (finish_single - start_single) + " ns");
    }

    public void testManyTodosChangeTime(int number_todos)
    {
        long start_whole = System.nanoTime();

        // Setup
        addManyTodos(number_todos);

        // Change single
        long start_single = System.nanoTime();
        changeLastTodo();
        long finish_single = System.nanoTime();

        // Reset state
        resetState();

        long finish_whole = System.nanoTime();
        System.out.println("Test Change Todo with " + number_todos + " Todos in Server:");
        System.out.println("\tTotal Test Time: " + (finish_whole - start_whole) + " ns");
        System.out.println("\tSingle Todo Change Time: " + (finish_single - start_single) + " ns");
    }

    @Test
    public void test10TodosAdd()
    {
        testManyTodosAddTime(10);
    }

    @Test
    public void test50TodosAdd()
    {
        testManyTodosAddTime(50);
    }

    @Test
    public void test100TodosAdd()
    {
        testManyTodosAddTime(100);
    }

    @Test
    public void test250TodosAdd()
    {
        testManyTodosAddTime(250);
    }

    @Test
    public void test500TodosAdd()
    {
        testManyTodosAddTime(500);
    }

    @Test
    public void test1000TodosAdd()
    {
        testManyTodosAddTime(1000);
    }

    @Test
    public void test10TodosRemove()
    {
        testManyTodosRemoveTime(10);
    }

    @Test
    public void test50TodosRemove()
    {
        testManyTodosRemoveTime(50);
    }

    @Test
    public void test100TodosRemove()
    {
        testManyTodosRemoveTime(100);
    }

    @Test
    public void test250TodosRemove()
    {
        testManyTodosRemoveTime(250);
    }

    @Test
    public void test500TodosRemove()
    {
        testManyTodosRemoveTime(500);
    }

    @Test
    public void test1000TodosRemove()
    {
        testManyTodosRemoveTime(1000);
    }

    @Test
    public void test10TodosChange()
    {
        testManyTodosChangeTime(10);
    }

    @Test
    public void test50TodosChange()
    {
        testManyTodosChangeTime(50);
    }

    @Test
    public void test100TodosChange()
    {
        testManyTodosChangeTime(100);
    }

    @Test
    public void test250TodosChange()
    {
        testManyTodosChangeTime(250);
    }

    @Test
    public void test500TodosChange()
    {
        testManyTodosChangeTime(500);
    }

    @Test
    public void test1000TodosChange()
    {
        testManyTodosChangeTime(1000);
    }
}
