import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

import java.net.MalformedURLException;
import java.net.URI;
import java.time.Duration;

public class BaseTest {

    protected static WebDriver driver;
    protected static WebDriverWait wait;
    protected String password = "";
    protected String email = "";
    protected String homeUrl = "";
    private final static int TIME = 7; // time to set up implicitlyWait for the browser


//    @BeforeSuite
//    protected static void setUpClass () {
//        WebDriverManager.chromedriver().setup();
//    }

    @BeforeMethod
    @Parameters ({"BaseUrl", "LoginEmail", "LoginPassword"})
    public void setUpBrowser (String BaseUrl, String LoginEmail, String LoginPassword) throws MalformedURLException{
        email = LoginEmail;
        password = LoginPassword;
        homeUrl = BaseUrl;
        driver = pickBrowser(System.getProperty("browser"));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(TIME));
        driver.manage().window().maximize();
    }
    private static WebDriver pickBrowser (String browser) throws MalformedURLException {
        DesiredCapabilities caps = new DesiredCapabilities();
        String gridUrl = "http://172.17.0.1:5555";
        System.out.println(browser);
        if (browser == null) {browser = "";}
        switch (browser) {
            case "firefox":
               WebDriverManager.firefoxdriver().setup();
               FirefoxOptions firefoxOptions = new FirefoxOptions();
               firefoxOptions.setBinary("/usr/bin/firefox/firefox");
               return driver = new FirefoxDriver(firefoxOptions);
            case "MicrosoftEdge":
                WebDriverManager.edgedriver().setup();
                return driver = new EdgeDriver();
            case "safari":
                WebDriverManager.safaridriver();
                return driver  = new SafariDriver();
            case "grid-firefox":
                caps.setCapability("browserName", "firefox");
                return driver = new RemoteWebDriver(URI.create(gridUrl).toURL(),caps);
            case "grid-edge":
                caps.setCapability("browserName", "MicrosoftEdge");
                return driver = new RemoteWebDriver(URI.create(gridUrl).toURL(),caps);
            case "grid-chrome":
                caps.setCapability("browserName", "chrome");
                return driver = new RemoteWebDriver(URI.create(gridUrl).toURL(),caps);
            default:
                WebDriverManager.chromedriver().setup();
                ChromeOptions options  = new ChromeOptions();
                options.addArguments("--disable-notifications", "--remote-allow-origins=*");
                return driver = new ChromeDriver(options);
        }
    }

    protected static void searchSong (String song){
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.main-scroll-wrap")));
        WebElement songEl = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[type = 'search']")));
        songEl.clear();
        String url = driver.getCurrentUrl();
        System.out.println("searching for " + song + " in " + url);
        songEl.sendKeys(song);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.results")));
    }

    protected static void viewAllSongs () {
        //view all results for songs
        WebElement searchResult = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("section.songs h1 button")
        ));
        searchResult.click();
        String url = driver.getCurrentUrl();
        System.out.println("searching for in " + url);
    }

    protected static void selectFirstSong () {
        WebElement songEl = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("#songResultsWrapper  tr > td.title")
        ));
        songEl.click();
    }

    protected String clickAddToPls (String playlist) {
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#songResultsWrapper button.btn-add-to"))).click();
        String xpathSelector = "//section[@id='songResultsWrapper']//li[contains(text(),'" + playlist + "')]";
        WebElement ourPlaylist = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpathSelector)));
        ourPlaylist.click();
        String msg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("div.success.show")
        )).getText();
        System.out.println(msg);
        return msg;
    }

    @AfterMethod
    public void closeBrowser () {
        driver.quit();
    }
}
