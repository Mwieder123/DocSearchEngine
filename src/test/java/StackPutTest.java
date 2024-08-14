import edu.yu.cs.com1320.project.Stack;
import edu.yu.cs.com1320.project.impl.StackImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class StackPutTest {
    private Stack<String> stack;

    @BeforeEach
    void setUp(){
        this.stack = new StackImpl<String>();
    }
    @Test
    public void pushTest(){
        assertThrows(IllegalArgumentException.class, () -> {
            this.stack.push(null);
        });
    }
    @Test
    public void popTest(){
        assertNull(this.stack.pop());

        this.stack.push("Hello");
        this.stack.push("Shalom");
        this.stack.push("Goodbye");
        this.stack.push("Today");

        assertEquals("Today", this.stack.pop());
        assertEquals("Goodbye", this.stack.pop());

        assertEquals(2, this.stack.size());
    }
    @Test
    public void peekTest(){
        assertNull(this.stack.peek());

        this.stack.push("Hello");
        this.stack.push("Shalom");
        this.stack.push("Goodbye");
        this.stack.push("Today");

        assertEquals("Today", this.stack.peek());
        assertEquals("Today", this.stack.peek());

        assertEquals(4, this.stack.size());
    }
    @Test
    public void sizeTest(){
        assertEquals(0, this.stack.size());

        this.stack.push("Hello");
        this.stack.push("Shalom");
        this.stack.push("Goodbye");
        this.stack.push("Today");

        assertEquals(4, this.stack.size());
    }


}
