package tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import com.github.javafaker.Faker;
import config.ConfigurationCenter;
import helpers.Attach;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.openqa.selenium.remote.DesiredCapabilities;
import pages.AuthOrRegistrationModalPage;
import pages.MainPage;
import pages.ProductPage;

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static java.lang.String.format;

public class TestBase {
    Faker faker = new Faker();
    MainPage mainPage = new MainPage();
    ProductPage productPage = new ProductPage();
    AuthOrRegistrationModalPage authOrRegistrationModalPage = new AuthOrRegistrationModalPage();

    @BeforeAll
    static void setUp() {
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide());

        String selenoidLogin = System.getProperty("selenoidLogin", ConfigurationCenter.configRemote.selenoidLogin()),
                selenoidPassword = System.getProperty("selenoidPassword", ConfigurationCenter.configRemote.selenoidPassword()),
                browserSize = System.getProperty("browserSize", "1980x1024"),
                browserName = System.getProperty("browserName", "CHROME"),
                browserVersion = System.getProperty("browserVersion", "99"),
                fullSelenoidUrlWithLogPass = format("https://%s:%s@selenoid.autotests.cloud/wd/hub",
                        selenoidLogin, selenoidPassword),
                host = System.getProperty("host", "local");

        Configuration.baseUrl = ConfigurationCenter.configTestData.url();
        Configuration.browser = browserName;
        Configuration.browserVersion = browserVersion;
        Configuration.browserSize = browserSize;

        if (host.equals("remote")) {
            Configuration.remote = fullSelenoidUrlWithLogPass;

            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability("enableVNC", true);
            capabilities.setCapability("enableVideo", true);
            Configuration.browserCapabilities = capabilities;
        }
    }

    @AfterEach
    @DisplayName("Добавить аттачи")
    void addAttachments() {
        Attach.screenshotAs("Итоговый скрин");
        Attach.pageSource();
        Attach.browserConsoleLogs();
        Attach.addVideo();
        closeWebDriver();
    }
}
