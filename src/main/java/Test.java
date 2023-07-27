import fricke.util.SQLColumns;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {

        FileInputStream inputStream = new FileInputStream(
                "C:\\Users\\p05865\\IdeaProjects\\untitled\\FrickeQuerySend\\files\\mergedFile.xlsx");
        Workbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);

        int rows = sheet.getLastRowNum();
        int m = 0;
        for (int i = 0; i < rows; i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                if (row.getCell(0) != null && row.getCell(0).getCellType() != CellType.BLANK && !row.getCell(0).getStringCellValue().isEmpty()) {
                    m++;
                    System.out.println(i + " : " + row.getCell(0).getStringCellValue());
                }
            }
        }
        System.out.println(m);
    }
}
