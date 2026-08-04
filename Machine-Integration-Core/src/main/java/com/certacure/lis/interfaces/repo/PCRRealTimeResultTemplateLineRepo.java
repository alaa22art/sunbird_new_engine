package com.certacure.lis.interfaces.repo;

import org.springframework.stereotype.Repository;

import com.certacure.core.base.repo.GenericRepository;
import com.certacure.lis.interfaces.entities.PCRRealTimeResultTemplateLine;

@Repository("PCRRealTimeResultTemplateLineRepo")
public interface PCRRealTimeResultTemplateLineRepo extends GenericRepository<PCRRealTimeResultTemplateLine> {

}
