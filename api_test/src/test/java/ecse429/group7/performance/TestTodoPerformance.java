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
public class TestTodoPerformance extends BasePerformanceTest {

    private final int number_todos;

    @Parameterized.Parameters(name = "{0} todos")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {10}, {50}, {100}, {250}, {500}, {1000}
        });
    }

    @Test
    public void testTodosAdd() {
        testManyTodosAddTime(number_todos);
    }

    @Test
    public void testTodosRemove() {
        testManyTodosRemoveTime(number_todos);
    }
    @Test
    public void testTodosChange() {
        testManyTodosChangeTime(number_todos);
    }




    public TestTodoPerformance(int num) {
        number_todos = num;
    }

    public void addRandomTodo() {
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
}
