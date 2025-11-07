package com.agsft.customer.Care.service;


import com.agsft.customer.Care.dto.request.CompanyRequestDTO;
import com.agsft.customer.Care.dto.request.PaginationRequestDTO;
import com.agsft.customer.Care.dto.request.RoleRequestDTO;
import com.agsft.customer.Care.dto.response.*;
import com.agsft.customer.Care.model.FileDetail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public interface AdminService {
    public CompanyResponseDTO addCompany(CompanyRequestDTO companyRequestDto);

    public RoleResponseDTO assignRoleToUser(String companyId, Long userId, RoleRequestDTO roleRequestDTO);

    // public BillCalculateResponseDto BillCalculateByUser(String companyId, Long userId, Date startDate,Date endDate);
    public void billCalculation(Long fileDetailId);

    public BillCalculateResponseDto billPdfList(String companyId, Long userId, Date startDate, Date endDate);

    FileDTO download(Long fileId) throws FileNotFoundException;

    PaginationResponseDTO getAllFiles(PaginationRequestDTO requestDTO);

    void parseCSV(Long fileId) throws IOException, InterruptedException;

    List<FileListResponseDTO> getFileDetailsList(Long fileId);

    File buildExcelFileDetailsRecord(List<FileListResponseDTO> userList) throws IOException;
}
