package com.hirecrux_backend.repository;

import com.hirecrux_backend.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
//Jpaexecutor for specification in search job...jpaspecificationExecutor<table>
public interface JobRepository extends JpaRepository<Job,Integer>, JpaSpecificationExecutor<Job> {

}
