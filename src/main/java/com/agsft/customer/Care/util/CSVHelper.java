package com.agsft.customer.Care.util;

import com.agsft.customer.Care.config.MessageConfiguration;
import com.agsft.customer.Care.enums.HttpErrorCode;
import com.agsft.customer.Care.exception.CustomerException;
import com.agsft.customer.Care.model.FileDetail;
import com.agsft.customer.Care.model.FileInput;
import com.agsft.customer.Care.service.UserService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Column;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
@Component
public class CSVHelper {
    @Autowired
    private MessageConfiguration messageSource;
    public static String TYPE = "text/csv";
    static String[] HEADERs = { "PhoneNumber" };
    final static Logger log = LoggerFactory.getLogger(UserService.class);

    public static boolean hasCSVFormat(MultipartFile file) {

        if (!TYPE.equals(file.getContentType())) {
            return false;
        }

        return true;
    }

    public  List<FileInput> csvToFileDetail(InputStream is, FileDetail fileDetail) throws IOException {
      BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader,
                     CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

            List<FileInput> fileInputs = new ArrayList<FileInput>();

            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            for (CSVRecord csvRecord : csvRecords) {
                FileInput fileInput = new FileInput(csvRecord.get("phoneNumber"));
                fileInput.setFileDetail(fileDetail);
                fileInputs.add(fileInput);
            }
            return fileInputs;
    }
}
