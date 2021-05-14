package com.lyh.mergezip.excel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReadCoreExcel {
    private int sheetIndex;
    private ArrayList<String> coreFlag= null;
    private Map<String, String> map = new ConcurrentHashMap<>();
    private Map<String, String> dbmap = new ConcurrentHashMap<>();
    public ReadCoreExcel(int sheetIndex,ArrayList<String> coreFlag) {
        this.sheetIndex = sheetIndex -1;
        this.coreFlag = coreFlag;
    }

    private  final String EXCEL_XLS = "xls";
    private  final String EXCEL_XLSX = "xlsx";

    private Workbook getWorkbok(InputStream in, File file) throws IOException {
        Workbook wb = null;
        if(file.getName().endsWith(EXCEL_XLS)){  //Excel 2003
            wb = new HSSFWorkbook(in);
        }else if(file.getName().endsWith(EXCEL_XLSX)){  // Excel 2007/2010
            wb = new XSSFWorkbook(in);
        }
        return wb;
    }

    /**
     * 判断文件是否是excel
     * @throws Exception
     */
    private boolean checkExcelVaild(File file){
        if(!file.exists()){
            return false;
        }
        if(!(file.isFile() && (file.getName().endsWith(EXCEL_XLS) || file.getName().endsWith(EXCEL_XLSX)))){
           return false;
        }
        return true;
    }


    public ListBean getLastFileName(String path) throws Exception {

        File file = new File(path);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File file1 : files) {
                if (this.checkExcelVaild(file1)) {
                    return read(file);
                }
            }
        }else {
            if (this.checkExcelVaild(file)) {
                return read(file);
            }

        }
        return null;
    }

    public ListBean read(File file) throws Exception {
        FileInputStream in = new FileInputStream(file); // 文件流
        ArrayList<String> fileNames = new ArrayList<>();
        ArrayList<String> fileNamesdb = new ArrayList<>();
        Workbook workbook = getWorkbok(in,file);
        Sheet sheets = workbook.getSheetAt(sheetIndex);
        for (Row row : sheets) {
            String cell0 = row.getCell(0).getStringCellValue();
            String cell1 = row.getCell(1).getStringCellValue();
            String cell4 = row.getCell(4) == null ? "":row.getCell(4).getStringCellValue();
            for (String s : coreFlag) {
                if(s.equals(cell1)){
                    map.put(cell1,cell0);
                    if(StringUtils.hasLength(cell4)){
                        dbmap.put(cell1 + "dbFile",cell0);
                    }
                }
            }
        }

        map.forEach((k,v)->{
            System.out.println("应用"+k+":"+v);
            fileNames.add(v);
        });
        dbmap.forEach((k,v)->{
            System.out.println("数据库"+k+":"+v);
            fileNamesdb.add(v);
        });
        map = new HashMap<>();
        dbmap = new ConcurrentHashMap<>();
        return new ListBean(fileNames,fileNamesdb);
    }

    public class ListBean{
        private ArrayList<String> fileNames;
        private ArrayList<String> fileNamesdb;

        public ListBean(ArrayList<String> fileNames, ArrayList<String> fileNamesdb) {
            this.fileNames = fileNames;
            this.fileNamesdb = fileNamesdb;
        }

        public ArrayList<String> getFileNames() {
            return fileNames;
        }

        public void setFileNames(ArrayList<String> fileNames) {
            this.fileNames = fileNames;
        }

        public ArrayList<String> getFileNamesdb() {
            return fileNamesdb;
        }

        public void setFileNamesdb(ArrayList<String> fileNamesdb) {
            this.fileNamesdb = fileNamesdb;
        }
    }

}
