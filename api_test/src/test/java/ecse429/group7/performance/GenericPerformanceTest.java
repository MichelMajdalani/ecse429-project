package ecse429.group7.performance;

import kong.unirest.Unirest;
import org.apache.commons.text.RandomStringGenerator;

import java.util.LinkedList;
import java.util.Random;

import static ecse429.group7.performance.CSVWriter.addLine;
import static org.junit.Assert.assertEquals;

public abstract class GenericPerformanceTest {
    LinkedList<Integer> id_list;
    String type;
    RandomStringGenerator string_generator;
    Random number_generator;

    public static final int STATIC_CODE_NOT_FOUND = 404;
    public static final int NUM_SAMPLES = 50;

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

    public abstract String addRandom();
    public abstract String changeLast();

    public int removeLast() {
        Unirest.delete("/" + type + "/" + id_list.getLast())
                .header("Content-Type", "application/json")
                .asJson();

        return id_list.removeLast();
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
        timeTask(num, TaskType.REMOVE);
    }

    public void timeChange(int num) {
        timeTask(num, TaskType.CHANGE);
    }

    private void timeTask(int num, TaskType t) {
        long start_whole = System.nanoTime();

        // Setup
        addMany(num);
        long single_avg = 0;

        // Perform operation
        for (int i = 0; i < NUM_SAMPLES; i++) {
            long start_single = System.nanoTime();

            String title = "";
            int id_deleted = 0;

            switch (t) {
                case ADD: title = addRandom(); break;
                case REMOVE: id_deleted = removeLast(); break;
                case CHANGE: title = changeLast(); break;
            }
            long finish_single = System.nanoTime();

            single_avg += finish_single - start_single;

            // Verify Accuracy
            switch (t) {
                case ADD:
                case CHANGE:
                    assertEquals(title, Unirest.get("/" + this.type + "/" + id_list.getLast()).asJson().getBody()
                            .getObject().getJSONArray(this.type).getJSONObject(0).getString("title"));
                    break;
                case REMOVE:
                    assertEquals(STATIC_CODE_NOT_FOUND, Unirest.get("/" + this.type + "/" + id_deleted).asJson().getStatus());
                    break;
            }

            //undo single operation
            switch (t) {
                case ADD: removeLast(); break;
                case CHANGE: removeLast(); addRandom(); break;
                case REMOVE: addRandom(); break;
            }

        }

        single_avg /= NUM_SAMPLES;


        // Reset state
        resetState();

        long finish_whole = System.nanoTime();
        addLine(new String[] {
                type, num+"", t.toString(),
                (finish_whole - start_whole) + "", single_avg+""
        });
        System.out.println("Test " + t.toString() + " " + type + " with " + num + " of them in Server:");
        System.out.println("\tTotal Test Time: " + (finish_whole - start_whole) + " ns");
        System.out.println("\tSingle " + type + " Add Time: " + single_avg + " ns");
    }
}
