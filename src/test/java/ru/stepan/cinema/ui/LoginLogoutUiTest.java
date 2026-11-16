package ru.stepan.cinema.ui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import ru.stepan.cinema.AbstractIntegrationTest;
import ru.stepan.cinema.service.UserService;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LoginLogoutUiTest extends AbstractIntegrationTest {

    private static final String EMAIL = "ui.user@example.com";
    private static final String PASSWORD = "secret123";

    @LocalServerPort
    private int port;
    @Autowired
    private UserService userService;

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUpBrowser() {
        userService.createUser("Ui", "User", EMAIL, "City", PASSWORD);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--window-size=1280,800");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void successfulLogin_showsHomePage() {
        logIn(EMAIL, PASSWORD);

        wait.until(ExpectedConditions.urlContains("/pages/films"));
        assertThat(driver.findElement(By.tagName("h1")).getText()).isEqualTo("Film List");
        assertThat(driver.findElements(By.cssSelector("form[action$='/logout'] button"))).hasSize(1);
    }

    @Test
    void logout_returnsToLoginPage() {
        logIn(EMAIL, PASSWORD);
        wait.until(ExpectedConditions.urlContains("/pages/films"));

        driver.findElement(By.cssSelector("form[action$='/logout'] button")).click();

        wait.until(ExpectedConditions.urlContains("/login"));
        assertThat(driver.getPageSource()).contains("Вы вышли из системы");
    }

    @Test
    void wrongPassword_staysOnLoginWithError() {
        logIn(EMAIL, "wrong-password");

        wait.until(ExpectedConditions.urlContains("error"));
        assertThat(driver.getPageSource()).contains("Неверный email или пароль");
    }

    @Test
    void protectedPageWithoutLogin_redirectsToLogin() {
        driver.get(url("/pages/films"));

        wait.until(ExpectedConditions.urlContains("/login"));
        assertThat(driver.findElements(By.name("username"))).hasSize(1);
    }

    private void logIn(String email, String password) {
        driver.get(url("/login"));
        driver.findElement(By.name("username")).sendKeys(email);
        driver.findElement(By.name("password")).sendKeys(password);
        driver.findElement(By.cssSelector("button[type='submit']")).click();
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }
}
