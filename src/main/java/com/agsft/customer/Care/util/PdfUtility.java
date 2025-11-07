package com.agsft.customer.Care.util;

import com.agsft.customer.Care.dto.response.BillCalculateResponseDto;
import com.agsft.customer.Care.dto.response.BillFileResponseDto;
import com.agsft.customer.Care.model.FileInput;
import com.agsft.customer.Care.model.User;
import com.agsft.customer.Care.repository.UserRepository;
import com.agsft.customer.Care.service.AdminService;

import com.itextpdf.kernel.pdf.EncryptionConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class PdfUtility {

    @Autowired
    UserRepository userRepository;
    final static Logger log = LoggerFactory.getLogger(CSVHelper.class);
    public  void pdfReport(User user, BillCalculateResponseDto responseDto, HttpServletResponse response) throws IOException {
        log.info("user uploaded file list pdf generation is starting");
        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);
        try {
            document.add(new Paragraph("Bill History Report").setBold().setPaddingLeft(200f));

            Table table = new Table(new float[]{10f, 10f, 10F, 15F, 10F, 15F, 15F, 15F});
            table.setWidthPercent(100)
                    .setPadding(0)
                    .setFontSize(9);

            Cell cell1 = new Cell(1,8);
            cell1.setTextAlignment(TextAlignment.LEFT);
            cell1.add("User Name : " +responseDto.getName());
            cell1.add("No. Of File : " +responseDto.getNoOfFiles());
            cell1.add("Total Bill : " +responseDto.getTotalBill());
            table.addCell(cell1);


            table.addCell(new Cell().add("Id").setBold());
            table.addCell(new Cell().add("File Name").setBold());
            table.addCell(new Cell().add("No. of Record").setBold());
            table.addCell(new Cell().add("Dnd Count").setBold());
            table.addCell(new Cell().add("Not Dnd Count").setBold());
            table.addCell(new Cell().add("NA Count").setBold());
            table.addCell(new Cell().add("Charge per Hit").setBold());
            table.addCell(new Cell().add("Total charges").setBold());

            Integer i=1;
            for(BillFileResponseDto bill:responseDto.getFileList()) {
                table.addCell(new Cell().add(String.valueOf(i++)));
                table.addCell(new Cell().add(bill.getFileName()));
                table.addCell(new Cell().add(String.valueOf(bill.getTotalElement())));
                table.addCell(new Cell().add(String.valueOf(bill.getDndCount())));
                table.addCell(new Cell().add(String.valueOf(bill.getNotDndCount())));
                table.addCell(new Cell().add(String.valueOf(bill.getNACount())));
                table.addCell(new Cell().add(String.valueOf(bill.getChargePerRecord())));
                table.addCell(new Cell().add(String.valueOf(bill.getTotalCharges())));
            }

            document.add(table);
            log.info("user uploaded file list pdf generated successfully");
            document.close();
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
