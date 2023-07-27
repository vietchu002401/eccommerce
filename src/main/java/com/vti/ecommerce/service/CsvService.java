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

    private void createCell(Row row, int columnCount, Object valueOfCell, CellStyle style) {
        Cell cell = row.createCell(columnCount);
        if (valueOfCell instanceof Integer) {
            cell.setCellValue((Integer) valueOfCell);
        } else if (valueOfCell instanceof Long) {
            cell.setCellValue((Long) valueOfCell);
        } else if (valueOfCell instanceof String) {
            cell.setCellValue((String) valueOfCell);
        } else {
            cell.setCellValue((Boolean) valueOfCell);
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
            Row rowHeader = sheet.createRow(0);
            CellStyle style = workbook.createCellStyle();
            XSSFFont font = workbook.createFont();
            font.setBold(true);
            font.setFontHeight(16);
            style.setFont(font);
            for (int i = 0; i < titles.size(); i++) {
                createCell(rowHeader, i, titles.get(i), style);
            }

            //write
            int rowCount = 1;
            CellStyle styleBody = workbook.createCellStyle();
            XSSFFont fontBody = workbook.createFont();
            fontBody.setFontHeight(14);
            styleBody.setFont(font);
            for (Product product: productList) {
                Row rowBody = sheet.createRow(rowCount++);
                int columnCount = 0;
                createCell(rowBody, columnCount++, product.getId(), style);
                createCell(rowBody, columnCount++, product.getName(), style);
//                createCell(rowBody, columnCount++, product.getPrice(), style);
                createCell(rowBody, columnCount++, product.getDescription(), style);
                createCell(rowBody, columnCount++, product.getAmount(), style);
                createCell(rowBody, columnCount++, product.getCategoryId(), style);
                createCell(rowBody, columnCount++, product.isStatus(), style);
                createCell(rowBody, columnCount++, product.getCreatedDate(), style);
                createCell(rowBody, columnCount, product.getUpdatedDate(), style);
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
