package com.certacure.lis.interfaces.repo;

import org.springframework.stereotype.Repository;

import com.certacure.core.base.repo.GenericRepository;
import com.certacure.lis.interfaces.entities.LkpPCRRealRimeWorkListStatus;

@Repository("LkpPCRRealRimeWorkListStatusRepo")
public interface LkpPCRRealRimeWorkListStatusRepo extends GenericRepository<LkpPCRRealRimeWorkListStatus> {

}
