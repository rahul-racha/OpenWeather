# OpenWeather
Android weather app

## Features
* To add a location click on the floating button.
* To edit a location long press on the item.
* To delete a location swipe from right.
* To view the forecast click on the item.

## Limitations
* The app saves the locations using Room Persistence library. While refreshing or launching the app all the location IDS are passed to Open Weather API as a group. But the API often fails to return results for multiple cities. Thus, only the places with weather information are displayed during refresh or application launch. 
* 
