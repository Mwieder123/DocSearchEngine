import edu.yu.cs.com1320.project.stage6.Document;
import edu.yu.cs.com1320.project.stage6.impl.DocumentStoreImpl;
import org.junit.jupiter.api.Test;
import edu.yu.cs.com1320.project.impl.TrieImpl;
import java.io.IOException;
import java.util.*;

public class Stage4ErrorsTest {
    private TrieImpl<Integer> trie;
    private DocumentStoreImpl store;

    @Test
    public void errorsTest() throws IOException {
        store = new DocumentStoreImpl();
        trie = new TrieImpl<>();
        trie.put("hellop", 2);
        List<Document> list = store.searchByPrefix(" hello");

        System.out.println(list);

        int size = list.size();
    }
}
