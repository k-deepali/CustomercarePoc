package com.agsft.customer.Care.service.impl;

import com.agsft.customer.Care.config.MessageConfiguration;
import com.agsft.customer.Care.constant.HttpStatusCodes;
import com.agsft.customer.Care.dto.request.LoginRequestDTO;
import com.agsft.customer.Care.dto.request.PaginationRequestUserDTO;
import com.agsft.customer.Care.dto.request.UserRequestDTO;
import com.agsft.customer.Care.dto.response.*;
import com.agsft.customer.Care.enums.FileStatus;
import com.agsft.customer.Care.enums.HttpErrorCode;
import com.agsft.customer.Care.enums.SortByConstant;
import com.agsft.customer.Care.exception.CustomerException;
import com.agsft.customer.Care.model.*;
import com.agsft.customer.Care.repository.CompanyRepository;
import com.agsft.customer.Care.repository.FileDetailRepository;
import com.agsft.customer.Care.repository.UserRepository;
import com.agsft.customer.Care.repository.UserTokenRepository;
import com.agsft.customer.Care.security.jwt.JwtTokenUtil;
import com.agsft.customer.Care.service.AdminService;
import com.agsft.customer.Care.service.UserService;
import com.agsft.customer.Care.util.CSVHelper;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    final static Logger log = LoggerFactory.getLogger(UserService.class);
    @Autowired
    UserRepository userRepository;
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Value("${file.location}")
    Path root;
    @Autowired
    UserTokenService userTokenService;
    @Autowired
    AdminService adminService;
    @Autowired
    private MessageConfiguration messageSource;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Value("${jwt.tokenValidity}")
    private Long jwTokenExpiry;
    @Autowired
    private UserTokenRepository userTokenRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private FileDetailRepository fileDetailRepository;
    @Autowired
    private CSVHelper csvHelper;

    public User registerUser(UserRequestDTO userRequestDTO) {
        // Check if the user already exists
        if (userRepository.findByEmail(userRequestDTO.getEmail()).isPresent()) {
            log.error(messageSource.messageSource().getMessage(HttpErrorCode.EMAIL_ALREADY_REGISTERED.getMessage(), null, null));
            throw new CustomerException(HttpErrorCode.EMAIL_ALREADY_REGISTERED.getCode(), messageSource.messageSource().getMessage(HttpErrorCode.EMAIL_ALREADY_REGISTERED.getMessage(), null, null));
        }
        if (userRepository.findByPhoneNumber(userRequestDTO.getPhoneNumber()).isPresent()) {
            log.error(messageSource.messageSource().getMessage(HttpErrorCode.PHONE_NO_ALREADY_REGISTERED.getMessage(), null, null));
            throw new CustomerException(HttpErrorCode.PHONE_NO_ALREADY_REGISTERED.getCode(), messageSource.messageSource().getMessage(HttpErrorCode.PHONE_NO_ALREADY_REGISTERED.getMessage(), null, null));
        }

        // Save the user in the database
        User user = new User();
        user.setFirstName(userRequestDTO.getFirstName());
        user.setLastName(userRequestDTO.getLastName());
        user.setEmail(userRequestDTO.getEmail());
        user.setUsername(userRequestDTO.getUsername());
        user.setPhoneNumber(userRequestDTO.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        Optional<Company> company = companyRepository.findById(userRequestDTO.getCompanyId());
        if (!company.isPresent()) {
            log.error(messageSource.messageSource().getMessage(HttpErrorCode.COMPANY_NOT_FOUND.getMessage(), null, null));
            throw new CustomerException(HttpErrorCode.COMPANY_NOT_FOUND.getCode(), messageSource.messageSource().getMessage(HttpErrorCode.COMPANY_NOT_FOUND.getMessage(), null, null));
        }
        user.setCompany(company.get());
        userRepository.save(user);
        log.info("User Registered Successfully");
        return user;
    }


    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        LoginResponseDTO loginResponseDTO = new LoginResponseDTO();
        log.info("user login is starting");
        Optional<User> user = userRepository.findByEmail(loginRequestDTO.getEmail());
        if (!user.isPresent()) {
            log.error(messageSource.messageSource().getMessage(HttpErrorCode.INVALID_MAIL.getMessage(), null, null));
            throw new CustomerException(HttpErrorCode.INVALID_MAIL.getCode(), messageSource.messageSource().getMessage(HttpErrorCode.INVALID_MAIL.getMessage(), null, null));
        }
        User currentUser = user.get();
        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), currentUser.getPassword())) {
            log.error(messageSource.messageSource().getMessage(HttpErrorCode.PASSWORD_NOT_MATCH.getMessage(), null, null));
            throw new CustomerException(HttpErrorCode.PASSWORD_NOT_MATCH.getCode(), messageSource.messageSource().getMessage(HttpErrorCode.PASSWORD_NOT_MATCH.getMessage(), null, null));
        }
        UserToken userToken = new UserToken();
        userToken.setToken(jwtTokenUtil.generateToken(currentUser));
        userToken.setCreatedAt(new Date());
        userToken.setExpirationTime(new Date(System.currentTimeMillis() + jwTokenExpiry * 1000));
        userToken.setUser(currentUser);
        userToken.setUpdatedAt(new Date());
        userTokenRepository.save(userToken);

        List<String> userRoles = new ArrayList<>();
        for (Role userRole : user.get().getRoles()) {
            userRoles.add(userRole.getName());
        }
        loginResponseDTO = modelMapper.map(currentUser, LoginResponseDTO.class);
        loginResponseDTO.setToken(userToken.getToken());
        loginResponseDTO.setRoles(userRoles);
        log.info("user login successfully");
        return loginResponseDTO;
    }

    @Override
    public ResponseDTO userLogout(HttpServletRequest httpServletRequest) {
        ResponseDTO responseDto = null;
        Optional<UserToken> userToken = userTokenService.getUserToken(httpServletRequest);
        if (!userToken.isPresent() || userToken == null) {
            log.error(messageSource.messageSource().getMessage(HttpErrorCode.NO_CONTENT.getMessage(), null, null));
            throw new CustomerException(HttpStatusCodes.NO_CONTENT.getValue(), HttpStatusCodes.NOT_FOUND.getReasonPhrase());
        } else {
            try {
                UserToken token = userToken.get();
                userTokenService.deleteToken(token);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        log.info("logout Successful");
        return responseDto;
    }

    /**
     * this api used for import csv file locally and store details in database
     */
    // @Async("ConcurrentTaskExecutor")
    @Override
    public UploadeFileResponseDTO uploadedFile(User user, MultipartFile file) throws IOException, InterruptedException {
        UploadeFileResponseDTO uploadFileResponse = new UploadeFileResponseDTO();
        BillFileResponseDto billFileResponseDto = new BillFileResponseDto();
        log.info("user uploaded file is starting");
        if (!CSVHelper.hasCSVFormat(file)) {
            log.error(messageSource.messageSource().getMessage(HttpErrorCode.FILE_INCORRECT.getMessage(), null, null));
            throw new CustomerException(HttpErrorCode.FILE_INCORRECT.getCode(), messageSource.messageSource().getMessage(HttpErrorCode.FILE_INCORRECT.getMessage(), null, null));
        }
        FileDetail fileDetail = new FileDetail();
        String[] fileName = file.getOriginalFilename().split(".csv");
        Timestamp currentTime = new Timestamp(new Date().getTime());
        List<FileInput> fileInputs;
        try {
            Files.copy(file.getInputStream(), root.resolve(fileName[0] + "_" + currentTime + ".csv"));
        } catch (Exception exception) {
            log.error(messageSource.messageSource().getMessage(HttpErrorCode.PATH_NOT_EXIIST.getMessage(), null, null));
            throw new CustomerException(HttpErrorCode.PATH_NOT_EXIIST.getCode(), messageSource.messageSource().getMessage(HttpErrorCode.PATH_NOT_EXIIST.getMessage(), null, null));
        }
        fileDetail.setName(fileName[0] + "_" + currentTime + ".csv");
        fileDetail.setPath(root.toString() + "/" + fileName[0] + "_" + currentTime + ".csv");
        fileDetail.setUser(user);
        fileDetail.setStatus(FileStatus.NEW.getValue());
        try {
            fileInputs = csvHelper.csvToFileDetail(file.getInputStream(), fileDetail);
        } catch (Exception exception) {
            log.error(messageSource.messageSource().getMessage(HttpErrorCode.INCORRECT_CSV.getMessage(), null, null));
            throw new CustomerException(HttpErrorCode.INCORRECT_CSV.getCode(), messageSource.messageSource().getMessage(HttpErrorCode.INCORRECT_CSV.getMessage(), null, null));
        }
        fileDetail.setFileInputs(fileInputs);
        fileDetailRepository.save(fileDetail);
        log.info("user uploaded file input data added successfully");
        uploadFileResponse = modelMapper.map(fileDetail, UploadeFileResponseDTO.class);
        uploadFileResponse.setNumberOfRecords(fileInputs.size());
        log.info("user uploaded file is successfully");
        adminService.parseCSV(fileDetail.getId());
        uploadFileResponse.setBillFileResponseDto(billFileResponseDto);
        return uploadFileResponse;
    }

    /**
     * Get the list of File by search sort pagination by filename or status based on user.
     * if sort parameter not provided it will consider default as filename in ascending
     *
     * @param user           contains for sort or seach data according to user.
     * @param requestUserDTO contains the request parameter for pagination
     * @return the content object containing list of File including pageable object
     */
    @Override
    public PaginationResponseUserDTO getAllUserUploadedDetails(User user, PaginationRequestUserDTO requestUserDTO) {
        PaginationResponseUserDTO responseUserDTO = new PaginationResponseUserDTO();
        log.info("generating user uploaded file list starting");
        int pageNo;
        int pageSize;
        Sort.Direction sortDirection;
        String sortBy;

        pageNo = Objects.isNull(requestUserDTO.getPageNo()) ? 0 : requestUserDTO.getPageNo();
        pageSize = Objects.isNull(requestUserDTO.getPageSize()) ? 10 : requestUserDTO.getPageSize();

        if (Objects.isNull(requestUserDTO.getSortDirection())) {
            sortDirection = Sort.Direction.ASC;
        } else {
            sortDirection = requestUserDTO.getSortDirection();
        }
        /* Set the sort by  file name or status at if null set the default as filename*/
        if (Objects.isNull(requestUserDTO.getSortBy())) {
            sortBy = SortByConstant.FILE_NAME.getName();
        } else if (requestUserDTO.getSortBy().equals(SortByConstant.FILE_NAME.getName())) {
            sortBy = SortByConstant.FILE_NAME.getName();
        } else if (requestUserDTO.getSortBy().equals(SortByConstant.STATUS.getName())) {
            sortBy = SortByConstant.STATUS.getName();
        } else {
            log.error(messageSource.messageSource().getMessage(HttpErrorCode.SORT_BY.getMessage(), null, null));
            throw new CustomerException(HttpErrorCode.SORT_BY.getCode(), messageSource.messageSource().getMessage(HttpErrorCode.SORT_BY.getMessage(), null, null));
        }

        Pageable pageableRequest = PageRequest.of(pageNo, pageSize, Sort.by(sortDirection, sortBy));
        Page<FileDetail> fileList = null;
        if (requestUserDTO.getSearchCriteriaDTO() == null) {
            fileList = fileDetailRepository.findAllByUser(pageableRequest, user);
        } else {
            if (requestUserDTO.getSearchCriteriaDTO().getCriteriaName().equalsIgnoreCase(SortByConstant.FILE_NAME.getName())) {
                fileList = fileDetailRepository.findByNameLikeAndUser("%" + requestUserDTO.getSearchCriteriaDTO().getSearchText() + "%", user, pageableRequest);
            }
            if (requestUserDTO.getSearchCriteriaDTO().getCriteriaName().equalsIgnoreCase(SortByConstant.STATUS.getName())) {
                fileList = fileDetailRepository.findByStatusLikeAndUser("%" + requestUserDTO.getSearchCriteriaDTO().getSearchText() + "%", user, pageableRequest);
            }
        }
        List<UploadedFileResponseDTO> list = new ArrayList<>();
        if (fileList.getContent() != null) {
            for (FileDetail fileDetail : fileList) {
                UploadedFileResponseDTO responseDTO = new UploadedFileResponseDTO();
                responseDTO.setId(fileDetail.getId());
                responseDTO.setName(fileDetail.getName());
                responseDTO.setSuccessCount(fileDetail.getSuccessCount());
                responseDTO.setFailureCount(fileDetail.getFailureCount());
                responseDTO.setStatus(fileDetail.getStatus());
                responseDTO.setPath(fileDetail.getPath());
                list.add(responseDTO);
            }
        }

        responseUserDTO.setCurrentPage(fileList.getNumber());
        responseUserDTO.setTotalPages(fileList.getTotalPages());
        responseUserDTO.setNumberOfElements((int) fileList.getTotalElements());
        responseUserDTO.setContent(list);
        log.info("user uploaded file list fetched successfully");
        return responseUserDTO;

    }

    @Override
    public User findUserDetail(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        User user = optionalUser.get();
        return user;
    }

//    @Override
//    public UploadeFileResponseDTO uploadedFile(User user, MultipartFile file) throws IOException {
////        UploadeFileResponseDTO uploadFileResponse = new UploadeFileResponseDTO();
////        BillFileResponseDto billFileResponseDto = new BillFileResponseDto();
//        log.info("user uploaded file is starting");
//        if (!CSVHelper.hasCSVFormat(file)) {
//            log.error(messageSource.messageSource().getMessage(HttpErrorCode.FILE_INCORRECT.getMessage(), null, null));
//            throw new CustomerException(HttpErrorCode.FILE_INCORRECT.getCode(), messageSource.messageSource().getMessage(HttpErrorCode.FILE_INCORRECT.getMessage(), null, null));
//        }
//        FileDetail fileDetail = new FileDetail();
//        String[] fileName = file.getOriginalFilename().split(".csv");
//        Timestamp currentTime = new Timestamp(new Date().getTime());
//        List<FileInput> fileInputs;
//        try {
//            Files.copy(file.getInputStream(), root.resolve(fileName[0] + "_" + currentTime + ".csv"));
//        } catch (Exception exception) {
//            log.error(messageSource.messageSource().getMessage(HttpErrorCode.PATH_NOT_EXIIST.getMessage(), null, null));
//            throw new CustomerException(HttpErrorCode.PATH_NOT_EXIIST.getCode(), messageSource.messageSource().getMessage(HttpErrorCode.PATH_NOT_EXIIST.getMessage(), null, null));
//        }
//        fileDetail.setName(fileName[0] + "_" + currentTime + ".csv");
//        fileDetail.setPath(root.toString() + "/" + fileName[0] + "_" + currentTime + ".csv");
//        fileDetail.setUser(user);
//        fileDetail.setStatus(FileStatus.NEW.getValue());
//        try {
//            fileInputs = csvHelper.csvToFileDetail(file.getInputStream(), fileDetail);
//        } catch (Exception exception) {
//            log.error(messageSource.messageSource().getMessage(HttpErrorCode.INCORRECT_CSV.getMessage(), null, null));
//            throw new CustomerException(HttpErrorCode.INCORRECT_CSV.getCode(), messageSource.messageSource().getMessage(HttpErrorCode.INCORRECT_CSV.getMessage(), null, null));
//        }
//        fileDetail.setFileInputs(fileInputs);
//        fileDetailRepository.save(fileDetail);
//        log.info("user uploaded file input data added successfully");
////        uploadFileResponse = modelMapper.map(fileDetail, UploadeFileResponseDTO.class);
////        uploadFileResponse.setNumberOfRecords(fileInputs.size());
//        log.info("user uploaded file is successfully");
////        billFileResponseDto = adminService.parseCSV(fileDetail.getId());
////        uploadFileResponse.setBillFileResponseDto(billFileResponseDto);
//
//        ExecutorService executorService= Executors.newCachedThreadPool();
//        System.out.println("Submitting the tasks for execution...");
//        for (FileInput fileInput:fileDetail.getFileInputs())
//        {
//            executorService.submit(new ExecutorServiceDemo(fileInput));
//        }
//        executorService.shutdown();
//        System.out.println("Thread main finished");
//        return null;
//    }
//    private Runnable createRunnable(Long fileId) {
//        Optional<FileDetail> fileDetailsOptional = fileDetailRepository.findById(fileId);
//        if (!fileDetailsOptional.isPresent()) {
//            log.error(messageSource.messageSource().getMessage(HttpErrorCode.FILE_NOT_FOUND.getMessage(), null, null));
//            throw new CustomerException(HttpErrorCode.FILE_NOT_FOUND.getCode(), messageSource.messageSource().getMessage(HttpErrorCode.FILE_NOT_FOUND.getMessage(), null, null));
//        }
//        if (fileDetailsOptional.get().getStatus().equalsIgnoreCase(FileStatus.COMPLETED.getValue())) {
//            log.error(messageSource.messageSource().getMessage(HttpErrorCode.FILE_ALREADY_PRESENT.getMessage(), null, null));
//            throw new CustomerException(HttpErrorCode.FILE_ALREADY_PRESENT.getCode(), messageSource.messageSource().getMessage(HttpErrorCode.FILE_ALREADY_PRESENT.getMessage(), null, null));
//        }
//        List<FileInput> fileInputs = fileInputRepository.findByFileDetailId(fileId);
//          Runnable aRunnable = new Runnable() {
//              @Override
//              public void run() {
//
//                int successCount = 0;
//                int failCount = 0;
//                for (FileInput fileInput : fileInputs) {
//                    PhoneNumberRestRequest request = new PhoneNumberRestRequest();
//                    request.setMobileNumber(fileInput.getPhoneNumber());
//                    PhoneNumberRestResponse response = null;
//                    try {
//                        log.info("Parsing started");
//                        response = phoneNumberUtility.phoneNumberSearch(request);
//                    } catch (Exception e) {
//                        failCount++;
//                    }
//
//                    if (response != null) {
//
//                        if (response.getCode() == 200) {
//                            successCount++;
//                            fileInput.setStatus(response.getBody());
//                        }
//                    } else {
//                        fileInput.setStatus("N/A");
//                    }
//                    fileInputRepository.save(fileInput);
//                }
//                log.info("Successfully parsed file");
//                fileDetailsOptional.get().setSuccessCount(successCount);
//                fileDetailsOptional.get().setFailureCount(failCount);
//                fileDetailsOptional.get().setStatus(FileStatus.PARSED.getValue());
//                fileDetailsOptional.get().setFileInputs(fileInputs);
//                fileDetailRepository.save(fileDetailsOptional.get());
//            }
//        };
//       // new Thread(aRunnable).start();
//        return aRunnable;
//    }


    @Override
    public void protectedPdf(MultipartFile file) {
        try{
            File name=new File("/home/agsuser/Downloads/"+file.getOriginalFilename());
            PDDocument pdd = PDDocument.load(name);

            // step 2.Creating instance of AccessPermission
            // class
            AccessPermission ap = new AccessPermission();

            // step 3. Creating instance of
            // StandardProtectionPolicy
            StandardProtectionPolicy stpp
                    = new StandardProtectionPolicy("abcd", "abcd", ap);

            // step 4. Setting the length of Encryption key
            stpp.setEncryptionKeyLength(128);

            // step 5. Setting the permission
            stpp.setPermissions(ap);

            // step 6. Protecting the PDF file
            pdd.protect(stpp);

            // step 7. Saving and closing the PDF Document
            pdd.save("/home/agsuser/"+file.getOriginalFilename());
            pdd.close();
        }catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }
}
