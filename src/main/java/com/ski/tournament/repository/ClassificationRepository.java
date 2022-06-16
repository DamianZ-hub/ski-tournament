package com.ski.tournament.repository;

import com.ski.tournament.model.Classification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassificationRepository extends JpaRepository<Classification,Integer> {
}
