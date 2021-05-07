package com.lyh.mergezip.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@ConfigurationProperties(prefix = "core")
@Component
public class CoreConfig {
    private String rootPathName;
    private String excelReadSheet;
    private String excelFileName;
    private ArrayList<String> rule;
    private ArrayList<String> coreFlag;

    public String getRootPathName() {
        return rootPathName;
    }

    public void setRootPathName(String rootPathName) {
        this.rootPathName = rootPathName;
    }

    public ArrayList<String> getRule() {
        return rule;
    }

    public void setRule(ArrayList<String> rule) {
        this.rule = rule;
    }

    public String getExcelReadSheet() {
        return excelReadSheet;
    }

    public void setExcelReadSheet(String excelReadSheet) {
        this.excelReadSheet = excelReadSheet;
    }

    public ArrayList<String> getCoreFlag() {
        return coreFlag;
    }

    public void setCoreFlag(ArrayList<String> coreFlag) {
        this.coreFlag = coreFlag;
    }

    public String getExcelFileName() {
        return excelFileName;
    }

    public void setExcelFileName(String excelFileName) {
        this.excelFileName = excelFileName;
    }
}

