package edu.yu.cs.com1320.project.stage6.impl;

import edu.yu.cs.com1320.project.stage6.Document;
import java.net.URI;
import java.util.*;

public class DocumentImpl implements Document{
    private URI uri;
    private String text;
    private byte[] binaryData;
    private long timeLastUsed;
    private Map<String,String> metadata;
    private Map<String, Integer> wordCounts;

    public DocumentImpl(URI uri, String txt, Map<String, Integer> wordCountMap){
        if(uri == null || uri.toString().isBlank() || txt == null || txt.isBlank() ){
            throw new IllegalArgumentException("URI or Data is null or blank");
        }
        this.uri = uri;
        this.text = txt;
        this.binaryData = null;
        this.timeLastUsed = System.nanoTime();
        this.metadata = new HashMap<>();

        if(wordCountMap == null){
            this.wordCounts = new HashMap<>();

            String[] words = txt.split(" ");
            List<String> wordList = new ArrayList<>();
            for(String word : words){
                word = word.replaceAll("[^0-9A-Za-z]", "").strip();
                wordList.add(word);
            }
            for(String word : wordList){
                word = word.replaceAll("[^0-9A-Za-z]", "").strip();
                this.wordCounts.put(word, Collections.frequency(wordList, word));
            }
        }
        else this.wordCounts = wordCountMap;

    }
    public DocumentImpl(URI uri, byte[] binaryData){
        if(uri == null || uri.toString().isBlank() || binaryData == null || binaryData.length == 0 ){
            throw new IllegalArgumentException("URI or Data is null or blank");
        }
        this.uri = uri;
        this.text = null;
        this.binaryData = binaryData;
        this.metadata = new HashMap<>();
        this.wordCounts = new HashMap<>();
    }
    @Override
    public int hashCode(){
        int result = this.uri.hashCode();
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(binaryData);
        return Math.abs(result);
    }
    @Override
    public boolean equals(Object o){
        if(o == this){
            return true;
        }
        if(!(o instanceof DocumentImpl)){
            return false;
        }
        DocumentImpl d = (DocumentImpl) o;
        return this.hashCode() == d.hashCode();
    }
    @Override
    public String setMetadataValue(String key, String value){
        if(key == null || key.isBlank()){
            throw new IllegalArgumentException("key is null or blank");
        }
        String oldValue = null;
        if(this.metadata.containsKey(key)){
            oldValue = this.metadata.get(key);
        }
        this.metadata.put(key,value);
        if(oldValue != null){
            return oldValue;
        }
        else return null;
    }
    @Override
    public String getMetadataValue(String key){
        if(key == null || key.isBlank()){
            throw new IllegalArgumentException();
        }
        if(!this.metadata.containsKey(key)){
            return null;
        }
        return this.metadata.get(key);
    }
    @Override
    public HashMap<String, String> getMetadata(){
        HashMap<String, String> copy = new HashMap<>();
        for(String key : this.metadata.keySet()){
            copy.put(key, this.metadata.get(key));
        }
        return copy;
    }

    @Override
    public void setMetadata(HashMap<String, String> metadata) {
        this.metadata = metadata;
    }

    @Override
    public String getDocumentTxt(){
        return this.text;
    }
    @Override
    public byte[] getDocumentBinaryData(){
        return this.binaryData;
    }
    @Override
    public URI getKey(){
        return this.uri;
    }

    @Override
    public int wordCount(String word) {
        if(this.wordCounts.get(word) == null) return 0;

        return this.wordCounts.get(word);
    }

    @Override
    public Set<String> getWords() {
        return this.wordCounts.keySet();
    }

    @Override
    public long getLastUseTime() {
        return this.timeLastUsed;
    }

    @Override
    public void setLastUseTime(long timeInNanoseconds) {
        this.timeLastUsed = timeInNanoseconds;
    }

    @Override
    public HashMap<String, Integer> getWordMap() {
        return (HashMap<String, Integer>) this.wordCounts;
    }

    @Override
    public void setWordMap(HashMap<String, Integer> wordMap) {
        this.wordCounts = wordMap;
    }

    @Override
    public int compareTo(Document doc) {
        return (int) (this.getLastUseTime() - doc.getLastUseTime());
    }
}