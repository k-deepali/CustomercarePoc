package com.agsft.customer.Care.repository;

import com.agsft.customer.Care.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company,String> {
    public Company findByName(String name);
}
