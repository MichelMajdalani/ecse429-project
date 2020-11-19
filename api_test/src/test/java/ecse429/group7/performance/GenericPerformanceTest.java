package ecse429.group7.performance;

import kong.unirest.Unirest;
import org.apache.commons.text.RandomStringGenerator;

import java.util.LinkedList;
import java.util.Random;

public abstract class GenericPerformanceTest {
    LinkedList<Integer> id_list;
    String type;
    RandomStringGenerator string_generator;
    Random number_generator;

    private enum TaskType { ADD, CHANGE, REMOVE }

    public GenericPerformanceTest(String type) {
        id_list = new LinkedList<>();
        this.type = type;
        string_generator = new RandomStringGenerator.Builder()
                .withinRange('a', 'z').build();
        number_generator = new Random();
    }

    public String getRandomString() {
        return string_generator.generate(number_generator.nextInt(18) + 2);
    }

    public String getRandomBool() {
        return number_generator.nextBoolean() + "";
    }

    public abstract void addRandom();
    public abstract void changeLast();

    public void removeLast() {
        Unirest.delete("/" + type + "/" + id_list.removeLast())
                .header("Content-Type", "application/json")
                .asJson();

    }

    public void addMany(int n) {
        for (int i = 0; i < n; i++) {
            addRandom();
        }
    }

    public void resetState() {
        for(int id: id_list) {
            Unirest.delete("/" + type + "/" + id).header("Content-Type", "application/json").asJson();
        }
        id_list.clear();
    }

    public void timeAdd(int num) {
        timeTask(num, TaskType.ADD);
    }

    public void timeRemove(int num) {
        timeTask(num, TaskType.CHANGE);
    }

    public void timeChange(int num) {
        timeTask(num, TaskType.REMOVE);
    }

    private void timeTask(int num, TaskType t) {
        long start_whole = System.nanoTime();

        // Setup
        addMany(num);

        // Add one category
        long start_single = System.nanoTime();
        switch (t) {
            case ADD: addRandom(); break;
            case REMOVE: removeLast(); break;
            case CHANGE: changeLast(); break;
        }
        long finish_single = System.nanoTime();

        // Reset state
        resetState();

        long finish_whole = System.nanoTime();
        System.out.println("Test " + t.toString() + " " + type + " with " + num + " of them in Server:");
        System.out.println("\tTotal Test Time: " + (finish_whole - start_whole) + " ns");
        System.out.println("\tSingle " + type + " Add Time: " + (finish_single - start_single) + " ns");
    }
}
