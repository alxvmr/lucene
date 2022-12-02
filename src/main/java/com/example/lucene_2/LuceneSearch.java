package com.example.lucene_2;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.index.DirectoryReader;

import org.apache.lucene.search.spell.SpellChecker;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.Scanner;

import org.apache.commons.lang3.*;

public class LuceneSearch {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static void CreateIndex(String indexPath, String filePath) throws IOException {
        String[] fields = new String[]{"title", "date", "user", "text", "tags", "hubs"};
        Directory directory = FSDirectory.open(Paths.get(indexPath));
        InMemoryLuceneIndex index = new InMemoryLuceneIndex(directory, new StandardAnalyzer());

        try {
            Reader in = new FileReader(filePath);
            CSVParser parser = new CSVParser( in, CSVFormat.DEFAULT );
            List<CSVRecord> list = parser.getRecords();

            for(CSVRecord row : list) {
                Map<String, String> dict = new HashMap<String, String>();
                for (int i = 0; i  < row.size(); i++) {
                    dict.put(fields[i], row.get(i));
                }
                index.indexDocument(dict.get("title"),
                        dict.get("date"),
                        dict.get("user"),
                        dict.get("text"),
                        dict.get("tags"),
                        dict.get("hubs"));
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String WordCheck (String s) throws IOException {
        Directory indexSpellDir = FSDirectory.open(Paths.get("resources/spellcheker"));
        SpellChecker spellChecker = new SpellChecker(indexSpellDir);
        String res = s;

        if (! spellChecker.exist(s)){ // если в словаре нет слова из запроса, то не исправляем
            int suggestionsNumber = 1;
            String[] suggestions = spellChecker.suggestSimilar(s, suggestionsNumber);

            if (suggestions!=null && suggestions.length > 0) {
                res = suggestions[0];
            }
        }

        return res;
    }

    public static String QueryCheck (String q) throws IOException {
        String[] subStr = q.split(" ");
        StringBuilder res = new StringBuilder();
        for (String s: subStr){
            res.append(WordCheck(s)).append(" ");
        }

        return res.toString();
    }
    public static void main(String[] args) throws Exception {
/*
* ИНДЕКСИРОВАНИЕ СОБРАННЫХ ФАЙЛОВ
* */
        //String indexDir = "resources/index";
        //String textDir = "resources/res.csv";
        //CreateIndex(indexDir, textDir);  // 5186 текстов

/*
* ИНДЕКСИРОВАНИЕ СЛОВАРЯ С РУСССКИМИ СЛОВАМИ
* */
        //StandardAnalyzer analyzer = new StandardAnalyzer();
        //IndexWriterConfig config = new IndexWriterConfig(analyzer);
        //InputStreamReader isr = new InputStreamReader(new FileInputStream(new File("resources/russian.txt")), "windows-1251");
        //PlainTextDictionary dictionary = new PlainTextDictionary(isr);
        //spellChecker.indexDictionary(dictionary, config, true);

/*
* ПРОВЕРКА ЗАПРОСА НА ОПЕЧАТКУ
* */
        Scanner in = new Scanner(System.in);
        System.out.print("Введите запрос: ");
        String q = in.nextLine();
        q = QueryCheck(q);
        //System.out.println(q);
/*
* ПОИК
* */

        String indexPath = "resources/index";
        String line = StringUtils.repeat(ANSI_PURPLE + "*" + ANSI_RESET,300);
        List<Double> bm25_scores = new ArrayList<Double>();

        Directory indexDir = FSDirectory.open(Paths.get(indexPath));
        IndexReader reader = DirectoryReader.open(indexDir);
        IndexSearcher searcher = new IndexSearcher(reader);
        searcher.setSimilarity(new BM25Similarity());
        //searcher.setSimilarity(new ClassicSimilarity());
        QueryParser query = new QueryParser("body", new StandardAnalyzer());
        TopDocs docs = searcher.search(query.parse(q), 5);

        int i = 0;
        for (final ScoreDoc scoreDoc : docs.scoreDocs) {
            double bm25 = scoreDoc.score;
            int docId = scoreDoc.doc;
            bm25_scores.add(bm25);

            Document d = reader.document(docId);

            System.out.println(line);
            System.out.printf(ANSI_PURPLE + "Документ № %d \nBM25 = %.2f\ndocId = %d\n" + ANSI_RESET, i + 1, bm25, docId);
            System.out.println(line);
            System.out.println(d);

            i += 1;
        }

        HashMap<String, Double> ndcg_score = NDCG.getNDCG(bm25_scores);

        System.out.println(line);
        System.out.printf(ANSI_PURPLE + "DCG = %.3f\n" + ANSI_RESET, ndcg_score.get("dcg"));
        System.out.println(ANSI_PURPLE + "NDCG = " + ndcg_score.get("ndcg") + ANSI_RESET);


//        ArrayList<Double> grades = new ArrayList<Double>();
//        // добавим в список ряд элементов
//        grades.add(3.0);
//        grades.add(1.0);
//        grades.add(3.0);
//        grades.add(1.0);
//        grades.add(1.0);
//        HashMap<String, Double> ndcg_score = NDCG.getNDCG(grades);
//
//        System.out.println(ANSI_PURPLE + "NDCG = " + ndcg_score.get("ndcg") + ANSI_RESET);

    }
}