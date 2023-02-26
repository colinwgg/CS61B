package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  @Test
  public void testThreeAddThreeRemove() {
    AListNoResizing anr = new AListNoResizing<>();
    BuggyAList ba = new BuggyAList<>();

    anr.addLast(4);
    anr.addLast(5);
    anr.addLast(6);
    ba.addLast(4);
    ba.addLast(5);
    ba.addLast(6);

    assertEquals(anr.size(), ba.size());
    assertEquals(anr.removeLast(), ba.removeLast());
    assertEquals(anr.removeLast(), ba.removeLast());
    assertEquals(anr.removeLast(), ba.removeLast());
  }

  @Test
  public void randomizedTest() {
    AListNoResizing<Integer> L = new AListNoResizing<>();
    BuggyAList B = new BuggyAList<>();

    int N = 5000;
    for (int i = 0; i < N; i += 1) {
      int operationNumber = StdRandom.uniform(0, 4);
      if (operationNumber == 0) {
        // addLast
        int randVal = StdRandom.uniform(0, 100);
        L.addLast(randVal);
        B.addLast(randVal);
      } else if (operationNumber == 1) {
        // size
        int size = L.size();
      }
      if (L.size() == 0) {
        continue;
      }
      if (operationNumber == 2) {
        int val = L.getLast();
        assertEquals(L.getLast(), B.getLast());
      }
      if (operationNumber == 3) {
        int val = L.removeLast();
        assertEquals(val, (int) B.removeLast());
      }
    }
  }
}
