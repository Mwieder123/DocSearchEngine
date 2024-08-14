import edu.yu.cs.com1320.project.stage6.DocumentStore;
import edu.yu.cs.com1320.project.stage6.impl.DocumentStoreImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.net.URI;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class UndoTest {
    private DocumentStoreImpl doc;
    private URI uri0;
    private URI uri1;
    private URI uri2;
    private InputStream string0;
    private InputStream string1;
    private InputStream string2;
    private InputStream string9;

    @BeforeEach
    public void setup() throws URISyntaxException {
        this.doc = new DocumentStoreImpl();
        this.uri0 = new URI("0");
        this.uri1 = new URI("1");
        this.uri2 = new URI("2");

        String str0 = 0 + "AnyPossibleString";
        String str1 = 1 + "AnyPossibleString";
        String str2 = 2 + "AnyPossibleString";
        String str9 = 9 + "AnyPossibleString";

        byte[] bytes0 = str0.getBytes(StandardCharsets.UTF_8);
        byte[] bytes1 = str1.getBytes(StandardCharsets.UTF_8);
        byte[] bytes2 = str2.getBytes(StandardCharsets.UTF_8);
        byte[] bytes9 = str9.getBytes(StandardCharsets.UTF_8);

        this.string0 = new ByteArrayInputStream(bytes0);
        this.string1 = new ByteArrayInputStream(bytes1);
        this.string2 = new ByteArrayInputStream(bytes2);
        this.string9 = new ByteArrayInputStream(bytes9);
    }

    @Test
    public void undoPutTester() throws URISyntaxException, IOException {
        assertThrows(IllegalStateException.class, () -> {
            this.doc.undo();
        });

        this.doc.put(string0, uri0, DocumentStore.DocumentFormat.TXT);
        this.doc.put(string1, uri1, DocumentStore.DocumentFormat.TXT);
        this.doc.put(string2, uri2, DocumentStore.DocumentFormat.TXT);

        this.doc.undo();
        assertNull(this.doc.get(uri2));

        this.doc.undo();
        assertNull(this.doc.get(uri1));

        this.doc.undo();
        assertNull(this.doc.get(uri0));

        assertThrows(IllegalStateException.class, () -> {
            this.doc.undo();
        });
    }

    @Test
    public void undoMetaData() throws IOException {
        this.doc.put(string0, uri0, DocumentStore.DocumentFormat.TXT);

        this.doc.setMetadata(uri0, "Time", "Jan30");
        assertEquals("Jan30", this.doc.getMetadata(uri0, "Time"));

        this.doc.setMetadata(uri0, "Time", "Feb30");

        this.doc.undo();
        assertEquals("Jan30", this.doc.getMetadata(uri0, "Time"));

        this.doc.setMetadata(uri0, "Price", "$1");

        this.doc.undo();
        assertNull(this.doc.getMetadata(uri0, "Price"));
    }

    @Test
    public void undoPutModDocTester() throws IOException {
        this.doc.put(string0, uri0, DocumentStore.DocumentFormat.TXT);
        this.doc.put(string1, uri1, DocumentStore.DocumentFormat.TXT);
        this.doc.put(string2, uri2, DocumentStore.DocumentFormat.TXT);

        assertEquals("0AnyPossibleString", this.doc.get(uri0).getDocumentTxt());
        assertEquals("1AnyPossibleString", this.doc.get(uri1).getDocumentTxt());
        assertEquals("2AnyPossibleString", this.doc.get(uri2).getDocumentTxt());

        this.doc.put(string9, uri1, DocumentStore.DocumentFormat.TXT);
        assertEquals("9AnyPossibleString", this.doc.get(uri1).getDocumentTxt());

        this.doc.undo();
        assertEquals("1AnyPossibleString", this.doc.get(uri1).getDocumentTxt());
    }

    @Test
    public void putTester() throws URISyntaxException, IOException {
        for (int i = 0; i < 5; i++) {
            String str = i + "AnyPossibleString";
            byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
            InputStream stream = new ByteArrayInputStream(bytes);
            doc.put(stream, new URI(Integer.toString(i)), DocumentStore.DocumentFormat.TXT);
        }

        this.doc.undo();

        assertNull(this.doc.get(new URI(Integer.toString(4))));

        String str = 2 + "String";
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        InputStream stream = new ByteArrayInputStream(bytes);
        doc.put(stream, new URI(Integer.toString(2)), DocumentStore.DocumentFormat.TXT);

        assertEquals(2 + "String", this.doc.get(new URI(Integer.toString(2))).getDocumentTxt());

        this.doc.undo();

        assertEquals(2 + "AnyPossibleString", this.doc.get(new URI(Integer.toString(2))).getDocumentTxt());
    }

    @Test
    public void undoDeleteTester() throws IOException {
        this.doc.put(string0, uri0, DocumentStore.DocumentFormat.TXT);
        this.doc.put(string1, uri1, DocumentStore.DocumentFormat.TXT);
        this.doc.put(string2, uri2, DocumentStore.DocumentFormat.TXT);

        this.doc.delete(uri0);
        this.doc.delete(uri1);
        this.doc.delete(uri2);

        this.doc.undo();
        this.doc.undo();
        this.doc.undo();

        assertEquals("0AnyPossibleString", this.doc.get(uri0).getDocumentTxt());
        assertEquals("1AnyPossibleString", this.doc.get(uri1).getDocumentTxt());
        assertEquals("2AnyPossibleString", this.doc.get(uri2).getDocumentTxt());

        this.doc.undo();
    }

    @Test
    public void specificNewDocUndoTester() throws URISyntaxException, IOException {
        doc.put(this.string2, uri2, DocumentStore.DocumentFormat.TXT);
        doc.put(this.string1, uri1, DocumentStore.DocumentFormat.TXT);

        this.doc.undo(uri2);

        assertNull(this.doc.get(uri2));

        assertThrows(IllegalStateException.class, () -> {
            this.doc.undo(uri0);
        });
        this.doc.undo();
        assertThrows(IllegalStateException.class, () -> {
            this.doc.undo();
        });
    }

    @Test
    public void specificModDocTester() throws URISyntaxException, IOException {
        doc.put(this.string2, uri2, DocumentStore.DocumentFormat.TXT);
        doc.put(this.string1, uri1, DocumentStore.DocumentFormat.TXT);
        doc.put(this.string0, uri2, DocumentStore.DocumentFormat.TXT);

        this.doc.undo(uri2);

        assertEquals("2AnyPossibleString", this.doc.get(uri2).getDocumentTxt());
    }

    @Test
    public void specificDocDeleteTester() throws URISyntaxException, IOException {
        doc.put(this.string2, uri2, DocumentStore.DocumentFormat.TXT);
        doc.put(this.string1, uri1, DocumentStore.DocumentFormat.TXT);
        doc.delete(uri2);
        this.doc.delete(uri1);

        assertNull(this.doc.get(uri2));
        assertNull(this.doc.get(uri1));

        this.doc.undo(uri2);
        assertNull(this.doc.get(uri1));

        assertEquals("2AnyPossibleString", this.doc.get(uri2).getDocumentTxt());
    }

    @Test
    public void specificMetaDataTest() throws IOException {
        doc.put(this.string2, uri2, DocumentStore.DocumentFormat.TXT);
        doc.put(this.string1, uri1, DocumentStore.DocumentFormat.TXT);

        this.doc.setMetadata(uri2, "Time", "Yesterday");
        this.doc.setMetadata(uri1, "Time", "Today");

        this.doc.undo(uri2);

        assertNull(this.doc.get(uri2).getMetadataValue("Time"));

        this.doc.setMetadata(uri1, "Time", "Tomorrow");
        this.doc.setMetadata(uri2, "Time", "Yesterday");

        this.doc.undo(uri1);
        assertEquals("Today", this.doc.get(uri1).getMetadataValue("Time"));

    }


    }

