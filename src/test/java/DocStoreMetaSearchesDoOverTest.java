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
import java.util.HashMap;
import java.util.Map;

public class DocStoreMetaSearchesDoOverTest {
    private DocumentStoreImpl docStore;
    private Map<String, String> map1;
    private Map<String, String> map2;
    private Map<String, String> map3;
    private final File baseDir = new File("RedoTesting");

    @BeforeEach
    void setUp() throws URISyntaxException, IOException {
        this.docStore = new DocumentStoreImpl(baseDir);
        this.map1 = new HashMap<>();
        this.map1.put("1", "Value");
        this.map2 = new HashMap<>();
        this.map2.put("2", "Value");
        this.map3 = new HashMap<>();
        this.map3.put("3", "Value");
        File file1 = new File("RedoTesting/www.yu.edu/documents/doc1.json");
        File file2 = new File("RedoTesting/www.yu.edu/documents/doc2.json");
        File file3 = new File("RedoTesting/www.yu.edu/documents/doc3.json");
        file1.delete();
        file2.delete();
        file3.delete();

        this.docStore.put(new ByteArrayInputStream("Hello".getBytes()), new URI("http://www.yu.edu/documents/doc1"), DocumentStore.DocumentFormat.TXT);
        this.docStore.setMetadata(new URI("http://www.yu.edu/documents/doc1"), "1", "Value");
        this.docStore.put(new ByteArrayInputStream("Shalom".getBytes()), new URI("http://www.yu.edu/documents/doc2"), DocumentStore.DocumentFormat.TXT);
        this.docStore.setMetadata(new URI("http://www.yu.edu/documents/doc2"), "2", "Value");
        this.docStore.put(new ByteArrayInputStream("Goodbye".getBytes()), new URI("http://www.yu.edu/documents/doc3"), DocumentStore.DocumentFormat.TXT);
        this.docStore.setMetadata(new URI("http://www.yu.edu/documents/doc3"), "3", "Value");
    }
    @Test
    public void setMaxDocsAndThenSearchMetadata() throws IOException{
        this.docStore.setMaxDocumentCount(2);

        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc2.json").exists());
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc3.json").exists());

        this.docStore.searchByMetadata(this.map1);
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc2.json").exists());
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
    }
    @Test
    public void setMaxBytesAndThenSearchMetadata() throws IOException{
        this.docStore.setMaxDocumentBytes(15);

        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc2.json").exists());
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc3.json").exists());

        this.docStore.searchByMetadata(this.map1);
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc2.json").exists());
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
    }
    @Test
    public void searchMetadataAndThenSetMaxDocs() throws IOException{
        this.docStore.searchByMetadata(this.map1);
        this.docStore.searchByMetadata(this.map2);

        this.docStore.setMaxDocumentCount(2);

        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc3.json").exists());
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc2.json").exists());
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
    }
    @Test
    public void searchMetadataAndThenSetMaxBytes() throws IOException{
        this.docStore.searchByMetadata(this.map1);
        this.docStore.searchByMetadata(this.map2);

        this.docStore.setMaxDocumentBytes(15);

        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc3.json").exists());
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc2.json").exists());
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
    }
    @Test
    public void getMetadataAndThenMaxDocs() throws IOException, URISyntaxException {
        this.docStore.getMetadata(new URI("http://www.yu.edu/documents/doc1"), "1");
        this.docStore.getMetadata(new URI("http://www.yu.edu/documents/doc2"), "2");

        this.docStore.setMaxDocumentCount(1);
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc3.json").exists());
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc2.json").exists());
    }
    @Test
    public void getMetadataAndThenMaxBytes() throws IOException, URISyntaxException {
        this.docStore.getMetadata(new URI("http://www.yu.edu/documents/doc1"), "1");
        this.docStore.getMetadata(new URI("http://www.yu.edu/documents/doc2"), "2");

        this.docStore.setMaxDocumentBytes(10);
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc3.json").exists());
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc2.json").exists());
    }
    @Test
    public void setMaxBytesAndThenGetMetadata() throws IOException, URISyntaxException{
        this.docStore.setMaxDocumentBytes(10);
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc2.json").exists());
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc3.json").exists());

        this.docStore.getMetadata(new URI("http://www.yu.edu/documents/doc1"), "1");
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc3.json").exists());
        this.docStore.getMetadata(new URI("http://www.yu.edu/documents/doc2"), "2");
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc2.json").exists());
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
    }
    @Test
    public void setMaxDocsAndThenGetMetadata() throws IOException, URISyntaxException{
        this.docStore.setMaxDocumentCount(1);
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc2.json").exists());
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc3.json").exists());

        this.docStore.getMetadata(new URI("http://www.yu.edu/documents/doc1"), "1");
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc3.json").exists());
        this.docStore.getMetadata(new URI("http://www.yu.edu/documents/doc2"), "2");
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc2.json").exists());
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
    }
    @Test
    public void setMaxDocsAndThenKeyWordMetadata() throws IOException{
        this.docStore.setMaxDocumentCount(1);
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc2.json").exists());
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc3.json").exists());

        this.docStore.searchByKeywordAndMetadata("Hello", this.map1);
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc3.json").exists());
        this.docStore.searchByKeywordAndMetadata("Shalom", this.map2);
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc2.json").exists());
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
    }
    @Test
    public void setMaxBytesAndThenKeyWordMetadata() throws IOException{
        this.docStore.setMaxDocumentBytes(10);
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc2.json").exists());
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc3.json").exists());

        this.docStore.searchByKeywordAndMetadata("Hello", this.map1);
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc3.json").exists());
        this.docStore.searchByKeywordAndMetadata("Shalom", this.map2);
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc2.json").exists());
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
    }
    @Test
    public void KeywordMetadataAndThenMaxBytes() throws IOException{
        this.docStore.searchByKeywordAndMetadata("Hello", this.map1);
        this.docStore.searchByKeywordAndMetadata("Shalom", this.map2);

        this.docStore.setMaxDocumentBytes(10);
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc3.json").exists());
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc2.json").exists());
    }
    @Test
    public void KeyWordMetadataAndThenMaxDocs() throws IOException{
        this.docStore.searchByKeywordAndMetadata("Hello", this.map1);
        this.docStore.searchByKeywordAndMetadata("Shalom", this.map2);

        this.docStore.setMaxDocumentCount(1);
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc3.json").exists());
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc2.json").exists());
    }
    @Test
    public void prefixMetadataAndThenMaxDocs() throws IOException{
        this.docStore.searchByPrefixAndMetadata("Hello", this.map1);
        this.docStore.searchByPrefixAndMetadata("Shalom", this.map2);

        this.docStore.setMaxDocumentCount(1);
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc3.json").exists());
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc2.json").exists());
    }
    @Test
    public void prefixMetadataAndThenMaxBytes() throws IOException{
        this.docStore.searchByPrefixAndMetadata("Hello", this.map1);
        this.docStore.searchByPrefixAndMetadata("Shalom", this.map2);

        this.docStore.setMaxDocumentBytes(10);
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc3.json").exists());
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc2.json").exists());
    }
    @Test
    public void SetMaxBytesANdThenPrefixMetadata() throws IOException{
        this.docStore.setMaxDocumentBytes(10);
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc2.json").exists());
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc3.json").exists());

        this.docStore.searchByPrefixAndMetadata("Hello", this.map1);
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc3.json").exists());
        this.docStore.searchByPrefixAndMetadata("Shalom", this.map2);
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc2.json").exists());
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
    }
    @Test
    public void SetMaxDocAndThenPrefixMetadata() throws IOException{
        this.docStore.setMaxDocumentCount(1);
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc2.json").exists());
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc3.json").exists());

        this.docStore.searchByPrefixAndMetadata("Hello", this.map1);
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc3.json").exists());
        this.docStore.searchByPrefixAndMetadata("Shalom", this.map2);
        assertFalse(new File("RedoTesting/www.yu.edu/documents/doc2.json").exists());
        assertTrue(new File("RedoTesting/www.yu.edu/documents/doc1.json").exists());
    }

}
