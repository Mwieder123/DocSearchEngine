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

public class Stage6Test {
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
    public void getTest() throws IOException{
        this.docStore.setMaxDocumentCount(3);
        assertEquals("Hello1", new String(this.docStore.get(URI.create("doc1")).getDocumentBinaryData()));

        System.out.print("hi");
    }
    @Test
    public void deleteFromMemoryTest() throws IOException {
        this.docStore.setMetadata(URI.create("doc3"), "Key", "Value");
        this.docStore.delete(URI.create("doc3"));
        assertNull(this.docStore.get(URI.create("doc3")));

        System.out.print("hi");
    }
    @Test
    public void deleteFromDiskTest() throws IOException{
        this.docStore.setMaxDocumentCount(4);
        this.docStore.delete(URI.create("doc1"));
        assertNull(this.docStore.get(URI.create("doc1")));
    }
    @Test
    public void metadataGetAndSet()throws IOException{
        this.docStore.setMaxDocumentCount(3);
        assertNull(this.docStore.setMetadata(URI.create("doc1"), "Key", "Value"));
        assertNull(this.docStore.getMetadata(URI.create("doc4"), "Key"));

        System.out.print("Hi");
    }
    @Test
    public void searchAndSearchByPrefix() throws IOException {
        this.docStore.delete(URI.create("doc1"));
        this.docStore.delete(URI.create("doc2"));
        this.docStore.delete(URI.create("doc3"));
        this.docStore.delete(URI.create("doc4"));
        this.docStore.delete(URI.create("doc5"));

        this.docStore.put(new ByteArrayInputStream("Hello1".getBytes()), URI.create("doc11"), DocumentStore.DocumentFormat.TXT);
        this.docStore.put(new ByteArrayInputStream("Hello4".getBytes()), URI.create("doc41"), DocumentStore.DocumentFormat.TXT);
        this.docStore.put(new ByteArrayInputStream("Hello3".getBytes()), URI.create("doc31"), DocumentStore.DocumentFormat.TXT);
        this.docStore.put(new ByteArrayInputStream("Hello2".getBytes()), URI.create("doc21"), DocumentStore.DocumentFormat.TXT);
        this.docStore.put(new ByteArrayInputStream("Hello5".getBytes()), URI.create("doc51"), DocumentStore.DocumentFormat.TXT);

        this.docStore.setMaxDocumentCount(3);
        assertEquals(1, this.docStore.search("Hello1").size());
        assertEquals("Hello4", this.docStore.searchByPrefix("Hello4").get(0).getDocumentTxt());

    }
    @Test void searchByMetaData_keyAndMetadata_prefixAndMetadata() throws IOException{
        this.docStore.delete(URI.create("doc1"));
        this.docStore.delete(URI.create("doc2"));
        this.docStore.delete(URI.create("doc3"));
        this.docStore.delete(URI.create("doc4"));
        this.docStore.delete(URI.create("doc5"));

        this.docStore.put(new ByteArrayInputStream("Hello1".getBytes()), URI.create("doc11"), DocumentStore.DocumentFormat.TXT);
        this.docStore.setMetadata(URI.create("doc11"), "Key1", "Value");
        this.docStore.put(new ByteArrayInputStream("Hello4".getBytes()), URI.create("doc41"), DocumentStore.DocumentFormat.TXT);
        this.docStore.setMetadata(URI.create("doc41"), "Key2", "Value");
        this.docStore.put(new ByteArrayInputStream("Hello3".getBytes()), URI.create("doc31"), DocumentStore.DocumentFormat.TXT);
        this.docStore.setMetadata(URI.create("doc31"), "Key3", "Value");
        this.docStore.put(new ByteArrayInputStream("Hello2".getBytes()), URI.create("doc21"), DocumentStore.DocumentFormat.TXT);
        this.docStore.put(new ByteArrayInputStream("Hello5".getBytes()), URI.create("doc51"), DocumentStore.DocumentFormat.TXT);

        this.docStore.setMaxDocumentCount(3);

        HashMap<String, String> map = new HashMap<>();
        map.put("Key1", "Value");
        assertEquals("Hello1", this.docStore.searchByPrefixAndMetadata("Hello1", map).get(0).getDocumentTxt());
        map.clear();
        map.put("Key2", "Value");
        assertEquals("Hello4", this.docStore.searchByPrefixAndMetadata("Hello4", map).get(0).getDocumentTxt());
        map.clear();
        map.put("Key3", "Value");
        assertEquals("Hello3", this.docStore.searchByPrefixAndMetadata("Hello3", map).get(0).getDocumentTxt());
    }
    @Test
    public void deleteTests() throws IOException{
        this.docStore.put(new ByteArrayInputStream("Hello1".getBytes()), URI.create("doc11"), DocumentStore.DocumentFormat.TXT);
        this.docStore.setMetadata(URI.create("doc11"), "Key1", "Value");
        this.docStore.put(new ByteArrayInputStream("Hello4".getBytes()), URI.create("doc41"), DocumentStore.DocumentFormat.TXT);
        this.docStore.setMetadata(URI.create("doc41"), "Key2", "Value");
        this.docStore.put(new ByteArrayInputStream("Hello3".getBytes()), URI.create("doc31"), DocumentStore.DocumentFormat.TXT);
        this.docStore.setMetadata(URI.create("doc31"), "Key3", "Value");
        this.docStore.put(new ByteArrayInputStream("Hello2".getBytes()), URI.create("doc21"), DocumentStore.DocumentFormat.TXT);
        this.docStore.put(new ByteArrayInputStream("Hello5".getBytes()), URI.create("doc51"), DocumentStore.DocumentFormat.TXT);

        this.docStore.setMaxDocumentCount(1);

        this.docStore.deleteAll("Hello5");
        this.docStore.deleteAllWithPrefix("Hello2");

        HashMap<String, String> map = new HashMap<>();
        map.put("Key1", "Value");
        this.docStore.deleteAllWithMetadata(map);

        map.clear();
        map.put("Key2", "Value");
        this.docStore.deleteAllWithKeywordAndMetadata("Hello4", map);

        map.clear();
        map.put("Key3", "Value");
        this.docStore.deleteAllWithPrefixAndMetadata("Hello", map);

    }

}
