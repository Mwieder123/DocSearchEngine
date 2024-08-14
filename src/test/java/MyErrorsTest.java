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
public class MyErrorsTest {
    private DocumentStoreImpl doc;
    @BeforeEach
    public void setup() throws URISyntaxException {
        this.doc = new DocumentStoreImpl();
    }
    @Test
    public void findMyErrors() throws IOException, URISyntaxException {
        for (int i = 0; i < 10; i++) {
            String str = i + "AnyPossibleString";
            byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
            InputStream string = new ByteArrayInputStream(bytes);
            this.doc.put(string, new URI("hi" + i), DocumentStore.DocumentFormat.TXT);
        }
        for (int j = 0; j < 10; j++) {
            j = j * 3;
            if (j < 10) this.doc.undo(new URI("hi" + j));
        }
        for (int k = 0; k < 10; k++) {
            k = k * 3;

            if (k < 10) assertNull(this.doc.get(new URI("hi" + k)));
        }


    }
    @Test
    public void findMyErrors2() throws IOException, URISyntaxException {
        for (int i = 0; i < 10; i++) {
            String str = i + "AnyPossibleString";
            byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
            InputStream string = new ByteArrayInputStream(bytes);
            this.doc.put(string, new URI("hi" + i), DocumentStore.DocumentFormat.TXT);
        }
        for (int j = 0; j < 10; j++) {
            this.doc.undo();
        }
        for (int k = 0; k < 10; k++) {
            assertNull(this.doc.get(new URI("hi" + (10 - k))));
        }

    }
    @Test
    public void findMyErrorsDelete() throws IOException, URISyntaxException {
        for (int i = 0; i < 10; i++) {
            String str = i + "AnyPossibleString";
            byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
            InputStream string = new ByteArrayInputStream(bytes);
            this.doc.put(string, new URI("hi" + i), DocumentStore.DocumentFormat.TXT);
        }
        for (int j = 0; j < 10; j++) {
            this.doc.delete(new URI("hi" + j));
        }
        for(int i = 0; i < 10; i++){
            this.doc.undo();
        }
        for (int k = 0; k < 10; k++) {
            assertEquals(k + "AnyPossibleString",this.doc.get(new URI("hi" + k)).getDocumentTxt());
        }
    }
    @Test
    public void findMyErrorsDeleteURI() throws IOException, URISyntaxException {
        for (int i = 0; i < 10; i++) {
            String str = i + "AnyPossibleString";
            byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
            InputStream string = new ByteArrayInputStream(bytes);
            this.doc.put(string, new URI("hi" + i), DocumentStore.DocumentFormat.TXT);
        }
        for (int j = 0; j < 10; j++) {
            j = j * 3;
            if (j < 10) this.doc.delete(new URI("hi" + j));
        }
        for(int i = 0; i < 10; i++){
            i = i * 3;
            if(i < 10) this.doc.undo(new URI("hi" + i));
        }
        for (int k = 0; k < 10; k++) {
            k = k * 3;
            if (k < 10) assertEquals(k + "AnyPossibleString",this.doc.get(new URI("hi" + k)).getDocumentTxt());
        }


    }
}
