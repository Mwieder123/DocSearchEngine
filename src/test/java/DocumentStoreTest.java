import edu.yu.cs.com1320.project.stage6.Document;
import edu.yu.cs.com1320.project.stage6.DocumentStore;
import edu.yu.cs.com1320.project.stage6.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage6.impl.DocumentStoreImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

public class DocumentStoreTest {
    private DocumentStoreImpl docStore;
    private InputStream streamText;
    private InputStream streamBinary;
    private InputStream anotherStream;
    private URI uriText = URI.create("foo://example.com:8042/over/there?name=ferret#nose");
    private URI uriBinary = URI.create("foofightersareaband://example.com:8042/over/there?name=ferret#nose");
    private URI uriBad = URI.create("thisisaabduri://example.com:8042/over/there?name=ferret#nose");

    @BeforeEach
    void setUp() throws IOException {
        this.docStore = new DocumentStoreImpl();
        try {
            this.streamText = Files.newInputStream(Paths.get("C://Users//mowie//OneDrive//Desktop//Data_Structures//SampleTextFiles//Sample.txt"));
        } catch (IOException e) {
            throw new IOException("Issue Reading File");
        }
        ;
        try {
            this.streamBinary = Files.newInputStream(Paths.get("C://Users//mowie//OneDrive//Desktop//Data_Structures//SampleTextFiles//SampleBinary.txt"));
        } catch (IOException e) {
            throw new IOException("Issue Reading File");
        }
        ;
        this.anotherStream = Files.newInputStream(Paths.get("C://Users//mowie//OneDrive//Desktop//Data_Structures//SampleTextFiles//sample.txt"));
        this.docStore.put(this.streamText, this.uriText, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(this.streamBinary, this.uriBinary, DocumentStore.DocumentFormat.BINARY);
    }

    @Test
    public void deleteCheck() {
        assertTrue(this.docStore.delete(this.uriText));
        assertFalse(this.docStore.delete(this.uriText));
    }

    @Test
    public void getDoc() throws IOException {
        DocumentImpl doc = new DocumentImpl(this.uriText, "Hello this is a test I hope this works - it looks like it does", null);
        assertEquals(doc, this.docStore.get(this.uriText));
        assertNull(this.docStore.get(URI.create("hello")));
    }

    @Test
    public void setMetaDataTest() throws IOException {
        assertNull(this.docStore.setMetadata(this.uriText,"date", "Jan30"));
        assertEquals("Jan30", this.docStore.setMetadata(this.uriText,"date", "Feb16"));
        assertThrows(IllegalArgumentException.class, () -> {
            this.docStore.setMetadata(URI.create(" "), "hello", "hello");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            this.docStore.setMetadata(null, "hello", "hello");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            this.docStore.setMetadata(this.uriText, "  ", "hello");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            this.docStore.setMetadata(this.uriText, null, "hello");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            this.docStore.setMetadata(URI.create("shalom"), "hello", "hello");
        });
    }
    @Test
    public void getMetadataTest() throws IOException {
        assertThrows(IllegalArgumentException.class, () -> {
            this.docStore.getMetadata(URI.create(" "), "hello");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            this.docStore.getMetadata(null, "hello");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            this.docStore.getMetadata(this.uriText, "  ");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            this.docStore.getMetadata(this.uriText, null);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            this.docStore.getMetadata(URI.create("shalom"), "hello");
        });
        this.docStore.setMetadata(this.uriText,"date", "Jan30");
        assertEquals("Jan30",this.docStore.getMetadata(this.uriText,"date"));
        assertNull(this.docStore.getMetadata(this.uriText, "time"));
    }
    @Test
    public void putTest() throws IOException {
        assertThrows(IllegalArgumentException.class, () -> {
            this.docStore.put(null, URI.create("  "), DocumentStore.DocumentFormat.TXT);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            this.docStore.put(null, null, DocumentStore.DocumentFormat.TXT);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            this.docStore.put(null, this.uriText, null);
        });
    }
    @Test
    public void putDelete() throws IOException {
        assertEquals(this.docStore.get(this.uriText).hashCode(),this.docStore.put(null, this.uriText, DocumentStore.DocumentFormat.TXT));
        assertEquals(0,this.docStore.put(null, this.uriText, DocumentStore.DocumentFormat.TXT));
        assertNull(this.docStore.get(this.uriText));


    }
}
