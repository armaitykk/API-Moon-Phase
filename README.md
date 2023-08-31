# API-Moon-Phase
The goal is to implement, from scratch, an application in Java 17 using JavaFX 17 that incorporates 2 APIs. The information output of the first API is used to generate information from the second API.

This app takes in a United States zip code and uses the zip code to search through the Ziptastic api (ziptasticapi.com) to get a city of that zip code. Then, the city is used to query the weather api (weatherapi.com) to get the moonrise, moonset, and moon phase. Note: the moon phase may be the same since all of the United States can have the same moon phase at the same time.

I learned how to use the SerializedName annotation from gson.annotations so that variables from the JSON response which have characters that do not conform to the standard checkstyle rules can be renamed.

If I were to start this project over, I would take more time to think on my idea instead of trying to code one first and changing my idea a lot of times after.
