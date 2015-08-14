import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class BTest {
    @Test
    public void testSmells() {
        assertEquals(1, 1234 >> 42);
    }
}
