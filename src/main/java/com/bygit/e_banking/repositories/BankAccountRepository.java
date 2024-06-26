package com.bygit.e_banking.repositories;

import com.bygit.e_banking.entities.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccount,String> {
}

