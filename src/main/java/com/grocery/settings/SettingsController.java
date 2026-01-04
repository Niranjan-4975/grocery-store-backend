package com.grocery.settings;

import com.grocery.common.ApiResponse;
import com.grocery.reports.DashBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/settings")
public class SettingsController {

    @Autowired
    private DashBoardService dashBoardService;

    @GetMapping
    public ResponseEntity<ApiResponse<AdminSettings>> getSettings() {
        AdminSettings settings = dashBoardService.getSettings();
        return ResponseEntity.ok(ApiResponse.success(null,settings));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<AdminSettings>> updateSettings(
            @RequestBody AdminSettings settings){
        AdminSettings upsettings = dashBoardService.saveSettings(settings);
        return ResponseEntity.ok(ApiResponse.success("System settings updated successfully!", upsettings));
    }
}
