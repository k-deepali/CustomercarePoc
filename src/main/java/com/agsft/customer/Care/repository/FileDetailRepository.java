package com.agsft.customer.Care.repository;

import com.agsft.customer.Care.model.FileDetail;
import com.agsft.customer.Care.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FileDetailRepository extends JpaRepository<FileDetail,Long> {

    Optional<FileDetail> findByName(String name);

    //@Query(nativeQuery = true, value = "select * from file_detail where id=:id")
    public Optional<FileDetail> findById(Long id);

    // @Query(value = "select * from file_detail where name like %:searchText% ", nativeQuery = true)
    Page<FileDetail> findByNameContaining(@Param("searchText") String searchText, Pageable pageable);

    //  @Query(value = "select * from file_detail where status =:status", nativeQuery = true)
    Page<FileDetail> findByStatus(@Param("status") String status, Pageable pageable);

    //   @Query(value = "select * from file_detail where name like %:searchText% ", nativeQuery = true)
    //
    Page<FileDetail> findByNameLikeAndUser(String name, User user, Pageable pageable);

    Page<FileDetail> findByStatusLikeAndUser(String status, User user, Pageable pageable);

    Page<FileDetail> findAllByUser(Pageable pageable, User user);

    public List<FileDetail> findByCreatedAtBetweenAndUserAndStatusLike(LocalDate startDate, LocalDate endDate, User user, String status);
}
