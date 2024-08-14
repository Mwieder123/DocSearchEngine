import static org.junit.jupiter.api.Assertions.*;

import edu.yu.cs.com1320.project.stage6.Document;
import edu.yu.cs.com1320.project.stage6.impl.DocumentImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import edu.yu.cs.com1320.project.impl.MinHeapImpl;

import java.net.URI;
import java.net.URISyntaxException;

public class MinHeapTest {
    private MinHeapImpl<Document> minHeap;
    private DocumentImpl doc1;
    private DocumentImpl doc2;
    private DocumentImpl doc3;
    private DocumentImpl doc4;
    private DocumentImpl doc5;
    private DocumentImpl doc6;
    private DocumentImpl doc7;
    private DocumentImpl doc8;

    @BeforeEach
    void setUp() throws URISyntaxException {
        this.minHeap = new MinHeapImpl<>();
        this.doc1 = new DocumentImpl(new URI("Doc1"), "1", null);
        this.doc2 = new DocumentImpl(new URI("Doc2"), "2", null);
        this.doc3 = new DocumentImpl(new URI("Doc3"), "3", null);
        this.doc4 = new DocumentImpl(new URI("Doc4"), "4", null);
        this.doc5 = new DocumentImpl(new URI("Doc5"), "5", null);
        this.doc6 = new DocumentImpl(new URI("Doc6"), "6", null);
        this.doc7 = new DocumentImpl(new URI("Doc7"), "7", null);
        this.doc8 = new DocumentImpl(new URI("Doc8"), "8", null);

        this.minHeap.insert(doc7);
        this.minHeap.insert(doc6);
        this.minHeap.insert(doc8);
        this.minHeap.insert(doc4);
        this.minHeap.insert(doc5);
        this.minHeap.insert(doc2);
        this.minHeap.insert(doc1);
        this.minHeap.insert(doc3);
    }
    @Test
    public void reHeapifyTest(){
        this.doc1.setLastUseTime(System.nanoTime());
        this.minHeap.reHeapify(doc1);

        assertEquals(this.doc2, this.minHeap.peek());

        this.doc2.setLastUseTime(System.nanoTime());
        this.minHeap.reHeapify(doc2);

        assertEquals(this.doc3, this.minHeap.peek());
    }
    @Test
    public void doublingTest(){
        for(int i = 0; i < 20;i++ ){
            this.minHeap.insert(doc3);
        }
        int x = 4;
    }


}
