(6) Team lemmings
=================

###Engine###
 - We are using libGDX

###Requirnments###
 - Android SDK
 - Gradle
 - Java Development Kit 7+ (JDK) (6 will not work!)

There is an indepth guide [here](https://github.com/libgdx/libgdx/wiki/Setting-up-your-Development-Environment-%28Eclipse%2C-Intellij-IDEA%2C-NetBeans%29#setting-up-eclipse) or rough instructions below

###Installing Gradle###
 - Gradle is an eclipse plugin needed to setup libGDX
 - There is a guide [here](https://github.com/spring-projects/eclipse-integration-gradle/#installing-gradle-tooling-from-update-site)
 - Note: Only install the stuff from the section it jumps to
 - Of the three update sites, **http://dist.springsource.com/release/TOOLS/gradle** seems to work fine

###Installing Java Development Kit 7+###
 - You can download from [here](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
 - You may need to set the **JAVA_HOME** system variable to where ever you choose to install the SDK

###How to setup###
 - Checkout the repo to your PC
 - In the root folder create a file named `local.properties`, inside add `sdk.dir=C:/Location/to/your/Android/android-sdk`, changing the path accordingly
 - Load up eclipse
 - Select File -> Import
 - Gradle -> Gradle Project
 - Locate the folder where you checked the project out to
 - Select **Build Model**, this will take some time as it needs to download stuff
 - Some check boxes will appear in the list, select them all
 - Hit next and follow the promps

###How to run###
 - Select which version you'd like to test [eg: desktop]
 - Right click it and select Run-As
  - For desktop -> Java Application
  - For android -> Android Application
 - Follow the on screen instructions

###Where do I put things?###
 - Assets, such as images go into the **android/assets folder**
 - Code goes into **core/src** folder

###Running Tests###
 - To setup the testing environment, ensure gradle has all the dependencies (Note: This is only needed if you setup your environment before testing was added)
  - Open eclipse
  - Right click on any project, select `Gradle -> Refresh All`
  - Wait for it to download any missing dependencies
 - Open a command window and navigate to to the root project directory
 - Type `gradlew core:test`, this will run all the tests
 - You can view the test results here `Lemmings/core/build/reports/tests/index.html`

###Alternate Way To Run Tests###
 - Right click on the `core` package
 - Select `Run As -> JUnit Test`
 - Eclipse should now run tests in the JUint testing interface

###Creating Tests###
 - Create a new class in the package `com.teamlemmings.tests`
 - Any functions that have the `@Test` tag will be run as tests

###Error Loading Resources?###
 - If the game is having trouble loading resources you'll need to clean the project
 - From the menus, select `Project -> Clean -> Clean all projects -> OK`
