(6) Team lemmings
=================

###Engine###
 - We are using libGDX

###Requirements###
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

###Level Format###
 - The level format is future proof, so, if we get time, it will be possible to make the level editor more efficently compile the maps, however, at this stage they seem to run fine anyways, so it doesn't matter.
 - Levels are stored in `android/assets/maps/`, they are are simply a JSON file, with instructions on how to build, and display a given map.
 - There is the numbers 1 - <totalScreens> as keys, which contain info on each of  the screens
  - physicsData This is the level gemoetry, it's basically a list of objects, objects also contain vertices, this field is just for walls
  - tileData This contains other tiles that are seperate from from walls, for example, sheep, the  goal, etc
  - visualData This is an array of tiles to draw, there will be 48 x 27 entries in this array, each entry is a string, containing the name of the tile to use. It's pretty unoptimised, but with the time constraint, it is reasonable.
 - There are other settings contained in the root of the file
  - mapName The name of the map, this should matchup with the filename
  - sheepToWin The number of sheep needed to get into the goal to win
  - mapTitle The title of the map, this is what will be seen in the menu

###Level Editor###
 - The level editor is basically hacked togeher, I kept changing my mind on how it would work, this is because I had to continue to lower the scope of what it could do because of time, and this is the main cause of it
 - The level editor can be found in the `leveleditor` folder
 - Maps are placed into `leveleditor/mapSRC`, the map format is a PNG file, any file names with an underscore (_) won't be compiled, the rest of the PNG files will be compiled
 - A map can also have a config file which allows you to change how a map is built and specify things like the number of sheep required to win
 - If a config file doesn't exist, when the map is compiled, it will generate a default config file, which will have the same name as the map, except with a .json extension
 - To add multiple screens, you simply make more images, add `_2`, `_3`, etc, to the end to change the screen number (this is why _ is ignored)
 - Each individual screen can also have a config attached to it, however, settings such as sheep required to win, map name, etc will be taken from the main screen
 - Please stick to a 4 x 4 grid when making maps, the level editor can support a different grid, however, that will take improvements which I don't have time to make
 - You can use the `template.png` file located in the root of the leveleditor folder as a base, to make it easy to stay on the grid
 - You can see the elements that can be placed by looking at `elements.png`, interactive objects will be configured by the config file generated by the compiler

###Compiling Maps###
 - You need [Node.js](http://nodejs.org) installed in order to compile maps
 - See: `compile.bat`, if you installed Node.js, you can simply run the bat file, which will compile all the maps in the mapSRC folder

###Extending the level editor###
 - See the line with the comment `// Tile finder`, the `registerRule` function below that contains most of the definitions for tiles
 - You just need to add another test case for what to search for, then add the tile name
 - You also need to add a case into the `loadLevel` function in the `GameScreen` class, to tell it what to spawn when it comes across your new tile.
