import edu.yu.cs.com1320.project.stage6.Document;
import edu.yu.cs.com1320.project.stage6.DocumentStore;
import edu.yu.cs.com1320.project.stage6.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage6.impl.DocumentStoreImpl;
import org.junit.jupiter.api.BeforeEach;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UndoStage4Test {
    private DocumentStoreImpl documentStore;
    private List<Document> list;
    private HashMap<String, String> map;
    private DocumentImpl doc1;
    private DocumentImpl doc2;
    private DocumentImpl doc3;
    @BeforeEach
    public void setup() throws URISyntaxException, IOException {
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
    public void deleteAllUndoTest() throws IOException {
        this.documentStore.deleteAll("Today");

        assertNull(this.documentStore.get(doc1.getKey()));
        assertNull(this.documentStore.get(doc2.getKey()));
        assertNull(this.documentStore.get(doc3.getKey()));

        this.documentStore.undo();

        assertNotNull(this.documentStore.get(doc1.getKey()));
        assertNotNull(this.documentStore.get(doc2.getKey()));
        assertNotNull(this.documentStore.get(doc3.getKey()));

        assertTrue(list.containsAll(this.documentStore.search("Today")) && this.documentStore.search("Today").containsAll(list));
        assertTrue(list.containsAll(this.documentStore.searchByMetadata(this.map)) && this.documentStore.searchByMetadata(this.map).containsAll(list));

        this.documentStore.undo();

        assertNull(doc2.getMetadataValue("Date"));
    }
    @Test
    public void deleteAllUndoURITest() throws IOException {
        this.documentStore.deleteAll("Today");

        assertNull(this.documentStore.get(doc1.getKey()));
        assertNull(this.documentStore.get(doc2.getKey()));
        assertNull(this.documentStore.get(doc3.getKey()));

        this.documentStore.undo(doc2.getKey());

        assertNull(this.documentStore.get(doc1.getKey()));
        assertNotNull(this.documentStore.get(doc2.getKey()));
        assertNull(this.documentStore.get(doc3.getKey()));


        this.documentStore.undo();

        assertNotNull(this.documentStore.get(doc1.getKey()));
        assertNotNull(this.documentStore.get(doc2.getKey()));
        assertNotNull(this.documentStore.get(doc3.getKey()));

        this.documentStore.undo();

        assertNull(doc2.getMetadataValue("Date"));
    }
    @Test
    public void deleteAllWithPrefixUndoTest() throws IOException {
        this.documentStore.deleteAllWithPrefix("YYY");

        assertNull(this.documentStore.get(doc1.getKey()));
        assertNull(this.documentStore.get(doc2.getKey()));
        assertNull(this.documentStore.get(doc3.getKey()));

        this.documentStore.undo();

        assertNotNull(this.documentStore.get(doc1.getKey()));
        assertNotNull(this.documentStore.get(doc2.getKey()));
        assertNotNull(this.documentStore.get(doc3.getKey()));

        assertTrue(list.containsAll(this.documentStore.search("Today")) && this.documentStore.search("Today").containsAll(list));
        assertTrue(list.containsAll(this.documentStore.searchByMetadata(this.map)) && this.documentStore.searchByMetadata(this.map).containsAll(list));

        this.documentStore.undo();

        assertNull(doc2.getMetadataValue("Date"));
    }
    @Test
    public void deleteAllWithMetadataUndoTest() throws IOException {
        this.map.put("Date", "3");
        this.documentStore.deleteAllWithMetadata(this.map);

        assertNotNull(this.documentStore.get(doc1.getKey()));
        assertNull(this.documentStore.get(doc2.getKey()));
        assertNotNull(this.documentStore.get(doc3.getKey()));

        this.documentStore.undo();

        assertNotNull(this.documentStore.get(doc1.getKey()));
        assertNotNull(this.documentStore.get(doc2.getKey()));
        assertNotNull(this.documentStore.get(doc3.getKey()));

        assertTrue(list.containsAll(this.documentStore.search("Today")) && this.documentStore.search("Today").containsAll(list));
        list.remove(doc1);
        list.remove(doc3);
        assertTrue(list.containsAll(this.documentStore.searchByMetadata(this.map)) && this.documentStore.searchByMetadata(this.map).containsAll(list));

        this.documentStore.undo();

        assertNull(doc2.getMetadataValue("Date"));
    }
    @Test
    public void deleteAllWithKeyWordAndMetadataUndoTest() throws IOException {
        this.documentStore.deleteAllWithKeywordAndMetadata("Today", this.map);

        assertNull(this.documentStore.get(doc1.getKey()));
        assertNull(this.documentStore.get(doc2.getKey()));
        assertNull(this.documentStore.get(doc3.getKey()));

        this.documentStore.undo();

        assertNotNull(this.documentStore.get(doc1.getKey()));
        assertNotNull(this.documentStore.get(doc2.getKey()));
        assertNotNull(this.documentStore.get(doc3.getKey()));

        assertTrue(list.containsAll(this.documentStore.search("Today")) && this.documentStore.search("Today").containsAll(list));
        assertTrue(list.containsAll(this.documentStore.searchByMetadata(this.map)) && this.documentStore.searchByMetadata(this.map).containsAll(list));

        this.documentStore.undo();

        assertNull(doc2.getMetadataValue("Date"));
    }
    @Test
    public void deleteAllWithPrefixAndMetadata() throws IOException {
        this.documentStore.deleteAllWithPrefixAndMetadata("WWW", this.map);

        assertNull(this.documentStore.get(doc1.getKey()));
        assertNotNull(this.documentStore.get(doc2.getKey()));
        assertNull(this.documentStore.get(doc3.getKey()));

        this.documentStore.undo();

        assertNotNull(this.documentStore.get(doc1.getKey()));
        assertNotNull(this.documentStore.get(doc2.getKey()));
        assertNotNull(this.documentStore.get(doc3.getKey()));

        assertTrue(list.containsAll(this.documentStore.search("Today")) && this.documentStore.search("Today").containsAll(list));
        assertTrue(list.containsAll(this.documentStore.searchByMetadata(this.map)) && this.documentStore.searchByMetadata(this.map).containsAll(list));

        this.documentStore.undo();

        assertNull(doc2.getMetadataValue("Date"));
    }
    @Test
    public void Stage4ErrorTest() throws IOException {
        this.documentStore.undo(doc3.getKey());
        assertNull(this.documentStore.get(doc3.getKey()).getMetadataValue("Time"));

        this.documentStore.delete(doc3.getKey());
        this.documentStore.delete(doc2.getKey());
        this.documentStore.delete(doc1.getKey());

        assertNull(this.documentStore.get(doc3.getKey()));

        this.documentStore.undo(doc3.getKey());

        assertEquals(doc3, this.documentStore.get(doc3.getKey()));
    }
}
