package fricke.util;

import fricke.model.BasketOfList;
import fricke.service.Service;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class WorkBookClass {
    private List<String> list;

    public WorkBookClass() {
        initList();
    }

    public void createXLSXFile(String filename) {
        FilePath.ifExistOverwrite(new File(getPathFile(filename)));
        try (FileOutputStream outputStream = new FileOutputStream(getPathFile(filename));
             Workbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = (XSSFSheet) workbook.createSheet("Table");
            XSSFRow row = sheet.createRow(0);
            createCellAndHeaderWrite(row);
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeXLSXFile(String filename, BasketOfList basketOfList) {
        try (FileInputStream inputStream = new FileInputStream(getPathFile(filename));
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);
            XSSFRow row;
            for (int i = 1; i <= basketOfList.getArticles().size(); i++) {
                row = sheet.createRow(i);
                writeCellValue(row, i, basketOfList);
            }
            autoSizeColumn(sheet);
            FileOutputStream outputStream = new FileOutputStream(getPathFile(filename));
            workbook.write(outputStream);
        } catch (IOException e) {
            Service.alert("Datei kann nicht geschrieben werden!", "writeXLSXFile");
            e.printStackTrace();
        }
    }

    public boolean readXLSXFile(File file, String filename) {
        try (FileInputStream inputStream = new FileInputStream(file);
             FileInputStream inputStream1 = new FileInputStream("files/" + filename + ".xlsx");
             Workbook workbook = new XSSFWorkbook(inputStream);
             Workbook workbook1 = new XSSFWorkbook(inputStream1);
             Workbook mergedWorkbook = new XSSFWorkbook();
             FileOutputStream fileOutputStream = new FileOutputStream("files/mergedFile.xlsx")) {
            XSSFSheet mergedSheet = (XSSFSheet) mergedWorkbook.createSheet("Merged Sheet");
            mergeSheets(mergedSheet, (XSSFSheet) workbook.getSheetAt(0), (XSSFSheet) workbook1.getSheetAt(0));
            mergedWorkbook.write(fileOutputStream);
            return true;
        } catch (IOException e) {
            Service.alert("Bitte überprüfen, ob die Datei von einem anderen Prozess verwendet wird!", "MergeSheets");
            e.printStackTrace();
        }
        return false;
    }

    private void mergeSheets(XSSFSheet mergedSheet, XSSFSheet sheetToBeMerged, XSSFSheet sheetToBeMerged1) {
        for (int i = 0; i <= sheetToBeMerged.getLastRowNum(); i++) {
            Row row = sheetToBeMerged.getRow(i);
            Row mergedRow = mergedSheet.createRow(i);
            for (int j = 0; j < row.getLastCellNum(); j++) {
                Cell cell = row.getCell(j);
                Cell mergedCell = mergedRow.createCell(j);
                mergedCell.setCellValue(cell.getStringCellValue());
            }
        }
        int k = 0;
        for (int i = mergedSheet.getLastRowNum() + 1; i <= sheetToBeMerged.getLastRowNum() + sheetToBeMerged1.getLastRowNum(); i++) {
            k++;
            Row row = sheetToBeMerged1.getRow(k);
            Row mergedRow = mergedSheet.createRow(i);
            if (row != null) {
                for (int j = 0; j < row.getLastCellNum(); j++) {
                    Cell cell = row.getCell(j);
                    Cell mergedCell = mergedRow.createCell(j);
                    mergedCell.setCellValue(cell.getStringCellValue());
                }
            }
        }
    }

    private void writeCellValue(XSSFRow row, int i, BasketOfList basketOfList) {
        row.createCell(0).setCellValue(basketOfList.getCountries().get(i - 1));
        row.createCell(1).setCellValue(basketOfList.getClients().get(i - 1));
        row.createCell(2).setCellValue(basketOfList.getDate().get(i - 1));
        row.createCell(3).setCellValue(basketOfList.getIds().get(i - 1));
        row.createCell(4).setCellValue(basketOfList.getArticles().get(i - 1));
        row.createCell(5).setCellValue(basketOfList.getSales_volume_action().get(i - 1));
        row.createCell(6).setCellValue(basketOfList.getQty_action().get(i - 1));
        row.createCell(7).setCellValue(basketOfList.getSales_volume_comparison().get(i - 1));
        row.createCell(8).setCellValue(basketOfList.getQty_comparison().get(i - 1));
    }

    //Cell wird erstellt und Kopfzeilen geschrieben
    private void createCellAndHeaderWrite(XSSFRow row) {
        XSSFCell cell;
        for (int i = 0; i < list.size(); i++) {
            cell = row.createCell(i);
            cell.setCellValue(list.get(i));
        }
    }

    private void autoSizeColumn(XSSFSheet sheet) {
        for (int i = 0; i < this.list.size(); i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void initList() {
        this.list = Arrays.asList("Land", "Mandant", "Datum", "Newsletter_ID", "Artikelnummer", "Umsatz Aktionszeitraum",
                "Menge Aktionszeitraum", "Umsatz Vergleichzeitraum", "Menge Vergleichzeitraum");
    }

    private String getPathFile(String filename) {
        return "files/" + filename + ".xlsx";
    }
}