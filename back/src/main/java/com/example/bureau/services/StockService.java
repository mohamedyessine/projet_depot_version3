package com.example.bureau.services;

import com.example.bureau.models.ArticleBureau;
import com.example.bureau.models.Bureau;
import com.example.bureau.models.Depot;
import com.example.bureau.payload.request.ArticleWithQuantities;
import com.example.bureau.payload.request.ArticleWithQuantity;
import com.example.bureau.repo.ArticleBureauRepo;
import com.example.bureau.repo.BureauRepo;
import com.example.bureau.repo.DepotRepo;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class StockService {
    private final ArticleBureauRepo articleBureauRepo;
    private final DepotService depotService;
    private final DepotRepo depotRepo;

    private final BureauRepo bureauRepo;
    private final BureauService bureauService;

    public StockService(ArticleBureauRepo articleBureauRepo, DepotService depotService, DepotRepo depotRepo, BureauRepo bureauRepo, BureauService bureauService) {
        this.articleBureauRepo = articleBureauRepo;
        this.depotService = depotService;
        this.depotRepo = depotRepo;
        this.bureauRepo = bureauRepo;
        this.bureauService = bureauService;
    }

    public List<ArticleBureau> getAllStock() {
        return articleBureauRepo.findAll();
    }
    public List<ArticleBureau> getArticleByBureauId(Long bureauId) {
        return articleBureauRepo.findAllByBureau_Id(bureauId);
    }

    public void exportArticlesWithQuantityToExcelByDepotId(Long depotId, HttpServletResponse response) throws IOException, SQLException {
        Depot depot = depotRepo.findById(depotId).orElse(null);
        if (depot == null) {
            // Handle the case when depot is not found
            return;
        }

        List<ArticleWithQuantity> articlesWithQuantity = depotService.findAllArticlesWithQuantityByDepotId(depotId);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Articles");

        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Article Name");
        headerRow.createCell(1).setCellValue("Quantity");

        // Populate data rows
        int rowIndex = 1;
        for (ArticleWithQuantity articleWithQuantity : articlesWithQuantity) {
            Row dataRow = sheet.createRow(rowIndex++);
            dataRow.createCell(0).setCellValue(articleWithQuantity.getArticle().getName());
            dataRow.createCell(1).setCellValue(articleWithQuantity.getQuantity());
        }

        // Set response headers
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + depot.getName() + ".xlsx");

        // Write the workbook to the response stream
        try (OutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            // Handle the exception appropriately
            throw e;
        }
    }

    public void exportArticlesWithQuantityAndDefectToExcelByDepotId(Long depotId, HttpServletResponse response) throws IOException, SQLException {
        Depot depot = depotRepo.findById(depotId).orElse(null);
        if (depot == null) {
            // Handle the case when depot is not found
            return;
        }

        List<ArticleWithQuantities> articlesWithQuantity = depotService.findAllArticlesWithQuantityAndQuantityDefectByDepotId(depotId);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Articles");

        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Article Name");
        headerRow.createCell(1).setCellValue("Quantity");
        headerRow.createCell(2).setCellValue("Quantity Defectieux");

        // Populate data rows
        int rowIndex = 1;
        for (ArticleWithQuantities articleWithQuantity : articlesWithQuantity) {
            Row dataRow = sheet.createRow(rowIndex++);
            dataRow.createCell(0).setCellValue(articleWithQuantity.getArticle().getName());
            dataRow.createCell(1).setCellValue(articleWithQuantity.getQuantity());
            dataRow.createCell(2).setCellValue(articleWithQuantity.getQuantityDefect());

        }

        // Set response headers
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + depot.getName() + ".xlsx");

        // Write the workbook to the response stream
        try (OutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            // Handle the exception appropriately
            throw e;
        }
    }

    public void exportArticlesWithQuantityToExcelByBureauId(Long bureauId, HttpServletResponse response) throws IOException {
        Bureau bureau = bureauRepo.findById(bureauId).orElse(null);
        if (bureau == null) {
            // Handle the case when bureau is not found
            return;
        }

        List<ArticleBureau> articleBureaux = articleBureauRepo.findByBureauId(bureauId);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Articles");

        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Bureau Name");
        headerRow.createCell(1).setCellValue("Article Name");
        headerRow.createCell(2).setCellValue("Quantity");
        headerRow.createCell(3).setCellValue("Quantity Defectieux");


        // Populate data rows
        int rowIndex = 1;
        for (ArticleBureau articleBureau : articleBureaux) {
            Row dataRow = sheet.createRow(rowIndex++);
            dataRow.createCell(0).setCellValue(bureau.getName());
            dataRow.createCell(1).setCellValue(articleBureau.getArticle().getName());
            dataRow.createCell(2).setCellValue(articleBureau.getQuantity());
            dataRow.createCell(3).setCellValue(articleBureau.getQuantityDefect());

        }

        // Set response headers
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + bureau.getName() + ".xlsx");

        // Write the workbook to the response stream
        try (OutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            // Handle the exception appropriately
            throw e;
        }
    }


    public void exportsArticlesWithQuantityToExcelByDepotId(Long depotId, HttpServletResponse response) throws IOException, SQLException {
        Depot depot = depotRepo.findById(depotId).orElse(null);
        if (depot == null) {
            // Handle the case when depot is not found
            return;
        }

        List<ArticleWithQuantity> articlesWithQuantity = depotService.findAllArticlesWithQuantityByDepotId(depotId);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Articles");

        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Depot Name");
        headerRow.createCell(1).setCellValue("Article Name");
        headerRow.createCell(2).setCellValue("Quantity");

        // Populate data rows
        int rowIndex = 1;
        for (ArticleWithQuantity articleWithQuantity : articlesWithQuantity) {
            Row dataRow = sheet.createRow(rowIndex++);
            dataRow.createCell(0).setCellValue(depot.getName());
            dataRow.createCell(1).setCellValue(articleWithQuantity.getArticle().getName());
            dataRow.createCell(2).setCellValue(articleWithQuantity.getQuantity());
        }

        // Set response headers
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + depot.getName() + ".xlsx");

        // Write the workbook to the response stream
        try (OutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            // Handle the exception appropriately
            throw e;
        }
    }

    public void exportsAllArticlesWithQuantityToExcelByAllDepotId(HttpServletResponse response) throws IOException, SQLException {
        List<Depot> depots = depotRepo.findAll();
        if (depots.isEmpty()) {
            // Handle the case when no depots are found
            return;
        }

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Articles");

        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Depot Name");
        headerRow.createCell(1).setCellValue("Article Name");
        headerRow.createCell(2).setCellValue("Quantity");

        // Populate data rows
        int rowIndex = 1;
        for (Depot depot : depots) {
            List<ArticleWithQuantity> articlesWithQuantity = depotService.findAllArticlesWithQuantityByDepotId(depot.getId());
            if (articlesWithQuantity.isEmpty()) {
                // Skip the depot if it doesn't have any articles
                continue;
            }

            for (ArticleWithQuantity articleWithQuantity : articlesWithQuantity) {
                Row dataRow = sheet.createRow(rowIndex++);
                dataRow.createCell(0).setCellValue(depot.getName());
                dataRow.createCell(1).setCellValue(articleWithQuantity.getArticle().getName());
                dataRow.createCell(2).setCellValue(articleWithQuantity.getQuantity());
            }
        }

        // Set response headers
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=Inventaire " + getCurrentDateAsString() + ".xlsx");

        // Write the workbook to the response stream
        try (OutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            // Handle the exception appropriately
            throw e;
        }
    }

    public void exportsAllArticlesWithQuantityAndQuantityDefectToExcelByAllDepotId(HttpServletResponse response) throws IOException, SQLException {
        List<Depot> depots = depotRepo.findAll();
        if (depots.isEmpty()) {
            // Handle the case when no depots are found
            return;
        }

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Articles");

        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Depot Name");
        headerRow.createCell(1).setCellValue("Article Name");
        headerRow.createCell(2).setCellValue("Quantity");
        headerRow.createCell(3).setCellValue("Quantity Defectieux");


        // Populate data rows
        int rowIndex = 1;
        for (Depot depot : depots) {
            List<ArticleWithQuantities> articlesWithQuantity = depotService.findAllArticlesWithQuantityAndQuantityDefectByDepotId(depot.getId());
            if (articlesWithQuantity.isEmpty()) {
                // Skip the depot if it doesn't have any articles
                continue;
            }

            for (ArticleWithQuantities articleWithQuantity : articlesWithQuantity) {
                Row dataRow = sheet.createRow(rowIndex++);
                dataRow.createCell(0).setCellValue(depot.getName());
                dataRow.createCell(1).setCellValue(articleWithQuantity.getArticle().getName());
                dataRow.createCell(2).setCellValue(articleWithQuantity.getQuantity());
                dataRow.createCell(3).setCellValue(articleWithQuantity.getQuantityDefect());

            }
        }

        // Set response headers
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=Inventaire " + getCurrentDateAsString() + ".xlsx");

        // Write the workbook to the response stream
        try (OutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            // Handle the exception appropriately
            throw e;
        }
    }
    private String getCurrentDateAsString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date currentDate = new Date();
        return dateFormat.format(currentDate);
    }




}
