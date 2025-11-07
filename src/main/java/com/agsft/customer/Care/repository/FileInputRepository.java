package com.agsft.customer.Care.repository;

import com.agsft.customer.Care.dto.response.PhoneNumberRestResponse;
import com.agsft.customer.Care.model.FileInput;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FileInputRepository extends JpaRepository<FileInput,Long> {
    PhoneNumberRestResponse findByPhoneNumber(String PhoneNumber);

    //  @Query(nativeQuery = true, value = "select * from file_input where file_detail_id=:fileId")
    List<FileInput> findByFileDetailId(Long fileId);

    public List<FileInput> findByCreatedAtBetweenAndStatusNotLike(LocalDate startDate, LocalDate endDate, String status);

    public List<FileInput> findByCreatedAtBetween(LocalDate startDate, LocalDate endDate);
}
