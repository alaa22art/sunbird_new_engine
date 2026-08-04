package com.certacure.lis.interfaces.admin.repo;



import org.springframework.stereotype.Repository;


import com.certacure.core.base.repo.GenericRepository;
import com.certacure.lis.interfaces.admin.model.SystemSetting;


/**
 * SystemSetting.java
 * 
 * @author Ala'a Himour <ahimour@optimiza.me>
 * @since Mar/13/20222
 */
@Repository("SystemSettingRepo")
public interface SystemSettingRepo extends GenericRepository<SystemSetting> {

}
