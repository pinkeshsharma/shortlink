package com.shortlink.db;

import com.shortlink.db.entity.DeadLetter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeadLetterRepository extends JpaRepository<DeadLetter, Long> {

}
