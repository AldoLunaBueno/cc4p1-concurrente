package uni.cc4p1.labo_01;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DijkstraTest {
    @Test
    void testExecute() {
        Dijkstra dijkstra = new Dijkstra();
        Node a = new Node("A");
        Node b = new Node("B");
        Node c = new Node("C");
        Node d = new Node("D");
        Node e = new Node("E");
        Node f = new Node("F");
        a.addDestination(b, 4);
        a.addDestination(c, 2);
        b.addDestination(a, 4);
        b.addDestination(c, 1);
        b.addDestination(d, 5);
        c.addDestination(a, 2);
        c.addDestination(b, 1);
        c.addDestination(d, 8);
        c.addDestination(e, 10);
        d.addDestination(b, 5);
        d.addDestination(c, 8);
        d.addDestination(e, 2);
        d.addDestination(f, 6);
        e.addDestination(c, 10);
        e.addDestination(d, 2);
        e.addDestination(f, 2);
        f.addDestination(d, 6);
        f.addDestination(e, 2);
        dijkstra.execute(a);

        var distances = dijkstra.getDistances();
        assertEquals(distances.get(a), 0.0);
        assertEquals(distances.get(b), 3.0);
        assertEquals(distances.get(c), 2.0);
        assertEquals(distances.get(d), 8.0);
        assertEquals(distances.get(e), 10.0);
        assertEquals(distances.get(f), 12.0);
        
    }
}
