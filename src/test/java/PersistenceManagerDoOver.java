import static org.junit.jupiter.api.Assertions.*;

import edu.yu.cs.com1320.project.stage6.Document;
import edu.yu.cs.com1320.project.stage6.DocumentStore;
import edu.yu.cs.com1320.project.stage6.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage6.impl.DocumentPersistenceManager;
import edu.yu.cs.com1320.project.stage6.impl.DocumentStoreImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
public class PersistenceManagerDoOver {
    private DocumentPersistenceManager pm;

    @BeforeEach
    void setUp(){
        this.pm = new DocumentPersistenceManager(new File("RedoTesting"));
    }

    @Test
    public void serializeDocPath() throws IOException, URISyntaxException {
        DocumentImpl doc = new DocumentImpl(new URI("http://www.yu.edu/documents/doc1"), "g".getBytes());
        this.pm.serialize(doc.getKey(), doc);
    }
    @Test
    public void serializeContents() throws IOException, URISyntaxException {
        Document doc = new DocumentImpl(new URI("http://www.yu.edu/documents/doc2"), "Hello to the world, hopefully this works", null);
        this.pm.serialize(doc.getKey(), doc);

        Document doc2 = new DocumentImpl(new URI("http://www.yu.edu/documents/doc3"), "Hello to the world, hopefully this works", null);
        this.pm.serialize(doc2.getKey(), doc2);
    }
    @Test
    public void deserializeDoc() throws IOException, URISyntaxException {
        this.pm.deserialize(new URI("http://www.yu.edu/documents/doc3"));
        this.pm.deserialize(new URI("http://www.yu.edu/documents/doc2"));
    }

}
