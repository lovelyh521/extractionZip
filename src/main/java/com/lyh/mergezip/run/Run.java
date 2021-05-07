package com.lyh.mergezip.run;

import com.lyh.mergezip.common.compress.ExtractionUtil;
import com.lyh.mergezip.config.CoreConfig;
import com.lyh.mergezip.excel.ReadCoreExcel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

@Component
public class Run implements CommandLineRunner {
    String source = "source";
    ArrayList<String> lastFileName = null;
    @Autowired
    CoreConfig coreConfig;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("请选择执行项：");
        System.out.println("1：解压核心");
        System.out.println("2：解压合并柜面");
        Scanner input=new Scanner(System.in);
        //接受String类型
        String str=input.next();
        switch (str){
            case "1":
                String rootpath = System.getProperty("user.dir");
                System.out.println("rootpath = " + rootpath);
                File file = new File(rootpath + File.separator + source);
                System.out.println("--------------------start----------------------");
                System.out.println("--------------开始读取excel-------------");
                int excelReadSheet = Integer.valueOf(coreConfig.getExcelReadSheet()) ;
                ArrayList<String> coreFlag = coreConfig.getCoreFlag();
                String excelFileName = coreConfig.getExcelFileName();
                ReadCoreExcel readCoreExcel = new ReadCoreExcel(excelReadSheet,coreFlag);
                lastFileName = readCoreExcel.getLastFileName(rootpath + File.separator + excelFileName);
                System.out.println("--------------完成读取excel-------------");

                coreZip(file,rootpath);
                System.out.println("-------------------finish---------------------");
                break;
            case "2":
                System.out.println("还没写");
                break;
        }
    }

    private void coreZip(File file, String rootpath) throws Exception {
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
                    System.out.println("s = " + s);
                    if (!file1.isDirectory()) {
                        System.out.println("name = " + name);
                        String outPath = rootpath + File.separator + "endFile" + File.separator + coreConfig.getRootPathName();
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