package com.example.bureau.services;

import com.example.bureau.models.Article;
import com.example.bureau.models.ArticleBureau;
import com.example.bureau.models.Bureau;
import com.example.bureau.models.Depot;
import com.example.bureau.repo.ArticleBureauRepo;
import com.example.bureau.repo.ArticleRepo;
import com.example.bureau.repo.DepotRepo;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Iterator;
import java.util.List;

@Service
public class ArticleService {
    private final ArticleRepo articleRepo;
    private final DepotRepo depotRepo;
    private final ArticleBureauRepo articleBureauRepo;

    public ArticleService(ArticleRepo articleRepo, DepotRepo depotRepo, ArticleBureauRepo articleBureauRepo) {
        this.articleRepo = articleRepo;
        this.depotRepo = depotRepo;
        this.articleBureauRepo = articleBureauRepo;
    }
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bureau";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private static final String INSERT_SQL = "INSERT INTO Article (name, code) VALUES (?, ?)";


    public void uploadAndInsertArticles(MultipartFile file) throws IOException, SQLException {
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

                Cell nameCell = row.getCell(0);
                Cell codeCell = row.getCell(1);

                if (nameCell == null || codeCell == null) {
                    continue; // Skip this row if any cell is null
                }

                String name = nameCell.getStringCellValue();
                String code = codeCell.getStringCellValue();

                // Check if article code already exists
                String selectSql = "SELECT id FROM article WHERE code = ?";
                try (PreparedStatement selectStatement = conn.prepareStatement(selectSql)) {
                    selectStatement.setString(1, code);
                    try (ResultSet resultSet = selectStatement.executeQuery()) {
                        if (resultSet.next()) {
                            continue; // Skip this row if article code exists
                        }
                    }
                }

                // Insert new article
                String insertSql = "INSERT INTO article (name, code) VALUES (?, ?)";
                try (PreparedStatement insertStatement = conn.prepareStatement(insertSql)) {
                    insertStatement.setString(1, name);
                    insertStatement.setString(2, code);
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



    public Article addArticle(Article article) {
        return articleRepo.save(article);
    }

    public Article findById(Long id) {
        return articleRepo.findById(id).orElse(null);
    }
    public Article findByCode(String code) {
        return articleRepo.findByCode(code);
    }
    public List<Article> getAllArticles() {
        return articleRepo.findAll();
    }

    public Article updateArticle(Article article) {
        return articleRepo.save(article);
    }

    public void addArticleToBureauGeneral(Article article, Bureau bureau, int quantity) throws IllegalArgumentException {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        ArticleBureau targetArticleBureau = articleBureauRepo.findByArticleAndBureau(article, bureau);
        int targetDepotQuantity = targetArticleBureau != null ? targetArticleBureau.getQuantity() : 0;

        if (targetArticleBureau == null) {
            targetArticleBureau = new ArticleBureau();
            targetArticleBureau.setArticle(article);
            targetArticleBureau.setBureau(bureau);
        }
        targetArticleBureau.setQuantity(targetDepotQuantity + quantity);
        targetArticleBureau.setArticleBureauId(article.getId(), bureau.getId());
        articleBureauRepo.save(targetArticleBureau);
    }
    public void addArticleToBureauAboutDepot(Article article, Bureau bureau, Depot depot, int quantity) throws IllegalArgumentException {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        ArticleBureau targetArticleBureau = articleBureauRepo.findByArticleAndBureau(article, bureau);
        int targetArticleBureauQuantity = targetArticleBureau != null ? targetArticleBureau.getQuantity() : 0;

        if (targetArticleBureau == null) {
            targetArticleBureau = new ArticleBureau();
            targetArticleBureau.setArticle(article);
            targetArticleBureau.setBureau(bureau);
        }
        targetArticleBureau.addQuantity(quantity);
        targetArticleBureau.setArticleBureauId(article.getId(), bureau.getId());
        articleBureauRepo.save(targetArticleBureau);

        // Update the depot quantity
       /* int updatedDepotQuantity = depot.getQuantity() + targetArticleBureauQuantity;
        depot.setQuantity(updatedDepotQuantity);
        depotRepo.save(depot);*/
    }



}
