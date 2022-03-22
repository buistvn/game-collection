<!-- PROJECT -->
# Game Collection

![Project Screenshot][project-screenshot]

Game Collection is a mobile app for browsing video games using data provided by the [RAWG API](https://rawg.io/apidocs). Users can find information on games such as ratings, developers, stores, screenshots, and trailers. While searching for games, users can narrow down their results by configuring the filters in the settings. The app has a local database which allows users to save games by favoriting them.



<!-- TECHNOLOGIES -->
## Technologies

* [Moshi](https://square.github.io/moshi/1.x/moshi/)
* [Retrofit](https://square.github.io/retrofit/)
* [Glide](https://bumptech.github.io/glide/)



<!-- INSTALLATION -->
## Installation

To set up a local copy of the project, follow these steps.

1. Clone the repository
   ```sh
   git clone https://github.com/osu-cs492-w22/game-collection.git
   ```
2. Generate an API key from [RAWG](https://rawg.io/apidocs)
3. Create a `gradle.properties` file in the Gradle user home directory (`$USER_HOME/.gradle` by default) with the API key
   ```sh
   RAWG_API_KEY="YOUR_API_KEY_GOES_HERE"
   ```



<!-- USAGE -->
## Usage

To run the project, follow these steps.

1. Open the project in Android Studio
2. Run the app with a device or an emulator



<!-- AUTHORS -->
## Authors

CS 492 Final Project Team 16

* [Steven Bui](https://github.com/buistvn)
* [Yuanfeng Guo](https://github.com/gyfgumeng)
* [Jordan Porter](https://github.com/4berry1)
* [Alex Young](https://github.com/axyoung)



<!-- LINKS & IMAGES -->
[project-screenshot]: /docs/game-collection.png
