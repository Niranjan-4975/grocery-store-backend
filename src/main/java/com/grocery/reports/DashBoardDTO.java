package com.grocery.reports;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class DashBoardDTO {

    private long totalSales;
    private long totalOrders;
    private double totalRevenue;
    private long pendingOrders;
    private List<LowStockDTO> lowStockProducts;
    private List<ChartDataDTO> salesTrend;
    private List<BestSellerDTO> bestSellers;

    @Data @AllArgsConstructor
    public static class LowStockDTO {
        private String name;
        private String category;
        private Integer stock;
    }

    @Data @AllArgsConstructor
    public static class ChartDataDTO {
        private String date;
        private Double amount;
    }

    @Data @AllArgsConstructor
    public static class BestSellerDTO {
        private String product;
        private Long soldQuantity;
    }
}
