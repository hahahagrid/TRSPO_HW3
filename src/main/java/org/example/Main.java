package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class CollatzCalculator implements Runnable {
    private final int start;
    private final int end;
    private final List<Integer> numbers;

    public CollatzCalculator(int start, int end, List<Integer> numbers) {
        this.start = start;
        this.end = end;
        this.numbers = numbers;
    }

    @Override
    public void run() {
        for (int i = start; i <= end; i++) {
            int steps = calculateCollatzSteps(i);
            synchronized (numbers) {
                numbers.add(steps);
            }
        }
    }

    private int calculateCollatzSteps(int n) {
        int steps = 0;
        while (n != 1) {
            if (n % 2 == 0) {
                n = n / 2;
            } else {
                n = 3 * n + 1;
            }
            steps++;
        }
        return steps;
    }
}

public class Main {
    public static void main(String[] args) {
        int N = 100; // К-сть чисел
        int numThreads = 4; // К-сть потоків

        List<Integer> numbers = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        int numbersPerThread = N / numThreads;
        int start = 1;
        int end = numbersPerThread;

        for (int i = 0; i < numThreads; i++) {
            if (i == numThreads - 1) {
                end = N;
            }
            Runnable worker = new CollatzCalculator(start, end, numbers);
            executor.execute(worker);
            start = end + 1;
            end = start + numbersPerThread - 1;
        }

        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            System.err.println("Calculation interrupted: " + e.getMessage());
        }

        double averageSteps = calculateAverage(numbers);
        System.out.println("Average steps: " + averageSteps);
    }

    private static double calculateAverage(List<Integer> numbers) {
        if (numbers.isEmpty()) {
            return 0;
        }
        int sum = 0;
        for (int num : numbers) {
            sum += num;
        }
        return (double) sum / numbers.size();
    }
}
