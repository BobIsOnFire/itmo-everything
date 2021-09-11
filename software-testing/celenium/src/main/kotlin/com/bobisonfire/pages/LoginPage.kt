package com.bobisonfire.pages

import com.bobisonfire.utilities.find
import org.junit.jupiter.api.fail
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.WebDriverWait
import java.io.FileInputStream
import java.util.*


class LoginPage(private val driver: WebDriver) {
    val EXPECTED_PAGE_URL = "https://stackoverflow.com/users/login"
    private var login: String
    private var password: String
    init {
        if (!driver.currentUrl.startsWith(EXPECTED_PAGE_URL))
            throw IllegalArgumentException(driver.currentUrl)
        val props = Properties()
        try {
            props.load(FileInputStream("src/main/test/resources/stackoverflow.properties"))
        } catch (e: NullPointerException) {
            fail("Props file not found")
        }
        login = props.getProperty("login")
        password = props.getProperty("password")
    }
    private val emailInputPath = "//input[@type='email']"
    private val passwordInputPath = "//input[@type='password']"
    private val loginButtonPath = "//button[@name='submit-button']"

    private val errorMsgPath = "//p[contains(@class, 'js-error-message')]"

    private val loginWithGoogleButtonPath = "//button[@data-provider='google' and @data-oauthserver='https://accounts.google.com/o/oauth2/auth']"
    private val loginWithGithubButtonPath = "//button[@data-provider='github' and @data-oauthserver='https://github.com/login/oauth/authorize']"
    private val loginWithFacebookButtonPath = "//button[@data-provider='facebook' and @data-oauthserver='https://www.facebook.com/v2.0/dialog/oauth']"

    fun enterEmail(email: String) = driver.findElement(By.xpath(emailInputPath)).sendKeys(email)
    fun enterPassword(password: String) = driver.findElement(By.xpath(passwordInputPath)).sendKeys(password)
    fun waitForUrl(url: String, timeout: Long) = WebDriverWait(driver, timeout).until { driver.currentUrl == url }

    fun clickLoginButton() = driver.findElement(By.xpath(loginButtonPath)).click()
    fun isErrorAppear(): Boolean = driver.find(By.xpath(errorMsgPath))

    fun clickLoginViaGoogleButton() = driver.findElement(By.xpath(loginWithGoogleButtonPath)).click()
    fun clickLoginViaGithubButton() = driver.findElement(By.xpath(loginWithGithubButtonPath)).click()
    fun clickLoginViaFacebookButton() = driver.findElement(By.xpath(loginWithFacebookButtonPath)).click()

    fun login(email: String = login, password: String = this.password) {
        enterEmail(email)
        enterPassword(password)
        clickLoginButton()
    }
}