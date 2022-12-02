module com.example.lucene_2 {
    requires javafx.controls;
    requires javafx.fxml;

    requires static lucene.core;
    requires static lucene.queryparser;
    requires static lucene.highlighter;
    //requires static maven.jxr;
    requires commons.csv;
    requires org.apache.commons.lang3;
    requires org.apache.lucene.benchmark;
    requires lucene.suggest;

    opens com.example.lucene_2 to javafx.fxml;
    exports com.example.lucene_2;
}