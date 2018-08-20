package com.cjmware.repo;

import com.cjmware.entities.ThresholdItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("ThresholdRepository")
public interface ThresholdRepository extends JpaRepository<ThresholdItem, Integer> {
}
