package com.lyh.mergezip.run;

import com.lyh.mergezip.common.compress.ExtractionUtil;
import com.lyh.mergezip.config.CoreConfig;
import com.lyh.mergezip.core.MergeCore;
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
        String rootpath = System.getProperty("user.dir");
        System.out.println("当前目录：" + rootpath);
        System.out.println("请选择执行项：");
        System.out.println("1：解压核心");
        System.out.println("2：解压合并柜面");
        Scanner input=new Scanner(System.in);
        //接受String类型
        String str=input.next();
        switch (str){
            case "1":
                File file = new File(rootpath + File.separator + source);
                System.out.println("--------------开始读取excel-------------");
                int excelReadSheet = Integer.valueOf(coreConfig.getExcelReadSheet()) ;
                ArrayList<String> coreFlag = coreConfig.getCoreFlag();
                String excelFileName = coreConfig.getExcelFileName();
                ReadCoreExcel readCoreExcel = new ReadCoreExcel(excelReadSheet,coreFlag);
                lastFileName = readCoreExcel.getLastFileName(rootpath + File.separator + excelFileName);
                System.out.println("--------------完成读取excel-------------");
                System.out.println("--------------------开始解压----------------------");
                MergeCore mergeCore = new MergeCore(lastFileName,coreConfig.getRootPathName());
                mergeCore.coreZip(file,rootpath);
                System.out.println("-------------------解压完成---------------------");
                break;
            case "2":
                System.out.println("暂时不能用");
                break;
        }
    }




}