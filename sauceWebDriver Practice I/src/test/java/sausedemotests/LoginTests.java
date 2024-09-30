package sausedemotests;

import core.BaseTests;
import org.example.BrowserTypes;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginTests extends BaseTests {

    @BeforeEach
    public void setup(){
        driver = startBrowser(BrowserTypes.FIREFOX);
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        driver.get(BASE_URL);
    }

    @Test
    public void login_with_lockedUser(){
        WebElement usernameField = driver.findElement(By.xpath(XPATH_USERNAME_FIELD));
        WebElement passwordField = driver.findElement(By.xpath(XPATH_PASS_FIELD));
        WebElement loginBtn = driver.findElement(By.xpath(XPATH_LOGIN_BTN));

        usernameField.sendKeys(LOCKED_USER_CREDENTIALS);
        passwordField.sendKeys(PASSWORD);
        loginBtn.click();

        //assert
        WebElement errorMessage = driver.findElement(By.xpath(XPATH_LOGIN_ERROR));
        var actualResult = errorMessage.getText();

        Assertions.assertEquals(ERROR_LOCKED_USER_MESSAGE, actualResult);
    }

    @ParameterizedTest
    @CsvSource({
            "standard_user, secret_sauce",
            "problem_user, secret_sauce",
            "error_user, secret_sauce",
            "visual_user, secret_sauce",
            //"performance_glitch_user, secret_sauce"
    })
    public void login(String username, String password) throws InterruptedException {

        WebElement usernameField = driver.findElement(By.xpath(XPATH_USERNAME_FIELD));
        WebElement passwordField = driver.findElement(By.xpath(XPATH_PASS_FIELD));
        WebElement loginBtn = driver.findElement(By.xpath(XPATH_LOGIN_BTN));

        usernameField.sendKeys(username);
        passwordField.sendKeys(password);
        loginBtn.click();

        //assert
        WebElement inventoryPageTitle = driver.findElement(By.xpath(XPATH_TITLE));
        var actualResult = inventoryPageTitle.getText();
        Assertions.assertEquals("Products", actualResult);

        logout();
    }
}
