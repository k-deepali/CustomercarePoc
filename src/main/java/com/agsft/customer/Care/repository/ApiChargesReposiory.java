package com.agsft.customer.Care.repository;

import com.agsft.customer.Care.model.ApiCharges;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiChargesReposiory extends JpaRepository<ApiCharges,Long> {
    public ApiCharges   findByMonth(String month);
}
