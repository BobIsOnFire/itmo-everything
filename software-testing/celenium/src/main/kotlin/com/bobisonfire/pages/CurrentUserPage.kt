package com.bobisonfire.pages

import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.WebDriverWait

class CurrentUserPage(private val driver: WebDriver) {

    private val EXPECTED_PAGE_URL = "https://stackoverflow.com/users/"
    init {
        WebDriverWait(driver, 1000).until {
            (driver as JavascriptExecutor).executeScript("return document.readyState") == "complete" &&
                    driver.currentUrl.startsWith(EXPECTED_PAGE_URL)
        }
    }

    private val bookmarksLinkPath = "//a[@class='s-navigation--item' and @title='Questions you have bookmarked']"
    private val summaryLinkPath = "//a[@class='s-navigation--item' and @title='Your overall summary']"
    private val followingLinkPath = "//a[@class='s-navigation--item' and @title='Posts you are following']"
    private val removeBookmarkBtnPath = "//button[contains(@class, 'js-bookmark-btn')]"
    private val editProfileLinkPath = "//a[contains(@href, '/users/edit/')]"
    private val unfollowBtnPath = "//button[contains(@class, 'js-unfollow-post')]"


    fun openMyBookmarks() = driver.findElement(By.xpath(bookmarksLinkPath)).click()
    fun openSummary() = driver.findElement(By.xpath(summaryLinkPath)).click()
    fun openEditProfile() = driver.findElement(By.xpath(editProfileLinkPath)).click()
    fun openMyFollowingQuestions() = driver.findElement(By.xpath(followingLinkPath)).click()

    fun removeFromBookmarks(path: String) = driver.findElement(By.xpath(path + removeBookmarkBtnPath)).click()
    fun unfollow() = driver.findElement(By.xpath(unfollowBtnPath)).click()

}