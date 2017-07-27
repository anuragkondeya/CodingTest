# CodingTest

# CodingTest


## Notes

1. The code is making use of List view. The view holder pattern is adopted for saving findViewById calls and better performance.

2. Loopers are used for network connections as loopers can perform efficiently with activity lifecycle

3. Using Volley (Sample code with Async task is included as well) for network calls to fetch JSON and jpegs from the server

4. Tested the app on firebase test labs (Robo tests) for API 19 and API 26 test passed.



 
## Question Statement

Build a list view using the following data feed:

https://aboutdoor.info/news?index=0

Display only headline and image for each item.

This API gives a list of 10 items at once, with an offset specified by the index query parameter. 
Your list view should display 10 rows of data initially, but when the user scrolls to the bottom of the list it should automatically load more data and add it to the list view.

Assume there is an unreasonably large number of items available.

Submit the URL of a public github repo that hosts your code. Supplement notes can be put in README.md.

Your test will be evaluated on the quality of the code, it is acceptable to have a bland UI.


