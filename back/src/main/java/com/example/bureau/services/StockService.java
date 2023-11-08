package com.example.bureau.services;

import com.example.bureau.models.ArticleBureau;
import com.example.bureau.models.Bureau;
import com.example.bureau.models.Depot;
import com.example.bureau.payload.request.ArticleWithQuantities;
import com.example.bureau.payload.request.ArticleWithQuantity;
import com.example.bureau.repo.ArticleBureauRepo;
import com.example.bureau.repo.BureauRepo;
import com.example.bureau.repo.DepotRepo;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.MediaType;
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
    String image1Filename = "https://res.cloudinary.com/applicationsportif/image/upload/v1688652832/9ba4a_udxqer.jpg";
    String image2Filename = "https://res.cloudinary.com/applicationsportif/image/upload/v1688652920/dgi_pjpwh7.png";
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

   /* public void exportArticlesWithQuantityAndDefectToPDFByDepotId(Long depotId, HttpServletResponse response) throws IOException, DocumentException, SQLException {
        Depot depot = depotRepo.findById(depotId).orElse(null);
        if (depot == null) {
            // Handle the case when depot is not found
            return;
        }

        List<ArticleWithQuantities> articlesWithQuantity = depotService.findAllArticlesWithQuantityAndQuantityDefectByDepotId(depotId);

        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());

        // Set response headers
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=" + depot.getName() + ".pdf");

        document.open();

        // Create a new PDF table with 3 columns
        PdfPTable table = new PdfPTable(3);

        // Set column widths
        table.setWidths(new float[]{3, 2, 2});

        // Add table headers
        table.addCell("Article Name");
        table.addCell("Quantity");
        table.addCell("Quantity Defectieux");

        // Populate data rows
        for (ArticleWithQuantities articleWithQuantity : articlesWithQuantity) {
            table.addCell(articleWithQuantity.getArticle().getName());
            table.addCell(String.valueOf(articleWithQuantity.getQuantity()));
            table.addCell(String.valueOf(articleWithQuantity.getQuantityDefect()));
        }

        // Add the table to the document
        document.add(table);

        // Close the document
        document.close();
    }*/

    public void exportArticlesWithQuantityAndDefectToPDFByDepotId(Long depotId, HttpServletResponse response) throws IOException, DocumentException, SQLException {
        Depot depot = depotRepo.findById(depotId).orElse(null);
        if (depot == null) {
            // Handle the case when depot is not found
            return;
        }

        List<ArticleWithQuantities> articlesWithQuantity = depotService.findAllArticlesWithQuantityAndQuantityDefectByDepotId(depotId);

        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());

        // Set response headers
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=" + depot.getName() + ".pdf");

        document.open();

        addTitle(document, image1Filename, image2Filename, "Inventaire de " + depot.getName());

        // Create a new PDF table with 3 columns
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100); // Set table width to 100% of the document

        // Set column widths
        table.setWidths(new float[]{2, 2, 2});

        // Add table headers
        PdfPCell headerCell = new PdfPCell();
        headerCell.setBackgroundColor(BaseColor.GRAY);
        headerCell.setPhrase(new Phrase("Article", new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE)));
        table.addCell(headerCell);

        headerCell.setPhrase(new Phrase("Quantité", new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE)));
        table.addCell(headerCell);

        headerCell.setPhrase(new Phrase("Quantité défectueuse", new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE)));
        table.addCell(headerCell);

        // Populate data rows
        PdfPCell dataCell = new PdfPCell();
        dataCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        dataCell.setPadding(5);
        dataCell.setNoWrap(true);

        for (ArticleWithQuantities articleWithQuantity : articlesWithQuantity) {
            dataCell.setPhrase(new Phrase(articleWithQuantity.getArticle().getName(), new Font(Font.FontFamily.HELVETICA, 8)));
            table.addCell(dataCell);

            dataCell.setPhrase(new Phrase(String.valueOf(articleWithQuantity.getQuantity()), new Font(Font.FontFamily.HELVETICA, 8)));
            table.addCell(dataCell);

            dataCell.setPhrase(new Phrase(String.valueOf(articleWithQuantity.getQuantityDefect()), new Font(Font.FontFamily.HELVETICA, 8)));
            table.addCell(dataCell);
        }

        // Add the table to the document
        document.add(table);

        addFooterSignature(document, getCurrentDateAsString(), "---------------------------");

        // Close the document
        document.close();
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
        headerRow.createCell(0).setCellValue("Bureau");
        headerRow.createCell(1).setCellValue("Article");
        headerRow.createCell(2).setCellValue("Quantité");
        headerRow.createCell(3).setCellValue("Quantité défectueuse");


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




    public void exportArticlesWithQuantityToPDFByBureauId(Long bureauId, HttpServletResponse response) throws IOException, DocumentException {
        Bureau bureau = bureauRepo.findById(bureauId).orElse(null);
        if (bureau == null) {
            // Handle the case when bureau is not found
            return;
        }

        List<ArticleBureau> articleBureaux = articleBureauRepo.findByBureauId(bureauId);

        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());

        // Set response headers
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=" + bureau.getName() + ".pdf");

        document.open();
        addTitle(document, image1Filename, image2Filename, "Inventaire de " + bureau.getName());

        // Create a new PDF table with 3 columns (Article, Quantité, Défectueux)
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);

        // Set column widths
        table.setWidths(new float[]{7, 3, 3});

        // Add table headers with color
        PdfPCell headerCell = new PdfPCell();
        headerCell.setBackgroundColor(BaseColor.GRAY);

        headerCell.setPhrase(new Phrase("Article", new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE)));
        table.addCell(headerCell);

        headerCell.setPhrase(new Phrase("Quantité", new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE)));
        table.addCell(headerCell);

        headerCell.setPhrase(new Phrase("Défectueux", new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE)));
        table.addCell(headerCell);

        // Populate data rows with styled cells
        PdfPCell dataCell = new PdfPCell();
        dataCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        dataCell.setPadding(5);
        dataCell.setNoWrap(true);

        for (ArticleBureau articleBureau : articleBureaux) {
//            dataCell.setPhrase(new Phrase(bureau.getName(), new Font(Font.FontFamily.HELVETICA, 8)));
//            table.addCell(dataCell);

            dataCell.setPhrase(new Phrase(articleBureau.getArticle().getName(), new Font(Font.FontFamily.HELVETICA, 8)));
            table.addCell(dataCell);

            dataCell.setPhrase(new Phrase(String.valueOf(articleBureau.getQuantity()), new Font(Font.FontFamily.HELVETICA, 8)));
            table.addCell(dataCell);

            dataCell.setPhrase(new Phrase(String.valueOf(articleBureau.getQuantityDefect()), new Font(Font.FontFamily.HELVETICA, 8)));
            table.addCell(dataCell);
        }

        // Add the table to the document
        document.add(table);

        addFooterSignature(document, getCurrentDateAsString(), "---------------------------");
        // Close the document
        document.close();
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
        headerRow.createCell(0).setCellValue("Depot");
        headerRow.createCell(1).setCellValue("Article");
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

  /*  public void exportsAllArticlesWithQuantityAndQuantityDefectToPDFByAllDepotId(HttpServletResponse response) throws IOException, DocumentException, SQLException {
        List<Depot> depots = depotRepo.findAll();
        if (depots.isEmpty()) {
            // Handle the case when no depots are found
            return;
        }

        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());

        // Set response headers
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=Inventaire " + getCurrentDateAsString() + ".pdf");

        document.open();

        // Create a new PDF table with 4 columns
        PdfPTable table = new PdfPTable(4);

        // Set column widths
        table.setWidths(new float[]{2, 3, 2, 2});

        // Add table headers
        table.addCell("Depot Name");
        table.addCell("Article Name");
        table.addCell("Quantity");
        table.addCell("Quantity Defectieux");

        // Populate data rows
        for (Depot depot : depots) {
            List<ArticleWithQuantities> articlesWithQuantity = depotService.findAllArticlesWithQuantityAndQuantityDefectByDepotId(depot.getId());
            if (articlesWithQuantity.isEmpty()) {
                // Skip the depot if it doesn't have any articles
                continue;
            }

            for (ArticleWithQuantities articleWithQuantity : articlesWithQuantity) {
                table.addCell(depot.getName());
                table.addCell(articleWithQuantity.getArticle().getName());
                table.addCell(String.valueOf(articleWithQuantity.getQuantity()));
                table.addCell(String.valueOf(articleWithQuantity.getQuantityDefect()));
            }
        }

        // Add the table to the document
        document.add(table);

        // Close the document
        document.close();
    }*/

    public void exportsAllArticlesWithQuantityAndQuantityDefectToPDFByAllDepotId(HttpServletResponse response) throws IOException, DocumentException, SQLException {
        List<Depot> depots = depotRepo.findAll();
        if (depots.isEmpty()) {
            // Handle the case when no depots are found
            return;
        }

        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());

        // Set response headers
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=Inventaire " + getCurrentDateAsString() + ".pdf");

        document.open();

        // Call the TitleService to add the title
        addTitle(document, image1Filename, image2Filename, "Inventaire Complet");

        // Create a new PDF table with 4 columns
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);

        // Set column widths
        table.setWidths(new float[]{8, 7, 3, 3, 3});

        // Add table headers with color
        PdfPCell headerCell = new PdfPCell();
        headerCell.setBackgroundColor(BaseColor.GRAY);

        headerCell.setPhrase(new Phrase("Dépôt", new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE)));
        table.addCell(headerCell);

        headerCell.setPhrase(new Phrase("Article", new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE)));
        table.addCell(headerCell);

        headerCell.setPhrase(new Phrase("Code", new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE)));
        table.addCell(headerCell);

        headerCell.setPhrase(new Phrase("Quantité", new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE)));
        table.addCell(headerCell);

        headerCell.setPhrase(new Phrase("Défectueux", new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE)));
        table.addCell(headerCell);

        // Populate data rows with styled cells
        PdfPCell dataCell = new PdfPCell();
        dataCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        dataCell.setPadding(5);
        dataCell.setNoWrap(true);

        for (Depot depot : depots) {
            List<ArticleWithQuantities> articlesWithQuantity = depotService.findAllArticlesWithQuantityAndQuantityDefectByDepotId(depot.getId());
            if (articlesWithQuantity.isEmpty()) {
                // Skip the depot if it doesn't have any articles
                continue;
            }

            for (ArticleWithQuantities articleWithQuantity : articlesWithQuantity) {
                dataCell.setPhrase(new Phrase(depot.getName(), new Font(Font.FontFamily.HELVETICA, 8)));
                table.addCell(dataCell);

                dataCell.setPhrase(new Phrase(articleWithQuantity.getArticle().getName(), new Font(Font.FontFamily.HELVETICA, 8)));
                table.addCell(dataCell);

                dataCell.setPhrase(new Phrase(articleWithQuantity.getArticle().getCode(), new Font(Font.FontFamily.HELVETICA, 8)));
                table.addCell(dataCell);

                dataCell.setPhrase(new Phrase(String.valueOf(articleWithQuantity.getQuantity()), new Font(Font.FontFamily.HELVETICA, 8)));
                table.addCell(dataCell);

                dataCell.setPhrase(new Phrase(String.valueOf(articleWithQuantity.getQuantityDefect()), new Font(Font.FontFamily.HELVETICA, 8)));
                table.addCell(dataCell);
            }
        }

        // Add the table to the document
        document.add(table);

        // Call the SignatureService to add the footer signature
        addFooterSignature(document, getCurrentDateAsString(), "---------------------------");

        // Close the document
        document.close();
    }

//    public static void addTitle(Document document, String image1Filename, String image2Filename, String titleText) throws DocumentException, IOException {
//        // Create a table for the current date
//        PdfPTable dateTable = new PdfPTable(1);
//        dateTable.setWidthPercentage(100);
//        PdfPCell dateCell = new PdfPCell();
//        dateCell.setBorder(PdfPCell.NO_BORDER);
//        Paragraph dateParagraph = new Paragraph();
//        dateParagraph.setAlignment(Element.ALIGN_RIGHT);
//        dateParagraph.add(new Chunk("Date: " + new SimpleDateFormat("yyyy-MM-dd").format(new Date()), new Font(Font.FontFamily.HELVETICA, 8)));
//        dateCell.addElement(dateParagraph);
//        dateTable.addCell(dateCell);
//        document.add(dateTable);
//
//
//        // Create a title paragraph with blue color
//        Paragraph title = new Paragraph();
//        title.setAlignment(Element.ALIGN_CENTER);
//        title.setSpacingAfter(30f); // Add spacing after the title
//
//        // Create a nested table to hold the two images and the title
//        PdfPTable titleTable = new PdfPTable(3);
//        titleTable.setWidthPercentage(100);
//
//        PdfPCell imageCell1 = new PdfPCell();
//        imageCell1.setBorder(PdfPCell.NO_BORDER);
//        Image image1 = Image.getInstance(image1Filename); // Replace with the path to your image1
//        image1.scaleToFit(50, 50);
//        imageCell1.addElement(image1);
//
//        PdfPCell titleCell = new PdfPCell();
//        titleCell.setBorder(PdfPCell.NO_BORDER);
//        Chunk titleTextChunk = new Chunk(titleText, new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.BLACK));
//        titleCell.addElement(titleTextChunk);
//
//        PdfPCell imageCell2 = new PdfPCell();
//        imageCell2.setBorder(PdfPCell.NO_BORDER);
//        Image image2 = Image.getInstance(image2Filename); // Replace with the path to your image2
//        image2.scaleToFit(50, 50);
//        image2.setAlignment(Element.ALIGN_RIGHT);
//        imageCell2.addElement(image2);
//
//        // Add the image and title cells to the title table
//        titleTable.addCell(imageCell1);
//        titleTable.addCell(titleCell);
//        titleTable.addCell(imageCell2);
//
//        // Add the title table to the title paragraph
//        title.add(titleTable);
//
//        // Add spacing after the title paragraph
//        title.setSpacingAfter(30f);
//
//        // Add the title paragraph to the document
//        document.add(title);
//    }
public static void addTitle(Document document, String image1Filename, String image2Filename, String titleText) throws DocumentException, IOException {
    // Create a table for the current date
    PdfPTable dateTable = new PdfPTable(1);
    dateTable.setWidthPercentage(100);
    PdfPCell dateCell = new PdfPCell();
    dateCell.setBorder(PdfPCell.NO_BORDER);
    Paragraph dateParagraph = new Paragraph();
    dateParagraph.setAlignment(Element.ALIGN_RIGHT);
    dateParagraph.add(new Chunk("Date: " + new SimpleDateFormat("yyyy-MM-dd").format(new Date()), new Font(Font.FontFamily.HELVETICA, 8)));
    dateCell.addElement(dateParagraph);
    dateTable.addCell(dateCell);
    document.add(dateTable);

    // Create a title paragraph with blue color
    Paragraph title = new Paragraph();
    title.setAlignment(Element.ALIGN_CENTER);
    title.setSpacingAfter(30f); // Add spacing after the title

    // Create a nested table to hold the two images and the title
    PdfPTable titleTable = new PdfPTable(5); // 5 columns

    PdfPCell imageCell1 = new PdfPCell();
    imageCell1.setBorder(PdfPCell.NO_BORDER);
    Image image1 = Image.getInstance(image1Filename); // Replace with the path to your image1
    image1.scaleToFit(50, 50);
    imageCell1.addElement(image1);
    imageCell1.setColspan(1); // 1/5 of the width

    PdfPCell titleCell = new PdfPCell();
    titleCell.setBorder(PdfPCell.NO_BORDER);
    Chunk titleTextChunk = new Chunk(titleText, new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.BLACK));
    titleCell.addElement(titleTextChunk);
    titleCell.setColspan(3); // 3/5 of the width
    titleCell.setHorizontalAlignment(Element.ALIGN_CENTER); // Align content to the center

    PdfPCell imageCell2 = new PdfPCell();
    imageCell2.setBorder(PdfPCell.NO_BORDER);
    Image image2 = Image.getInstance(image2Filename); // Replace with the path to your image2
    image2.scaleToFit(50, 50);
    imageCell2.addElement(image2);
    imageCell2.setColspan(1); // 1/5 of the width

    // Add the cells to the title table
    titleTable.addCell(imageCell1);
    titleTable.addCell(titleCell);
    titleTable.addCell(imageCell2);

    // Add the title table to the title paragraph
    title.add(titleTable);

    // Add spacing after the title paragraph
    title.setSpacingAfter(30f);

    // Add the title paragraph to the document
    document.add(title);
}
    public static void addFooterSignature(Document document, String date, String signature) throws DocumentException {
        // Add a section for the signature space at the end of the document
        Paragraph signatureSpace = new Paragraph();
        signatureSpace.setAlignment(Element.ALIGN_LEFT);

        // Add a few newlines to push the signature to the bottom
        for (int i = 0; i < 2; i++) {
            signatureSpace.add(Chunk.NEWLINE);
        }

        // Add "CRCISfax" with formatting
        Chunk crcisfaxLabel = new Chunk("CRCISfax", new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL));
        signatureSpace.add(crcisfaxLabel);

        signatureSpace.add(Chunk.NEWLINE);

        // Add "Date" with formatting
        Chunk dateLabel = new Chunk("Nom et prénom      : ---------------------------");
        dateLabel.setFont(new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL));
        signatureSpace.add(dateLabel);

        signatureSpace.add(Chunk.NEWLINE);

        // Add "Signature" with formatting
        Chunk signatureLabel = new Chunk("Signature               : " + signature, new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL));
        signatureSpace.add(signatureLabel);

        // Add the signature space to the document
        document.add(signatureSpace);
    }
    private String getCurrentDateAsString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date currentDate = new Date();
        return dateFormat.format(currentDate);
    }





}
