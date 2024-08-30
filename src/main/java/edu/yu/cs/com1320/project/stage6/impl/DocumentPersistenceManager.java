package edu.yu.cs.com1320.project.stage6.impl;

import com.google.gson.*;
import jakarta.xml.bind.DatatypeConverter;
import edu.yu.cs.com1320.project.stage6.Document;
import edu.yu.cs.com1320.project.stage6.PersistenceManager;
import java.io.*;
import java.lang.reflect.Type;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class DocumentPersistenceManager implements PersistenceManager<URI, Document> {
    private File baseDir;
    public DocumentPersistenceManager(File baseDir){
        if (baseDir == null){
            this.baseDir = new File(System.getProperty("user.dir"));
        }
        else{
            this.baseDir = new File(String.valueOf(baseDir));
            this.baseDir.mkdir();
        }
    }
    @Override
    public void serialize(URI key, Document val) throws IOException {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(DocumentImpl.class, new DocumentSerializer());
        Gson gson = builder.create();

        String str = key.toString();
        if (str.startsWith("http://")) {
            str = str.replaceFirst("http://", "");
        }
        str = str.concat(".json");
        String directories = null;
        if(str.lastIndexOf("/") != -1){
            directories = this.baseDir.toString() + "/" + str.substring(0, str.lastIndexOf("/"));
        }
        else directories = this.baseDir.toString();
        File fullFile = new File(this.baseDir, str);

        Path path = Paths.get(directories);
        Files.createDirectories(path);
        try {
            FileWriter file = new FileWriter(fullFile);
            file.write(gson.toJson(val));
            file.close();
        }catch (IOException e){
            throw new IOException();
        }
    }

    @Override
    public Document deserialize(URI key) throws IOException {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(DocumentImpl.class, new DocumentDeserializer());
        Gson gson = builder.create();
        String str = key.toString();
        if (str.startsWith("http://")) {
            str = str.replaceFirst("http://", "");
        }
        str = str.concat(".json");
        Document doc = null;
        File myObj = new File(this.baseDir, str);
        FileReader reader = new FileReader(myObj);
        try {
            BufferedReader myReader = new BufferedReader(reader);
            doc = gson.fromJson(myReader, DocumentImpl.class);
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
        }
        this.delete(key);

        return doc;
    }

    @Override
    public boolean delete(URI key) throws IOException {
        String str = key.toString();
        if (str.startsWith("http://")) {
            str = str.replaceFirst("http://", "");
        }
        str = str.concat(".json");
        File myObj = new File(this.baseDir, str);
        String directories = myObj.toString();
        Boolean deleted;
        try{
            Files.delete(myObj.toPath());
            deleted = true;
        } catch(NoSuchFileException x){
            deleted =  false;
        }
        while(directories.contains("\\")){
            directories = directories.substring(0, directories.lastIndexOf("\\"));
            File dir = new File(directories);
            if(dir.list() == null || dir.list().length == 0)
                dir.delete();
        }
        return deleted;
    }
    private class DocumentSerializer implements JsonSerializer<Document>{
        @Override
        public JsonElement serialize(Document document, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject jSonDoc = new JsonObject();
            jSonDoc.add("uri", new JsonPrimitive(document.getKey().toString()));
            if(document.getDocumentTxt() != null){
                jSonDoc.add("text", new JsonPrimitive(document.getDocumentTxt()));
            }
            else {
                String base64Encoded = DatatypeConverter.printBase64Binary(document.getDocumentBinaryData());
                jSonDoc.add("binaryData", new JsonPrimitive(base64Encoded));
            }
            JsonArray metaData = new JsonArray();
            for(String metadataKey : document.getMetadata().keySet()){
                JsonObject meta = new JsonObject();
                meta.add(metadataKey, new JsonPrimitive(document.getMetadataValue(metadataKey)));
                metaData.add(meta);
            }
            jSonDoc.add("metaData", metaData);

            JsonArray wordCount = new JsonArray();
            for(String word : document.getWords()){
                JsonObject thisWord = new JsonObject();
                thisWord.add(word, new JsonPrimitive(document.wordCount(word)));
                wordCount.add(thisWord);
            }
            jSonDoc.add("wordCounts", wordCount);

            return jSonDoc;

        }
    }
    private class DocumentDeserializer implements JsonDeserializer<Document> {

        @Override
        public Document deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jSonDoc = jsonElement.getAsJsonObject();

            URI uri = URI.create(jSonDoc.get("uri").getAsString());

            Map<String, String> metaData = new HashMap();
            if(jSonDoc.get("metadata") != null){
                for (JsonElement keyValuePair : jSonDoc.get("metadata").getAsJsonArray()) {
                    for (String key : keyValuePair.getAsJsonObject().keySet()) {
                        metaData.put(key, keyValuePair.getAsJsonObject().get(key).getAsString());
                    }
                }
            }

            Map<String, Integer> wordCounts = new HashMap<>();
            if(jSonDoc.get("wordCounts") != null){
                for (JsonElement keyValuePair : jSonDoc.get("wordCounts").getAsJsonArray()) {
                    for (String key : keyValuePair.getAsJsonObject().keySet()) {
                        wordCounts.put(key, keyValuePair.getAsJsonObject().get(key).getAsInt());
                    }
                }
            }

            if(wordCounts.isEmpty()){
                byte[] bytes = DatatypeConverter.parseBase64Binary(jSonDoc.get("binaryData").getAsString());
                DocumentImpl doc = new DocumentImpl(uri, bytes);
                doc.setMetadata((HashMap<String, String>) metaData);
                return doc;
            }
            else{
                String text = jSonDoc.get("text").getAsString();
                DocumentImpl doc = new DocumentImpl(uri, text, wordCounts);
                doc.setMetadata((HashMap<String, String>) metaData);
                return doc;
            }
        }
    }

}
