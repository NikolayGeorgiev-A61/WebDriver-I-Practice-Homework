package sausedemotests;

import core.BaseTests;
import org.example.BrowserTypes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class ProductTests extends BaseTests {

    //Checkout and order constants
    public static final String XPATH_ORDER_FINISH_BTN = "//button[@data-test='finish']";
    public static final String XPATH_ORDER_COMPLETED_LOGO = "//div[@id='checkout_complete_container']//img[@class='pony_express']";
    public static final String XPATH_SHOPCART_ITEM_COUNTER = "//a[@class='shopping_cart_link']//span[@class='shopping_cart_badge']";

    @BeforeEach
    public void setup(){
        driver = startBrowser(BrowserTypes.FIREFOX);
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        driver.get(BASE_URL);

        authenticateWithUser(STANDARD_USER_CREDENTIALS, PASSWORD);
    }

    @Test
    public void addProduct_by_name(){
        var product = getProductByTitle("Sauce Labs Backpack");
        product.findElement(By.className("btn_inventory")).click();
    }

    @Test
    public void findAllProducts(){
        var productList = getAllProducts();
    }

    @ParameterizedTest //this won't run with @BeforeAll
    @CsvSource({
            "Sauce Labs Backpack, Sauce Labs Fleece Jacket",
            "Sauce Labs Onesie, Sauce Labs Bolt T-Shirt",
            "Sauce Labs Fleece Jacket, Sauce Labs Bike Light"
    })
    public void productAddedToShoppingCart_when_addToCart(String product1,String product2){
        add2ProductsToCart(product1, product2);
        goToShoppingCart();

        //assert
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(String.format(XPATH_SHOP_CART_ITEM, product1))));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(String.format(XPATH_SHOP_CART_ITEM, product2))));
        var cartItem1 = getCartProductByTitle(product1);
        var cartItem2 = getCartProductByTitle(product2);
        Assertions.assertEquals(product1, cartItem1);
        Assertions.assertEquals(product2, cartItem2);

        //cleanup
        remove2ProductsFromCart(product1, product2);
        logout();
    }

    @ParameterizedTest
    @CsvSource({
            "Bate, Pesho, Azkaban",
            "Bai, Gosho, Tutrakan",
            "a, a, 189503"
    })

    public void userDetailsAdded_when_checkoutWithValidInformation(String firstName, String lastName, String postCode){
        add2ProductsToCart("Sauce Labs Backpack", "Sauce Labs Fleece Jacket"); //hardcoded because prev test covers cart combinations (tried adding it to the parameters at line 66-68 and 71, but it was just more bloat)
        goToShoppingCart();
        goToCheckout();
        //fill user info
        fillUserInfoAtCheckout(firstName, lastName, postCode);
        //go summary
        goToCheckoutSummary();
        //verify summary
        verifyCheckoutSummary();

        //cleanup
        driver.get("https://www.saucedemo.com/cart.html");
        remove2ProductsFromCart("Sauce Labs Backpack", "Sauce Labs Fleece Jacket");
        logout();
    }

    @Test
    public void orderCompleted_when_addProduct_and_checkout_withConfirm(){
        add2ProductsToCart("Sauce Labs Backpack","Sauce Labs Fleece Jacket");
        goToShoppingCart();
        goToCheckout();
        fillUserInfoAtCheckout("Bai", "Gosho", "Tutrakan"); //hardcoded because prev parameterized test covers this functionality
        goToCheckoutSummary();
        verifyCheckoutSummary();
        //complete order
        WebElement finishBtn = driver.findElement(By.xpath(XPATH_ORDER_FINISH_BTN));
        wait.until(ExpectedConditions.elementToBeClickable(finishBtn));
        finishBtn.click();
        //verify orderCompleted page
        WebElement greenTickLogo = driver.findElement(By.xpath(XPATH_ORDER_COMPLETED_LOGO));
        wait.until(ExpectedConditions.visibilityOf(greenTickLogo));

        //no badge element (the one counting items in cart) means cart is empty
        List<WebElement> shopCart = driver.findElements(By.xpath(XPATH_SHOPCART_ITEM_COUNTER));
        Assertions.assertTrue(shopCart.isEmpty());
        logout();
    }

//    @Test //evil path (you can check out without items)
//    public void expressCheckout(){
//        goToShoppingCart();
//        goToCheckout();
//        fillUserInfoAtCheckout("l", "o", "l");
//        goToCheckoutSummary();
//    }
}
