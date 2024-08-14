import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import edu.yu.cs.com1320.project.impl.TrieImpl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class TrieTest {
    private TrieImpl<Integer> trie;

    @BeforeEach
    void setUp(){
        this.trie = new TrieImpl<>();
    }

    @Test
    public void putTest(){
        this.trie.put("09AZaz", 5);
        this.trie.put("09AZaz", 9);
        this.trie.put("!0/9,AZ.az", 11);

        this.trie.put("09AZaz", 5);
    }
    @Test
    public void getTest(){
        this.trie.put("09AZaz", 5);
        this.trie.put("09AZaz", 9);
        this.trie.put("!0/9,AZ.az", 11);

        Set<Integer> check = new HashSet<>();
        check.add(5);
        check.add(9);
        check.add(11);

        assertEquals(check, this.trie.get("09AZaz"));
    }
    @Test
    public void deleteAllTest(){
        this.trie.put("09AZaz", 5);
        this.trie.put("09AZaz", 9);
        this.trie.put("!0/9,AZ.az", 11);

        Set<Integer> deletedSet = this.trie.deleteAll("09AZaz");

        Set<Integer> check = new HashSet<>();
        check.add(5);
        check.add(9);
        check.add(11);

        Set<Integer> empty = new HashSet<>();

        assertEquals(check, deletedSet);
        assertEquals(empty, this.trie.get("09AZaz"));
    }
    @Test
    public void deleteTest(){
        this.trie.put("09AZaz", 5);
        this.trie.put("09AZaz", 9);
        this.trie.put("!0/9,AZ.az", 11);

        int value = this.trie.delete("09AZaz", 9);

        Set<Integer> check = new HashSet<>();
        check.add(5);
        check.add(11);

        assertEquals(check, this.trie.get("09AZaz"));
        assertEquals(9, value);
    }
    @Test
    public void deletePrefixTest(){
        this.trie.put("Hello", 3);
        this.trie.put("Hellman's", 4);
        this.trie.put("He", 23);

        Set<Integer> deletedSet = this.trie.deleteAllWithPrefix("He");

        Set<Integer> check = new HashSet<>();
        check.add(3);
        check.add(4);
        check.add(23);

        Set<Integer> empty = new HashSet<>();

        assertEquals(check, deletedSet);
        assertEquals(empty, this.trie.get("Hello"));
        assertEquals(empty, this.trie.get("Hellman's"));
        assertEquals(empty, this.trie.get("He"));
    }
    @Test
    public void getPrefixOrderedTest(){
        this.trie.put("Hello", 3);
        this.trie.put("He7lman's", 4);
        this.trie.put("He34433", 93);
        this.trie.put("He0fhbyf", 230);
        this.trie.put("He", 231);

        ArrayList<Integer> check = new ArrayList<>();
        check.add(3);
        check.add(4);
        check.add(93);
        check.add(230);
        check.add(231);

        sortByNumber sort = new sortByNumber();
        assertEquals(check, this.trie.getAllWithPrefixSorted("He", sort));
    }
    public Set<Integer> seeRecursion(Set<Integer> set, int duration){
        for(int i = 0; i< duration; i++){
            set.add(i);
            seeRecursion(set, duration-1);
        }
        return set;
    }
    @Test
    public void compareTest(){
        this.trie.put("He", 3);
        this.trie.put("He", 4);
        this.trie.put("He", 23);
        this.trie.put("He", 100);
        this.trie.put("He", 7);
        this.trie.put("He", 0);
        this.trie.put("He", 2);

        ArrayList<Integer> check = new ArrayList<>();
        check.add(0);
        check.add(2);
        check.add(3);
        check.add(4);
        check.add(7);
        check.add(23);
        check.add(100);

        Comparator<Integer> sortByNumber = new sortByNumber();
        assertEquals(check, this.trie.getSorted("He", sortByNumber));
    }

        private class sortByNumber implements Comparator<Integer> {

        @Override
        public int compare(Integer o1, Integer o2) {
            return o1 - o2;
        }
    }

    /*
    Things Left To Do in Stage 4:
    - Implement undo
    - Check deletes remove all traces of documents from all Maps and Tries
    - Further Testing
    - Try to clean up TrieImpl implementation
    - Check Piazza
     */
}
