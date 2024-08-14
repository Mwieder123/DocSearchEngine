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
public class DocStoreSearchesDoOverTest {
    private DocumentStoreImpl docStore;
    private final File baseDir = new File("RedoTesting");

    @BeforeEach
    void setUp() throws URISyntaxException, IOException {
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
    public void setMaxDocsAndThenSetMetadataTest() throws URISyntaxException, IOException {
        this.docStore.setMaxDocumentCount(1);

        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc2.json").exists());

        this.docStore.setMetadata(new URI("http://www.yu.edu/documents/doc1"), "Key", "Value");
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc3.json").exists());
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
    }
    @Test
    public void setMetaDataAndTheMaxDocs() throws URISyntaxException, IOException{
        this.docStore.setMetadata(new URI("http://www.yu.edu/documents/doc1"), "Key", "Value");
        this.docStore.setMetadata(new URI("http://www.yu.edu/documents/doc2"), "Key", "Value");

        this.docStore.setMaxDocumentCount(2);
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc3.json").exists());
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc2.json").exists());
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
    }
    @Test
    public void setMetaDataAndThenMaxBytes()throws IOException, URISyntaxException{
        this.docStore.setMetadata(new URI("http://www.yu.edu/documents/doc1"), "Key", "Value");
        this.docStore.setMetadata(new URI("http://www.yu.edu/documents/doc2"), "Key", "Value");

        this.docStore.setMaxDocumentBytes(10);
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc3.json").exists());
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc2.json").exists());
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
    }
    @Test
    public void setMaxBytesAndThenSetMetadata() throws IOException, URISyntaxException{
        this.docStore.setMaxDocumentBytes(10);

        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc2.json").exists());

        this.docStore.setMetadata(new URI("http://www.yu.edu/documents/doc1"), "Key", "Value");
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc3.json").exists());
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
    }
    @Test
    public void getAndThenSetMaxDocs() throws IOException, URISyntaxException{
        this.docStore.get(new URI("http://www.yu.edu/documents/doc1"));
        this.docStore.get(new URI("http://www.yu.edu/documents/doc2"));

        this.docStore.setMaxDocumentCount(1);
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc2.json").exists());
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc3.json").exists());
    }
    @Test
    public void setMaxDocsAndThenGet() throws IOException, URISyntaxException{
        this.docStore.setMaxDocumentCount(1);
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc3.json").exists());
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc2.json").exists());

        this.docStore.get(new URI("http://www.yu.edu/documents/doc1"));
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc3.json").exists());

        this.docStore.get(new URI("http://www.yu.edu/documents/doc2"));
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc2.json").exists());
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
    }
    @Test
    public void setMaxBytesAndThenGet() throws IOException, URISyntaxException{
        this.docStore.setMaxDocumentBytes(10);
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc3.json").exists());
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc2.json").exists());

        this.docStore.get(new URI("http://www.yu.edu/documents/doc1"));
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc3.json").exists());

        this.docStore.get(new URI("http://www.yu.edu/documents/doc2"));
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc2.json").exists());
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
    }
    @Test
    public void getAndThenSetMaxBytes() throws IOException, URISyntaxException{
        this.docStore.get(new URI("http://www.yu.edu/documents/doc1"));
        this.docStore.get(new URI("http://www.yu.edu/documents/doc2"));

        this.docStore.setMaxDocumentCount(1);
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc2.json").exists());
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc3.json").exists());
    }
    @Test
    public void searchAndThenMaxDocs() throws IOException{
        this.docStore.search("Hello");
        this.docStore.search("Shalom");

        this.docStore.setMaxDocumentCount(1);
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc2.json").exists());
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc3.json").exists());
    }
    @Test
    public void searchAndThenMaxBytes()throws IOException{
        this.docStore.search("Hello");
        this.docStore.search("Shalom");

        this.docStore.setMaxDocumentBytes(10);
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc2.json").exists());
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc3.json").exists());
    }
    @Test
    public void setMaxBytesAndThenSearch() throws IOException{
        this.docStore.setMaxDocumentBytes(10);

        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc2.json").exists());

        this.docStore.search("Hello");
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc3.json").exists());
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
    }
    @Test
    public void setMaxDocAndThenSearch() throws IOException{
        this.docStore.setMaxDocumentCount(1);

        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc2.json").exists());

        this.docStore.search("Hello");
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc3.json").exists());
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
    }
    @Test
    public void MaxBytesAndThenSearch() throws IOException{
        this.docStore.setMaxDocumentBytes(15);
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc2.json").exists());
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc3.json").exists());

        this.docStore.search("Hello");

        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc2.json").exists());
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
    }
    @Test
    public void pSearchAndThenMaxDocs() throws IOException{
        this.docStore.searchByPrefix("Hello");
        this.docStore.searchByPrefix("Shalom");

        this.docStore.setMaxDocumentCount(1);
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc2.json").exists());
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc3.json").exists());
    }
    @Test
    public void pSearchANdThenMaxBytes() throws IOException{
        this.docStore.searchByPrefix("Hello");
        this.docStore.searchByPrefix("Shalom");

        this.docStore.setMaxDocumentBytes(10);
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc2.json").exists());
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc3.json").exists());
    }
    @Test
    public void pSetMaxBytesAndThenSearch() throws IOException{
        this.docStore.setMaxDocumentBytes(10);

        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc2.json").exists());

        this.docStore.searchByPrefix("Hello");
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc3.json").exists());
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
    }
    @Test
    public void pSetMaxDocsAndThenSearch() throws IOException{
        this.docStore.setMaxDocumentCount(1);

        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc2.json").exists());

        this.docStore.searchByPrefix("Hello");
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc3.json").exists());
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
    }
}
