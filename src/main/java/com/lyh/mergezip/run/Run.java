package com.lyh.mergezip.run;

import com.lyh.mergezip.common.compress.ExtractionUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Component
public class Run implements CommandLineRunner {
    String source = "source";

    @Override
    public void run(String... args) throws Exception {
        for (String arg : args) {
            System.out.println("arg = " + arg);
        }
        String rootpath = System.getProperty("user.dir");
        System.out.println("rootpath = " + rootpath);
        File file = new File(rootpath + File.separator + source);
        System.out.println("--------------------start----------------------");
        extracted(file,rootpath);
        System.out.println("-------------------finish---------------------");
    }

    private void extracted(File file,String rootpath) throws IOException {
        ExtractionUtil unCompressUtil = new ExtractionUtil();
        File[] files = file.listFiles();
        for (File file1 : files) {
            String name = file1.getName();
            if (!file1.isDirectory()) {
                System.out.println("name = " + name);
                if (name.toLowerCase().endsWith(".zip")) {
                    unCompressUtil.extractionZip(new FileInputStream(file1), rootpath + File.separator + "endFile");
                }else if(name.toLowerCase().endsWith(".tar.gz")){
                    unCompressUtil.extractionGz(new FileInputStream(file1),rootpath+File.separator+"endFile");
                }
            }else {
                extracted(file1,rootpath);
            }
        }
    }


}