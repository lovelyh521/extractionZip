package com.lyh.mergezip.core;

import com.lyh.mergezip.common.compress.ExtractionUtil;
import com.lyh.mergezip.config.CoreConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

public class MergeCore {
    private ArrayList<String> lastFileName;
    private String corePath;
    public MergeCore(ArrayList<String> lastFileName,String corePath) {
        this.corePath = corePath;
        this.lastFileName = lastFileName;
    }

    public void coreZip(File file, String rootpath) throws Exception {
        ExtractionUtil unCompressUtil = new ExtractionUtil();
        File[] files = file.listFiles();
        /*if(files.length==0){
            System.out.println("-------------请将要处理的文件放在当前目录下的source文件夹内--------------");
            return;
        }*/

        for (File file1 : files) {
            String name = file1.getName();
            for (String s : lastFileName) {
                if(name.indexOf(s)>=0 || file.getName().indexOf(s)>=0){
                    if (!file1.isDirectory()) {
                        System.out.println("解压：" + name);
                        String outPath = rootpath + File.separator + "endFile" + File.separator + corePath;
                        if (name.toLowerCase().endsWith(".zip")) {
                            unCompressUtil.extractionZip(new FileInputStream(file1), outPath);
                        }else if(name.toLowerCase().endsWith(".tar.gz")){
                            unCompressUtil.extractionGz(new FileInputStream(file1), outPath);
                        }
                    }else {
                        coreZip(file1,rootpath);
                    }
                }
            }
        }
    }
}
