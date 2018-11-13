package org.simple.core.generator;

/**
 * Created by arebya on 18/4/8.
 */
public class SnowFlakeIDGeneratorTest {

    public static void main(String[] args) {
        SnowFlakeIDGenerator idWorker = new SnowFlakeIDGenerator(0, 0);
        for (int i = 0; i < 1000; i++) {
            long id = idWorker.nextId();
            System.out.println(Long.toBinaryString(id));
            System.out.println(id);
        }
    }
}
