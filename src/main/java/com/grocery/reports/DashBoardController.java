package com.grocery.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
public class DashBoardController {

    @Autowired
    private DashBoardService dashBoardService;

    @GetMapping("/stats")
    public ResponseEntity<?> getDashboardStas(
            @RequestParam(defaultValue = "7") String range,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return ResponseEntity.ok(dashBoardService.getStats(range,startDate, endDate));
    }

    @GetMapping("/reports/export-full")
    public ResponseEntity<byte[]> exportFullPdf(
            @RequestParam(defaultValue = "7") String range,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) throws Exception {

        // 1. Fetch the same data used by the dashboard
        DashBoardDTO stats = dashBoardService.getStats(range, startDate, endDate);

        // 2. Generate PDF with charts
        byte[] pdfContent =dashBoardService.generateFullAnalysisPdf(stats);
        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=Full_Analysis_Report.pdf")
                .body(pdfContent);
    }

}
