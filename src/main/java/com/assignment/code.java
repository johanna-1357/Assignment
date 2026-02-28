package com.assignment;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.util.*;

public class code {

    public static void main(String[] args) {

        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        Map<String, Integer> wordCount = new HashMap<>();

        try {
            driver.get("https://elpais.com/");
            driver.manage().window().maximize();

            // Accept cookies
            try {
                WebElement accept = wait.until(
                        ExpectedConditions.elementToBeClickable(By.xpath("//button"))
                );
                accept.click();
            } catch (Exception ignored) {}

            // Open Opinion section
            WebElement opinion = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//a[contains(@href,'opinion')]"))
            );
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", opinion);

            // Collect first 5 article links
            List<WebElement> articles = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("article h2 a"))
            );

            List<String> links = new ArrayList<>();
            for (int i = 0; i < Math.min(5, articles.size()); i++) {
                links.add(articles.get(i).getAttribute("href"));
            }

            int index = 1;

            // Visit each article
            for (String link : links) {
                driver.get(link);

                WebElement article = wait.until(
                        ExpectedConditions.presenceOfElementLocated(By.tagName("article"))
                );

                String title = article.findElement(By.tagName("h1")).getText();

                System.out.println("\n==============================");
                System.out.println("ARTICLE " + index);
                System.out.println("TITLE (Spanish): " + title);
                System.out.println("CONTENT:");
                System.out.println(article.getText());

                try {
                    WebElement img = driver.findElement(By.cssSelector("figure img"));
                    String imgUrl = img.getAttribute("src");

                    if (imgUrl == null || imgUrl.isEmpty()) {
                        imgUrl = img.getAttribute("data-src");
                    }

                    if (imgUrl != null && !imgUrl.isEmpty()) {
                        downloadImage(imgUrl, "article_" + index + ".jpg");
                        System.out.println("Image saved: article_" + index + ".jpg");
                    } else {
                        System.out.println("No image found for article " + index);
                    }
                } catch (Exception e) {
                    System.out.println("No image found for article " + index);
                }

                // translation
                String translated = translateToEnglish(title);
                System.out.println("TITLE (English): " + translated);

                //word count
                for (String word : translated.toLowerCase().split("\\W+")) {
                    if (word.length() > 3) {
                        wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
                    }
                }

                index++;
            }

            //
            System.out.println("\n REPEATED WORDS (>2) ");
            for (Map.Entry<String, Integer> entry : wordCount.entrySet()) {
                if (entry.getValue() > 2) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
            }

        } finally {
            driver.quit();
        }
    }

    public static String translateToEnglish(String text) {
        try {
            URL url = new URL("https://libretranslate.com/translate");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String json = "{"
                    + "\"q\":\"" + text.replace("\"", "\\\"") + "\","
                    + "\"source\":\"es\","
                    + "\"target\":\"en\","
                    + "\"format\":\"text\""
                    + "}";

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)
            );

            String response = br.readLine();

            // Extract translatedText safely
            int start = response.indexOf(":\"") + 2;
            int end = response.lastIndexOf("\"");

            return response.substring(start, end);

        } catch (Exception e) {
            return "Translation failed";
        }
    }

    // downloading the images
    public static void downloadImage(String imageUrl, String fileName) throws IOException {
        try (InputStream in = new URL(imageUrl).openStream()) {
            Files.copy(in, new File(fileName).toPath(),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }
    }
}