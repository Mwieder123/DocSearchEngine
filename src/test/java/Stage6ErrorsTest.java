import static org.junit.jupiter.api.Assertions.*;
import edu.yu.cs.com1320.project.stage6.DocumentStore;
import edu.yu.cs.com1320.project.stage6.impl.DocumentStoreImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Stage6ErrorsTest {
    private DocumentStoreImpl docStore;
    private final File baseDir = new File("RedoTesting");

    @BeforeEach
    void setUp() throws IOException, URISyntaxException{
        this.docStore = new DocumentStoreImpl(baseDir);
        File file1 = new File("RedoTesting/www.yu.edu/documents/doc1.json");
        File file2 = new File("RedoTesting/www.yu.edu/documents/doc2.json");
        File file3 = new File("RedoTesting/www.yu.edu/documents/doc3.json");
        file1.delete();
        file2.delete();
        file3.delete();

        this.docStore.put(new ByteArrayInputStream("Hello".getBytes()), new URI("http://www.yu.edu/documents/doc1"), DocumentStore.DocumentFormat.TXT);
        this.docStore.put(new ByteArrayInputStream("Shalom".getBytes()), new URI("http://www.yu.edu/documents/doc2"), DocumentStore.DocumentFormat.TXT);
        this.docStore.put(new ByteArrayInputStream("Goodbye".getBytes()), new URI("http://www.yu.edu/documents/doc3"), DocumentStore.DocumentFormat.TXT);
    }

    @Test
    public void stage6PushToDiskViaMaxDocCountViaUndoDelete() throws IOException, URISyntaxException{
        this.docStore.delete(new URI("http://www.yu.edu/documents/doc2"));
        this.docStore.setMaxDocumentCount(1);
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc2.json").exists());
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc3.json").exists());
        assertNull(this.docStore.get(new URI("http://www.yu.edu/documents/doc2")));

        this.docStore.put(new ByteArrayInputStream("No".getBytes()), new URI("http://www.yu.edu/documents/doc4"), DocumentStore.DocumentFormat.TXT);
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc3.json").exists());

        this.docStore.undo(new URI("http://www.yu.edu/documents/doc2"));
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc4.json").exists());
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc2.json").exists());
        assertNotNull(this.docStore.get(new URI("http://www.yu.edu/documents/doc2")));
    }
    @Test
    public void sameAsAboveWithNotSpecificUndo() throws IOException, URISyntaxException{
        this.docStore.delete(new URI("http://www.yu.edu/documents/doc2"));
        this.docStore.setMaxDocumentCount(1);
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc2.json").exists());
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc3.json").exists());
        assertNull(this.docStore.get(new URI("http://www.yu.edu/documents/doc2")));


        this.docStore.undo();
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc3.json").exists());
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc2.json").exists());
        assertNotNull(this.docStore.get(new URI("http://www.yu.edu/documents/doc2")));
    }
    @Test
    public void stage6PushToDiskViaMaxDocCountBringBackInViaDeleteAndSearch() throws IOException, URISyntaxException{
        this.docStore.setMaxDocumentCount(2);
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc2.json").exists());
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc3.json").exists());

        this.docStore.delete(new URI("http://www.yu.edu/documents/doc3"));

        this.docStore.search("Hello");
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc2.json").exists());
    }
}
