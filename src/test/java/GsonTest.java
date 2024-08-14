import edu.yu.cs.com1320.project.stage6.Document;
import edu.yu.cs.com1320.project.stage6.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage6.impl.DocumentPersistenceManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

public class GsonTest {
    private Document doc;
    private DocumentPersistenceManager pm;
    @BeforeEach
    void setUp(){
        this.pm = new DocumentPersistenceManager(new File("Practice"));
        this.doc = new DocumentImpl(URI.create("HHH"), "HHH", null);
    }
    @Test
    public void serializeTest() throws IOException {
        this.pm.serialize(URI.create("HHH/Documents///Jopl"), this.doc);
    }
    @Test
    public void deserializeTest() throws IOException {
        Document doc = (DocumentImpl) this.pm.deserialize(URI.create("HHH/Documents///Jopl"));
        System.out.println(doc.getDocumentTxt() + doc.getKey());
    }
    @Test
    public void serializeBinaryTest() throws IOException{
        Document binaryDoc = new DocumentImpl(URI.create("BinaryDoc"), "ShalomToYou".getBytes());
        binaryDoc.setMetadataValue("Time" , "Now");
        binaryDoc.setMetadataValue("Date", "Today");
        this.pm.serialize(binaryDoc.getKey(), binaryDoc);
    }
    @Test
    public void deserializeBinaryTest() throws IOException{
        Document doc = this.pm.deserialize(URI.create("BinaryDoc"));
        System.out.println(Arrays.equals(doc.getDocumentBinaryData(), "ShalomToYou".getBytes()) + " " + doc.getKey());
    }
}
