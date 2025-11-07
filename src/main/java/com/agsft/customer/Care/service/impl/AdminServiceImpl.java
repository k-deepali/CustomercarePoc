package com.agsft.customer.Care.service.impl;

import com.agsft.customer.Care.config.MessageConfiguration;
import com.agsft.customer.Care.dto.request.CompanyRequestDTO;
import com.agsft.customer.Care.dto.request.PaginationRequestDTO;
import com.agsft.customer.Care.dto.request.PhoneNumberRestRequest;
import com.agsft.customer.Care.dto.request.RoleRequestDTO;
import com.agsft.customer.Care.dto.response.*;
import com.agsft.customer.Care.enums.*;
import com.agsft.customer.Care.exception.CustomerException;
import com.agsft.customer.Care.model.*;
import com.agsft.customer.Care.repository.*;
import com.agsft.customer.Care.service.AdminService;
import com.agsft.customer.Care.util.ApplicationUtility;
import com.agsft.customer.Care.util.PhoneNumberUtility;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    ApiChargesReposiory apiChargesReposiory;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private MessageConfiguration messageSource;
    @Autowired
    private FileInputRepository fileInputRepository;
    @Autowired
    private FileDetailRepository fileDetailRepository;
    @Value("${file.location}")
    Path root;
    @Value("${file.location}")
    private String fileLocation;

    @Autowired
    PhoneNumberUtility phoneNumberUtility;
    final static Logger log = LoggerFactory.getLogger(AdminService.class);

    /**
     * this api is used to add new company in system and company name must be unique.
     *
     * @param companyRequestDto contains company name and city
     * @return the companyResponse Object containing details of company.
     */

    @Override
    public CompanyResponseDTO addCompany(CompanyRequestDTO companyRequestDto) {
        log.info("company registration is starting");
        CompanyResponseDTO companyResponseDTO = new CompanyResponseDTO();
        log.info("checking company name is already present or not");
        if (Objects.nonNull(companyRepository.findByName(companyRequestDto.getName()))) {
            log.error(messageSource.messageSource().getMessage(HttpErrorCode.NAME_ALREADY_REGISTERED.getMessage(), null, null));
            throw new CustomerException(HttpErrorCode.NAME_ALREADY_REGISTERED.getCode(), messageSource.messageSource().getMessage(HttpErrorCode.NAME_ALREADY_REGISTERED.getMessage(), null, null));
        }
        Company company = new Company();
        company.setId(UUID.randomUUID().toString());
        company.setName(companyRequestDto.getName());
        company.setCity(companyRequestDto.getCity());
        companyRepository.save(company);
        companyResponseDTO = modelMapper.map(company, CompanyResponseDTO.class);
        log.info("company register successfully");
        return companyResponseDTO;
    }

    /**
     * This api is used to download a file from a particular path location
     *
     * @param fileId file id of the file
     * @return downloads the file in our given path
     * @throws FileNotFoundException if File not found while processing throws exception
     */
    @Override
    public FileDTO download(Long fileId) throws FileNotFoundException {
        FileInputStream fileInputStream = null;
        log.info("Download started");
        //get filename from fileId;
        Optional<FileDetail> existingFile = fileDetailRepository.findById(fileId);

        if (!existingFile.isPresent()) {
            throw new CustomerException(HttpErrorCode.NOT_FOUND.getCode(), messageSource.messageSource().getMessage(HttpErrorCode.NOT_FOUND.getMessage(), null, null));
        }
//        if (!existingFile.get().getStatus().equals("new")) {
//            throw new CustomerException(HttpErrorCode.FILE_COMPLETE.getCode(), messageSource.messageSource().getMessage(HttpErrorCode.FILE_COMPLETE.getMessage(), null, null));
//        }

        String fileName = existingFile.get().getName();
        // Define the path of the file to be downloaded
        String filePath = fileLocation + "/" + fileName;

        try {
            // Load the file
            log.info("Download processing");
            java.io.File file = new java.io.File(filePath);
            fileInputStream = new FileInputStream(file);

        } catch (Exception e) {
            throw new FileNotFoundException();
        }
        //set response
        FileDTO fileDTO = new FileDTO();
        fileDTO.setInputStream(fileInputStream);
        fileDTO.setFileName(fileName);
       // existingFile.get().setStatus(FileStatus.PENDING.getValue());
       // fileDetailRepository.save(existingFile.get());
        log.info("Download Completed");
        return fileDTO;
    }

    /**
     * Get the list of File by search sort pagination
     * if sort parameter not provided it will consider default as filename in ascending
     *
     * @param requestDTO contains the request parameter for pagination
     * @return the content object containing list of File including pageable object
     */
    @Override
    public PaginationResponseDTO getAllFiles(PaginationRequestDTO requestDTO) {

        PaginationResponseDTO paginationResponseDto = new PaginationResponseDTO();
        int pageNo;
        int pageSize;
        Sort.Direction sortDirection;
        String sortBy;

        /* Set the page number and page size */
        pageNo = Objects.isNull(requestDTO.getPageNo()) ? 0 : requestDTO.getPageNo();
        pageSize = Objects.isNull(requestDTO.getPageSize()) ? 10 : requestDTO.getPageSize();

        /* Set the sorting default as asc */
        if (Objects.isNull(requestDTO.getSortDirection())) {
            sortDirection = Sort.Direction.ASC;
        } else {
            sortDirection = requestDTO.getSortDirection();
        }

        /* Set the sort by  file name or status at if null set the default as filename*/
        if (Objects.isNull(requestDTO.getSortBy())) {
            sortBy = SortByConstant.FILE_NAME.getName();
        } else if (requestDTO.getSortBy().equals(SortByConstant.FILE_NAME.getName())) {
            sortBy = SortByConstant.FILE_NAME.getName();
        } else if (requestDTO.getSortBy().equals(SortByConstant.STATUS.getName())) {
            sortBy = SortByConstant.STATUS.getName();
        } else {
            /*SortBy Should Be FileName Or UpdatedAt if not throw an exception*/
            log.error(messageSource.messageSource().getMessage(HttpErrorCode.SORT_BY.getMessage(), null, null));
            throw new CustomerException(HttpErrorCode.SORT_BY.getCode(), messageSource.messageSource().getMessage(HttpErrorCode.SORT_BY.getMessage(), null, null));
        }

        /* Get the data from database */
        Pageable pageableRequest = PageRequest.of(pageNo, pageSize, Sort.by(sortDirection, sortBy));
        Page<FileDetail> fileList = null;
        if (requestDTO.getSearchCriteriaDTO() == null) {
            fileList = fileDetailRepository.findAll(pageableRequest);
        } else {
            if (requestDTO.getSearchCriteriaDTO().getCriteriaName().equalsIgnoreCase(SortByConstant.FILE_NAME.getName())) {
                fileList = fileDetailRepository.findByNameContaining(requestDTO.getSearchCriteriaDTO().getSearchText(), pageableRequest);
            } else if (requestDTO.getSearchCriteriaDTO().getCriteriaName().equalsIgnoreCase(SortByConstant.STATUS.getName())) {
                fileList = fileDetailRepository.findByStatus(requestDTO.getSearchCriteriaDTO().getSearchText(), pageableRequest);
            }
        }
        log.info("File List Fetched Successfully");
        List<FileResponseDTO> fileResponseDTOS = new ArrayList<>();

        if (fileList.getContent() != null) {

            for (FileDetail fileDetail : fileList.getContent()) {
                FileResponseDTO responseDTO = new FileResponseDTO();
                responseDTO.setId(fileDetail.getId());
                responseDTO.setName(fileDetail.getName());
                responseDTO.setMainstatus(fileDetail.getStatus());
                responseDTO.setPath(fileDetail.getPath());
                fileResponseDTOS.add(responseDTO);
            }

        }
        /* Build the response Dto */
        paginationResponseDto.setContent(fileResponseDTOS);
        paginationResponseDto.setCurrentPage(fileList.getNumber());
        paginationResponseDto.setTotalPages(fileList.getTotalPages());
        paginationResponseDto.setNumberOfElements((int) fileList.getTotalElements());

        return paginationResponseDto;
    }

    /**
     * this api used for assign role to user
     *
     * @param companyId
     * @param userId
     * @param roleRequestDTO contanis superadmin and staffadmin role
     * @return
     */
    @Override
    public RoleResponseDTO assignRoleToUser(String companyId, Long userId, RoleRequestDTO roleRequestDTO) {
        log.info("assign role to user is starting");
        RoleResponseDTO roleResponseDTO = new RoleResponseDTO();
        Optional<Company> company = companyRepository.findById(companyId);
        if (!company.isPresent()) {
            log.error(messageSource.messageSource().getMessage(HttpErrorCode.COMPANY_NOT_FOUND.getMessage(), null, null));
            throw new CustomerException(HttpErrorCode.COMPANY_NOT_FOUND.getCode(), messageSource.messageSource().getMessage(HttpErrorCode.COMPANY_NOT_FOUND.getMessage(), null, null));
        }
        User user = userRepository.findByIdAndCompany(userId, company);
        if (Objects.isNull(user)) {
            log.error(messageSource.messageSource().getMessage(HttpErrorCode.USER_NOT_FOUND.getMessage(), null, null));
            throw new CustomerException(HttpErrorCode.USER_NOT_FOUND.getCode(), messageSource.messageSource().getMessage(HttpErrorCode.USER_NOT_FOUND.getMessage(), null, null));
        }
        List<Role> roles = new ArrayList<>();
        if (roleRequestDTO.getIsStaffAdmin() == Boolean.TRUE) {
            log.info("user assign staff admin role processing");
            Role role = roleRepository.findByName(RoleContants.STAFF_ADMIN.getValue());
            roles.add(role);
        }
        if (roleRequestDTO.getIsSuperAdmin() == Boolean.TRUE) {
            log.info("user assign super admin role processing");
            Role role = roleRepository.findByName(RoleContants.SUPER_ADMIN.getValue());
            roles.add(role);
        }
        user.setRoles(roles);
        userRepository.save(user);
        log.info("user assign to role successfully");
        roleResponseDTO = modelMapper.map(user, RoleResponseDTO.class);
        return roleResponseDTO;
    }


    /**
     * This api is used to parse the file details to third party api and get details of phone no's against it and calculates the bill for the user who has uploaded the file
     *
     * @param fileId File id for file name
     * @return The bill for particular user who parsed the file and calculated charges
     */
    public void parseCSV(Long fileId) throws IOException, InterruptedException {

        Optional<FileDetail> fileDetailsOptional = fileDetailRepository.findById(fileId);
        if (!fileDetailsOptional.isPresent()) {
            log.error(messageSource.messageSource().getMessage(HttpErrorCode.FILE_NOT_FOUND.getMessage(), null, null));
            throw new CustomerException(HttpErrorCode.FILE_NOT_FOUND.getCode(), messageSource.messageSource().getMessage(HttpErrorCode.FILE_NOT_FOUND.getMessage(), null, null));
        }
        if (fileDetailsOptional.get().getStatus().equalsIgnoreCase(FileStatus.COMPLETED.getValue())) {
            log.error(messageSource.messageSource().getMessage(HttpErrorCode.FILE_ALREADY_PRESENT.getMessage(), null, null));
            throw new CustomerException(HttpErrorCode.FILE_ALREADY_PRESENT.getCode(), messageSource.messageSource().getMessage(HttpErrorCode.FILE_ALREADY_PRESENT.getMessage(), null, null));
        }

        List<FileInput> fileInputs = fileInputRepository.findByFileDetailId(fileId);
        // double size=0.01*fileInputs.size();
        List<Future<?>> futures = new ArrayList<Future<?>>();
        ExecutorService threadPoolExecutor = Executors.newCachedThreadPool();

        int chunkSize = (int) (.056 * fileInputs.size());
        if (chunkSize == 0) {
            chunkSize = 5;
        }
        List<List<FileInput>> fileInputList = splitListIntoChunks(fileInputs, chunkSize);

        for (List<FileInput> inputs : fileInputList) {
            //  threadPoolExecutor.submit(serachPhonenumber(inputs, fileDetailsOptional.get()));
            Future<String> future = threadPoolExecutor.submit(serachPhonenumberCallable(inputs, fileDetailsOptional.get()));
            futures.add(future);
        }
        for (Future<?> fut : futures) {
            try {
                log.info("Future Object =" + new Date() + "::" + fut.get());

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        threadPoolExecutor.shutdownNow();
//        threadPoolExecutor.awaitTermination((long) size, TimeUnit.SECONDS);;
        FileDetail fileDetail = fileDetailsOptional.get();
        fileDetail.setStatus(FileStatus.PARSED.getValue());
        fileDetailRepository.save(fileDetail);
//        log.info("Bill Generation started");
        billCalculation(fileDetailsOptional.get().getId());
//        log.info("Bill Generation completd");

    }


    @Override
    public List<FileListResponseDTO> getFileDetailsList(Long fileId) {
        List<FileListResponseDTO> fileList = new ArrayList<>();
        fileList = fileInputRepository.findByFileDetailId(fileId).stream().map(fileInput -> modelMapper.map(fileInput, FileListResponseDTO.class)).collect(Collectors.toList());
        log.info("FileList successful");
        return fileList;
    }

    /**
     * Get the Excel for the given file id
     *
     * @param userList
     * @return The File xsl report for the dnd(Do Not Disturb) activated deactivated
     * @throws IOException
     */
    public File buildExcelFileDetailsRecord(List<FileListResponseDTO> userList) throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook();

        // create a new Excel sheet
        HSSFSheet sheet = workbook.createSheet("File Information");
        sheet.setDefaultColumnWidth(30);
        log.info("Creating New Excel");
        // create style for header cells
        HSSFCellStyle blueStyle = workbook.createCellStyle();
        HSSFFont font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 12);
        blueStyle.setFillBackgroundColor(HSSFColor.LIGHT_YELLOW.index);
        blueStyle.setFillBackgroundColor(HSSFColor.BLUE.index);
        blueStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        blueStyle.setAlignment(HorizontalAlignment.CENTER);
        font.setBold(true);
        // blueStyle.setBorderBottom(blueStyle.BORDER_DASH_DOT_DOT);
        font.setColor(HSSFColor.WHITE.index);
        blueStyle.setFont(font);

        HSSFCellStyle style = workbook.createCellStyle();
        HSSFFont hssfFont = workbook.createFont();
        hssfFont.setFontName("Arial");
        hssfFont.setFontHeightInPoints((short) 11);
        style.setAlignment(HorizontalAlignment.CENTER);

        style.setFont(hssfFont);

        HSSFFont twelveFont = workbook.createFont();
        twelveFont.setFontHeightInPoints((short) 12);
        twelveFont.setFontName("Arial");

        HSSFRow mergeRow = sheet.createRow(0);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));
        HSSFCell mergecell = mergeRow.createCell(0);
        mergecell.setCellValue("File Details Records......");
        mergecell.setCellStyle(blueStyle);
        HSSFRow headerRow = sheet.createRow(1);
        HSSFCell headerCell1 = headerRow.createCell(0);
        headerCell1.setCellValue("File Id");
        headerCell1.setCellStyle(blueStyle);

        HSSFCell headerCell2 = headerRow.createCell(1);
        headerCell2.setCellValue("Phone Number");
        headerCell2.setCellStyle(blueStyle);

        HSSFCell headerCell3 = headerRow.createCell(2);
        headerCell3.setCellValue("Status");
        headerCell3.setCellStyle(blueStyle);
        int dataRowCount = 3;

        for (int i = 0; i < userList.size(); i++) {
            HSSFRow dataRow = sheet.createRow(dataRowCount);

            HSSFCell dataRowCell1 = dataRow.createCell(0);
            dataRowCell1.setCellValue(userList.get(i).getId());
            HSSFCell dataRowCell2 = dataRow.createCell(1);
            dataRowCell2.setCellValue(userList.get(i).getPhoneNumber());
            HSSFCell dataRowCell3 = dataRow.createCell(2);
            HSSFCellStyle style1 = workbook.createCellStyle();
            if (userList.get(i).getStatus() != null ) {
                    if (userList.get(i).getStatus().equals("true")) {
                        style1.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
                        style1.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                        dataRowCell1.setCellStyle(style1);
                        dataRowCell2.setCellStyle(style1);
                        dataRowCell3.setCellStyle(style1);
                        dataRowCell3.setCellValue(userList.get(i).getStatus());
                    } else if(userList.get(i).getStatus().equals("false")) {
                        style1.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
                        style1.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                        dataRowCell1.setCellStyle(style1);
                        dataRowCell2.setCellStyle(style1);
                        dataRowCell3.setCellStyle(style1);
                        dataRowCell3.setCellValue(userList.get(i).getStatus());
                    }else {
                        style1.setFillForegroundColor(IndexedColors.WHITE.getIndex());
                        style1.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                        dataRowCell1.setCellStyle(style1);
                        dataRowCell2.setCellStyle(style1);
                        dataRowCell3.setCellStyle(style1);
                        dataRowCell3.setCellValue(userList.get(i).getStatus());
                    }

            }

            dataRowCell1.getCellStyle().setFont(twelveFont);
            dataRowCell2.getCellStyle().setFont(twelveFont);
            dataRowCell3.getCellStyle().setFont(twelveFont);
            dataRowCount++;
        }

        // Write the workbook in file system
        File dir = new File(System.getProperty("java.io.tmpdir") + File.separator + "excel");
        dir.mkdirs();
        Timestamp currentTime = new Timestamp(new Date().getTime());
        File reportFile = new File(dir, "FileReport" + currentTime + ".xls");
        FileOutputStream out = new FileOutputStream(reportFile);
        workbook.write(out);
        out.close();
        log.info("Excel Generated at:" + reportFile);
        System.out.println("FileInfo.xlsx written successfully on disk.");
        return reportFile;
    }




    @Override
    public void billCalculation(Long fileDetailId) {
        log.info("bill calculation is starting");
     //   BillFileResponseDto responseDto = new BillFileResponseDto();
        Optional<FileDetail> optionalFileDetail= Optional.ofNullable(fileDetailRepository.findById(fileDetailId).orElseThrow(() -> new CustomerException(HttpErrorCode.FILE_NOT_FOUND.getCode(), messageSource.messageSource().getMessage(HttpErrorCode.FILE_NOT_FOUND.getMessage(), null, null))));
        FileDetail fileDetail=optionalFileDetail.get();
        Float bill = 0F;
        Integer trueCount = 0, falseCount = 0,naCount=0;
//        responseDto.setId(fileDetail.getId());
//        responseDto.setFileName(fileDetail.getName());
//        responseDto.setTotalElement(fileDetail.getFileInputs().size());
//        responseDto.setNACount(fileDetail.getFailureCount());
        String month = fileDetail.getCreatedAt().format(DateTimeFormatter.ofPattern("MM/uuuu"));
        ApiCharges apiCharges = apiChargesReposiory.findByMonth(month);
        if (Objects.isNull(apiCharges)) {
            apiCharges = apiChargesReposiory.findByMonth("00/0000");
        }
     //   responseDto.setChargePerRecord(apiCharges.getCharges());
        for (FileInput fileInput : fileDetail.getFileInputs()) {
            if (fileInput.getStatus().equalsIgnoreCase(FileInputStatus.DND.getValue())) {
                fileInput.setCharge(apiCharges.getCharges());
                bill = apiCharges.getCharges() + bill;
                trueCount++;
            }
            if (fileInput.getStatus().equalsIgnoreCase(FileInputStatus.NOTDND.getValue())) {
                fileInput.setCharge(apiCharges.getCharges());
                bill = apiCharges.getCharges() + bill;
                falseCount++;
            }
            if (fileInput.getStatus().equalsIgnoreCase(FileInputStatus.NA.getValue())) {
                fileInput.setCharge(0.0F);
                bill = 0.0F + bill;
                naCount++;
            }
            fileInputRepository.save(fileInput);
        }
        fileDetail.setStatus(FileStatus.COMPLETED.getValue());
        fileDetail.setFailureCount((int) fileDetail.getFileInputs().stream().filter(i->i.getStatus().equalsIgnoreCase(FileInputStatus.NA.getValue())).count());
        fileDetail.setSuccessCount((int) fileDetail.getFileInputs().stream().filter(i->!i.getStatus().equalsIgnoreCase(FileInputStatus.NA.getValue())).count());
        fileDetailRepository.save(fileDetail);
//        responseDto.setDndCount(trueCount);
//        responseDto.setNotDndCount(falseCount);
//        responseDto.setTotalCharges(bill);
//        responseDto.setNACount(naCount);
//        responseDto.setCreatedAt(String.valueOf(fileDetail.getCreatedAt()));
        log.info("bill calculation is successfully");
    }

    /* this api is used for get file details based on user uploade file and generate pdf between created date of file start date and end date.
     *
     * @param companyId for retrive user
     * @param userId for retrive uploaded file
     * @param startDate for createat date duration
     * @param endDate for createat date duration
     * @return pdf file
     */
    @Override
    public BillCalculateResponseDto billPdfList(String companyId, Long userId, Date startDate, Date endDate) {
        BillCalculateResponseDto calculateResponseDto = new BillCalculateResponseDto();
        log.info("bill list generation is starting");
        Optional<Company> company = companyRepository.findById(companyId);
        if (!company.isPresent()) {
            log.error(messageSource.messageSource().getMessage(HttpErrorCode.COMPANY_NOT_FOUND.getMessage(), null, null));
            throw new CustomerException(HttpErrorCode.COMPANY_NOT_FOUND.getCode(), messageSource.messageSource().getMessage(HttpErrorCode.COMPANY_NOT_FOUND.getMessage(), null, null));
        }
        User user = userRepository.findByIdAndCompany(userId, company);
        if (Objects.isNull(user)) {
            throw new CustomerException(HttpErrorCode.USER_NOT_FOUND.getCode(), messageSource.messageSource().getMessage(HttpErrorCode.USER_NOT_FOUND.getMessage(), null, null));
        }
        LocalDate stDate = ApplicationUtility.convertDateToLocalDate(startDate);
        LocalDate edDate = ApplicationUtility.convertDateToLocalDate(endDate);
        List<FileDetail> fileDetails = fileDetailRepository.findByCreatedAtBetweenAndUserAndStatusLike(stDate, edDate, user, FileStatus.COMPLETED.getValue() + "%");
        calculateResponseDto.setName(user.getFirstName() + " " + user.getLastName());
        calculateResponseDto.setNoOfFiles(fileDetails.size());
        if (fileDetails.isEmpty()) {
            log.error(messageSource.messageSource().getMessage(HttpErrorCode.FILE_NOT_UPLOAD.getMessage(), null, null));
            throw new CustomerException(HttpErrorCode.FILE_NOT_UPLOAD.getCode(), messageSource.messageSource().getMessage(HttpErrorCode.FILE_NOT_UPLOAD.getMessage(), null, null));
        }
        Float bill = 0.0F;
        Float totalBill = 0.0F;
        Integer trueCount = 0, falseCount = 0,naCount=0;
        List<BillFileResponseDto> fileResponseDtoList = new ArrayList<>();
        for (FileDetail fileDetail : fileDetails) {
            BillFileResponseDto responseDto = new BillFileResponseDto();
            responseDto.setId(fileDetail.getId());
            responseDto.setFileName(fileDetail.getName());
            responseDto.setTotalElement(fileDetail.getFileInputs().size());
//            responseDto.setNACount(fileDetail.getFailureCount());
            String month = fileDetail.getCreatedAt().format(DateTimeFormatter.ofPattern("MM/uuuu"));
            ApiCharges apiCharges = apiChargesReposiory.findByMonth(month);
            if (Objects.isNull(apiCharges)) {
                apiCharges = apiChargesReposiory.findByMonth("00/0000");
            }
            responseDto.setChargePerRecord(apiCharges.getCharges());
            responseDto.setNotDndCount(fileDetail.getFailureCount());
            for (FileInput fileInput : fileDetail.getFileInputs()) {
                if (fileInput.getStatus().equalsIgnoreCase(FileInputStatus.DND.getValue())) {
                    trueCount++;
                }
                if (fileInput.getStatus().equalsIgnoreCase(FileInputStatus.NOTDND.getValue())) {
                    falseCount++;
                }if (fileInput.getStatus().equalsIgnoreCase(FileInputStatus.NA.getValue())) {
                    naCount++;
                }

                bill = fileInput.getCharge() + bill;
            }
            responseDto.setTotalCharges(bill);
            responseDto.setCreatedAt(String.valueOf(fileDetail.getCreatedAt()));
            totalBill = totalBill + bill;
            bill = 0F;
            responseDto.setDndCount(trueCount);
            responseDto.setNotDndCount(falseCount);
            responseDto.setNACount(naCount);
            trueCount = 0;
            falseCount = 0;
            naCount=0;
            fileResponseDtoList.add(responseDto);
        }
        calculateResponseDto.setTotalBill(totalBill);
        calculateResponseDto.setFileList(fileResponseDtoList);
        log.info("bill list fetching successfully");
        return calculateResponseDto;
    }

    public Runnable serachPhonenumber(List<FileInput> fileInputs, FileDetail fileDetail){
        return new Runnable() {
            @Override
            public void run() {
                log.info("Running Thread Name: = "+ Thread.currentThread().getName() +"  priority = "+Thread.currentThread().getPriority() + "  Current Thread ID: "
                        + Thread.currentThread().getId());

//                while (!Thread.currentThread().isInterrupted()) {
//                    boolean error = false;
//                    Exception ex = null;
//                    try {
                        // In some cases it's make sense to run this method in a separate thread.
                        // For example if you want to give some time to the last worker thread to complete
                        // before interrupting it from repeatable worker
                        int successCount = 0;
                        int failCount = 0;
                        for (FileInput fileInput : fileInputs) {
                            log.info("Parsing started for record " + fileInput.getId());
                            PhoneNumberRestRequest request = new PhoneNumberRestRequest();
                            request.setMobileNumber(fileInput.getPhoneNumber());
                            PhoneNumberRestResponse response = null;
                            try {
                                log.info("Parsing started");
                                response = phoneNumberUtility.phoneNumberSearch(request);
                            } catch (Exception e) {
                                failCount++;
                            }

                            if (response != null) {

                                if (response.getCode() == 200) {
                                    successCount++;
                                    fileInput.setStatus(response.getBody());
                                }
                            } else {
                                fileInput.setStatus("N/A");
                            }
                            fileInputRepository.save(fileInput);
                            log.info("Successfully parsed file");
//                    fileDetail.setSuccessCount(successCount);
//                    fileDetail.setFailureCount(failCount);
//                    fileDetail.setStatus(FileStatus.PARSED.getValue());
//                    fileDetailRepository.save(fileDetail);
                        }
//                    }catch (Exception e)
//                    {
//                        ex = e;
//                    }
//
//                    if (Thread.currentThread().isInterrupted()) {
//                        System.out.println("worker was interrupted");
//                        // just exit as last task was interrupted
//                        continue;
//                    }
//
//                    if (!error) {
//                        System.out.println("worker task was finished normally");
//                    } else {
//                        System.out.println("worker task was finished due to error " + ex.getMessage() +" thread name = "+Thread.currentThread().getName());
//                    }
//                }
//                System.out.println("repeatable task was finished");
               }
            };
    }

    public Callable<String> serachPhonenumberCallable(List<FileInput> fileInputs, FileDetail fileDetail){
        return new Callable<String>() {
            @Override
            public String call() {
                log.info("Running Thread Name: = "+ Thread.currentThread().getName() +"  priority = "+Thread.currentThread().getPriority() + "  Current Thread ID: "
                        + Thread.currentThread().getId());
                int successCount = 0;
                int failCount = 0;
                boolean retry = false;
                do {
                    try {
                for (FileInput fileInput : fileInputs) {
                    log.info("Parsing started for record " + fileInput.getId());
                    PhoneNumberRestRequest request = new PhoneNumberRestRequest();
                    request.setMobileNumber(fileInput.getPhoneNumber());
                    PhoneNumberRestResponse response = null;
                    try {
                        log.info("Parsing started");
                        response = phoneNumberUtility.phoneNumberSearch(request);
                    } catch (Exception e) {
                        failCount++;
                    }

                    if (response != null) {

                        if (response.getCode() == 200) {
                            successCount++;
                            fileInput.setStatus(response.getBody());
                        }
                    } else {
                        fileInput.setStatus("N/A");
                    }
//                    try {
                        fileInputRepository.save(fileInput);
//                    }
//                    catch (Exception e)
//                    {
//                        log.info("Parsing failed for record " + fileInput.getId());
                }
                } catch (Exception e) { //timeout, network failure exceptions
                            log.error("Exception in running thread: "
                                    + Thread.currentThread().getName() + ", restarting job");
                            retry = true;
                        }
                        log.info("Successfully parsed file");

//                    fileDetail.setSuccessCount(successCount);
//                    fileDetail.setFailureCount(failCount);
//                    fileDetail.setStatus(FileStatus.PARSED.getValue());
//                    fileDetailRepository.save(fileDetail);
                    } while (retry);

                    log.info("Successfully parsed file");

//                    fileDetail.setSuccessCount(successCount);
//                    fileDetail.setFailureCount(failCount);
//                    fileDetail.setStatus(FileStatus.PARSED.getValue());
//                    fileDetailRepository.save(fileDetail);
                return Thread.currentThread().getName();
            }
        };
    }

    private <T> List<List<T>> splitListIntoChunks(List<T> searchData,int chunkSize){
        List<List<T>> chunks=new ArrayList<>();
        for(int i=0;i<searchData.size();i+=chunkSize){
            int endIndex=Math.min(i +chunkSize,searchData.size());
            List<T> chunk=searchData.subList(i,endIndex);
            chunks.add(chunk);
        }
        return chunks;
    }



}



