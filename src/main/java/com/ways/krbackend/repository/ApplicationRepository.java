package com.ways.krbackend.repository;

import com.ways.krbackend.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application,Integer> {
    Application getApplicationById(int appId);

}
