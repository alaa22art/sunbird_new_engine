package com.sunbird.lis.interfaces.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sunbird.core.base.repo.GenericRepository;
import com.sunbird.lis.interfaces.entities.AppointmentEntity;


@Repository("AppointmentRepo")
public interface AppointmentRepo extends GenericRepository<AppointmentEntity> {
	
	@Query("select a from AppointmentEntity a where a.AppointmentId = :appointmentId and a.PatientCode = :PatientCode and a.NationalId = :nationalId")
	public List <AppointmentEntity> checkIfAppointmentExists(@Param("appointmentId") String appointmentId, @Param("PatientCode") String PatientCode, @Param("nationalId") String nationalId);

}
