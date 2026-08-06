package com.sunbird.lis.interfaces.admin.repo;



import org.springframework.stereotype.Repository;

import com.sunbird.core.base.repo.GenericRepository;
import com.sunbird.lis.interfaces.admin.model.SystemSetting;


/**
 * SystemSetting.java
 * 
 * @author Ala'a Himour <ahimour@optimiza.me>
 * @since Mar/13/20222
 */
@Repository("SystemSettingRepo")
public interface SystemSettingRepo extends GenericRepository<SystemSetting> {

}
