
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ResizeTest {
    private HashMap<String, Integer> myTable;
    @BeforeEach
    void setUp(){
        this.myTable = new HashMap<>();
    }
    @Test
    public void checkResize(){
        for(int i = 0; i < 100; i++){
            this.myTable.put(i + "hi", i);
        }


        assertEquals(100, this.myTable.size());
    }
    @Test
    public void checkByteSizes(){
        System.out.println("Hello".getBytes().length);
    }



}
