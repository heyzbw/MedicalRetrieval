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
    // getters and setters
}

