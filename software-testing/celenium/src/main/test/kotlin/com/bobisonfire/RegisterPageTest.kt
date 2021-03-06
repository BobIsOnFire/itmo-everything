package com.bobisonfire

import com.bobisonfire.pages.GithubAuthPage
import com.bobisonfire.pages.RegisterPage
import com.bobisonfire.utilities.CaptchaAnalyzer
import com.bobisonfire.utilities.ProvideWebDrivers
import com.bobisonfire.utilities.Utils
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.openqa.selenium.StaleElementReferenceException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.FluentWait
import java.time.Duration


class RegisterPageTest {
    private lateinit var drivers: List<WebDriver>
    private lateinit var registerPage: RegisterPage
    private lateinit var githubAuthPage: GithubAuthPage

    @AfterEach
    fun tearDown() {
        drivers.forEach(WebDriver::quit)
    }

    @ParameterizedTest
    @ProvideWebDrivers
    fun testRegisterViaGoogle(drivers: List<WebDriver>) {
        this.drivers = drivers
        drivers.forEach { driver ->

            driver.get("https://stackoverflow.com/users/signup")
            registerPage = RegisterPage(driver)
            registerPage.clickRegisterViaGoogleButton()

            Assertions.assertNotEquals("https://stackoverflow.com/users/signup", driver.currentUrl)
            Assertions.assertTrue(driver.currentUrl.startsWith("https://accounts.google.com/"))
        }
    }

    @ParameterizedTest
    @ProvideWebDrivers
    fun testRegisterViaGithub(drivers: List<WebDriver>) {
        this.drivers = drivers
        drivers.forEach { driver ->
            driver.get("https://stackoverflow.com/users/signup")
            registerPage = RegisterPage(driver)
            registerPage.clickRegisterViaGithubButton()
            Utils.waitForCaptchaIfExists(driver)

            githubAuthPage = GithubAuthPage(driver)
            Assertions.assertNotEquals("https://stackoverflow.com/users/signup", driver.currentUrl)
            Assertions.assertTrue(driver.currentUrl.startsWith("https://github.com/login"))
        }
    }

    @ParameterizedTest
    @ProvideWebDrivers
    fun testRegisterViaFacebook(drivers: List<WebDriver>) {
        this.drivers = drivers
        drivers.forEach { driver ->
            driver.get("https://stackoverflow.com/users/signup")
            registerPage = RegisterPage(driver)
            registerPage.clickRegisterViaFacebookButton()

            Assertions.assertTrue(driver.currentUrl.startsWith("https://www.facebook.com/login.php?"))
        }
    }

    @ParameterizedTest
    @ProvideWebDrivers
    fun testRegister(drivers: List<WebDriver>) {
        this.drivers = drivers
        drivers.forEach { driver ->
            driver.get("https://stackoverflow.com/users/signup")
            registerPage = RegisterPage(driver)

            registerPage.agree()
            registerPage.enterName("new_user_test")
            registerPage.enterEmail("new_user_email${System.currentTimeMillis()}@register.com")
            registerPage.enterPassword("passworD123")

            FluentWait(driver)
                .pollingEvery(Duration.ofSeconds(2))
                .withTimeout(Duration.ofMinutes(10))
                .ignoring(StaleElementReferenceException::class.java)
                .until {
                    !registerPage.isCaptchaWindowShowed()
                    CaptchaAnalyzer.isCaptchaSolved(
                        driver, registerPage.getCaptchaElement()
                    )
                }

            registerPage.clickRegister()
            Assertions.assertTrue(registerPage.isRegistrationSucceeded())
        }
    }
}