package com.jiaruiblog.service;

import com.jiaruiblog.entity.AskOptional;
import com.jiaruiblog.util.AskGpt;
import com.jiaruiblog.util.CorrectContent;
import org.junit.Test;

public class testGpt {
    @Test
    public void testAsk(){
        AskGpt askGpt = new AskGpt();
//        System.out.println(askGpt.ask(AskOptional.SUMMARIZE,"中南药学 Central South Pharmacy ISSN 1672-2981,CN 43-1408/R 《中南药学》网络首发论文 题目： 非小细胞肺癌靶向治疗固定剂量持续给药模式的利与弊 作者： 马进安，吴元强，马弋然，颜苗 网络首发日期： 2023-03-14 引用格式： 马进安，吴元强，马弋然，颜苗．非小细胞肺癌靶向治疗固定剂量持续给药模式的利与弊[J/OL]．中南药学. https://kns.cnki.net/kcms/detail/43.1408.r.20230310.0930.002.html 网络首发：在编辑部工作流程中，稿件从录用到出版要经历录用定稿、排版定稿、整期汇编定稿等阶段。录用定稿指内容已经确定，且通过同行评议、主编终审同意刊用的稿件。排版定稿指录用定稿按照期刊特定版式（包括网络呈现版式）排版后的稿件，可暂不确定出版年、卷、期和页码。整期汇编定稿指出版年、卷、期、页码均已确定的印刷或数字出版的整期汇编稿件。录用定稿网络首发稿件内容必须符合《出版管理条例》和《期刊出版管理规定》的有关规定；学术研究成果具有创新性、科学性和先进性，符合编辑部对刊文的录用要求，不存在学术不端行为及其他侵权行为；稿件内容应基本符合国家有关书刊编辑、出版的技术标准，正确使用和统一规范语言文字、符号、数字、外文字母、法定计量单位及地图标注等。为确保录用定稿网络首发的严肃性，录用定稿一经发布，不得修改论文题目、作者、机构名称和学术内容，只可基于编辑规范进行少量文字的修改。 出版确认：纸质期刊编辑部通过与《中国学术期刊（光盘版）》电子杂志社有限公司签约，在《中国学术期刊（网络版）》出版传播平台上创办与纸质期刊内容一致的网络版，以单篇或整期出版形式，在印刷出版之前刊发论文的录用定稿、排版定稿、整期汇编定稿。因为《中国学术期刊（网络版）》是国家新闻出版广电总局批准的网络连续型出版物（ISSN 2096-4188，CN 11-6037/Z），所以签约期刊的网络版上网络首发论文视为正式出版"));;
//        System.out.println(askGpt.ask(AskOptional.ASK,"什么是白血病？"));
        System.out.println(askGpt.ask(AskOptional.CORRECT,"挠魔瘤临床管理"));
//        挠魔瘤的独立预后因素

    }
//
}
