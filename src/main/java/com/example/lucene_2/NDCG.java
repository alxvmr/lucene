package com.example.lucene_2;
import java.util.*;

public class NDCG {
    private static double log2 (int x) {
        return Math.log(x) / Math.log(2);
    }

    private static double calc_dcg (List<Double> grades){
        double res = 0;
        for (int  i = 0; i < grades.size(); i++){
            res += grades.get(i) / log2(i + 2);
        }
        return res;
    }

    public static HashMap<String, Double> getNDCG (List<Double> grades){
        double dcg = calc_dcg(grades);
        Collections.sort(grades, Collections.reverseOrder()); //отсортировали оценки по убыванию
        double maxdcg = calc_dcg(grades);
        HashMap<String, Double> res = new HashMap<String, Double>();{
            res.put("dcg", dcg);
            res.put("ndcg", dcg / maxdcg);
        }
        return res;
    }
}