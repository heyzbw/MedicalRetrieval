package com.jiaruiblog.util;

import com.jiaruiblog.entity.ocrResult.EsSearch;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;

import java.util.*;
import java.util.stream.DoubleStream;

//对得分进行分位数归一化（Quantile normalization）
public class QuantileNormalization {

    public static void quantileNormalize(List<EsSearch> documents, String attribute, double newMin, double newMax, boolean applyLogTransform) {
        double[] values = documents.stream()
                .mapToDouble(doc -> "like".equals(attribute) ? doc.getLike_num() : doc.getClick_num())
                .toArray();
        if (applyLogTransform) {
            for (int i = 0; i < values.length; i++) {
                values[i] = Math.log1p(values[i]);
            }
        }
        double[] sortedValues = Arrays.copyOf(values, values.length);
        Arrays.sort(sortedValues);

        Percentile percentile = new Percentile();
        percentile.setData(sortedValues);
        Map<Double, Double> quantiles = new HashMap<>();
        double range = newMax - newMin;

        for (double value : sortedValues) {
            double quantile = percentile.evaluate(value + 1e-9) / 100.0;
            double newValue = newMin + range * quantile;
            quantiles.put(value, newValue);
        }

        for (EsSearch doc : documents) {
            if (applyLogTransform){
                if ("like".equals(attribute)) {
                    doc.setLikeScore(quantiles.get(Math.log1p((double) doc.getLike_num())));
                    System.out.println("likeScore");
                } else {
                    doc.setClickScore(quantiles.get(Math.log1p((double) doc.getClick_num())));
                }
            }
            else {
                if ("like".equals(attribute)) {
                    doc.setLikeScore(quantiles.get((double) doc.getLike_num()));
                } else {
                    doc.setLikeScore(quantiles.get((double) doc.getClick_num()));
                }
            }
        }
    }

    public static void linearNormalize(List<EsSearch> documents,String attribute, double newMin, double newMax, boolean applyLogTransform){
        double[] values = documents.stream()
                .mapToDouble(doc -> "like".equals(attribute) ? doc.getLike_num() : doc.getClick_num())
                .toArray();

    // 获取最大值和最小值
        DoubleSummaryStatistics stats = DoubleStream.of(values).summaryStatistics();
        double minValue = stats.getMin();
        double maxValue = stats.getMax();

        double[] normalizedValues = new double[values.length];

        if(minValue == maxValue){
            Arrays.fill(normalizedValues, 1);
        }
        else {
            // 归一化到0-1之间
            normalizedValues = DoubleStream.of(values)
                    .map(value -> (value - minValue) / (maxValue - minValue))
                    .toArray();
        }

        for(int i=0;i<documents.size();i++)
        {
            if("like".equals(attribute)){
                documents.get(i).setLikeScore(normalizedValues[i] * newMax);
            }
            else {
                documents.get(i).setClickScore(normalizedValues[i] * newMax);
            }
        }
    }
}