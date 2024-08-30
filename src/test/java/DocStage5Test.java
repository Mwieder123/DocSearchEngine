import static org.junit.jupiter.api.Assertions.*;

import edu.yu.cs.com1320.project.stage6.impl.DocumentImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;

public class DocStage5Test {
    private DocumentImpl doc1;
    DocumentImpl doc2;
    @BeforeEach
    void setUp() throws URISyntaxException {
        this.doc1 = new DocumentImpl(new URI("Hello"), "Hello To You!", null);
        this.doc2 = new DocumentImpl(new URI("Hi"), "Hi", null);
    }

    @Test
    public void TimeTest(){
        assertTrue(doc2.getLastUseTime() - doc1.getLastUseTime() > 0);
        this.doc1.setLastUseTime(System.nanoTime());
        assertTrue(doc2.getLastUseTime() - doc1.getLastUseTime() < 0);
    }
    @Test
    public void CompareToTest(){
        assertTrue(doc2.compareTo(doc1) > 0);
    }
}
