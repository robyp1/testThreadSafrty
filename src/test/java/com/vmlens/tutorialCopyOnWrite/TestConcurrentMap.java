package com.vmlens.tutorialCopyOnWrite;

import com.vmlens.api.AllInterleavings;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertEquals;

/**
 * Each method of ConcurrentHashMap is thread-safe. But calling multiple methods from ConcurrentHashMap for the same key leads to race conditions.
 * And calling the same method from ConcurrentHashMap recursively for different keys leads to deadlocks.
 * <p>
 * For example reading and update the same key by two thread can lead to race condition :
 * The race condition happens when we read an element from the map, modify this element, and write the element back into the map.
 */
public class TestConcurrentMap {


    /**
     * Questo update non è threadsafe perchè non è sinchronizzato in modo che lettura e scritture siano atomiche
     * usare compute oppure synchronize con double check locking
     * @param map
     */
    @Ignore
    public void updateWithRaceCondition(ConcurrentHashMap<Integer, Integer> map) {
        Integer result = map.get(1); //i due thread possono leggere lo stesso valore, 1
        if (result == null) {
            map.put(1, 1); //quindi entrambi aggiornano a 1
        } else {
            map.put(1, result + 1);
        }
    }

    /**
     * Questo update è threadsafe perchè compute è sinchronizzato in modo che lettura e scritture sono atomiche
     * @param map
     */
    public void updateOK(ConcurrentHashMap<Integer, Integer> map) {
        map.compute(1, (key, value) -> {
            if (value == null) {
                return 1;
            }
            return value + 1;
        });
    }

    @Test
    public void testUpdate() throws InterruptedException {
        try (AllInterleavings allInterleavings =
                     new AllInterleavings("TestUpdateWrong");) {
            while (allInterleavings.hasNext()) {
                final ConcurrentHashMap<Integer, Integer> map =
                        new ConcurrentHashMap<Integer, Integer>();
                Thread first = new Thread(() -> {
                    updateWithRaceCondition(map);
                });
                Thread second = new Thread(() -> {
                    updateWithRaceCondition(map);
                });
                first.start();
                second.start();
                first.join();
                second.join();
                assertEquals(2, map.get(1).intValue());
            }
        }
    }
}

