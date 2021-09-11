package com.bobisonfire

import com.bobisonfire.pages.GithubAuthPage
import com.bobisonfire.pages.LoginPage
import com.bobisonfire.utilities.ProvideWebDrivers
import com.bobisonfire.utilities.Utils
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.fail
import org.junit.jupiter.params.ParameterizedTest
import org.openqa.selenium.WebDriver
import java.io.FileInputStream
import java.util.*


class LoginPageTest {
    private lateinit var drivers: List<WebDriver>
    private lateinit var loginPage: LoginPage
    private lateinit var githubAuthPage: GithubAuthPage
    private var login: String
    private var password: String

    init {
        val props = Properties()
        try {
            props.load(FileInputStream("src/main/test/resources/stackoverflow.properties"))
        } catch (e: NullPointerException) {
            fail("Props file not found")
        }
        login = props.getProperty("login")
        password = props.getProperty("password")
    }

    @AfterEach
    fun tearDown() {
        drivers.forEach(WebDriver::quit)
    }

    @ParameterizedTest
    @ProvideWebDrivers
    fun testLoginWithWrongEmail(drivers: List<WebDriver>) {
        this.drivers = drivers
        drivers.forEach { driver ->
            driver.get("https://stackoverflow.com/users/login")
            loginPage = LoginPage(driver)
            loginPage.enterEmail("aaaaaaaa")
            loginPage.enterPassword("aaaaaaaa")

            loginPage.clickLoginButton()

            Assertions.assertTrue(loginPage.isErrorAppear())
        }
    }

    @ParameterizedTest
    @ProvideWebDrivers
    fun testLoginWithWrongPassword(drivers: List<WebDriver>) {
        this.drivers = drivers
        drivers.forEach { driver ->
            driver.get("https://stackoverflow.com/users/login")
            loginPage = LoginPage(driver)
            loginPage.enterEmail(login)
            loginPage.enterPassword("wrong password")

            loginPage.clickLoginButton()

            Assertions.assertTrue(loginPage.isErrorAppear())
        }
    }

    @ParameterizedTest
    @ProvideWebDrivers
    fun testLoginViaEmail(drivers: List<WebDriver>) {
        this.drivers = drivers
        drivers.forEach { driver ->
            driver.get("https://stackoverflow.com/users/login")
            loginPage = LoginPage(driver)
            loginPage.enterEmail(login)
            loginPage.enterPassword(password)
            loginPage.clickLoginButton()

            if (Utils.waitForCaptchaIfExists(driver)) {
                Assertions.assertNotEquals("https://stackoverflow.com/", driver.currentUrl)
                return
            }

            loginPage.waitForUrl("https://stackoverflow.com/", 1)
            Assertions.assertEquals("https://stackoverflow.com/", driver.currentUrl)
        }
    }


    @ParameterizedTest
    @ProvideWebDrivers
    fun testLoginViaGoogle(drivers: List<WebDriver>) {
        this.drivers = drivers
        drivers.forEach { driver ->
            driver.get("https://stackoverflow.com/users/login")
            loginPage = LoginPage(driver)
            loginPage.clickLoginViaGoogleButton()

            if (Utils.waitForCaptchaIfExists(driver)) {
                return
            }

            Assertions.assertNotEquals(loginPage.EXPECTED_PAGE_URL, driver.currentUrl)
            Assertions.assertTrue(driver.currentUrl.startsWith("https://accounts.google.com/"))
        }

    }


    @ParameterizedTest
    @ProvideWebDrivers
    fun testLoginViaGithub(drivers: List<WebDriver>) {
        this.drivers = drivers
        drivers.forEach { driver ->
            driver.get("https://stackoverflow.com/users/login")
            loginPage = LoginPage(driver)
            loginPage.clickLoginViaGithubButton()

            if (Utils.waitForCaptchaIfExists(driver)) {
                return
            }

            githubAuthPage = GithubAuthPage(driver)
            Assertions.assertNotEquals(loginPage.EXPECTED_PAGE_URL, driver.currentUrl)
            Assertions.assertTrue(githubAuthPage.isOnGithubAuth())

            val props = Properties()
            try {
                props.load(FileInputStream("src/main/test/resources/github.properties"))
            } catch (e: NullPointerException) {
                fail("Props file not found")
            }

            githubAuthPage.enterEmail(props.getProperty("login"))
            githubAuthPage.enterPassword(props.getProperty("password"))

            githubAuthPage.pressSignIn()
            if (Utils.waitForCaptchaIfExists(driver)) {
                return
            }
            Assertions.assertEquals("https://stackoverflow.com/", driver.currentUrl)
        }
    }

    @ParameterizedTest
    @ProvideWebDrivers
    fun testLoginViaFacebook(drivers: List<WebDriver>) {
        this.drivers = drivers
        drivers.forEach { driver ->
            driver.get("https://stackoverflow.com/users/login")
            loginPage = LoginPage(driver)
            loginPage.clickLoginViaFacebookButton()

            if (Utils.waitForCaptchaIfExists(driver)) {
                return
            }

            Assertions.assertTrue(driver.currentUrl.startsWith("https://www.facebook.com/login.php?"))
        }
    }
}