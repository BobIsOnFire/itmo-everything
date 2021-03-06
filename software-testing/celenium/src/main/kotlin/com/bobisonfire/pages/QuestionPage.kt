package com.bobisonfire.pages

import com.bobisonfire.utilities.find
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.WebDriverWait
import java.lang.IllegalArgumentException

class QuestionPage(private val driver: WebDriver) {
    private val EXPECTED_PAGE_URL = "https://stackoverflow.com/questions/"

    init {
        WebDriverWait(driver, 1000).until {
            (driver as JavascriptExecutor).executeScript("return document.readyState") == "complete" &&
                    (driver.currentUrl.startsWith(EXPECTED_PAGE_URL))
        }
    }

    private val postAnswerTextareaPath = "//textarea[@name='post-text']"
    private val buttonPath = "//button[@type='submit']"
    private val emailErrorMsgPath = "//div[@class='message-text']"
    private val showMoreCommentsPath = "//div[@class='answer']//a[@title='Expand to show all comments on this post']"
//    private val addCommentPath = "//div[@class='answer']//a[contains(.,'Add a comment')]"
    private val addToBookmarksBtnPath = "//button[contains(@class, 'js-bookmark-btn')]"
    private val addToFollowingBtnPath = "//button[contains(@class, 'js-follow-post js-follow-question')]"
    private val myProfileLinkPath = "//a[contains(@class, 'my-profile')]"
    private val shareLinkPath = "//a[@rel='nofollow' and @itemprop='url' and @class='js-share-link js-gps-track']"
    private val shareLinkPopupPath = "//div[@class='s-popover z-dropdown s-anchors s-anchors__default is-visible']"

    fun writeAnswerToQuestion(text: String) = driver.findElement(By.xpath(postAnswerTextareaPath)).sendKeys(text)
    fun isAnswerValid(): Boolean {
        return !driver.find(
            By.xpath("//div[contains(@class, 'js-stacks-validation-message')]")
        )
    }

    fun isEmailNotPresentedToPost(): Boolean {
        WebDriverWait(driver, 10).until {
            driver.findElement(By.xpath(emailErrorMsgPath)).text == "An email is required to post."
        }
        return driver.findElement(By.xpath(emailErrorMsgPath)).text == "An email is required to post."
    }

    fun clickToPostAnswer() {
        if (driver.findElement(By.xpath(buttonPath)).text == "Post Your Answer") {
            driver.findElement(By.xpath(buttonPath)).click()
        }

    }

    fun areErrorsPresented(): Boolean {
        WebDriverWait(driver, 10).until {
            driver.find(By.xpath("//div[contains(@class, 'js-general-error general-error')]"))
        }
        return driver.find(By.xpath("//div[contains(@class, 'js-general-error general-error')]")) ||
                driver.findElement(By.xpath("//div[@class='message-text']")).text == "To answer a question, you must either sign up for an account or post as a guest."
    }

    fun comment() {
        driver.findElement(By.linkText("Add a comment")).click()
    }

    fun dismiss() = driver.findElement(By.xpath("//button[@title='Dismiss']")).click()

    fun addToBookmarks() = driver.findElement(By.xpath(addToBookmarksBtnPath)).click()
    fun addToFollowingQuestions() = driver.findElement(By.xpath(addToFollowingBtnPath)).click()
    fun openMyProfile() = driver.findElement(By.xpath(myProfileLinkPath)).click()

    fun clickShareQuestion(): Boolean {
        driver.findElement(By.xpath(shareLinkPath)).click()
        return driver.find(By.xpath(shareLinkPopupPath)) &&
                driver.findElement(By.xpath("$shareLinkPopupPath//input[@type='text']")).getAttribute("value").startsWith("https://stackoverflow.com/q/")


    }

}