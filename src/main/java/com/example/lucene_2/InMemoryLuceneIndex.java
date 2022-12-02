package com.example.lucene_2;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.BytesRef;

//import org.apache.maven.jxr.DirectoryIndexer;

import java.io.IOException;


public class InMemoryLuceneIndex {
    private Directory directory;
    private StandardAnalyzer analyzer;

    public InMemoryLuceneIndex(Directory directory, StandardAnalyzer analyzer){
        this.directory = directory;
        this.analyzer = analyzer;
    }

    public void indexDocument(String title, String dat, String user, String body, String tags, String hubs){
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        try {
            IndexWriter writer = new IndexWriter(directory, config);
            Document document = new Document();

            document.add(new SortedDocValuesField("title", new BytesRef(title)));
            document.add(new StoredField("title", title));
            document.add(new TextField("date", dat, Field.Store.YES));
            document.add(new TextField("user", user, Field.Store.YES));
            document.add(new TextField("body", body, Field.Store.YES));
            document.add(new TextField("tags", tags, Field.Store.YES));
            document.add(new TextField("hubs", hubs, Field.Store.YES));

            writer.addDocument(document);
            writer.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

//    public String highlight(Highlighter highlighter, Document doc){
//        String filePath = Paths.get(doc.get(DirectoryIndexer.FILENAME));
//        if(!Files.exists(filePath)){
//            return "Unable to find this file";
//        }
//        try{
//        String documentText = Files.readString(filePath);
//        }
//        catch(MalformedInputException e) {
//            return "Unable to read this file";
//        }
//        return highlighter.highlight(documentText);
//    }
}
