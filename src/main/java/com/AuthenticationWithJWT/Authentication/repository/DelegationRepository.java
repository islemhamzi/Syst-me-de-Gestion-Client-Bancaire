package com.AuthenticationWithJWT.Authentication.repository;

import com.AuthenticationWithJWT.Authentication.entities.Delegation;
import com.AuthenticationWithJWT.Authentication.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DelegationRepository extends JpaRepository<Delegation, Long> {
    List<Delegation> findByDelegateAndStartDateLessThanEqualAndEndDateGreaterThanEqual(User delegate, LocalDate startDate, LocalDate endDate);
}
