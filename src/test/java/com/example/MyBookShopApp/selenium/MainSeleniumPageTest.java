package com.example.MyBookShopApp.selenium;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class MainSeleniumPageTest {
    private static ChromeDriver driver;
    private final static String BASE_URL = "http://localhost:8085/";
    private final static List<String> BASE_URL_LIST = List.of(
            "http://localhost:8085/",
            "http://localhost:8085/genres",
            "http://localhost:8085/books/recent",
            "http://localhost:8085/books/popular",
            "http://localhost:8085/authors"
    );

    @BeforeAll
    static void beforeAll() {
        System.setProperty("webdriver.chrome.driver", "C:/Users/schli/OneDrive/Desktop/ChromeDriver/chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @AfterAll
    static void afterAll() {
        driver.quit();
    }

    @Test
    public void testBookShopBaseUrlsAndSubUrls() throws InterruptedException {
        BASE_URL_LIST.forEach(url -> {
            try {
                testBaseUrlAndGetSubUrlsForTesting(url);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void testBaseUrlAndGetSubUrlsForTesting(String url) throws InterruptedException {
        try {
            driver.get(url);
            testPageContent(url);

            Thread.sleep(1000);

            List<String> urlList = driver.findElements(By.tagName("a"))
                    .stream()
                    .map(webElement -> webElement.getAttribute("href"))
                    .filter(Objects::nonNull)
                    .filter(webElementUrl -> !webElementUrl.equals(url))
                    .filter(this::isValidTestingUrl)
                    .distinct()
                    .collect(Collectors.toList());

            urlList.forEach(webElementUrl -> {
                driver.get(webElementUrl);
                testPageContent(webElementUrl);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void testPageContent(String url) {
        String pageTitle = driver.getTitle();
        switch (url) {
            case "http://localhost:8085/" -> assertEquals("Main Page", pageTitle);
            case "http://localhost:8085/genres" -> assertEquals("Genres", pageTitle);
            case "http://localhost:8085/books/recent" -> assertEquals("Recent Books", pageTitle);
            case "http://localhost:8085/books/popular" -> assertEquals("Popular Books", pageTitle);
            case "http://localhost:8085/authors" -> {
                assertEquals("Authors", pageTitle);
                assertTrue(driver.getPageSource().contains("Cramb Adaline"));
            }
            case "http://localhost:8085/tags/detective" -> {
                String title = driver.findElementByXPath("/html/body/div/div/main/div/div[1]/h1").getText();
                assertEquals("Tags", pageTitle);
                assertEquals("DETECTIVE", title);
            }
        }
    }

    private boolean isValidTestingUrl(String webElementUrl) {
        return webElementUrl.startsWith(BASE_URL) &&
            !webElementUrl.contains("/api") &&
            !webElementUrl.contains("#") &&
            !webElementUrl.endsWith(".html") &&
            !webElementUrl.endsWith("postponed") &&
            !webElementUrl.endsWith("cart") &&
            !webElementUrl.endsWith("signup") &&
            !webElementUrl.endsWith("signin") &&
            !webElementUrl.endsWith("github") &&
            !webElementUrl.endsWith("google") &&
            !BASE_URL_LIST.contains(webElementUrl);
    }
}
