package com.example.bureau.repo;

import com.example.bureau.models.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferRepo extends JpaRepository<Transfer,Long> {

}
