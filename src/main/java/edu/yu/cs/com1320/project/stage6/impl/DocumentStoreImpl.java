package edu.yu.cs.com1320.project.stage6.impl;

import edu.yu.cs.com1320.project.Stack;
import edu.yu.cs.com1320.project.impl.MinHeapImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.impl.TrieImpl;
import edu.yu.cs.com1320.project.stage6.Document;
import edu.yu.cs.com1320.project.stage6.DocumentStore;
import edu.yu.cs.com1320.project.impl.BTreeImpl;
import edu.yu.cs.com1320.project.undo.CommandSet;
import edu.yu.cs.com1320.project.undo.GenericCommand;
import edu.yu.cs.com1320.project.undo.Undoable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DocumentStoreImpl implements DocumentStore {
    private BTreeImpl<URI, Document> documents;
    private StackImpl<Undoable> commandStack;
    private TrieImpl<UriEntry> textTrie;
    private TrieImpl<TrieImpl<UriEntry>> metaTrie;
    private MinHeapImpl<UriEntry> minHeap;
    private ArrayList<UriEntry> uris;
    private int docMemory;
    private int bytesMemory;
    private int maxDocCount;
    private int maxBytesCount;

    public class UriEntry implements Comparable<UriEntry> {
        private URI uri;
        private Boolean inMemory;
        private UriEntry(URI uri){
            this.inMemory = true;
            this.uri = uri;
        }
        private Boolean getInMemory(){return this.inMemory;}
        private void setInMemory(Boolean inMemory){this.inMemory = inMemory;}
        private URI getURI(){
            return this.uri;
        }
        @Override
        public int compareTo(UriEntry uriEntry) {
            Long doc1Time = documents.get(this.getURI()).getLastUseTime();
            Long doc2Time = documents.get(uriEntry.getURI()).getLastUseTime();

            return doc1Time.compareTo(doc2Time);
        }
        @Override
        public int hashCode(){
          return this.uri.hashCode();
        }
        @Override
        public boolean equals(Object o){
            if(o == this){
                return true;
            }
            if(!(o instanceof UriEntry)){
                return false;
            }
            UriEntry u = (UriEntry) o;
            return this.hashCode() - u.hashCode() == 0;
        }
    }

    public DocumentStoreImpl(){
        this.documents = new BTreeImpl<>();
        this.documents.setPersistenceManager(new DocumentPersistenceManager(null));
        this.commandStack = new StackImpl<>();
        this.textTrie = new TrieImpl<>();
        this.metaTrie = new TrieImpl<>();
        this.minHeap = new MinHeapImpl<>();
        this.maxBytesCount = -1;
        this.maxDocCount = -1;
        this.docMemory = 0;
        this.bytesMemory = 0;
        this.uris = new ArrayList<>();
    }
    public DocumentStoreImpl(File baseDir){
        this.documents = new BTreeImpl<>();
        this.documents.setPersistenceManager(new DocumentPersistenceManager(baseDir));
        this.commandStack = new StackImpl<>();
        this.textTrie = new TrieImpl<>();
        this.metaTrie = new TrieImpl<>();
        this.minHeap = new MinHeapImpl<>();
        this.maxBytesCount = -1;
        this.maxDocCount = -1;
        this.docMemory = 0;
        this.bytesMemory = 0;
        this.uris = new ArrayList<>();
    }


    @Override
    public String setMetadata(URI uri, String key, String value) throws IOException {
        if(key == null || key.isBlank() || uri == null || uri.toString().isBlank() || !this.uris.contains(new UriEntry(uri))){
            throw new IllegalArgumentException();
        }
        Document thisDoc = this.documents.get(uri);
        UriEntry uriEntry = new UriEntry(thisDoc.getKey());
        String oldMetadata = thisDoc.getMetadataValue(key);
        thisDoc.setLastUseTime(System.nanoTime());
        boolean inMemory = getMemoryStatus(uriEntry);
        changeMemoryStatus(uriEntry, true);
        if(!inMemory){
            this.minHeap.insert(uriEntry);
            this.docMemory++;
            if(thisDoc.getDocumentTxt() != null){
                this.bytesMemory += thisDoc.getDocumentTxt().getBytes().length;
            }
            else this.bytesMemory += thisDoc.getDocumentBinaryData().length;
        }
        this.minHeap.reHeapify(uriEntry);
        Set<UriEntry> pushedOut = enforceMemoryLimits();

        GenericCommand<UriEntry> element;
        if(oldMetadata == null){
            element = new GenericCommand<>(uriEntry, url -> {
                this.deleteDocsByOneMetadata(key, value, uriEntry);
                thisDoc.setMetadataValue(key, null);
                try {
                    if(!inMemory)
                        this.removeFromMemory(uriEntry);
                    this.putBackInMemory(pushedOut);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        else{
            element = new GenericCommand<>(uriEntry, url -> {
                thisDoc.setMetadataValue(key, oldMetadata);
                this.deleteDocsByOneMetadata(key, value, uriEntry);
                this.putMetadata(key, oldMetadata, uriEntry);
                try {
                    if(!inMemory)
                        this.removeFromMemory(uriEntry);
                    this.putBackInMemory(pushedOut);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        this.commandStack.push(element);

        if(oldMetadata != null){
            this.deleteDocsByOneMetadata(key, oldMetadata, uriEntry);
        }
        this.putMetadata(key, value, uriEntry);
        thisDoc.setMetadataValue(key, value);

        return oldMetadata;
    }
    @Override
    public String getMetadata(URI uri, String key) throws IOException {
        if(key == null || key.isBlank() || uri == null || uri.toString().isBlank() || !this.uris.contains(new UriEntry(uri))){
            throw new IllegalArgumentException();
        }
        Document thisDoc = this.documents.get(uri);
        UriEntry uriEntry = new UriEntry(thisDoc.getKey());
        boolean inMemory = getMemoryStatus(uriEntry);
        changeMemoryStatus(uriEntry, true);
        thisDoc.setLastUseTime(System.nanoTime());
        if(!inMemory){
            this.minHeap.insert(new UriEntry(thisDoc.getKey()));
            this.docMemory++;
            if(thisDoc.getDocumentTxt() != null){
                this.bytesMemory += thisDoc.getDocumentTxt().getBytes().length;
            }
            else this.bytesMemory += thisDoc.getDocumentBinaryData().length;
        }
        this.minHeap.reHeapify(uriEntry);
        this.enforceMemoryLimits();


        return thisDoc.getMetadataValue(key);
    }

    @Override
    public int put(InputStream input, URI uri, DocumentFormat format) throws IOException {
        if(uri == null || uri.toString().isBlank() || format == null){
            throw new IllegalArgumentException();
        }
        int oldHash = 0;
        Document oldDoc = this.documents.get(uri);
        UriEntry uriEntry = new UriEntry(uri);
        if(oldDoc != null){
            oldHash =  oldDoc.hashCode();
            }
        if(input == null){
            delete(uri);
            return oldHash;}
        byte[] byteStream = readBytes(input);
        if(this.maxBytesCount != -1 && byteStream.length > this.maxBytesCount){
            throw new IllegalArgumentException("Document Is Too Big");
        }
        Document newDoc;
        if(format == DocumentFormat.TXT)
            newDoc = new DocumentImpl(uri, new String(byteStream), null);
        else
            newDoc = new DocumentImpl(uri, byteStream);

        newDoc.setLastUseTime(System.nanoTime());

        if(oldDoc != null){
            removeOldDoc(oldDoc);
        }


        this.docMemory++;

        this.documents.put(uri, newDoc);
        this.bytesMemory += byteStream.length;
        this.minHeap.insert(uriEntry);

        if(!this.uris.contains(uriEntry)){
            this.uris.add(uriEntry);
        }

        Set<UriEntry> pushedOut = this.enforceMemoryLimits();

        if(oldHash == 0){
            commandForNewPut(new UriEntry(newDoc.getKey()), newDoc);}
        else{
            this.pushCommand(new UriEntry(newDoc.getKey()), uri, new UriEntry(oldDoc.getKey()), pushedOut);
        }
        for(String word : newDoc.getWords()){
            this.textTrie.put(word, uriEntry);}
        changeMemoryStatus(uriEntry, true);

        return oldHash;
    }
    @Override
    public Document get(URI url) throws IOException{
        if(!this.uris.contains(new UriEntry(url)))
            return null;
        Document thisDoc = this.documents.get(url);
        if(thisDoc != null){
            UriEntry uri = new UriEntry(thisDoc.getKey());
            thisDoc.setLastUseTime(System.nanoTime());
            if(!getMemoryStatus(uri)){
                this.minHeap.insert(uri);
                this.docMemory++;
                if(thisDoc.getDocumentTxt() != null){
                    this.bytesMemory += thisDoc.getDocumentTxt().getBytes().length;
                }
                else this.bytesMemory += thisDoc.getDocumentBinaryData().length;
            }
            this.minHeap.reHeapify(uri);
            changeMemoryStatus(uri, true);
            this.enforceMemoryLimits();
        }
        return thisDoc;
    }

    @Override
    public boolean delete(URI url) {
        if(!this.uris.contains(new UriEntry(url))) return false;
        Document thisDoc = null;
        try {
            thisDoc = getPrivate(url);
        } catch (IOException e) {
            return false;
        }
        if(thisDoc == null){
            return false;
        }
        GenericCommand<UriEntry> element = getCommandForDelete(new UriEntry(url), thisDoc);

        this.commandStack.push(element);

        for(String word : thisDoc.getWords()){
            this.textTrie.delete(word, new UriEntry(url));
        }
        this.deleteDocsByMetadata(new UriEntry(url));
        this.deleteFromHeap(new UriEntry(url));
        this.uris.remove(new UriEntry(url));
        this.docMemory--;
        if(thisDoc.getDocumentTxt() != null){
            this.bytesMemory -= thisDoc.getDocumentTxt().getBytes().length;
        }
        else this.bytesMemory -= thisDoc.getDocumentBinaryData().length;
        return this.documents.put(url, null) != null;
    }

    @Override
    public void undo() throws IllegalStateException {
        if(this.commandStack.size() == 0){
            throw new IllegalStateException("No Actions to Undo");
        }
        Undoable command = this.commandStack.pop();
        command.undo();
    }

    @Override
    public void undo(URI url) throws IllegalStateException {
        Stack<Undoable> tempStack = new StackImpl<>();
        boolean found = false;
        while(this.commandStack.size() != 0 && !found){
            Undoable topCommand = this.commandStack.peek();
            if(topCommand instanceof GenericCommand){
                UriEntry uriEntry = (UriEntry) ((GenericCommand<?>) topCommand).getTarget();
                URI uri = uriEntry.getURI();
                if(uri.equals(url)) {
                    this.undo();
                    found = true;}
                else{tempStack.push(topCommand);
                    this.commandStack.pop();}}
            else{CommandSet<UriEntry> commandSet = (CommandSet<UriEntry>) topCommand;
                Document docToUndo = null;
                for (GenericCommand<UriEntry> command : commandSet) {
                    UriEntry uriEntry = command.getTarget();
                    URI uri = uriEntry.getURI();
                    if (uri.equals(url)) {docToUndo = this.documents.get(uri);
                        found = true;
                        break;}
                    else {tempStack.push(topCommand);
                        this.commandStack.pop();}}
                if(docToUndo != null) {
                    commandSet.undo(new UriEntry(docToUndo.getKey()));
                    topCommand = commandSet;
                    if (((CommandSet<?>) topCommand).isEmpty()) {
                        this.commandStack.pop();}}}}
        this.putBackStack(tempStack, found);}
    @Override
    public List<Document> search(String keyword) throws IOException {
        List<UriEntry> uriEntries = searchPrivate(keyword);
        List<Document> documentList = new ArrayList<>();
        long newTime = System.nanoTime();
        for(UriEntry uriEntry : uriEntries){
            Document doc = this.documents.get(uriEntry.getURI());
            documentList.add(doc);
            doc.setLastUseTime(newTime);
            UriEntry uri = new UriEntry(doc.getKey());
            if(!getMemoryStatus(new UriEntry(doc.getKey()))){
                this.minHeap.insert(uri);
                this.docMemory++;
                if(doc.getDocumentTxt() != null){
                    this.bytesMemory += doc.getDocumentTxt().getBytes().length;
                }
                else this.bytesMemory += doc.getDocumentBinaryData().length;
            }

            this.minHeap.reHeapify(uri);
            changeMemoryStatus(new UriEntry(doc.getKey()), true);
            this.enforceMemoryLimits();
        }
        return documentList;
    }

    @Override
    public List<Document> searchByPrefix(String keywordPrefix) throws IOException {
        List<UriEntry> uriEntries = searchByPrefixPrivate(keywordPrefix);
        List<Document> documentList = new ArrayList<>();
        long newTime = System.nanoTime();
        for(UriEntry uriEntry : uriEntries){
            Document doc = this.documents.get(uriEntry.getURI());
            documentList.add(doc);
            doc.setLastUseTime(newTime);
            UriEntry uri = new UriEntry(doc.getKey());
            if(!getMemoryStatus(uri)){
                this.minHeap.insert(uri);
                this.docMemory++;
                if(doc.getDocumentTxt() != null){
                    this.bytesMemory += doc.getDocumentTxt().getBytes().length;
                }
                else this.bytesMemory += doc.getDocumentBinaryData().length;
            }
            this.minHeap.reHeapify(uri);
            changeMemoryStatus(new UriEntry(doc.getKey()), true);
            this.enforceMemoryLimits();
        }
        return documentList;
    }

    @Override
    public Set<URI> deleteAll(String keyword) {
        Set<UriEntry> uriEntries = this.textTrie.deleteAll(keyword);
        return deleteEntries((Set<UriEntry>) uriEntries);
    }
    @Override
    public Set<URI> deleteAllWithPrefix(String keywordPrefix) {
        Set<UriEntry> uriEntries = this.textTrie.deleteAllWithPrefix(keywordPrefix);
        return deleteEntries((Set<UriEntry>) uriEntries);
    }

    private Set<URI> deleteEntries(Set<UriEntry> uriEntries) {
        Set<URI> uris = new HashSet<>();

        CommandSet<UriEntry> setOfDocsToUndo = new CommandSet<>();

        for(UriEntry uriEntry: uriEntries){
            uris.add(uriEntry.getURI());
            this.deleteDocsByMetadata(uriEntry);
            this.deleteFromHeap(uriEntry);
            this.uris.remove(uriEntry);
            this.docMemory--;
            Document doc = this.documents.get(uriEntry.getURI());
            if(doc.getDocumentTxt() != null){
                this.bytesMemory -= doc.getDocumentTxt().getBytes().length;
            }
            else this.bytesMemory -= doc.getDocumentBinaryData().length;

            GenericCommand<UriEntry> command = getCommandForDelete(uriEntry, doc);
            setOfDocsToUndo.addCommand(command);
            this.documents.put(uriEntry.getURI(), null);
        }
        this.commandStack.push(setOfDocsToUndo);

        return uris;
    }

    @Override
    public List<Document> searchByMetadata(Map<String, String> keysValues) throws IOException{
        if(keysValues == null || keysValues.keySet().isEmpty()){
            return new ArrayList<>();
        }
        List<UriEntry> uriEntries = searchByMetadataPrivate(keysValues);
        List<Document> documents = new ArrayList<>();

        for(UriEntry uriEntry : uriEntries){
            Document doc = this.documents.get(uriEntry.getURI());
            documents.add(doc);
            dealWithSearchResults(doc);
        }
        return documents;
    }

    private void dealWithSearchResults(Document doc) throws IOException {
        doc.setLastUseTime(System.nanoTime());
        UriEntry uri = new UriEntry(doc.getKey());
        if(!getMemoryStatus(uri)){
            this.minHeap.insert(uri);
            this.docMemory++;
            if(doc.getDocumentTxt() != null){
                this.bytesMemory += doc.getDocumentTxt().getBytes().length;
            }
            else this.bytesMemory += doc.getDocumentBinaryData().length;
        }
        this.minHeap.reHeapify(uri);
        changeMemoryStatus(new UriEntry(doc.getKey()), true);
        this.enforceMemoryLimits();
    }

    @Override
    public List<Document> searchByKeywordAndMetadata(String keyword, Map<String, String> keysValues) throws IOException {
        List<Document> documents = this.getKeywordMetadataMatches(keyword, keysValues);
        for(Document doc : documents){
            dealWithSearchResults(doc);
        }
        return documents;
    }

    @Override
    public List<Document> searchByPrefixAndMetadata(String keywordPrefix, Map<String, String> keysValues) throws IOException {
        List<Document> documents = this.getPrefixMetadataMatches(keywordPrefix, keysValues);
        for(Document doc : documents){
            dealWithSearchResults(doc);
        }
        return documents;
    }

    @Override
    public Set<URI> deleteAllWithMetadata(Map<String, String> keysValues) throws IOException{
        if(keysValues == null || keysValues.keySet().isEmpty()){
            return new HashSet<>();
        }
        Set<UriEntry> docsToDelete = new HashSet<>(this.searchByMetadataPrivate(keysValues));
        Set<URI> uris = new HashSet<>();
        CommandSet<UriEntry> setOfDocsToUndo = new CommandSet<>();

        for(UriEntry uriEntry : docsToDelete){
            uris.add(uriEntry.getURI());
            this.deleteDocsByMetadata(uriEntry);
            this.deleteFromHeap(uriEntry);
            this.uris.remove(uriEntry);
            this.docMemory--;
            Document doc = this.documents.get(uriEntry.getURI());
            if(doc.getDocumentTxt() != null){
                this.bytesMemory -= doc.getDocumentTxt().getBytes().length;
            }
            else this.bytesMemory -= doc.getDocumentBinaryData().length;

            for(String word : doc.getWords()){
                this.textTrie.delete(word, new UriEntry(doc.getKey()));
            }

            GenericCommand<UriEntry> command = getCommandForDelete(new UriEntry(doc.getKey()), doc);
            setOfDocsToUndo.addCommand(command);
            this.documents.put(doc.getKey(), null);
        }
        this.commandStack.push(setOfDocsToUndo);

        return uris;
    }
    @Override
    public Set<URI> deleteAllWithKeywordAndMetadata(String keyword, Map<String, String> keysValues) throws IOException {
        List<Document> documentMatches = this.getKeywordMetadataMatches(keyword, keysValues);
        Set<URI> uris = new HashSet<>();

        CommandSet<UriEntry> setOfDocsToUndo = new CommandSet<>();

        for(Document document : documentMatches){
            this.deleteDocsByMetadata(new UriEntry(document.getKey()));
            uris.add(document.getKey());
            this.deleteFromHeap(new UriEntry(document.getKey()));
            this.uris.remove(new UriEntry(document.getKey()));
            this.docMemory--;
            if(document.getDocumentTxt() != null){
                this.bytesMemory -= document.getDocumentTxt().getBytes().length;
            }
            else this.bytesMemory -= document.getDocumentBinaryData().length;

            for(String word : document.getWords()){
                this.textTrie.delete(word, new UriEntry(document.getKey()));
            }

            GenericCommand<UriEntry> command = getCommandForDelete(new UriEntry(document.getKey()), document);
            setOfDocsToUndo.addCommand(command);
            this.documents.put(document.getKey(), null);
        }
        this.commandStack.push(setOfDocsToUndo);

        return uris;
    }

    @Override
    public Set<URI> deleteAllWithPrefixAndMetadata(String keywordPrefix, Map<String, String> keysValues) throws IOException {
        List<Document> documentMatches = this.getPrefixMetadataMatches(keywordPrefix, keysValues);
        Set<URI> uris = new HashSet<>();

        CommandSet<UriEntry> setOfDocsToUndo = new CommandSet<>();

        for(Document document : documentMatches){
            this.deleteDocsByMetadata(new UriEntry(document.getKey()));
            uris.add(document.getKey());
            this.deleteFromHeap(new UriEntry(document.getKey()));
            this.uris.remove(new UriEntry(document.getKey()));
            this.docMemory--;
            if(document.getDocumentTxt() != null){
                this.bytesMemory -= document.getDocumentTxt().getBytes().length;
            }
            else this.bytesMemory -= document.getDocumentBinaryData().length;

            for(String word : document.getWords()){
                this.textTrie.delete(word, new UriEntry(document.getKey()));
            }

            GenericCommand<UriEntry> command = getCommandForDelete(new UriEntry(document.getKey()), document);
            setOfDocsToUndo.addCommand(command);
            this.documents.put(document.getKey(), null);
        }
        this.commandStack.push(setOfDocsToUndo);

        return uris;
    }

    @Override
    public void setMaxDocumentCount(int limit) {
        if(limit < 1){
            throw new IllegalArgumentException();
        }
        this.maxDocCount = limit;
        try {
            enforceMemoryLimits();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setMaxDocumentBytes(int limit) {
        if(limit < 1){
            throw new IllegalArgumentException();
        }
        this.maxBytesCount = limit;
        try {
            enforceMemoryLimits();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private List<Document> getPrefixMetadataMatches(String keywordPrefix, Map<String, String> keysValues){
        List<UriEntry> keyWordMatches = this.searchByPrefixPrivate(keywordPrefix);
        List<UriEntry> metadataMatches = this.searchByMetadataPrivate(keysValues);
        List<UriEntry> containsBoth = new ArrayList<>();

        if(keyWordMatches.size() > metadataMatches.size()){
            for(UriEntry keywordMatch : keyWordMatches){
                if(metadataMatches.contains(keywordMatch))
                    containsBoth.add(keywordMatch);
            }
        }
        else{
            for(UriEntry metadataMatch : metadataMatches){
                if(keyWordMatches.contains(metadataMatch))
                    containsBoth.add(metadataMatch);
            }
        }
        List<Document> sortedDocs = new ArrayList<>();
        List<UriEntry> uriEntries = new ArrayList<>(containsBoth);
        sortByFrequency comparator = new sortByFrequency(keywordPrefix);
        uriEntries.sort((Comparator) comparator);

        for(UriEntry uriEntry : uriEntries){
            sortedDocs.add(this.documents.get(uriEntry.getURI()));
        }

        return sortedDocs;
    }
    private List<Document> getKeywordMetadataMatches(String keyword, Map<String, String> keysValues){
        List<UriEntry> keyWordMatches = this.searchPrivate(keyword);
        List<UriEntry> metadataMatches = this.searchByMetadataPrivate(keysValues);
        List<UriEntry> containsBoth = new ArrayList<>();

        if(keyWordMatches.size() > metadataMatches.size()){
            for(UriEntry keywordMatch : keyWordMatches){
                if(metadataMatches.contains(keywordMatch))
                    containsBoth.add(keywordMatch);
            }
        }
        else{
            for(UriEntry metadataMatch : metadataMatches){
                if(keyWordMatches.contains(metadataMatch))
                    containsBoth.add(metadataMatch);
            }
        }
        List<Document> sortedDocs = new ArrayList<>();
        List<UriEntry> uriEntries = new ArrayList<>(containsBoth);
        sortByFrequency comparator = new sortByFrequency(keyword);
        uriEntries.sort((Comparator) comparator);

        for(UriEntry uriEntry : uriEntries){
            sortedDocs.add(this.documents.get(uriEntry.getURI()));
        }

        return sortedDocs;
    }
    private void putMetadata(String key, String value, UriEntry uriEntry){
        if(this.metaTrie.get(key).isEmpty()){
            this.metaTrie.put(key, new TrieImpl<UriEntry>());
        }
        TrieImpl<UriEntry> valueTrie = this.metaTrie.get(key).iterator().next();
        if(value != null)
            valueTrie.put(value, new UriEntry(uriEntry.getURI()));
    }
    private Set<UriEntry> getMetadataDocs(String key, String value){
        key = key.replaceAll("[^0-9A-Za-z]", "").trim();
        value = value.replaceAll("[^0-9A-Za-z]", "").trim();
        TrieImpl<UriEntry> valueTrie = this.metaTrie.get(key).iterator().next();
        return new HashSet<>(valueTrie.get(value));
    }
    private void deleteDocsByMetadata(UriEntry uri){
        Document document = this.documents.get(uri.getURI());
        for(String key : document.getMetadata().keySet()){
            key = key.replaceAll("[^0-9A-Za-z]", "").trim();
            String value = document.getMetadataValue(key);
            if(value == null){
                break;
            }
            value = value.replaceAll("[^0-9A-Za-z]", "").trim();
            TrieImpl<UriEntry> valueTrie = this.metaTrie.get(key).iterator().next();
            valueTrie.delete(value, uri);
        }
    }
    private void deleteDocsByOneMetadata(String key, String value, UriEntry uri){
        key = key.replaceAll("[^0-9A-Za-z]", "").trim();
        value = value.replaceAll("[^0-9A-Za-z]", "").trim();
        TrieImpl<UriEntry> valueTrie = this.metaTrie.get(key).iterator().next();
        valueTrie.delete(value, uri);
    }
    private void putBackStack(Stack<Undoable> tempStack, boolean found){
        while(tempStack.size() != 0){
            Undoable topCommand = tempStack.pop();
            this.commandStack.push(topCommand);
        }

        if(!found){
            throw new IllegalStateException("No Documents with that URI");
        }
    }
    private void commandForNewPut(UriEntry newUri, Document newDoc){
        GenericCommand<UriEntry> element = new GenericCommand<>(newUri, url -> {
            for(String word : newDoc.getWords()){
                this.textTrie.delete(word, newUri);}
            this.deleteDocsByMetadata(newUri);
            this.deleteFromHeap(newUri);
            this.uris.remove(newUri);
            this.docMemory--;
            if(newDoc.getDocumentTxt() != null){
                this.bytesMemory -= newDoc.getDocumentTxt().getBytes().length;
            }
            else this.bytesMemory -= newDoc.getDocumentBinaryData().length;
            this.documents.put(newUri.getURI(), null);
        });
        this.commandStack.push(element);
    }
    private void pushCommand(UriEntry newUri, URI uri, UriEntry oldUri, Set<UriEntry> pushedOut){
        Document oldDoc = this.documents.get(oldUri.getURI());
        Document newDoc = this.documents.get(newUri.getURI());
        boolean inMemory = getMemoryStatus(new UriEntry(oldDoc.getKey()));

        if(oldDoc.getDocumentTxt() != null && this.maxBytesCount != -1 && oldDoc.getDocumentTxt().getBytes().length > this.maxBytesCount){
            throw new IllegalArgumentException("Doc is too big");
        }
        else if(oldDoc.getDocumentBinaryData() != null  && this.maxBytesCount != -1 && oldDoc.getDocumentBinaryData().length > this.maxBytesCount){
            throw new IllegalArgumentException("Doc is too big");
        }
        GenericCommand<UriEntry> element = new GenericCommand<>(newUri, url -> {
            this.documents.put(uri, null);
            this.documents.put(uri, oldDoc);

            try {
                putBackInMemory(pushedOut);
                if(!inMemory)
                    removeFromMemory(new UriEntry(oldDoc.getKey()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            for(String word : newDoc.getWords()){
                this.textTrie.delete(word, newUri);
            }
            this.deleteDocsByMetadata(newUri);
            for(String word : oldDoc.getWords()){
                this.textTrie.put(word, oldUri);}
            for(String metadataKey : oldDoc.getMetadata().keySet()){
                this.putMetadata(metadataKey, oldDoc.getMetadataValue(metadataKey), oldUri);
            }
            this.deleteFromHeap(newUri);
            this.minHeap.insert(oldUri);
            if(newDoc.getDocumentTxt() != null){
                this.bytesMemory -= newDoc.getDocumentTxt().getBytes().length;
            }
            else this.bytesMemory -= newDoc.getDocumentBinaryData().length;
            if(oldDoc.getDocumentTxt() != null){
                this.bytesMemory += oldDoc.getDocumentTxt().getBytes().length;
            }
            else this.bytesMemory += oldDoc.getDocumentBinaryData().length;
            try {
                enforceMemoryLimits();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        this.commandStack.push(element);
    }
    private byte[] readBytes(InputStream input) throws IOException {
        byte[] bytes = new byte[0];
        try{
            bytes = input.readAllBytes();
        }
        catch(IOException e){
            throw new IOException("Issue Reading Input");
        }
        finally {
            input.close();
        }
        return bytes;
    }
    private GenericCommand<UriEntry> getCommandForDelete(UriEntry uriEntry, Document doc) {
        if(doc.getDocumentTxt() != null && this.maxBytesCount != -1 && doc.getDocumentTxt().getBytes().length > this.maxBytesCount){
            throw new IllegalArgumentException("Doc is too big");
        }
        else if(doc.getDocumentBinaryData() != null && this.maxBytesCount != -1 && doc.getDocumentBinaryData().length > this.maxBytesCount){
            throw new IllegalArgumentException("Doc is too big");
        }
        GenericCommand<UriEntry> command = new GenericCommand<>(uriEntry, url -> {
            this.uris.add(uriEntry);
            boolean inMemory = this.getMemoryStatus(uriEntry);
            this.documents.put(uriEntry.getURI(), doc);
            if(!inMemory) {
                try {
                    this.documents.moveToDisk(uriEntry.getURI());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            for(String word : doc.getWords()){
                this.textTrie.put(word, uriEntry);
            }
            for(String metadataKey : doc.getMetadata().keySet()){
                this.putMetadata(metadataKey, doc.getMetadataValue(metadataKey), uriEntry);
            }
            this.docMemory++;
            if(doc.getDocumentTxt() != null){
                this.bytesMemory += doc.getDocumentTxt().getBytes().length;
            }
            else this.bytesMemory += doc.getDocumentBinaryData().length;

            try {
                enforceMemoryLimits();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            this.minHeap.insert(uriEntry);
        });
        return command;
    }
    private void deleteFromHeap(UriEntry uri){
        List<UriEntry> tempList = new ArrayList<>();
        boolean found = false;

        while(!found && this.minHeap.peek() != null){
            UriEntry uriEntry = this.minHeap.remove();
            if(uriEntry.equals(uri)){
                found = true;
            }
            else{
                tempList.add(uriEntry);
            }
        }
        for(UriEntry uriEntry : tempList){
            this.minHeap.insert(uriEntry);
        }
    }
    private List<UriEntry> searchPrivate(String keyword) {
        sortByFrequency comparator = new sortByFrequency(keyword);
        return this.textTrie.getSorted(keyword, (Comparator) comparator);
    }
    private List<UriEntry> searchByPrefixPrivate(String keywordPrefix) {
        Pattern startsWith = Pattern.compile(keywordPrefix + "\\*");
        sortByFrequency comparator = new sortByFrequency(startsWith);
        List<UriEntry> uriEntries = this.textTrie.getAllWithPrefixSorted(keywordPrefix, (Comparator) comparator);
        return uriEntries;
    }
    private List<UriEntry> searchByMetadataPrivate(Map<String, String> keysValues) {
        if (keysValues == null || keysValues.keySet().isEmpty()) {
            return new ArrayList<>();
        }
        Set<String> keys = keysValues.keySet();
        String firstKey = keys.iterator().next();
        String firstValue = keysValues.get(firstKey);
        List<UriEntry> uriEntries =  new ArrayList<>(this.getMetadataDocs(firstKey, firstValue));
        for(String key : keys){
            uriEntries.retainAll(this.getMetadataDocs(key, keysValues.get(key)));
        }
        return uriEntries;
    }
    private Set<UriEntry> enforceMemoryLimits() throws IOException{
        Set<UriEntry> uriEntries = new HashSet<>();
        while((this.maxDocCount != -1 && this.docMemory > this.maxDocCount) || (this.maxBytesCount != -1 && this.bytesMemory > this.maxBytesCount)) {
            UriEntry uriToDelete = this.minHeap.peek();

            this.docMemory--;
            Document doc = this.documents.get(uriToDelete.getURI());
            if (doc.getDocumentTxt() != null) {
                this.bytesMemory -= doc.getDocumentTxt().getBytes().length;
            } else this.bytesMemory -= doc.getDocumentBinaryData().length;

            deleteFromHeap(uriToDelete);
            this.changeMemoryStatus(uriToDelete, false);
            this.documents.moveToDisk(uriToDelete.getURI());

            uriEntries.add(uriToDelete);
        }
        return uriEntries;
    }
    private void changeMemoryStatus(UriEntry uri, Boolean inMemory){
        this.uris.get(this.uris.indexOf(uri)).setInMemory(inMemory);
    }
    private boolean getMemoryStatus(UriEntry uri){
        if(this.uris.contains(uri))
            return this.uris.get(this.uris.indexOf(uri)).getInMemory();
        return false;
    }
    private void putBackInMemory(Set<UriEntry> uriEntries) throws IOException {
        for(UriEntry uriEntry : uriEntries){
            this.getPrivate(uriEntry.getURI());
            this.changeMemoryStatus(uriEntry, true);
            this.minHeap.insert(uriEntry);
        }
    }
    private void removeFromMemory(UriEntry uriEntry) throws IOException{
            this.deleteFromHeap(uriEntry);
            this.changeMemoryStatus(uriEntry, false);
            this.documents.moveToDisk(uriEntry.getURI());
    }
    private Document getPrivate(URI url) throws IOException{
        return this.documents.get(url);
    }
    private void removeOldDoc(Document oldDoc){
        if(oldDoc == null)
            return;
        UriEntry oldUri = new UriEntry(oldDoc.getKey());
        deleteDocsByMetadata(oldUri);
        deleteFromHeap(oldUri);

        for(String word : oldDoc.getWords()){
            this.textTrie.delete(word, oldUri);
        }
        docMemory--;
        if (oldDoc.getDocumentTxt() != null) {
            this.bytesMemory -= oldDoc.getDocumentTxt().getBytes().length;
        } else this.bytesMemory -= oldDoc.getDocumentBinaryData().length;
    }
private class sortByFrequency implements Comparator<UriEntry> {
    private String keyword;
    private Pattern prefix;
    private sortByFrequency(String keyword){
        this.keyword = keyword;
        }
    private sortByFrequency(Pattern prefix){
        this.prefix = prefix;
        }
    @Override
    public int compare(UriEntry uri1, UriEntry uri2) {
        if (this.keyword != null) {
            return documents.get(uri2.getURI()).wordCount(this.keyword) - documents.get(uri1.getURI()).wordCount(this.keyword);
        }
        else{
            Predicate<String> predicate = word -> word.startsWith(this.prefix.toString());
            Set<String> wordsWithPrefix = documents.get(uri1.getURI()).getWords().stream()
                    .filter(predicate).collect(Collectors.toSet());
            wordsWithPrefix.addAll(documents.get(uri2.getURI()).getWords().stream()
                    .filter(predicate).collect(Collectors.toSet()));
            int doc1WordCount = 0;
            int doc2WordCount = 0;

            for(String word : wordsWithPrefix){
                doc1WordCount += documents.get(uri1.getURI()).wordCount(word);
                doc2WordCount += documents.get(uri2.getURI()).wordCount(word);
            }
            return doc2WordCount - doc1WordCount;
        }

        }
    }
}