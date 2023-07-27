package com.vti.ecommerce.service;

import com.vti.ecommerce.model.Product;
import com.vti.ecommerce.repository.ProductRepository;
import com.vti.ecommerce.response.ResponseData;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvService {
    @Autowired
    private ProductRepository productRepository;

    private void createCell(Row row, int columnCount, Object value, CellStyle style, XSSFSheet sheet) {
        Cell cell = row.createCell(columnCount);
        sheet.autoSizeColumn(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        }else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        }else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        }else{
            cell.setCellValue(String.valueOf(value));
        }
        cell.setCellStyle(style);
    }

    public ResponseEntity<ResponseData> downloadProduct(HttpServletResponse response){
        try {
            Class<Product> productClass = Product.class;
            Field[] fields = productClass.getDeclaredFields();
            List<String> titles = new ArrayList<>();
            titles.add("STT");
            for (Field field : fields) {
                titles.add(field.getName());
            }
            List<Product> productList = productRepository.findAll();

            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Products");

            //header
            Row rowHeader = sheet.createRow(0);
            CellStyle styleHeader = workbook.createCellStyle();
            XSSFFont fontHeader = workbook.createFont();
            fontHeader.setBold(true);
            fontHeader.setFontHeight(16);
            styleHeader.setFont(fontHeader);
            for (int i = 0; i < titles.size(); i++) {
                createCell(rowHeader, i, titles.get(i), styleHeader, sheet);
            }

            //write
            int rowCount = 1;
            CellStyle styleBody = workbook.createCellStyle();
            XSSFFont fontBody = workbook.createFont();
            fontBody.setFontHeight(14);
            styleBody.setFont(fontBody);
            int stt = 1;
            for (Product product: productList) {
                Row rowBody = sheet.createRow(rowCount++);
                int columnCount = 0;
                createCell(rowBody, columnCount++, stt++, styleBody, sheet);
                createCell(rowBody, columnCount++, product.getId(), styleBody, sheet);
                createCell(rowBody, columnCount++, product.getName(), styleBody, sheet);
                createCell(rowBody, columnCount++, product.getPrice(), styleBody, sheet);
                createCell(rowBody, columnCount++, product.getDescription(), styleBody, sheet);
                createCell(rowBody, columnCount++, product.getAmount(), styleBody, sheet);
                createCell(rowBody, columnCount++, product.getCategoryId(), styleBody, sheet);
                createCell(rowBody, columnCount++, product.isStatus(), styleBody, sheet);
                createCell(rowBody, columnCount++, product.getCreatedDate(), styleBody, sheet);
                createCell(rowBody, columnCount, product.getUpdatedDate(), styleBody, sheet);
            }
            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();


            return ResponseEntity.ok(new ResponseData(HttpStatus.OK, "Dowloaded", null));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
