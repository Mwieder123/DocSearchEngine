import edu.yu.cs.com1320.project.stage6.DocumentStore;
import edu.yu.cs.com1320.project.stage6.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage6.impl.DocumentStoreImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
public class DocStoreStage5Test {
    private DocumentStoreImpl docStore;
    private DocumentImpl doc1;
    private DocumentImpl doc2;
    private DocumentImpl doc3;
    private DocumentImpl doc4;
    private DocumentImpl doc5;
    private DocumentImpl doc6;
    private DocumentImpl doc7;
    private DocumentImpl doc8;
    private DocumentImpl doc9;


    @BeforeEach
    void setUp() throws URISyntaxException, IOException {
        this.docStore = new DocumentStoreImpl();
        this.doc1 = new DocumentImpl(new URI("doc1"), "1", null);
        this.doc2 = new DocumentImpl(new URI("doc2"), "2", null);
        this.doc3 = new DocumentImpl(new URI("doc3"), "3", null);
        this.doc4 = new DocumentImpl(new URI("doc4"), "4", null);
        this.doc5 = new DocumentImpl(new URI("doc5"), "5", null);
        this.doc6 = new DocumentImpl(new URI("doc6"), "6", null);
        this.doc7 = new DocumentImpl(new URI("doc7"), "7", null);
        this.doc8 = new DocumentImpl(new URI("doc8"), "8", null);
        this.doc9 = new DocumentImpl(new URI("doc9"), "9", null);
        byte[] bytes1 = doc1.getDocumentTxt().getBytes();
        byte[] bytes2 = doc2.getDocumentTxt().getBytes();
        byte[] bytes3 = doc3.getDocumentTxt().getBytes();
        byte[] bytes4 = doc4.getDocumentTxt().getBytes();
        byte[] bytes5 = doc5.getDocumentTxt().getBytes();
        byte[] bytes6 = doc6.getDocumentTxt().getBytes();
        byte[] bytes7 = doc7.getDocumentTxt().getBytes();
        byte[] bytes8 = doc8.getDocumentTxt().getBytes();
        byte[] bytes9 = doc9.getDocumentTxt().getBytes();
        InputStream stream1 = new ByteArrayInputStream(bytes1);
        InputStream stream2 = new ByteArrayInputStream(bytes2);
        InputStream stream3 = new ByteArrayInputStream(bytes3);
        InputStream stream4 = new ByteArrayInputStream(bytes4);
        InputStream stream5 = new ByteArrayInputStream(bytes5);
        InputStream stream6 = new ByteArrayInputStream(bytes6);
        InputStream stream7 = new ByteArrayInputStream(bytes7);
        InputStream stream8 = new ByteArrayInputStream(bytes8);
        InputStream stream9 = new ByteArrayInputStream(bytes9);
        this.docStore.put(stream1, doc1.getKey(), DocumentStore.DocumentFormat.TXT);
        this.docStore.put(stream2, doc2.getKey(), DocumentStore.DocumentFormat.TXT);
        this.docStore.put(stream3, doc3.getKey(), DocumentStore.DocumentFormat.TXT);
        this.docStore.put(stream4, doc4.getKey(), DocumentStore.DocumentFormat.TXT);
        this.docStore.put(stream5, doc5.getKey(), DocumentStore.DocumentFormat.TXT);
        this.docStore.put(stream6, doc6.getKey(), DocumentStore.DocumentFormat.TXT);
        this.docStore.put(stream7, doc7.getKey(), DocumentStore.DocumentFormat.TXT);
        this.docStore.put(stream8, doc8.getKey(), DocumentStore.DocumentFormat.TXT);
        this.docStore.put(stream9, doc9.getKey(), DocumentStore.DocumentFormat.TXT);
    }

    @Test
    public void setBytesLimit() throws IOException {
        this.docStore.setMaxDocumentBytes(5);
        for(int i = 1; i < 2; i++){
            assertEquals(doc1, this.docStore.search(String.valueOf(i)).get(0));
        }
        assertEquals(doc5, this.docStore.search("5").get(0));
        assertEquals(doc6, this.docStore.search("6").get(0));
        assertEquals(doc7, this.docStore.search("7").get(0));
        assertEquals(doc8, this.docStore.search("8").get(0));
        assertEquals(doc9, this.docStore.search("9").get(0));
    }
    @Test
    public void setDocLimit() throws IOException {
        this.docStore.setMaxDocumentCount(1);
        for(int i = 7; i < 8; i++){
            assertEquals(doc7, this.docStore.search(String.valueOf(i)).get(0));
        }
        assertEquals(doc9, this.docStore.search("9").get(0));
    }
    @Test
    public void tooBigDocPut(){
        this.docStore.setMaxDocumentBytes(10);
        InputStream stream = new ByteArrayInputStream("Hello World! How Are You Doing Today!".getBytes());
        assertThrows(IllegalArgumentException.class, () -> {
            this.docStore.put(stream, new URI("HelloWorld"), DocumentStore.DocumentFormat.TXT);
        });
    }
    @Test
    public void tooBigDocUndo() throws URISyntaxException, IOException {
        InputStream stream = new ByteArrayInputStream("Hello World! How Are You Doing Today!".getBytes());
        this.docStore.put(stream, new URI("HelloWorld"), DocumentStore.DocumentFormat.TXT);
        this.docStore.delete(new URI("HelloWorld"));
//
//        this.docStore.setMaxDocumentBytes(10);
//        this.docStore.undo();
//
//        assertThrows(IllegalArgumentException.class, () -> {
//            this.docStore.undo();
//        });
    }
    @Test
    public void putEnforceLimitsTest() throws URISyntaxException, IOException {
        this.docStore.setMaxDocumentBytes(10);
        InputStream stream = new ByteArrayInputStream("Hello".getBytes());
        this.docStore.put(stream, new URI("HelloWorld"), DocumentStore.DocumentFormat.TXT);
        for(int i = 4; i < 5; i++){
            assertEquals(doc4, this.docStore.search(String.valueOf(i)).get(0));
        }
        assertEquals(doc5, this.docStore.search("5").get(0));
        assertEquals(doc6, this.docStore.search("6").get(0));
        assertEquals(doc7, this.docStore.search("7").get(0));
        assertEquals(doc8, this.docStore.search("8").get(0));
        assertEquals(doc9, this.docStore.search("9").get(0));
    }
    @Test
    public void putEnforceLimitsTestAfterUpdatedTime() throws URISyntaxException, IOException {
        this.docStore.setMaxDocumentBytes(10);
        InputStream stream = new ByteArrayInputStream("Hello".getBytes());
        this.docStore.search("1");
        this.docStore.search("2");
        this.docStore.search("3");

        this.docStore.put(stream, new URI("HelloWorld"), DocumentStore.DocumentFormat.TXT);
        for(int i = 7; i < 8; i++){
            assertEquals(doc7, this.docStore.search(String.valueOf(i)).get(0));
        }
        assertEquals(doc1, this.docStore.search("1").get(0));
        assertEquals(doc2, this.docStore.search("2").get(0));
        assertEquals(doc3, this.docStore.search("3").get(0));
        assertEquals(doc8, this.docStore.search("8").get(0));
        assertEquals(doc9, this.docStore.search("9").get(0));
    }
    @Test
    public void correctUndoEnforceLimitsTest() throws URISyntaxException, IOException {
        this.docStore.setMaxDocumentBytes(10);
        this.docStore.delete(doc3.getKey());
        this.docStore.delete(doc5.getKey());
        InputStream stream = new ByteArrayInputStream("Hello".getBytes());
        this.docStore.put(stream, new URI("HelloWorld"), DocumentStore.DocumentFormat.TXT);
        assertNotNull(this.docStore.get(doc1.getKey()));
        assertNotNull(this.docStore.get(doc2.getKey()));
        assertEquals(doc4, this.docStore.get(doc4.getKey()));
        assertEquals(doc6, this.docStore.get(doc6.getKey()));
        this.docStore.undo(doc3.getKey());
        this.docStore.undo(doc5.getKey());
        assertEquals(doc3, this.docStore.get(doc3.getKey()));
        assertEquals(doc5, this.docStore.get(doc5.getKey()));
        assertNotNull(this.docStore.get(doc7.getKey()));
        assertNotNull(this.docStore.get(doc8.getKey()));
    }
    @Test
    public void docLimitsTest() throws IOException {
        this.docStore.delete(doc3.getKey());
        this.docStore.delete(doc5.getKey());
        this.docStore.setMaxDocumentCount(7);
        this.docStore.undo();
        this.docStore.undo();
        assertEquals(doc3, this.docStore.get(doc3.getKey()));
        assertEquals(doc5, this.docStore.get(doc5.getKey()));
        assertNotNull(this.docStore.get(doc1.getKey()));
        assertNotNull(this.docStore.get(doc2.getKey()));
    }
    @Test
    public void docLimitsTestUpdatedTime() throws IOException {
        this.docStore.delete(doc3.getKey());
        this.docStore.delete(doc5.getKey());
        this.docStore.setMaxDocumentCount(7);
        this.docStore.searchByPrefix("1");
        this.docStore.search("2");
        this.docStore.setMetadata(doc4.getKey(), "Hi", "Hello");
        this.docStore.undo(doc3.getKey());
        this.docStore.undo(doc5.getKey());
        assertEquals(doc3, this.docStore.get(doc3.getKey()));
        assertEquals(doc5, this.docStore.get(doc5.getKey()));
        assertNotNull(this.docStore.get(doc6.getKey()));
        assertNotNull(this.docStore.get(doc7.getKey()));
    }

}
