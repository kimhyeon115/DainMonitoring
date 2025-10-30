package com.dain23.management.service;

import java.util.Map;

public interface SystemService {
	
	Map<String, Object> getDmsSettingFrame();
	
	Map<String, Object> getMoveAndBackupFrame();
	
	Map<String, Object> getDataDeleteFrame();
	
}
