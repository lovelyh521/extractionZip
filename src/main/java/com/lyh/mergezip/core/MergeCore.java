package com.lyh.mergezip.core;

import com.lyh.mergezip.common.compress.ExtractionUtil;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashSet;


public class MergeCore {
    private ArrayList<String> lastFileName;
    private String corePath;
    private HashSet<String> filter = new HashSet<>();
    public MergeCore(ArrayList<String> lastFileName,String corePath,HashSet<String> filter) {
        this.corePath = corePath;
        this.lastFileName = lastFileName;
        this.filter = filter;
    }

    public void coreZip(File file, String rootpath,String subFileName) throws Exception {
        ExtractionUtil unCompressUtil = new ExtractionUtil();
        File[] files = file.listFiles();
        for (File file1 : files) {
            String name = file1.getName();
            for (String s : lastFileName) {
                if(name.indexOf(s)>=0 || file.getName().indexOf(s)>=0){
                    if(!CollectionUtils.isEmpty(filter)){
                        for (String s1 : filter) {
                            if(name.indexOf(s1)>-1){
                                exe(rootpath, subFileName, unCompressUtil, file1, name);
                            }
                        }
                    }else {
                        exe(rootpath, subFileName, unCompressUtil, file1, name);
                    }
                }
            }
        }
        unCompressUtil.clean();
    }

    private void exe(String rootpath, String subFileName, ExtractionUtil unCompressUtil, File file1, String name) throws Exception {
        if (!file1.isDirectory()) {
            System.out.println("解压：" + name);
            String outPath = rootpath + File.separator + "endFile" + File.separator + corePath + "_" + subFileName+File.separator+name.substring(0,name.indexOf("."));
            if (name.toLowerCase().endsWith(".zip")) {
                unCompressUtil.extractionZip(new FileInputStream(file1), outPath);
            }else if(name.toLowerCase().endsWith(".tar.gz")){
                unCompressUtil.extractionGz(new FileInputStream(file1), outPath);
            }
        }else {
            coreZip(file1,rootpath,subFileName);
        }
    }
}
