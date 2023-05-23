package com.jiaruiblog.entity;

import lombok.Data;

import java.util.List;

@Data
public class PredictCaseOutcome {
    private List<String> disease; // 疾病
    private List<String> body; // 身体
    private List<String> symptom; // 症状
    private List<String> medicalProcedure; // 医疗程序
    private List<String> medicalEquipment; // 医疗设备
    private List<String> medicine; // 药物
    private List<String> department; // 科室
    private List<String> microorganism; // 微生物类
    private List<String> medicalExamination; // 医学检验项目
    private String diagnosisDiseaseTypes;
    //    病例的内容
    private String diagnosis;

    public String toString(){
//        我需要将所有的内容转化为一个string，并添加足够的注释

        String result = "";

//        病例的内容
        result += "病例内容：";
        result += diagnosis;

        if (disease != null) {
            result += "疾病";
            for (String s : disease) {
                result += s + " ";
            }
        }
        if (body != null) {
            result += "身体";
            for (String s : body) {
                result += s + " ";
            }
        }
        if (symptom != null) {
            result += "症状";
            for (String s : symptom) {
                result += s + " ";
            }
        }
        if (medicalProcedure != null) {
            result += "医疗程序";
            for (String s : medicalProcedure) {
                result += s + " ";
            }
        }
        if (medicalEquipment != null) {
            result += "医疗设备";
            for (String s : medicalEquipment) {
                result += s + " ";
            }
        }
        if (medicine != null) {
            result += "药物";
            for (String s : medicine) {
                result += s + " ";
            }
        }
        if (department != null) {
            result += "科室";
            for (String s : department) {
                result += s + " ";
            }
        }
        if (microorganism != null) {
            result += "微生物类";
            for (String s : microorganism) {
                result += s + " ";
            }
        }
        if (medicalExamination != null) {
            result += "医学检验项目";
            for (String s : medicalExamination) {
                result += s + " ";
            }
        }
        return result;
    }
    // getters and setters
}

