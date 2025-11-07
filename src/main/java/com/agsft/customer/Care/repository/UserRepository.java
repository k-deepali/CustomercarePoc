package com.agsft.customer.Care.repository;

import com.agsft.customer.Care.model.Company;
import com.agsft.customer.Care.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    public Optional<User> findByEmail(String Email);
    public User findByIdAndCompany(Long id, Optional<Company> company);

    public Optional<User> findByPhoneNumber(String phoneNumber);
}
