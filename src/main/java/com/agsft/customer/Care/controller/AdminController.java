package com.agsft.customer.Care.controller;

import com.agsft.customer.Care.config.MessageConfiguration;
import com.agsft.customer.Care.dto.request.CompanyRequestDTO;
import com.agsft.customer.Care.dto.request.PaginationRequestDTO;
import com.agsft.customer.Care.dto.request.RoleRequestDTO;
import com.agsft.customer.Care.dto.response.*;
import com.agsft.customer.Care.enums.HttpErrorCode;
import com.agsft.customer.Care.enums.HttpSuccessCodes;
import com.agsft.customer.Care.model.User;
import com.agsft.customer.Care.repository.UserRepository;
import com.agsft.customer.Care.service.AdminService;
import com.agsft.customer.Care.service.UserService;
import com.agsft.customer.Care.util.PdfUtility;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = {"/admin"})
@CrossOrigin("*")
public class AdminController {
    @Autowired
    MessageConfiguration messageSource;
    @Autowired
    private AdminService adminService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @RequestMapping(method = RequestMethod.POST, path = "/company", consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> addCompany(@Valid @RequestBody CompanyRequestDTO companyRequestDto) {
        CompanyResponseDTO companyResponseDTO = adminService.addCompany(companyRequestDto);
        return ResponseEntity.ok(new ResponseDTO(HttpSuccessCodes.COMPANY_SUCCESS.getValue(), messageSource.messageSource().getMessage(HttpSuccessCodes.COMPANY_SUCCESS.getReasonPhrase(), null, null), companyResponseDTO));
    }

    @RequestMapping(method = RequestMethod.POST, path = "/downloads/file", consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public void download(@RequestParam Long fileId, HttpServletResponse response) throws IOException {
        FileDTO fileDTO = adminService.download(fileId);
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileDTO.getFileName());
        response.getOutputStream().write(IOUtils.copy(fileDTO.getInputStream(), response.getOutputStream()));
    }

    @RequestMapping(method = RequestMethod.POST, path = "/adminGetAllUserFiles", consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getAllFiles(@Valid @RequestBody PaginationRequestDTO requestDTO) {
        PaginationResponseDTO paginationResponseDTO = adminService.getAllFiles(requestDTO);
        ResponseDTO responseDTO = new ResponseDTO();
        if (paginationResponseDTO.getNumberOfElements().equals(0)) {
            responseDTO.setCode(HttpSuccessCodes.OK.getValue());
            responseDTO.setMessage(messageSource.messageSource().getMessage(HttpErrorCode.FILE_EMPTY.getMessage(), null, null));
        } else {
            responseDTO.setCode(HttpSuccessCodes.OK.getValue());
            responseDTO.setMessage(messageSource.messageSource().getMessage(HttpSuccessCodes.LIST_FETCHED.getReasonPhrase(), null, null));
            responseDTO.setBody(paginationResponseDTO);
        }
        return ResponseEntity.ok(responseDTO);
    }

//    @PostMapping("/adminUploadsFileToParse")
//    public ResponseEntity<?> uploadFileToParse(@RequestParam("fileId") Long fileId) throws IOException {
//       BillFileResponseDto billCalculateResponseDto= adminService.parseCSV(fileId);
//        return ResponseEntity.ok(new ResponseDTO(HttpSuccessCodes.FILE_PARSE_SUCCESS.getValue(), messageSource.messageSource().getMessage(HttpSuccessCodes.FILE_PARSE_SUCCESS.getReasonPhrase(), null, null), billCalculateResponseDto));
//    }

    @RequestMapping(method = RequestMethod.GET, path = "/downloadExcelOfUser")
    public ResponseEntity<?> adminGeneratesUserReport(@RequestParam("fileId") Long fileId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        File file = null;
        int BUFFER_SIZE = 2098;
        List<FileListResponseDTO> userList = adminService.getFileDetailsList(fileId);
        file = adminService.buildExcelFileDetailsRecord(userList);
        FileInputStream inputStream = new FileInputStream(file);

        String mimeType = String.valueOf(MediaType.APPLICATION_OCTET_STREAM);
        response.setContentType(mimeType);
        response.setContentLength((int) file.length());
        String headerValue = String.format("attachment; filename=\"%s\"", file.getName());

        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, headerValue);
        OutputStream outStream = response.getOutputStream();
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }
        inputStream.close();
        outStream.close();

        return ResponseEntity.ok(new ResponseDTO(HttpSuccessCodes.FILE_DOWNLOADED_SUCCESS.getValue(), messageSource.messageSource().getMessage(HttpSuccessCodes.FILE_DOWNLOADED_SUCCESS.getReasonPhrase(), null, null), null));

    }

    @PreAuthorize("hasAnyAuthority('Super Admin')")
    @PostMapping(value = "/assignUserRole/{companyId}/{userId}")
    public ResponseEntity<?> assignUserRoles(@PathVariable("companyId") String companyId, @PathVariable("userId") Long userId, @RequestBody RoleRequestDTO roleRequestDTO) {
        RoleResponseDTO roleResponseDTO = adminService.assignRoleToUser(companyId, userId, roleRequestDTO);
        return ResponseEntity.ok(new ResponseDTO(HttpSuccessCodes.ROLE_SUCCESS.getValue(), messageSource.messageSource().getMessage(HttpSuccessCodes.ROLE_SUCCESS.getReasonPhrase(), null, null), roleResponseDTO));
    }

    @RequestMapping(method = RequestMethod.GET, path = "/billpdf/{companyId}/{userId}", produces = MediaType.APPLICATION_PDF_VALUE)
    public void downloadeBillHistory(@PathVariable("companyId") String companyId, @PathVariable("userId") Long userId, @RequestParam(value = "startDate", required = true) @DateTimeFormat(pattern = "MM/dd/yyyy") Date startDate, @RequestParam(value = "endDate", required = true) @DateTimeFormat(pattern = "MM/dd/yyyy") Date endDate, HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=bill_"+ currentDateTime +".pdf";
        response.setHeader(headerKey, headerValue);
        BillCalculateResponseDto billCalculateResponseDto = adminService.billPdfList(companyId, userId, startDate, endDate);
        PdfUtility pdfUtility = new PdfUtility();
        User user = userService.findUserDetail(userId);
        pdfUtility.pdfReport(user, billCalculateResponseDto, response);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/billHistory/{companyId}/{userId}")
    public ResponseEntity<?> getBillHistoryByUser(@PathVariable("companyId") String companyId, @PathVariable("userId") Long userId, @RequestParam(value = "startDate", required = true) @DateTimeFormat(pattern = "MM/dd/yyyy") Date startDate, @RequestParam(value = "endDate", required = true) @DateTimeFormat(pattern = "MM/dd/yyyy") Date endDate) throws IOException {
        BillCalculateResponseDto billCalculateResponseDto = adminService.billPdfList(companyId, userId, startDate, endDate);
        return ResponseEntity.ok(new ResponseDTO(HttpSuccessCodes.BILL_DETAIL.getValue(), messageSource.messageSource().getMessage(HttpSuccessCodes.BILL_DETAIL.getReasonPhrase(), null, null), billCalculateResponseDto));
    }
}



