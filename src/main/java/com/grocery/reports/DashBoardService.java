package com.grocery.reports;

import com.grocery.exception.ReportGenerationException;
import com.grocery.order.repository.OrderRepository;
import com.grocery.product.repository.ProductRepository;
import com.grocery.settings.AdminSettings;
import com.grocery.settings.AdminSettingsRepo;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashBoardService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductRepository productRepository;


    public DashBoardDTO getStats(String range, String start, String end){
        DashBoardDTO dto = new DashBoardDTO();

        //1. Basic Counts
        dto.setTotalOrders(orderRepository.countActiveOrders());
        dto.setPendingOrders(orderRepository.countPendingOrders());
        Double revenue = orderRepository.calculateTotalRevenue();
        dto.setTotalRevenue(revenue !=null ? revenue: 0.0);

        // 2. Low Stock (Directly from ProductRepository)
        // Query: SELECT * FROM products WHERE stock < 10 AND status = 'ACTIVE'
        dto.setLowStockProducts(productRepository.findByStockLessThan(10)
                .stream()
                .map(p -> new DashBoardDTO.LowStockDTO(p.getName(), p.getCategory().getName(), p.getStock()))
                .collect(Collectors.toList()));

        Integer days = null;
        if (!range.equals("custom")) {
            try {
                days = Integer.parseInt(range);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid range format: " + range);
            }
        }
        // 3. Sales Trend Mapping
        // Pass days/dates to Repository
        List<Object[]> trendData = (range.equals("custom"))
                ? orderRepository.getSalesTrendCustom(start, end)
                : orderRepository.getSalesTrend(days);
        dto.setSalesTrend(trendData.stream()
                .map(obj -> new DashBoardDTO.ChartDataDTO(obj[0].toString(), Double.parseDouble(obj[1].toString())))
                .collect(Collectors.toList()));

        // 4. Best Sellers Mapping
        List<Object[]> sellerData = orderRepository.getBestSellers();
        dto.setBestSellers(sellerData.stream()
                .map(obj -> new DashBoardDTO.BestSellerDTO(obj[0].toString(), Long.parseLong(obj[1].toString())))
                .collect(Collectors.toList()));

        // Total Sales count (Sum of quantities sold)
        dto.setTotalSales(dto.getBestSellers().stream().mapToLong(DashBoardDTO.BestSellerDTO::getSoldQuantity).sum());
        return dto;
    }

    //ADMIN: Export report
    public byte[] generateFullAnalysisPdf(DashBoardDTO stats){
        try(ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();

            // 1. Header Section
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, Color.BLUE);
            Paragraph title = new Paragraph("Business Performance Analysis", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("Generated on: " + java.time.LocalDate.now()));
            document.add(new Paragraph(" "));

            // 2. Summary Cards Table (Revenue, Orders, etc.)
            PdfPTable statsTable = new PdfPTable(4);
            statsTable.setWidthPercentage(100);
            addCell(statsTable, "Total Revenue", "₹" + stats.getTotalRevenue());
            addCell(statsTable, "Total Orders", String.valueOf(stats.getTotalOrders()));
            addCell(statsTable, "Items Sold", String.valueOf(stats.getTotalSales()));
            addCell(statsTable, "Pending", String.valueOf(stats.getPendingOrders()));
            document.add(statsTable);
            document.add(new Paragraph(" "));

            // 3. PIE CHART: Best Sellers Analysis
            document.add(new Paragraph("Best Selling Products Distribution", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
            DefaultPieDataset dataset = new DefaultPieDataset();
            stats.getBestSellers().forEach(s -> dataset.setValue(s.getProduct(), s.getSoldQuantity()));
            JFreeChart pieChart = ChartFactory.createPieChart("", dataset, true, true, false);
            document.add(chartToImage(pieChart, 500, 300));

            // 4. DATA TABLE: Low Stock Alerts
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Inventory Alerts (Low Stock)", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
            document.add(new Paragraph(""));
            PdfPTable lowStockTable = new PdfPTable(3);
            lowStockTable.setWidthPercentage(100);
            lowStockTable.addCell("Product");
            lowStockTable.addCell("Category");
            lowStockTable.addCell("Stock");

            for (DashBoardDTO.LowStockDTO p : stats.getLowStockProducts()) {
                lowStockTable.addCell(p.getName());
                lowStockTable.addCell(p.getCategory());
                lowStockTable.addCell(String.valueOf(p.getStock()));
            }
            document.add(lowStockTable);
            document.close();
            return out.toByteArray();
        }catch (Exception e){
            throw new ReportGenerationException("Failed to generate PDF report due to: " + e.getMessage(), e);
        }
    }

    // Helper: Convert JFreeChart to iText Image
    private Image chartToImage(JFreeChart chart, int width, int height) throws Exception {
        BufferedImage bufferedImage = chart.createBufferedImage(width, height);
        ByteArrayOutputStream chartOut = new ByteArrayOutputStream();
        javax.imageio.ImageIO.write(bufferedImage, "png", chartOut);
        Image image = Image.getInstance(chartOut.toByteArray());
        image.setAlignment(Element.ALIGN_CENTER);
        return image;
    }

    private void addCell(PdfPTable table, String header, String value) {
        PdfPCell cell = new PdfPCell(new Phrase(header + "\n" + value));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(10);
        table.addCell(cell);
    }


    @Autowired
    private AdminSettingsRepo settingsRepo;
    // ADMIN: get Settings
    public AdminSettings getSettings(){
        return settingsRepo.findById(1L).orElse(new AdminSettings());
    }

    public AdminSettings saveSettings(AdminSettings settings){
        settings.setId(1L);
        return settingsRepo.save(settings);
    }
}
