# EcommerceApp
Ecommerce Application in Kotlin 

Used this API for fetching the result
https://fakestoreapi.com/docs

Dependencies used:
DataStore preferences(for storing login token)
Retrofit(for REST API call)
Room Database(for storing the user's added items in cart)
Coroutines(for light weight operations)
Coil(for loading the image)
Jetpack Navigation(for navigations between screens)

Screens
1) Login screen: For making the user log in to the app using login API response token.
2) Products Listing Screen: For showing the products. It has filter(ascending/descending order based on product id) and sorting(based on categories like electronics, jewelery, etc) options. User can add the items in their cart from here.
3) Product Details Screen: For displaying the product details.
4) Checkout Screen: For showing the added cart items. I have stored these items locally using Room Database. User can update/remove the item from their cart in this screen as well.
5) User can also log out of the app and their login token and cart items will be deleted before logging out.
