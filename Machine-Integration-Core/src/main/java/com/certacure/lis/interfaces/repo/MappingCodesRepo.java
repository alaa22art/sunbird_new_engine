package com.certacure.lis.interfaces.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.certacure.core.base.repo.GenericRepository;
import com.certacure.lis.interfaces.entities.MappingCodes;

@Repository("MappingCodesRepo")
public interface MappingCodesRepo extends GenericRepository<MappingCodes> {

	@Query("select mc from MappingCodes mc where mc.vistaItemCode = :vistaItemCode")
	List<MappingCodes> getCertaItemCodeByVistaItemCode(@Param("vistaItemCode") String fT1_7_TransactionCode);

	@Query("select mc from MappingCodes mc where mc.vistaItemCode = :vistaItemCode and mc.section = :section")
	List<MappingCodes> getCertaItemCodeByVistaItemCodeAndServSection(@Param("vistaItemCode") String fT1_7_TransactionCode,
			@Param("section") String obr24_DiagnosticServSectID);

	
}
