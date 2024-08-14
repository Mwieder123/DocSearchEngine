import static org.junit.jupiter.api.Assertions.*;

import edu.yu.cs.com1320.project.stage6.DocumentStore;
import edu.yu.cs.com1320.project.stage6.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage6.impl.DocumentStoreImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
public class DoStoreDoOverTest {
    private DocumentStoreImpl docStore;
    private final File baseDir = new File("RedoTesting");

    @BeforeEach
    void setUp(){
        this.docStore = new DocumentStoreImpl(baseDir);
    }

    @Test
    public void putAndThenMaxDocCount() throws URISyntaxException, IOException {
        this.docStore.put(new ByteArrayInputStream("Hello".getBytes()), new URI("http://www.yu.edu/documents/doc1"), DocumentStore.DocumentFormat.TXT);
        this.docStore.put(new ByteArrayInputStream("Shalom".getBytes()), new URI("http://www.yu.edu/documents/doc2"), DocumentStore.DocumentFormat.TXT);
        this.docStore.put(new ByteArrayInputStream("Goodbye".getBytes()), new URI("http://www.yu.edu/documents/doc3"), DocumentStore.DocumentFormat.TXT);

        this.docStore.setMaxDocumentCount(1);

        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc2.json").exists());
    }
    @Test
    public void MaxDocCountAndThenPut() throws URISyntaxException, IOException{
        this.docStore.setMaxDocumentCount(2);

        this.docStore.put(new ByteArrayInputStream("Shalom".getBytes()), new URI("http://www.yu.edu/documents/file1"), DocumentStore.DocumentFormat.TXT);
        this.docStore.put(new ByteArrayInputStream("Goodbye".getBytes()), new URI("http://www.yu.edu/documents/file2"), DocumentStore.DocumentFormat.TXT);
        this.docStore.put(new ByteArrayInputStream("No".getBytes()), new URI("http://www.yu.edu/documents/file3"), DocumentStore.DocumentFormat.TXT);

        assertTrue(new File("RedoTesting/www.yu.edu/documents/file1.json").exists());

        this.docStore.put(new ByteArrayInputStream("Yes".getBytes()), new URI("http://www.yu.edu/documents/file4"), DocumentStore.DocumentFormat.TXT);

        assertTrue(new File("RedoTesting/www.yu.edu/documents/file2.json").exists());
    }
    @Test
    public void MaxBytesAndThenPut() throws URISyntaxException, IOException{
        this.docStore.setMaxDocumentBytes(10);

        this.docStore.put(new ByteArrayInputStream("Hello".getBytes()), new URI("http://www.yu.edu/documents/Rachel1"), DocumentStore.DocumentFormat.TXT);
        this.docStore.put(new ByteArrayInputStream("Shalom".getBytes()), new URI("http://www.yu.edu/documents/Rachel2"), DocumentStore.DocumentFormat.TXT);
        assertTrue(new File("RedoTesting/www.yu.edu/documents/Rachel1.json").exists());
        this.docStore.put(new ByteArrayInputStream("Goodbye".getBytes()), new URI("http://www.yu.edu/documents/Rachel3"), DocumentStore.DocumentFormat.TXT);
        assertTrue(new File("RedoTesting/www.yu.edu/documents/Rachel2.json").exists());


        this.docStore.put(new ByteArrayInputStream("Hello".getBytes()), new URI("http://www.yu.edu/documents/Rachel4"), DocumentStore.DocumentFormat.TXT);

        assertTrue(new File("RedoTesting/www.yu.edu/documents/file3.json").exists());
    }
    @Test
    public void PutAndThenMaxBytes() throws URISyntaxException, IOException{
        this.docStore.put(new ByteArrayInputStream("Hello".getBytes()), new URI("http://www.yu.edu/documents/Rachel1"), DocumentStore.DocumentFormat.TXT);
        this.docStore.put(new ByteArrayInputStream("Shalom".getBytes()), new URI("http://www.yu.edu/documents/Rachel2"), DocumentStore.DocumentFormat.TXT);
        this.docStore.put(new ByteArrayInputStream("Goodbye".getBytes()), new URI("http://www.yu.edu/documents/Rachel3"), DocumentStore.DocumentFormat.TXT);
        this.docStore.put(new ByteArrayInputStream("Hello".getBytes()), new URI("http://www.yu.edu/documents/Rachel4"), DocumentStore.DocumentFormat.TXT);
        this.docStore.setMaxDocumentBytes(15);

        assertTrue(new File("RedoTesting/www.yu.edu/documents/Rachel1.json").exists());
        assertTrue(new File("RedoTesting/www.yu.edu/documents/Rachel2.json").exists());


        assertFalse(new File("RedoTesting/www.yu.edu/documents/file3.json").exists());
    }

}
