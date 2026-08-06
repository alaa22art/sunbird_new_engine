package com.sunbird.lis.interfaces.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sunbird.core.base.service.GenericService;
import com.sunbird.lis.interfaces.entities.AppointmentEntity;
import com.sunbird.lis.interfaces.repo.AppointmentRepo;

@Service("AppointmentService")
public class AppointmentService extends GenericService<AppointmentEntity, AppointmentRepo>{
	
	@Autowired
	private AppointmentRepo repo;

	@Override
	protected AppointmentRepo getRepository() {
		// TODO Auto-generated method stub
		return repo;
	}

	public void addAppointment(AppointmentEntity appointment) {
		getRepository().save(appointment);
		
	}
	
	
	public List <AppointmentEntity> getAppointment(AppointmentEntity appointment) {
		return getRepository().checkIfAppointmentExists(appointment.getAppointmentId().toString() , appointment.getPatientCode().toString(), appointment.getNationalId().toString());
	
	}

}
