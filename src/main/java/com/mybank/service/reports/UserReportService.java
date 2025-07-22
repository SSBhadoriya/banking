package com.mybank.service.reports;

import com.mybank.entities.User;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class UserReportService {

    public ByteArrayInputStream generateUserExcel(List<User> users) throws IOException {
        String[] headers = {"User ID", "User Name", "User Role", "Email", "Registration Date","Password Expiry Date"};

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Users Details");

            // Header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            CellStyle dateCellStyle = workbook.createCellStyle();
            CreationHelper createHelper = workbook.getCreationHelper();
            dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-mm-dd hh:mm:ss"));



            // Data rows
            int rowIdx = 1;
            for (User user : users) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(user.getUserId());
                row.createCell(1).setCellValue(user.getUserName());
                row.createCell(2).setCellValue(user.getRole().name());
                row.createCell(3).setCellValue(user.getEmail());

                Cell dateCell = row.createCell(4); // Assuming column 4 is date
                dateCell.setCellValue(user.getRegistrationDate()); // or any Date object
                dateCell.setCellStyle(dateCellStyle);

                Cell dateCell2 = row.createCell(5); // Assuming column 5 is date
                dateCell2.setCellValue(user.getPasswordExpiryDate()); // or any Date object
                dateCell2.setCellStyle(dateCellStyle);

            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}
