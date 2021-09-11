package com.bobisonfire

import com.bobisonfire.pages.MainQuestionsPage
import com.bobisonfire.pages.StartPage
import com.bobisonfire.pages.UsersPage
import com.bobisonfire.utilities.ProvideWebDrivers
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.openqa.selenium.WebDriver

class StartPageTest {
    private lateinit var drivers: List<WebDriver>
    private lateinit var startPage: StartPage

    @AfterEach
    fun tearDown() {
        drivers.forEach(WebDriver::quit)
    }

    @ParameterizedTest
    @ProvideWebDrivers
    fun testOpenSearch(drivers: List<WebDriver>) {
        this.drivers = drivers
        drivers.forEach{ driver ->
            driver.get("https://stackoverflow.com")
            startPage = StartPage(driver)
            startPage.clickSearchContentLink()

            assertEquals("https://stackoverflow.com/questions", driver.currentUrl)
        }
    }

    @ParameterizedTest
    @ProvideWebDrivers
    fun testOpenDiscoverTeams(drivers: List<WebDriver>) {
        this.drivers = drivers
        drivers.forEach { driver ->
            driver.get("https://stackoverflow.com")
            startPage = StartPage(driver)
            startPage.clickDiscoverTeamsContentLink()

            assertEquals("https://stackoverflow.com/teams", driver.currentUrl)
        }
    }

    @ParameterizedTest
    @ProvideWebDrivers
    fun testPressJoinCommunity(drivers: List<WebDriver>) {
        this.drivers = drivers
        drivers.forEach { driver ->
            driver.get("https://stackoverflow.com")
            startPage = StartPage(driver)
            startPage.clickJoinCommunity()

            assertEquals("https://stackoverflow.com/users/signup", driver.currentUrl)
        }
    }

    @ParameterizedTest
    @ProvideWebDrivers
    fun testPressCreateTeam(drivers: List<WebDriver>) {
        this.drivers = drivers
        drivers.forEach { driver ->
            driver.get("https://stackoverflow.com")
            startPage = StartPage(driver)
            startPage.clickCreateTeam()

            assertEquals("https://stackoverflow.com/teams/create/free", driver.currentUrl)
        }
    }

    @ParameterizedTest
    @ProvideWebDrivers
    fun testSearchUser(drivers: List<WebDriver>) {
        this.drivers = drivers
        drivers.forEach {  driver ->
            driver.get("https://stackoverflow.com/questions")
            val mainPage = MainQuestionsPage(driver)
            mainPage.clickUsersPageLink()

            val usersPage = UsersPage(driver)
            usersPage.searchUser("Nikita Akatyev")

            Assertions.assertTrue(usersPage.isUserPresented("Nikita Akatyev"))

        }
    }
}