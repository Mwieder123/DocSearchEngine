import edu.yu.cs.com1320.project.stage6.Document;
import edu.yu.cs.com1320.project.stage6.DocumentStore;
import edu.yu.cs.com1320.project.stage6.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage6.impl.DocumentStoreImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import edu.yu.cs.com1320.project.impl.TrieImpl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
public class DeleteAllTracesTest {
    private DocumentStoreImpl documentStore;
    private List<Document> list;
    private HashMap<String, String> map;
    private DocumentImpl doc1;
    private DocumentImpl doc2;
    private DocumentImpl doc3;

    @BeforeEach
    void setUp() throws IOException, URISyntaxException {
        this.documentStore = new DocumentStoreImpl();
        this.doc1 = new DocumentImpl(new URI("doc1"), "Today YYYis a day with today and WWWyesterday", null);
        this.doc2 = new DocumentImpl(new URI("doc2"), "Today, YYYToday Today Today LLLTomorrow, Yesterday", null);
        this. doc3 = new DocumentImpl(new URI("doc3"), "Today WWWtoday Today and YYYyesterday", null);
        byte[] bytes1 = doc1.getDocumentTxt().getBytes();
        byte[] bytes2 = doc2.getDocumentTxt().getBytes();
        byte[] bytes3 = doc3.getDocumentTxt().getBytes();
        InputStream stream1 = new ByteArrayInputStream(bytes1);
        InputStream stream2 = new ByteArrayInputStream(bytes2);
        InputStream stream3 = new ByteArrayInputStream(bytes3);
        this.documentStore.put(stream1, doc1.getKey(), DocumentStore.DocumentFormat.TXT);
        this.documentStore.put(stream2, doc2.getKey(), DocumentStore.DocumentFormat.TXT);
        this.documentStore.put(stream3, doc3.getKey(), DocumentStore.DocumentFormat.TXT);

        this.documentStore.setMetadata(doc1.getKey(), "Time", "7:00PM");
        this.documentStore.setMetadata(doc3.getKey(), "Time", "7:00PM");
        this.documentStore.setMetadata(doc2.getKey(), "Time", "7:00PM");
        this.documentStore.setMetadata(doc2.getKey(), "Date", "3");

        this.list = new ArrayList<>();
        list.add(doc1);
        list.add(doc2);
        list.add(doc3);

        this.map = new HashMap<String, String>();
        this.map.put("Time", "7:00PM");
    }
    @Test
    public void deleteAllKeyword() throws IOException {
        assertTrue(list.containsAll(this.documentStore.search("Today")) && this.documentStore.search("Today").containsAll(list));
        assertTrue(list.containsAll(this.documentStore.searchByMetadata(this.map)) && this.documentStore.searchByMetadata(this.map).containsAll(list));
        this.documentStore.deleteAll("Today");

        list.clear();

        assertEquals(list, this.documentStore.search("Today"));
        assertEquals(list, this.documentStore.searchByMetadata(this.map));
        assertNull(this.documentStore.get(doc1.getKey()));
        assertNull(this.documentStore.get(doc2.getKey()));
        assertNull(this.documentStore.get(doc3.getKey()));
    }
    @Test
    public void deleteAllWithPrefixTest() throws IOException {
        list.remove(doc2);
        this.documentStore.setMetadata(doc2.getKey(), "Time", "Now");
        assertFalse(this.documentStore.searchByMetadata(this.map).contains(doc2));
        assertTrue(list.containsAll(this.documentStore.searchByPrefix("WWW")) && this.documentStore.searchByPrefix("WWW").containsAll(list));
        assertTrue(list.containsAll(this.documentStore.searchByMetadata(this.map)) && this.documentStore.searchByMetadata(this.map).containsAll(list));
        this.documentStore.deleteAllWithPrefix("WWW");

        list.clear();

        assertEquals(list, this.documentStore.searchByPrefix("WWW"));
        assertEquals(list, this.documentStore.searchByMetadata(this.map));
        assertNull(this.documentStore.get(doc1.getKey()));
        assertNull(this.documentStore.get(doc3.getKey()));
    }
    @Test
    public void deleteAllWithMetadata() throws IOException {
        this.map.put("Date", "3");
        list.remove(doc1);
        list.remove(doc3);

        assertTrue(list.containsAll(this.documentStore.searchByPrefix("LLL")) && this.documentStore.searchByPrefix("LLL").containsAll(list));
        assertTrue(list.containsAll(this.documentStore.searchByMetadata(this.map)) && this.documentStore.searchByMetadata(this.map).containsAll(list));
        list.add(doc1);
        list.add(doc3);
        this.documentStore.deleteAllWithMetadata(this.map);


        assertEquals(new ArrayList<>(), this.documentStore.searchByPrefix("LLL"));
        assertEquals(new ArrayList<>(), this.documentStore.searchByMetadata(this.map));
        assertNull(this.documentStore.get(doc2.getKey()));

        assertNotNull(this.documentStore.get(doc3.getKey()));
        assertNotNull(this.documentStore.get(doc1.getKey()));
    }
    @Test
    public void deleteAllWithKeyWordAndMetadata() throws IOException {
        assertTrue(list.containsAll(this.documentStore.searchByKeywordAndMetadata("Today", this.map)) && this.documentStore.searchByKeywordAndMetadata("Today", this.map).containsAll(list));
        assertTrue(list.containsAll(this.documentStore.searchByMetadata(this.map)) && this.documentStore.searchByMetadata(this.map).containsAll(list));

        this.documentStore.deleteAllWithKeywordAndMetadata("Today", this.map);

        assertEquals(new ArrayList<>(), this.documentStore.search("Today"));
        assertEquals(new ArrayList<>(), this.documentStore.searchByMetadata(this.map));
        assertNull(this.documentStore.get(doc1.getKey()));
        assertNull(this.documentStore.get(doc2.getKey()));
        assertNull(this.documentStore.get(doc3.getKey()));
    }
    @Test
    public void deleteAllWithPrefixAndMetadata() throws IOException {
        this.documentStore.deleteAllWithPrefixAndMetadata("YYY", this.map);

        assertEquals(new ArrayList<>(), this.documentStore.search("Today"));
        assertEquals(new ArrayList<>(), this.documentStore.searchByMetadata(this.map));
        assertNull(this.documentStore.get(doc1.getKey()));
        assertNull(this.documentStore.get(doc2.getKey()));
        assertNull(this.documentStore.get(doc3.getKey()));

    }
    @Test
    public void deleteURI() throws IOException {
        this.documentStore.delete(doc2.getKey());

        list.remove(doc2);

        assertTrue(list.containsAll(this.documentStore.search("Today")) && this.documentStore.search("Today").containsAll(list));
        assertTrue(list.containsAll(this.documentStore.searchByMetadata(this.map)) && this.documentStore.searchByMetadata(this.map).containsAll(list));
        assertNull(this.documentStore.get(doc2.getKey()));
        assertNotNull(this.documentStore.get(doc1.getKey()));
        assertNotNull(this.documentStore.get(doc3.getKey()));
    }
}
