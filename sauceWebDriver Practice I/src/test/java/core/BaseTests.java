package core;


import org.example.BrowserTypes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class BaseTests {
    public static final String BASE_URL = "https://www.saucedemo.com/";

    //login method constants (credentials and fields)
    public static final String XPATH_USERNAME_FIELD = "//input[@data-test='username']";
    public static final String XPATH_PASS_FIELD = "//input[@data-test='password']";
    public static final String XPATH_LOGIN_BTN = "//input[@data-test='login-button']";
    public static final String XPATH_TITLE = "//span[@data-test='title']";
    public static final String STANDARD_USER_CREDENTIALS = "standard_user";
    public static final String PASSWORD = "secret_sauce";
    //locked user constants
    public static final String LOCKED_USER_CREDENTIALS = "locked_out_user";
    public static final String XPATH_LOGIN_ERROR = "//h3[@data-test='error']";
    public static final String ERROR_LOCKED_USER_MESSAGE = "Epic sadface: Sorry, this user has been locked out.";
    //Add/remove products
    public static final String ADD_CART_BTN = "btn_inventory";
    public static final String XPATH_SHOP_CART_ITEM = "//div[@data-test='inventory-item' and descendant::div[text()='%s']]";
    public static final String XPATH_REMOVE_ITEM_BTN = "//div[@class='cart_item']//div[@class='inventory_item_name' and text()='%s']/ancestor::div[@class='cart_item']";
    public static final String REMOVE_ITEM_BTN = "cart_button";


    public static WebDriver driver;
    public static WebDriverWait wait;

    protected static WebDriver startBrowser(BrowserTypes browserTypes) {

        //Chrome, FF, Edge
        switch (browserTypes) {
            case CHROME:
                ChromeOptions chromeOptions = new ChromeOptions(); //may not be needed
                return new ChromeDriver(chromeOptions);
            case FIREFOX:
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                return new FirefoxDriver(firefoxOptions);
            case EDGE:
                EdgeOptions edgeOptions = new EdgeOptions();
                return new EdgeDriver(edgeOptions);
        }
        return null;
    }

    protected static void authenticateWithUser(String username, String password) {
        WebElement usernameField = driver.findElement(By.xpath(XPATH_USERNAME_FIELD));
        WebElement passwordField = driver.findElement(By.xpath(XPATH_PASS_FIELD));
        WebElement loginBtn = driver.findElement(By.xpath(XPATH_LOGIN_BTN));

        usernameField.sendKeys(username);
        passwordField.sendKeys(password);
        loginBtn.click();

        WebElement inventoryPageTitle = driver.findElement(By.xpath("//div[@class='app_logo']"));
        wait.until(ExpectedConditions.visibilityOf(inventoryPageTitle));
    }

    protected static void logout() {
        WebElement burgerBtn = driver.findElement(By.xpath("//div[@class='bm-burger-button']"));
        burgerBtn.click();

        WebElement logoutBtn = driver.findElement(By.xpath("//a[@data-test='logout-sidebar-link']"));
        wait.until(ExpectedConditions.elementToBeClickable(logoutBtn));
        logoutBtn.click();
    }

    @AfterEach
    public void afterTest() {
        if (driver != null) {
            driver.close();
        }
    }

    protected static WebElement getProductByTitle(String title) {
        return driver.findElement(By.xpath(String.format("//div[@class='inventory_item' and descendant::div[text()='%s']]", title)));
    }

    protected List<WebElement> getAllProducts() {
        return driver.findElements(By.xpath("//div[@data-test='inventory-item']"));
    }

    protected String getCartProductByTitle(String title) {
        WebElement productElement = driver.findElement(By.xpath(String.format("//div[@class='inventory_item_name' and text()='%s']", title)));
        return productElement.getText();
    }

    protected static void remove2ProductsFromCart(String titleProduct1, String titleProduct2){
        WebElement product1 = driver.findElement(By.xpath(String
                .format(XPATH_REMOVE_ITEM_BTN, titleProduct1)));
        product1.findElement(By.className(REMOVE_ITEM_BTN)).click();

        WebElement product2 = driver.findElement(By.xpath(String
                .format(XPATH_REMOVE_ITEM_BTN, titleProduct2)));
        product2.findElement(By.className(REMOVE_ITEM_BTN)).click();
    }


    //---Add 2 products to shopping cart
    protected static void add2ProductsToCart(String titleProduct1, String titleProduct2){
        var product1 = getProductByTitle(titleProduct1);
        product1.findElement(By.className(ADD_CART_BTN)).click();
        var product2 = getProductByTitle(titleProduct2);
        product2.findElement(By.className(ADD_CART_BTN)).click();
    }
    //---Go to shopping Cart
    protected static void goToShoppingCart(){
        WebElement shopCartBtn = driver.findElement(By.xpath("//a[@data-test='shopping-cart-link']"));
        wait.until(ExpectedConditions.elementToBeClickable(shopCartBtn));
        shopCartBtn.click();
    }
    //---Go to Checkout
    protected static void goToCheckout(){
        WebElement checkoutBtn = driver.findElement(By.xpath("//button[@data-test='checkout']")); //id=checkout
        wait.until(ExpectedConditions.elementToBeClickable(checkoutBtn));
        checkoutBtn.click();
    }
    //---Fill User Info
    protected static void fillUserInfoAtCheckout(String firstName, String lastName, String postCode){
        WebElement firstNameField = driver.findElement(By.xpath("//input[@data-test='firstName']"));
        WebElement lastNameField = driver.findElement(By.xpath("//input[@data-test='lastName']"));
        WebElement postalCodeField = driver.findElement(By.xpath("//input[@data-test='postalCode']"));

        firstNameField.sendKeys(firstName);
        lastNameField.sendKeys(lastName);
        postalCodeField.sendKeys(postCode);

    }
    //---Go to summary Page
    protected static void goToCheckoutSummary(){
        WebElement continueBtn = driver.findElement(By.xpath("//input[@data-test='continue']"));
        wait.until(ExpectedConditions.elementToBeClickable(continueBtn));
        continueBtn.click();
    }

    protected static void verifyCheckoutSummary(){
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@data-test='payment-info-label']")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@data-test='shipping-info-label']")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@data-test='total-info-label']")));
        //verify Finish button and that is clickable
        WebElement finishBtn = driver.findElement(By.xpath("//button[@data-test='finish']"));
        wait.until(ExpectedConditions.elementToBeClickable(finishBtn));
        Assertions.assertTrue(finishBtn.isEnabled());
    }
}
