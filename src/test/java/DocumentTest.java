import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import edu.yu.cs.com1320.project.stage6.Document;
import edu.yu.cs.com1320.project.stage6.impl.DocumentImpl;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

public class DocumentTest{
    private DocumentImpl docString;
    private DocumentImpl docBinary;
    private URI uri = URI.create("foo://example.com:8042/over/there?name=ferret#nose");
    private byte[] binaryData = {-56, -123, -109, -109, -106, 64, -26,
            -106, -103, -109, -124, 90};

    @BeforeEach
    void setUp(){
        this.docString = new DocumentImpl(this.uri, "HelloWorld", null);
        this.docBinary = new DocumentImpl(this.uri, this.binaryData);
    }

    @Test
    public void badConstructors(){
        assertThrows(IllegalArgumentException.class, () -> {
             DocumentImpl doc = new DocumentImpl(this.uri, "  ", null);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            DocumentImpl doc = new DocumentImpl(URI.create(" "), "hello", null);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            DocumentImpl doc = new DocumentImpl(this.uri, new byte[0]);
        });
        assertThrows(IllegalArgumentException.class, () -> {
           DocumentImpl doc = new DocumentImpl(URI.create(" "),this.binaryData);
        });
    }
    @Test
    public void testGetters(){
        assertEquals("foo://example.com:8042/over/there?name=ferret#nose", this.docString.getKey().toString());
        assertEquals(-109, this.docBinary.getDocumentBinaryData()[2]);
        assertEquals("HelloWorld", this.docString.getDocumentTxt());
    }
    @Test
    public void metadataGetter(){
        assertNull(this.docString.setMetadataValue("date", "Jan30"));
        assertEquals("Jan30", this.docString.getMetadataValue("date"));
        assertNull(this.docString.getMetadataValue("time"));
        assertThrows(IllegalArgumentException.class, () -> {
            String error = this.docString.getMetadataValue(" ");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            String error = this.docString.getMetadataValue(null);
        });
    }
    @Test
    public void metadataSetter(){
        this.docString.setMetadataValue("date", "Jan30");
        assertEquals("Jan30", this.docString.setMetadataValue("date", "Feb16"));
        assertThrows(IllegalArgumentException.class, () -> {
            this.docString.setMetadataValue("  ", "Jan30");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            this.docString.setMetadataValue(null, "Jan30");
        });
    }
    @Test
    public void metadataMapGetter(){
        this.docString.setMetadataValue("date", "Jan30");
        this.docString.setMetadataValue("time", "5PM");
        this.docString.setMetadataValue("location", "NY");

        HashMap<String, String> testMap = new HashMap<>();
        testMap.put("date", "Jan30");
        testMap.put("time", "5PM");
        testMap.put("location", "NY");

        assertTrue(testMap.values().containsAll(this.docString.getMetadata().values()) && this.docString.getMetadata().values().containsAll(testMap.values()));
    }
}


