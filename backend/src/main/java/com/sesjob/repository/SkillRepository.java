package com.sesjob.repository;

import com.sesjob.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

    Optional<Skill> findByName(String name);

    List<Skill> findByCategory(String category);

    @Query("SELECT s FROM Skill s WHERE s.name LIKE %:keyword% OR s.category LIKE %:keyword%")
    List<Skill> searchByKeyword(@Param("keyword") String keyword);

    boolean existsByName(String name);
}
