package com.sunbird.lis.interfaces.repo;

import org.springframework.stereotype.Repository;

import com.sunbird.core.base.repo.GenericRepository;
import com.sunbird.lis.interfaces.entities.PCRRealTimeResultTemplateLine;

@Repository("PCRRealTimeResultTemplateLineRepo")
public interface PCRRealTimeResultTemplateLineRepo extends GenericRepository<PCRRealTimeResultTemplateLine> {

}
