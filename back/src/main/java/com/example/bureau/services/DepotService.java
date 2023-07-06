package com.example.bureau.services;


import com.example.bureau.models.*;
import com.example.bureau.payload.request.ArticleWithQuantities;
import com.example.bureau.payload.request.ArticleWithQuantity;
import com.example.bureau.payload.request.ArticleWithQuantityAndBureau;
import com.example.bureau.repo.ArticleBureauRepo;
import com.example.bureau.repo.ArticleRepo;
import com.example.bureau.repo.BureauRepo;
import com.example.bureau.repo.DepotRepo;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DepotService {

    private final DepotRepo depotRepo;
    private final BureauService bureauService;
    private final BureauRepo bureauRepo;
    private final ArticleBureauRepo articleBureauRepo;
    private final ArticleRepo articleRepo;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bureau";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private static final String INSERT_SQL = "INSERT INTO Depot (name, numero) VALUES (?, ?)";

    public DepotService(DepotRepo depotRepo, BureauService bureauService, BureauRepo bureauRepo, ArticleBureauRepo articleBureauRepo, ArticleRepo articleRepo) {
        this.depotRepo = depotRepo;
        this.bureauService = bureauService;
        this.bureauRepo = bureauRepo;
        this.articleBureauRepo = articleBureauRepo;
        this.articleRepo = articleRepo;
    }

    public Depot addDepot(Depot depot) {
        return depotRepo.save(depot);
    }

    public Depot findById(Long id) {
        return depotRepo.findById(id).orElse(null);
    }
    public Depot findByNumero(String numero) {
        return depotRepo.findByNumero(numero);
    }

    public List<Depot> getAllDepots() {
        return depotRepo.findAll();
    }
    public List<Depot> findDepotsByNameContaining(String name) {
        return depotRepo.findByNameContaining(name);
    }

    public List<Long> getListBureauIdsByDepotId(Long depotId) {
        Depot depot = depotRepo.findById(depotId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid depot ID: " + depotId));

        return depot.getBureaux()
                .stream()
                .map(Bureau::getId)
                .collect(Collectors.toList());
    }
    public List<Article> findAllArticlesByDepotId(Long depotId) {
        List<Long> bureauIds = getListBureauIdsByDepotId(depotId);

        Set<Long> articleIds = articleBureauRepo.findAllByBureau_IdIn(bureauIds)
                .stream()
                .map(ArticleBureau::getArticle)
                .map(Article::getId)
                .collect(Collectors.toSet());

        return articleRepo.findAllById(articleIds);
    }
    public List<ArticleBureau> findAllArticleByDepotId(Long depotId) {
        List<Long> bureauIds = getListBureauIdsByDepotId(depotId);

        return articleBureauRepo.findAllByBureau_IdIn(bureauIds);
    }




    public int getAllQte(Long depotId) {
        List<ArticleBureau> articleBureaus = findAllArticleByDepotId(depotId);

        int totalQuantity = articleBureaus.stream()
                .mapToInt(ArticleBureau::getQuantity)
                .sum();

        return totalQuantity;
    }


    public List<ArticleWithQuantity>findAllArticlesWithQuantityByDepotId(Long depotId) {
        List<ArticleBureau> articleBureaus = findAllArticleByDepotId(depotId);

        Map<Long, Integer> articleQuantityMap = new HashMap<>();

        for (ArticleBureau articleBureau : articleBureaus) {
            Long articleId = articleBureau.getArticle().getId();
            int quantity = articleBureau.getQuantity() + articleBureau.getQuantityDefect();

            if (articleQuantityMap.containsKey(articleId)) {
                quantity += articleQuantityMap.get(articleId);
            }

            articleQuantityMap.put(articleId, quantity);
        }

        List<ArticleWithQuantity> articlesWithQuantity = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : articleQuantityMap.entrySet()) {
            Long articleId = entry.getKey();
            int quantity = entry.getValue();

            Article article = articleRepo.findById(articleId).orElse(null);

            if (article != null) {
                ArticleWithQuantity articleWithQuantity = new ArticleWithQuantity(article, quantity);
                articlesWithQuantity.add(articleWithQuantity);
            }
        }

        return articlesWithQuantity;
    }

    public List<ArticleWithQuantities> findAllArticlesWithQuantityAndQuantityDefectByDepotId(Long depotId) {
        List<ArticleBureau> articleBureaus = findAllArticleByDepotId(depotId);

        Map<Long, Integer> articleQuantityMap = new HashMap<>();
        Map<Long, Integer> articleQuantityDefectMap = new HashMap<>();

        for (ArticleBureau articleBureau : articleBureaus) {
            Long articleId = articleBureau.getArticle().getId();
            int quantity = articleBureau.getQuantity();

            if (articleQuantityMap.containsKey(articleId)) {
                quantity += articleQuantityMap.get(articleId);
            }

            articleQuantityMap.put(articleId, quantity);

            int quantityDefect = articleBureau.getQuantityDefect();

            if (articleQuantityDefectMap.containsKey(articleId)) {
                quantityDefect += articleQuantityDefectMap.get(articleId);
            }

            articleQuantityDefectMap.put(articleId, quantityDefect);
        }

        List<ArticleWithQuantities> articlesWithQuantity = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : articleQuantityMap.entrySet()) {
            Long articleId = entry.getKey();
            int quantity = entry.getValue();
            int quantityDefect = articleQuantityDefectMap.getOrDefault(articleId, 0);

            Article article = articleRepo.findById(articleId).orElse(null);

            if (article != null) {
                ArticleWithQuantities articleWithQuantity = new ArticleWithQuantities(article, quantity, quantityDefect);
                articlesWithQuantity.add(articleWithQuantity);
            }
        }

        return articlesWithQuantity;
    }

    public List<ArticleWithQuantityAndBureau> findAllArticlesWithQuantityAndBureauByDepotId(Long depotId) {
        List<ArticleBureau> articleBureaus = findAllArticleByDepotId(depotId);

        Map<ArticleBureauId, Integer> articleQuantityMap = new HashMap<>();

        for (ArticleBureau articleBureau : articleBureaus) {
            ArticleBureauId articleBureauId = articleBureau.getId();
            int quantity = articleBureau.getQuantity();

            if (articleQuantityMap.containsKey(articleBureauId)) {
                quantity += articleQuantityMap.get(articleBureauId);
            }

            articleQuantityMap.put(articleBureauId, quantity);
        }

        List<ArticleWithQuantityAndBureau> articlesWithQuantityAndBureau = new ArrayList<>();

        for (Map.Entry<ArticleBureauId, Integer> entry : articleQuantityMap.entrySet()) {
            ArticleBureauId articleBureauId = entry.getKey();
            int quantity = entry.getValue();

            Article article = articleRepo.findById(articleBureauId.getArticleId()).orElse(null);
            Bureau bureau = bureauRepo.findById(articleBureauId.getBureauId()).orElse(null);

            if (article != null && bureau != null) {
                ArticleWithQuantity articleWithQuantity = new ArticleWithQuantity(article, quantity);
                ArticleWithQuantityAndBureau articleWithQuantityAndBureau = new ArticleWithQuantityAndBureau(articleWithQuantity, bureau);
                articlesWithQuantityAndBureau.add(articleWithQuantityAndBureau);
            }
        }

        return articlesWithQuantityAndBureau;
    }


    private List<Bureau> getBureauxByDepotId(Long depotId) {
        Depot depot = depotRepo.findById(depotId).orElse(null);
        if (depot != null) {
            return bureauService.getAllBureauByDepot(depot);
        }
        return Collections.emptyList(); // Return an empty list if depot not found
    }


    public void uploadAndInsertDepots(MultipartFile file) throws IOException, SQLException {
        InputStream inputStream = file.getInputStream();
        Workbook workbook = WorkbookFactory.create(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            conn.setAutoCommit(false);

            Iterator<Row> rowIterator = sheet.iterator();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                if (row.getRowNum() == 0) {
                    continue;
                }

                String name = row.getCell(0).getStringCellValue();
                String numero = row.getCell(1).getStringCellValue();

                // Check if depot already exists
                String selectSql = "SELECT id FROM depot WHERE numero = ?";
                try (PreparedStatement selectStatement = conn.prepareStatement(selectSql)) {
                    selectStatement.setString(1, numero);
                    try (ResultSet resultSet = selectStatement.executeQuery()) {
                        if (resultSet.next()) {
                            continue; // Skip this row if depot exists
                        }
                    }
                }

                // Insert new depot
                String insertSql = "INSERT INTO depot (name, numero) VALUES (?, ?)";
                try (PreparedStatement insertStatement = conn.prepareStatement(insertSql)) {
                    insertStatement.setString(1, name);
                    insertStatement.setString(2, numero);
                    insertStatement.executeUpdate();
                }
            }

            conn.commit();
        } catch (SQLException e) {
            throw e;
        } finally {
            workbook.close();
            inputStream.close();
        }
    }

    public void exportAllDepotsToExcel(String filePath) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Depots");

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Name");
            headerRow.createCell(2).setCellValue("Address");
            headerRow.createCell(3).setCellValue("Number");

            // Fetch all depots
            Iterable<Depot> depots = depotRepo.findAll();

            int rowNum = 1;
            for (Depot depot : depots) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(depot.getId());
                row.createCell(1).setCellValue(depot.getName());
                row.createCell(2).setCellValue(depot.getAdresse());
                row.createCell(3).setCellValue(depot.getNumero());
            }

            // Write the workbook to the file
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }

            System.out.println("Exported all depots to Excel successfully.");
        } catch (Exception e) {
            System.err.println("Error exporting depots to Excel: " + e.getMessage());
        }
    }





}
