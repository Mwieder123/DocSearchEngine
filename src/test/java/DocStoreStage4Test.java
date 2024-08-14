import edu.yu.cs.com1320.project.impl.TrieImpl;
import edu.yu.cs.com1320.project.stage6.Document;
import edu.yu.cs.com1320.project.stage6.DocumentStore;
import edu.yu.cs.com1320.project.stage6.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage6.impl.DocumentStoreImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import edu.yu.cs.com1320.project.impl.TrieImpl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DocStoreStage4Test{
    private DocumentStoreImpl docStore;
    public DocStoreStage4Test(){
        this.docStore = new DocumentStoreImpl();
    }
    @Test
    public void searchTest() throws URISyntaxException, IOException {
        DocumentImpl doc1 = new DocumentImpl(new URI("doc1"), "Today is a day with today and yesterday", null);
        DocumentImpl doc2 = new DocumentImpl(new URI("doc2"), "Today, Today Today Today Tomorrow, Yesterday", null);
        DocumentImpl doc3 = new DocumentImpl(new URI("doc3"), "Today today Today and yesterday", null);
        byte[] bytes1 = doc1.getDocumentTxt().getBytes();
        byte[] bytes2 = doc2.getDocumentTxt().getBytes();
        byte[] bytes3 = doc3.getDocumentTxt().getBytes();
        InputStream stream1 = new ByteArrayInputStream(bytes1);
        InputStream stream2 = new ByteArrayInputStream(bytes2);
        InputStream stream3 = new ByteArrayInputStream(bytes3);
        this.docStore.put(stream1, doc1.getKey(), DocumentStore.DocumentFormat.TXT);
        this.docStore.put(stream2, doc2.getKey(), DocumentStore.DocumentFormat.TXT);
        this.docStore.put(stream3, doc3.getKey(), DocumentStore.DocumentFormat.TXT);

        List<DocumentImpl> check = new ArrayList<>();
        check.add(doc2);
        check.add(doc3);
        check.add(doc1);

        assertEquals(check, this.docStore.search("Today"));
    }
    @Test
    public void deleteByKeywordTest() throws IOException, URISyntaxException {
        DocumentImpl doc1 = new DocumentImpl(new URI("doc1"), "Today is a day with today and yesterday", null);
        DocumentImpl doc2 = new DocumentImpl(new URI("doc2"), "Today, Today Today Today Tomorrow, Yesterday", null);
        DocumentImpl doc3 = new DocumentImpl(new URI("doc3"), "Today today Today and yesterday", null);
        byte[] bytes1 = doc1.getDocumentTxt().getBytes();
        byte[] bytes2 = doc2.getDocumentTxt().getBytes();
        byte[] bytes3 = doc3.getDocumentTxt().getBytes();
        InputStream stream1 = new ByteArrayInputStream(bytes1);
        InputStream stream2 = new ByteArrayInputStream(bytes2);
        InputStream stream3 = new ByteArrayInputStream(bytes3);
        this.docStore.put(stream1, doc1.getKey(), DocumentStore.DocumentFormat.TXT);
        this.docStore.put(stream2, doc2.getKey(), DocumentStore.DocumentFormat.TXT);
        this.docStore.put(stream3, doc3.getKey(), DocumentStore.DocumentFormat.TXT);

        this.docStore.deleteAll("Today");
        assertNull(this.docStore.get(doc1.getKey()));
        assertNull(this.docStore.get(doc2.getKey()));
        assertNull(this.docStore.get(doc3.getKey()));
    }
    @Test
    public void deleteByPrefixTest() throws URISyntaxException, IOException {
        DocumentImpl doc1 = new DocumentImpl(new URI("doc1"), "Today is a day with today and WWWyesterday", null);
        DocumentImpl doc2 = new DocumentImpl(new URI("doc2"), "WWWToday, Today Today Today Tomorrow, Yesterday", null);
        DocumentImpl doc3 = new DocumentImpl(new URI("doc3"), "Today WWWtoday Today and yesterday", null);
        byte[] bytes1 = doc1.getDocumentTxt().getBytes();
        byte[] bytes2 = doc2.getDocumentTxt().getBytes();
        byte[] bytes3 = doc3.getDocumentTxt().getBytes();
        InputStream stream1 = new ByteArrayInputStream(bytes1);
        InputStream stream2 = new ByteArrayInputStream(bytes2);
        InputStream stream3 = new ByteArrayInputStream(bytes3);
        this.docStore.put(stream1, doc1.getKey(), DocumentStore.DocumentFormat.TXT);
        this.docStore.put(stream2, doc2.getKey(), DocumentStore.DocumentFormat.TXT);
        this.docStore.put(stream3, doc3.getKey(), DocumentStore.DocumentFormat.TXT);

        this.docStore.deleteAllWithPrefix("WWW");

        assertNull(this.docStore.get(doc1.getKey()));
        assertNull(this.docStore.get(doc2.getKey()));
        assertNull(this.docStore.get(doc3.getKey()));
    }
    @Test
    public void searchByPrefixTest() throws IOException, URISyntaxException {
        DocumentImpl doc1 = new DocumentImpl(new URI("doc1"), "WWW", null);
        DocumentImpl doc2 = new DocumentImpl(new URI("doc2"), "WWWToday, WWWToday WWWToday Today WWWTomorrow, WWWYesterday", null);
        DocumentImpl doc3 = new DocumentImpl(new URI("doc3"), "WWWToday WWWtoday WWWp WWWm Today and yesterday", null);
        byte[] bytes1 = doc1.getDocumentTxt().getBytes();
        byte[] bytes2 = doc2.getDocumentTxt().getBytes();
        byte[] bytes3 = doc3.getDocumentTxt().getBytes();
        InputStream stream1 = new ByteArrayInputStream(bytes1);
        InputStream stream2 = new ByteArrayInputStream(bytes2);
        InputStream stream3 = new ByteArrayInputStream(bytes3);
        this.docStore.put(stream1, doc1.getKey(), DocumentStore.DocumentFormat.TXT);
        this.docStore.put(stream2, doc2.getKey(), DocumentStore.DocumentFormat.TXT);
        this.docStore.put(stream3, doc3.getKey(), DocumentStore.DocumentFormat.TXT);

        List<Document> check = new ArrayList<>();
        check.add(doc2);
        check.add(doc3);
        check.add(doc1);
        String REGEX = ".*WWW.*";
        Pattern pattern = Pattern.compile(REGEX);
        sortByFrequency comparator = new sortByFrequency(pattern);
        check.sort(comparator);
        List<Document> sortedDocs = this.docStore.searchByPrefix("WWW");

        assertEquals(check, sortedDocs);
    }
    @Test
    public void searchByMetadataTest() throws URISyntaxException, IOException {
        DocumentImpl doc1 = new DocumentImpl(new URI("doc1"), "Today is a day with today and WWWyesterday", null);
        DocumentImpl doc2 = new DocumentImpl(new URI("doc2"), "WWWToday, Today Today Today Tomorrow, Yesterday", null);
        DocumentImpl doc3 = new DocumentImpl(new URI("doc3"), "Today WWWtoday Today and yesterday", null);
        byte[] bytes1 = doc1.getDocumentTxt().getBytes();
        byte[] bytes2 = doc2.getDocumentTxt().getBytes();
        byte[] bytes3 = doc3.getDocumentTxt().getBytes();
        InputStream stream1 = new ByteArrayInputStream(bytes1);
        InputStream stream2 = new ByteArrayInputStream(bytes2);
        InputStream stream3 = new ByteArrayInputStream(bytes3);
        this.docStore.put(stream1, doc1.getKey(), DocumentStore.DocumentFormat.TXT);
        this.docStore.put(stream2, doc2.getKey(), DocumentStore.DocumentFormat.TXT);
        this.docStore.put(stream3, doc3.getKey(), DocumentStore.DocumentFormat.TXT);

        this.docStore.setMetadata(doc1.getKey(), "Time", "7:00PM");
        this.docStore.setMetadata(doc3.getKey(), "Time", "7:00PM");
        this.docStore.setMetadata(doc2.getKey(), "Time", "7:00PM");
        this.docStore.setMetadata(doc2.getKey(), "Date", "3");

        Map<String, String> table = new HashMap<>();
        table.put("Time", "7:00PM");
        table.put("Date", "3");

        List<Document> list = new ArrayList<>();
        list.add(doc2);
        assertEquals(list, this.docStore.searchByMetadata(table));
    }
    @Test
    public void searchByMetadataAndKeyWordTest() throws URISyntaxException, IOException {
        DocumentImpl doc1 = new DocumentImpl(new URI("doc1"), "Today is a day with today and WWWyesterday", null);
        DocumentImpl doc2 = new DocumentImpl(new URI("doc2"), "WWWToday, Today Today Today Tomorrow, Yesterday", null);
        DocumentImpl doc3 = new DocumentImpl(new URI("doc3"), "Today WWWtoday Today and Yesterday", null);
        byte[] bytes1 = doc1.getDocumentTxt().getBytes();
        byte[] bytes2 = doc2.getDocumentTxt().getBytes();
        byte[] bytes3 = doc3.getDocumentTxt().getBytes();
        InputStream stream1 = new ByteArrayInputStream(bytes1);
        InputStream stream2 = new ByteArrayInputStream(bytes2);
        InputStream stream3 = new ByteArrayInputStream(bytes3);
        this.docStore.put(stream1, doc1.getKey(), DocumentStore.DocumentFormat.TXT);
        this.docStore.put(stream2, doc2.getKey(), DocumentStore.DocumentFormat.TXT);
        this.docStore.put(stream3, doc3.getKey(), DocumentStore.DocumentFormat.TXT);

        this.docStore.setMetadata(doc1.getKey(), "Time", "7:00PM");
        this.docStore.setMetadata(doc3.getKey(), "Time", "7:00PM");
        this.docStore.setMetadata(doc2.getKey(), "Time", "7:00PM");
        this.docStore.setMetadata(doc2.getKey(), "Date", "3");

        List<DocumentImpl> check = new ArrayList<>();
        check.add(doc1);
        check.add(doc2);
        check.add(doc3);

        Map<String, String> table = new HashMap<>();
        table.put("Time", "7:00PM");
        table.put("Date", "3");

        List<Document> list = new ArrayList<>();
        list.add(doc2);
        assertEquals(list, this.docStore.searchByKeywordAndMetadata("Yesterday",table));

        table.remove("Date");
        list.add(doc3);
        assertTrue(list.containsAll(this.docStore.searchByKeywordAndMetadata("Yesterday",table)) && this.docStore.searchByKeywordAndMetadata("Yesterday",table).containsAll(list));
    }
    @Test
    public void searchByMetadataAndPrefixTest() throws URISyntaxException, IOException {
        DocumentImpl doc1 = new DocumentImpl(new URI("doc1"), "Today is a day with today and WWWyesterday", null);
        DocumentImpl doc2 = new DocumentImpl(new URI("doc2"), "WWWToday, Today Today Today Tomorrow, Yesterday", null);
        DocumentImpl doc3 = new DocumentImpl(new URI("doc3"), "Today WWWtoday Today and Yesterday", null);
        byte[] bytes1 = doc1.getDocumentTxt().getBytes();
        byte[] bytes2 = doc2.getDocumentTxt().getBytes();
        byte[] bytes3 = doc3.getDocumentTxt().getBytes();
        InputStream stream1 = new ByteArrayInputStream(bytes1);
        InputStream stream2 = new ByteArrayInputStream(bytes2);
        InputStream stream3 = new ByteArrayInputStream(bytes3);
        this.docStore.put(stream1, doc1.getKey(), DocumentStore.DocumentFormat.TXT);
        this.docStore.put(stream2, doc2.getKey(), DocumentStore.DocumentFormat.TXT);
        this.docStore.put(stream3, doc3.getKey(), DocumentStore.DocumentFormat.TXT);

        this.docStore.setMetadata(doc1.getKey(), "Time", "7:00PM");
        this.docStore.setMetadata(doc3.getKey(), "Time", "7:00PM");
        this.docStore.setMetadata(doc2.getKey(), "Time", "7:00PM");
        this.docStore.setMetadata(doc2.getKey(), "Date", "3");

        List<DocumentImpl> check = new ArrayList<>();
        check.add(doc1);
        check.add(doc2);
        check.add(doc3);

        Map<String, String> table = new HashMap<>();
        table.put("Time", "7:00PM");
        table.put("Date", "3");

        List<Document> list = new ArrayList<>();
        list.add(doc2);
        assertEquals(list, this.docStore.searchByPrefixAndMetadata("WWW",table));

        table.remove("Date");
        list.add(doc3);
        list.add(doc1);
        assertTrue(list.containsAll(this.docStore.searchByPrefixAndMetadata("WWW",table)) && this.docStore.searchByPrefixAndMetadata("WWW",table).containsAll(list));
    }
    @Test
    public void nullTest() throws IOException {
        assertNotNull(this.docStore.searchByPrefix("Todays"));
    }
    private class sortByFrequency implements Comparator<Document> {
        private String keyword;
        private Pattern prefix;
        private sortByFrequency(String keyword){
            this.keyword = keyword;
        }
        private sortByFrequency(Pattern prefix){
            this.prefix = prefix;
        }
        @Override
        public int compare(Document doc1, Document doc2) {
            if (this.keyword != null) {
                return doc2.wordCount(this.keyword) - doc1.wordCount(this.keyword);
            }
            else{
                Predicate<String> predicate = word -> word.startsWith(this.prefix.toString());
                Set<String> wordsWithPrefix = doc1.getWords().stream()
                        .filter(predicate).collect(Collectors.toSet());
                wordsWithPrefix.addAll(doc2.getWords().stream()
                        .filter(predicate).collect(Collectors.toSet()));
                int doc1WordCount = 0;
                int doc2WordCount = 0;

                for(String word : wordsWithPrefix){
                    doc1WordCount += doc1.wordCount(word);
                    doc2WordCount += doc2.wordCount(word);
                }
                return doc2WordCount - doc1WordCount;
            }

        }
    }
	
}