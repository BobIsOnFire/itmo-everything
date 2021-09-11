package com.bobisonfire

import com.bobisonfire.pages.AskQuestionPage
import com.bobisonfire.pages.LoginPage
import com.bobisonfire.pages.MainQuestionsPage
import com.bobisonfire.utilities.ProvideWebDrivers
import com.bobisonfire.utilities.Utils
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import java.util.concurrent.TimeUnit


class AskQuestionTest {
    private lateinit var drivers: List<WebDriver>

    @AfterEach
    fun tearDown() {
        drivers.forEach(WebDriver::quit)
    }

    @ParameterizedTest
    @ProvideWebDrivers
    fun unauthorizedUserAskQuestionTest(drivers: List<WebDriver>) {
        this.drivers = drivers
        drivers.forEach { driver ->
            driver.get("https://stackoverflow.com/questions")
            val questionsPage = MainQuestionsPage(driver)
            questionsPage.clickToAskQuestion()

            Assertions.assertTrue(driver.currentUrl.startsWith("https://stackoverflow.com/users/login?"))

            val path = "//p[@class='val-textemphasis']"
            Assertions.assertTrue(
                driver.findElement(By.xpath(path)).text == "You must be logged in to ask a question on Stack Overflow"
            )
        }
    }

    @ParameterizedTest
    @ProvideWebDrivers
    fun authorizedUserAskQuestionEmptyFieldsTest(drivers: List<WebDriver>) {
        this.drivers = drivers
        drivers.forEach { driver ->
            driver.get("https://stackoverflow.com/users/login")
            val loginPage = LoginPage(driver)
            loginPage.login()
            if (Utils.waitForCaptchaIfExists(driver)) {
                return
            }
            driver.get("https://stackoverflow.com/questions")
            val questionsPage = MainQuestionsPage(driver)
            questionsPage.clickToAskQuestion()
            val askQuestionPage = AskQuestionPage(driver)
            askQuestionPage.writeTitle("")
            askQuestionPage.writePost("")
            askQuestionPage.writeTag("")
            askQuestionPage.reviewQuestion()

            Assertions.assertFalse(askQuestionPage.isTitlePresented())
            Assertions.assertFalse(askQuestionPage.isPostPresented())
            Assertions.assertFalse(askQuestionPage.isTagPresented())
        }

    }

    @ParameterizedTest
    @ProvideWebDrivers
    fun authorizedUserAskQuestionShortFieldsTest(drivers: List<WebDriver>) {
        this.drivers = drivers
        drivers.forEach { driver ->
            driver.get("https://stackoverflow.com/users/login")
            val loginPage = LoginPage(driver)
            loginPage.login()
            if (Utils.waitForCaptchaIfExists(driver)) {
                return
            }
            driver.get("https://stackoverflow.com/questions")
            val questionsPage = MainQuestionsPage(driver)
            questionsPage.clickToAskQuestion()
            val askQuestionPage = AskQuestionPage(driver)

            askQuestionPage.writeTitle("a")
            askQuestionPage.writePost("a")
            askQuestionPage.writeTag("a")

            askQuestionPage.reviewQuestion()

            Assertions.assertTrue(askQuestionPage.isTitleShort())
            Assertions.assertTrue(askQuestionPage.isTagHavingError())

            askQuestionPage.postQuestion()
            Assertions.assertTrue(askQuestionPage.isBodyShort())
        }

    }

    @ParameterizedTest
    @ProvideWebDrivers
    fun authorizedUserAskQuestionLongTitleTest(drivers: List<WebDriver>) {
        this.drivers = drivers
        drivers.forEach { driver ->
            driver.get("https://stackoverflow.com/users/login")
            val loginPage = LoginPage(driver)
            loginPage.login()
            if (Utils.waitForCaptchaIfExists(driver)) {
                return
            }
            driver.get("https://stackoverflow.com/questions")
            val questionsPage = MainQuestionsPage(driver)
            questionsPage.clickToAskQuestion()
            val askQuestionPage = AskQuestionPage(driver)

            askQuestionPage.writeTitle("jfhueias`cnhaiaisudhrfiuawhgiuszawshaiuhdisuyhbfiusdfhegpHGAUYFSGjfhueias`cnhaiaisudhrfiuawhgiuszawshaiuhdisuyhbfiusdfhegpHGAUYFSGjfhueias`cnhaiaisudhrfiuawhgiuszawshaiuhdisuyhbfiusdfhegpHGAUYFSGjfhueias`cnhaiaisudhrfiuawhgiuszawshaiuhdisuyhbfiusdfhegpHGAUYFSGjfhueias`cnhaiaisudhrfiuawhgiuszawshaiuh")
            askQuestionPage.writeTagAndEnter("ojhilugyftdytufygiuhoijpokjihougiyfutdyrsydfgijnoidnvodufnvosdnvoaisnfcioncdjoiasjisohcfohihihvhvoudhvduofhoifhohfohfohfsodhfohfio")
            askQuestionPage.writePost("a")

            Assertions.assertTrue(askQuestionPage.isTitleHavingError())
            Assertions.assertTrue(askQuestionPage.isTagHavingError())
        }
    }

    @ParameterizedTest
    @ProvideWebDrivers
    fun authorizedUserAskQuestionBoldButtonTest(drivers: List<WebDriver>) {
        this.drivers = drivers
        drivers.forEach { driver ->
            driver.get("https://stackoverflow.com/users/login")
            val loginPage = LoginPage(driver)
            loginPage.login()
            if (Utils.waitForCaptchaIfExists(driver)) {
                return
            }
            driver.get("https://stackoverflow.com/questions")
            val questionsPage = MainQuestionsPage(driver)
            questionsPage.clickToAskQuestion()
            val askQuestionPage = AskQuestionPage(driver)

            askQuestionPage.writePost(
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt" +
                        " ut labore et dolore magna aliqua."
            )

            askQuestionPage.highlightText(0, 5)
            askQuestionPage.clickBold()
            Thread.sleep(500)
            Assertions.assertEquals(
                "<p><strong>Lorem</strong> ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.</p>\n",
                askQuestionPage.getPreview()
            )

            askQuestionPage.clickBold()
            Thread.sleep(500)
            Assertions.assertEquals(
                "<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.</p>\n",
                askQuestionPage.getPreview()
            )
        }
    }

    @ParameterizedTest
    @ProvideWebDrivers
    fun authorizedUserAskQuestionItalicButtonTest(drivers: List<WebDriver>) {
        this.drivers = drivers
        drivers.forEach { driver ->
            driver.get("https://stackoverflow.com/users/login")
            val loginPage = LoginPage(driver)
            loginPage.login()
            if (Utils.waitForCaptchaIfExists(driver)) {
                return
            }
            driver.get("https://stackoverflow.com/questions")
            val questionsPage = MainQuestionsPage(driver)
            questionsPage.clickToAskQuestion()
            val askQuestionPage = AskQuestionPage(driver)

            askQuestionPage.writePost(
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt" +
                        " ut labore et dolore magna aliqua."
            )

            askQuestionPage.highlightText(6, 11)
            askQuestionPage.clickItalic()
            Thread.sleep(500)
            Assertions.assertEquals(
                "<p>Lorem <em>ipsum</em> dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.</p>\n",
                askQuestionPage.getPreview()
            )

            askQuestionPage.clickItalic()
            Thread.sleep(500)
            Assertions.assertEquals(
                "<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.</p>\n",
                askQuestionPage.getPreview()
            )
        }
    }

    @ParameterizedTest
    @ProvideWebDrivers
    fun authorizedUserAskQuestionQuoteButtonTest(drivers: List<WebDriver>) {
        this.drivers = drivers
        drivers.forEach { driver ->
            driver.get("https://stackoverflow.com/users/login")
            val loginPage = LoginPage(driver)
            loginPage.login()
            if (Utils.waitForCaptchaIfExists(driver)) {
                return
            }
            driver.get("https://stackoverflow.com/questions")
            val questionsPage = MainQuestionsPage(driver)
            questionsPage.clickToAskQuestion()
            val askQuestionPage = AskQuestionPage(driver)

            askQuestionPage.writePost(
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt" +
                        " ut labore et dolore magna aliqua."
            )

            askQuestionPage.highlightText(18, 24)
            askQuestionPage.clickQuote()
            Thread.sleep(500)
            Assertions.assertEquals(
                "<p>Lorem ipsum dolor</p>\n" +
                        "<blockquote>\n" +
                        "<p>sit am</p>\n" +
                        "</blockquote>\n" +
                        "<p>et, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.</p>\n",
                askQuestionPage.getPreview()
            )

            askQuestionPage.clickQuote()
            Thread.sleep(500)
            Assertions.assertEquals(
                "<p>Lorem ipsum dolor</p>\n" +
                        "<p>sit am</p>\n" +
                        "<p>et, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.</p>\n",
                askQuestionPage.getPreview()
            )
        }
    }

    @ParameterizedTest
    @ProvideWebDrivers
    fun authorizedUserAskQuestionLinkButtonTest(drivers: List<WebDriver>) {
        this.drivers = drivers
        drivers.forEach { driver ->
            driver.get("https://stackoverflow.com/users/login")
            val loginPage = LoginPage(driver)
            loginPage.login()
            if (Utils.waitForCaptchaIfExists(driver)) {
                return
            }
            driver.get("https://stackoverflow.com/questions")
            val questionsPage = MainQuestionsPage(driver)
            questionsPage.clickToAskQuestion()
            val askQuestionPage = AskQuestionPage(driver)

            askQuestionPage.writePost(
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt" +
                        " ut labore et dolore magna aliqua."
            )

            askQuestionPage.highlightText(18, 24)
            Thread.sleep(2000)
            askQuestionPage.addLink("google.com")
            Thread.sleep(500)
            Assertions.assertEquals(
                "<p>Lorem ipsum dolor <a href=\"http://google.com\">sit am</a>et, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.</p>\n",
                askQuestionPage.getPreview()
            )

            askQuestionPage.removeLink()
            Thread.sleep(500)
            Assertions.assertEquals(
                "<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.</p>\n",
                askQuestionPage.getPreview()
            )
        }
    }

    @ParameterizedTest
    @ProvideWebDrivers
    fun authorizedUserAskQuestionCodeButtonTest(drivers: List<WebDriver>) {
        this.drivers = drivers
        drivers.forEach { driver ->
            driver.get("https://stackoverflow.com/users/login")
            val loginPage = LoginPage(driver)
            loginPage.login()
            if (Utils.waitForCaptchaIfExists(driver)) {
                return
            }
            driver.get("https://stackoverflow.com/questions")
            val questionsPage = MainQuestionsPage(driver)
            questionsPage.clickToAskQuestion()
            val askQuestionPage = AskQuestionPage(driver)

            askQuestionPage.writePost(
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt" +
                        " ut labore et dolore magna aliqua."
            )

            askQuestionPage.highlightText(6, 11)
            askQuestionPage.clickCode()

            Thread.sleep(500)
            Assertions.assertEquals(
                "<p>Lorem <code>ipsum</code> dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.</p>\n",
                askQuestionPage.getPreview()
            )

            askQuestionPage.clickCode()
            Thread.sleep(500)
            Assertions.assertEquals(
                "<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.</p>\n",
                askQuestionPage.getPreview()
            )
        }
    }
}