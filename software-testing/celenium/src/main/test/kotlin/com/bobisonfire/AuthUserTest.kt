package com.bobisonfire

import com.bobisonfire.pages.*
import com.bobisonfire.utilities.ProvideWebDrivers
import com.bobisonfire.utilities.Utils
import com.bobisonfire.utilities.find
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import java.util.concurrent.TimeUnit


class AuthUserTest {
    private lateinit var drivers: List<WebDriver>
    private lateinit var loginPage: LoginPage
    private lateinit var mainQuestionsPage: MainQuestionsPage
    private lateinit var questionPage: QuestionPage
    private lateinit var userPage: CurrentUserPage
    private lateinit var editProfilePage: EditProfilePage
    private val link = "//a[contains(@href, '/questions/64959651/why-os-allocate-empty-segment-before-pthread-stack')]"
    private val query = "Why os allocate empty segment before pthread stack?"
    private val title = "Why os allocate empty segment before pthread stack?"

    @AfterEach
    fun tearDown() {
        drivers.forEach(WebDriver::quit)
    }



    @ParameterizedTest
    @ProvideWebDrivers
    fun searchQuestionTest(drivers: List<WebDriver>) {
        this.drivers = drivers
        drivers.forEach { driver ->
            driver.get("https://stackoverflow.com/users/login")
            loginPage = LoginPage(driver)
            loginPage.login()

            if (Utils.waitForCaptchaIfExists(driver)) {
                return
            }

            mainQuestionsPage = MainQuestionsPage(driver)

            mainQuestionsPage.search(query)
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS)
            mainQuestionsPage.clickToLink(link)

            questionPage = QuestionPage(driver)
            val path = "//a[contains(@class, 'question-hyperlink')]"
            Assertions.assertTrue(driver.findElement(By.xpath(path)).text == title)
        }
    }

    @ParameterizedTest
    @ProvideWebDrivers
    fun leaveCommentTest(drivers: List<WebDriver>) {
        this.drivers = drivers
        drivers.forEach {  driver ->
            driver.get("https://stackoverflow.com/users/login")
            loginPage = LoginPage(driver)
            loginPage.login()

            if (Utils.waitForCaptchaIfExists(driver)) {
                return
            }

            mainQuestionsPage = MainQuestionsPage(driver)

            Utils.waitForCaptchaIfExists(driver)

            mainQuestionsPage.search(query)
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS)
            mainQuestionsPage.clickToLink(link)

            questionPage = QuestionPage(driver)
            questionPage.comment()

            Assertions.assertTrue(driver.find(By.xpath("//div[@class='message-text' and .//a[@href='/help/privileges/comment']]")))
        }
    }

    @ParameterizedTest
    @ProvideWebDrivers
    fun bookmarksTest(drivers: List<WebDriver>) {
        this.drivers = drivers
        drivers.forEach {  driver ->
            driver.get("https://stackoverflow.com/users/login")
            loginPage = LoginPage(driver)
            loginPage.login()

            if (Utils.waitForCaptchaIfExists(driver)) {
                return
            }

            mainQuestionsPage = MainQuestionsPage(driver)

            Utils.waitForCaptchaIfExists(driver)

            mainQuestionsPage.search(query)
            driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS)
            mainQuestionsPage.clickToLink(link)

            questionPage = QuestionPage(driver)

            questionPage.addToBookmarks()
            questionPage.openMyProfile()

            userPage = CurrentUserPage(driver)
            userPage.openMyBookmarks()
            driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS)

            Assertions.assertTrue(driver.findElement(By.xpath("//a[@class='question-hyperlink']")).text == title
            )

            userPage.removeFromBookmarks("//div[@class='user-questions']/div[contains(@class, 'question-summary')]")
            driver.navigate().refresh()

            Assertions.assertFalse(driver.find(By.xpath("//a[@class='question-hyperlink']")))
        }

    }

    @ParameterizedTest
    @ProvideWebDrivers
    fun followQuestionTest(drivers: List<WebDriver>) {
        this.drivers = drivers
        drivers.forEach {  driver ->
            driver.get("https://stackoverflow.com/users/login")
            loginPage = LoginPage(driver)
            loginPage.login()

            if (Utils.waitForCaptchaIfExists(driver)) {
                return
            }

            mainQuestionsPage = MainQuestionsPage(driver)

            Utils.waitForCaptchaIfExists(driver)

            mainQuestionsPage.search(query)
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS)
            mainQuestionsPage.clickToLink(link)

            questionPage = QuestionPage(driver)

            questionPage.addToFollowingQuestions()
            questionPage.openMyProfile()

            userPage = CurrentUserPage(driver)
            userPage.openMyFollowingQuestions()

            Assertions.assertTrue(driver.findElement(By.xpath(
                "//a[@class='question-hyperlink' and @href='/questions/64959651/why-os-allocate-empty-segment-before-pthread-stack']"
            )).text == title
            )

            userPage.unfollow()
            driver.navigate().refresh()

            Assertions.assertFalse(driver.find(By.xpath(
                "//a[@class='question-hyperlink' and @href='/questions/64959651/why-os-allocate-empty-segment-before-pthread-stack']"
            )))
        }

    }

    @ParameterizedTest
    @ProvideWebDrivers
    fun editProfileTest(drivers: List<WebDriver>) {
        this.drivers = drivers
        drivers.forEach {  driver ->
            driver.get("https://stackoverflow.com/users/login")
            loginPage = LoginPage(driver)

            loginPage.login()

            if (Utils.waitForCaptchaIfExists(driver)) {
                return
            }

            mainQuestionsPage = MainQuestionsPage(driver)

            if (Utils.waitForCaptchaIfExists(driver)) {
                return
            }

            mainQuestionsPage.openUserProfile()

            userPage = CurrentUserPage(driver)

            userPage.openEditProfile()
            editProfilePage = EditProfilePage(driver)

            editProfilePage.changeLocation("Saint Petersburg, Russia")
            editProfilePage.changeTitle("aaaaa")

            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS)
            editProfilePage.saveChanges()
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS)

            Assertions.assertTrue(editProfilePage.isDone())

            Assertions.assertTrue(driver.findElement(By.xpath(editProfilePage.locationInputPath)).getAttribute("value") == "Saint Petersburg, Russia")
            Assertions.assertTrue(driver.findElement(By.xpath(editProfilePage.titleInputPath)).getAttribute("value") == "aaaaa")
        }
    }

    @ParameterizedTest
    @ProvideWebDrivers
    fun shareQuestionTest(drivers: List<WebDriver>) {
        this.drivers = drivers
        drivers.forEach {  driver ->
            driver.get("https://stackoverflow.com/users/login")
            loginPage = LoginPage(driver)
            loginPage.login()

            if (Utils.waitForCaptchaIfExists(driver)) {
                return
            }

            mainQuestionsPage = MainQuestionsPage(driver)

            Utils.waitForCaptchaIfExists(driver)

            mainQuestionsPage.search(query)
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS)
            mainQuestionsPage.clickToLink(link)

            questionPage = QuestionPage(driver)
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS)
            Assertions.assertTrue(questionPage.clickShareQuestion())
        }
    }

    @ParameterizedTest
    @ProvideWebDrivers
    fun logoutTest(drivers: List<WebDriver>) {
        this.drivers = drivers
        drivers.forEach {  driver ->
            driver.get("https://stackoverflow.com/users/login")
            loginPage = LoginPage(driver)
            loginPage.login()

            if (Utils.waitForCaptchaIfExists(driver)) {
                return
            }

            val mainPage = MainQuestionsPage(driver)
            mainPage.logout()

            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS)

            Assertions.assertEquals("https://stackoverflow.com/", driver.currentUrl)
        }
    }
}