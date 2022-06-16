package com.ski.tournament.repository;

import com.ski.tournament.model.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitRepository extends JpaRepository<Unit,Integer> {

    Unit findByfullName(String fullName);
}

