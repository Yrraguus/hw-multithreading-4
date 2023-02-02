// https://github.com/netology-code/jd-homeworks/blob/video/concurrent_collections/task1/README.md

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Main {
    public static final int lengthOfString = 10_000; // 10_000
    public static final int amountOfStrings = 100_000; // 100_000
    public static BlockingQueue<String> as = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> bs = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> cs = new ArrayBlockingQueue<>(100);

    public static void main(String[] args) throws InterruptedException {
        AtomicReference<String> aMaxString = new AtomicReference<>();
        AtomicReference<String> bMaxString = new AtomicReference<>();
        AtomicReference<String> cMaxString = new AtomicReference<>();
        AtomicInteger maxA = new AtomicInteger();
        AtomicInteger maxB = new AtomicInteger();
        AtomicInteger maxC = new AtomicInteger();
        Thread threadGen = new Thread(() -> {
            for (int i = 0; i < amountOfStrings; i++) {
                String text = generateText("abc", lengthOfString);
                try {
                    as.put(text);
                    bs.put(text);
                    cs.put(text);
                } catch (InterruptedException e) {
                    return;
                }
            }
        });
        Thread threadA = new Thread(() -> {
            int max = 0;
            String s = null;
            for (int i = 0; i < amountOfStrings; i++) {
                int counter = 0;
                try {
                    s = as.take();
                } catch (InterruptedException e) {
                    return;
                }
                for (int j = 0; j < lengthOfString; j++) {
                    if (s.charAt(j) == 'a') {
                        counter++;
                    }
                }
                if (max < counter) {
                    max = counter;
                    aMaxString.set(s);
                }
            }
            maxA.set(max);
        });
        Thread threadB = new Thread(() -> {
            int max = 0;
            String s = null;
            for (int i = 0; i < amountOfStrings; i++) {
                int counter = 0;
                try {
                    s = bs.take();
                } catch (InterruptedException e) {
                    return;
                }
                for (int j = 0; j < lengthOfString; j++) {
                    if (s.charAt(j) == 'b') {
                        counter++;
                    }
                }
                if (max < counter) {
                    max = counter;
                    bMaxString.set(s);
                }
            }
            maxB.set(max);
        });
        Thread threadC = new Thread(() -> {
            int max = 0;
            String s = null;
            for (int i = 0; i < amountOfStrings; i++) {
                int counter = 0;
                try {
                    s = cs.take();
                } catch (InterruptedException e) {
                    return;
                }
                for (int j = 0; j < lengthOfString; j++) {
                    if (s.charAt(j) == 'c') {
                        counter++;
                    }
                }
                if (max < counter) {
                    max = counter;
                    cMaxString.set(s);
                }
            }
            maxC.set(max);
        });

        threadGen.start();
        threadA.start();
        threadB.start();
        threadC.start();
        threadA.join();
        threadB.join();
        threadC.join();
        threadGen.join();

        System.out.println("Текст, в котором содержится максимальное количество (" + maxA + ") символов 'a': " + aMaxString);
        System.out.println("Текст, в котором содержится максимальное количество (" + maxB + ") символов 'b': " + bMaxString);
        System.out.println("Текст, в котором содержится максимальное количество (" + maxC + ") символов 'c': " + cMaxString);
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}