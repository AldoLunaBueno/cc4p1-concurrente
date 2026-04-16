package uni.cc4p1.labo_01;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;


public class PriorityQueueTest {
    private PriorityQueue pq;

    @BeforeEach
    void setUp() {
        pq = new PriorityQueue();
    }

    @Test
    @DisplayName("Debería extraer el nodo con la distancia mínima")
    void testRemoveMinDistance() {
        pq.add(new PQItem("A").setPriority(15));
        pq.add(new PQItem("B").setPriority(10));
        pq.add(new PQItem("C").setPriority(5));
        
        assertEquals(5, pq.remove().getPriority());
    }

    @Test
    @DisplayName("Debería mantener el orden tras múltiples extracciones")
    void testMultipleRemoves() {
        pq.add(new PQItem("D").setPriority(20));
        pq.add(new PQItem("A").setPriority(5));
        pq.add(new PQItem("C").setPriority(15));
        pq.add(new PQItem("B").setPriority(10));

        assertEquals(5, pq.remove().getPriority());
        assertEquals(10, pq.remove().getPriority());
        assertEquals(15, pq.remove().getPriority());
        assertEquals(20, pq.remove().getPriority());
    }

    @Test
    @DisplayName("Debería funcionar correctamente con elementos duplicados")
    void testDuplicateDistances() {
        pq.add(new PQItem().setPriority(10));
        pq.add(new PQItem().setPriority(10));

        assertEquals(10, pq.remove().getPriority());
        assertEquals(10, pq.remove().getPriority());
    }
}

