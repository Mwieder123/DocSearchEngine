import static org.junit.jupiter.api.Assertions.*;

import edu.yu.cs.com1320.project.stage6.DocumentStore;
import edu.yu.cs.com1320.project.stage6.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage6.impl.DocumentStoreImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
public class Stage6UndoTest {
    private DocumentStoreImpl docStore;
    @BeforeEach
    void setUp() throws IOException {
        this.docStore = new DocumentStoreImpl();
        this.docStore.put(new ByteArrayInputStream("Hello1".getBytes()), URI.create("doc1"), DocumentStore.DocumentFormat.BINARY);
        this.docStore.put(new ByteArrayInputStream("Hello4".getBytes()), URI.create("doc4"), DocumentStore.DocumentFormat.BINARY);
        this.docStore.put(new ByteArrayInputStream("Hello3".getBytes()), URI.create("doc3"), DocumentStore.DocumentFormat.BINARY);
        this.docStore.put(new ByteArrayInputStream("Hello2".getBytes()), URI.create("doc2"), DocumentStore.DocumentFormat.BINARY);
        this.docStore.put(new ByteArrayInputStream("Hello5".getBytes()), URI.create("doc5"), DocumentStore.DocumentFormat.BINARY);
    }
    @Test
    public void undoPutTest() throws IOException{
        this.docStore.undo();
        assertNull(this.docStore.get(URI.create("doc5")));
    }
    @Test
    public void undoPutToMemoryTest() throws IOException{
        this.docStore.setMaxDocumentCount(4);
        this.docStore.put(new ByteArrayInputStream("Tomorrow".getBytes()), URI.create("doc1"), DocumentStore.DocumentFormat.BINARY);
        this.docStore.undo(URI.create("doc1"));
    }
}
