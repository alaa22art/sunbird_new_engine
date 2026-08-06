package com.certacure.lis.interfaces.repo;

import org.springframework.stereotype.Repository;

import com.certacure.core.base.repo.GenericRepository;
import com.certacure.lis.interfaces.entities.LkpPrintFormat;

@Repository("LkpPrintFormatRepo")
public interface LkpPrintFormatRepo extends GenericRepository<LkpPrintFormat> {

}
