package com.grocery.settings;

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
    public ResponseEntity<?> getSettings() {
        return ResponseEntity.ok(dashBoardService.getSettings());
    }

    @PutMapping
    public ResponseEntity<?> updateSettings(@RequestBody AdminSettings settings){
        return ResponseEntity.ok(dashBoardService.saveSettings(settings));
    }
}
