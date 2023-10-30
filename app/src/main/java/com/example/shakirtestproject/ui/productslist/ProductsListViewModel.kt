package com.example.shakirtestproject.ui.productslist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shakirtestproject.api.ApiStatus
import com.example.shakirtestproject.models.*
import com.example.shakirtestproject.repositories.AppRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

private const val TAG = "ProductsListViewModel"

class ProductsListViewModel(private val appRepository: AppRepository) : ViewModel() {

    private val _productsLiveData = MutableLiveData<List<ProductsListItem>>()
    val productsLiveData: LiveData<List<ProductsListItem>> get() = _productsLiveData

    private val _cartModelLiveData = MutableLiveData<CartModel>()
    val cartModelLiveData: LiveData<CartModel> get() = _cartModelLiveData

    private val _categoryListLiveData = MutableLiveData<ArrayList<CategoryModel>>()
    val categoryListLiveData: LiveData<ArrayList<CategoryModel>> get() = _categoryListLiveData

    private var _cartItemsList = MutableLiveData<ArrayList<CartItemModel>>()
    private val cartItemList: LiveData<ArrayList<CartItemModel>> get() = _cartItemsList

    private val _isNotifyItemChangedLiveData = MutableLiveData<NotifyItemModel>()
    val isNotifyItemChangedLiveData: LiveData<NotifyItemModel> get() = _isNotifyItemChangedLiveData

    private val _isSortActionLiveData = MutableLiveData<String?>()
    val isSortActionLiveData: MutableLiveData<String?> get() = _isSortActionLiveData

    private val _isRetryLiveData = MutableLiveData<Boolean>()
    val isRetryLiveData: MutableLiveData<Boolean> get() = _isRetryLiveData

    fun getProducts(limit: Int, sort: String?): Flow<ApiStatus<ProductsList?>> {
        return appRepository.getProducts(limit, sort)
    }

    fun saveProductsToLiveData(currentList: List<ProductsListItem>) {
        viewModelScope.launch {
//            val list = appRepository.getCartItems()
            val cartItemsDeferred = async {
                appRepository.getCartItems()
            }

            // Wait for the result
            val list = cartItemsDeferred.await()
            _cartItemsList.value = ArrayList(list)
            Log.d(TAG, "saveProductsToLiveData: _cartItemsList ${_cartItemsList.value}")
            Log.d(TAG, "saveProductsToLiveData: cartItemList ${cartItemList.value}")
            val cartList = cartItemList.value

            if (cartList != null) {
                for (cartItem in cartList) {
                    for (currentItem in currentList) {
                        if (currentItem.id == cartItem.product_id) {
                            currentItem.quantity = cartItem.quantity
                        }
                    }
                }
            }
            updateCartValues()
            _productsLiveData.postValue(currentList)
        }
    }

    fun getAllCategories(): Flow<ApiStatus<List<String>?>> {
        return appRepository.getCategories()
    }

    fun storeCustomCategoryList(categoriesList: ArrayList<String>) {
        val tempList = arrayListOf<CategoryModel>()
        categoriesList.forEachIndexed { index, string ->
            tempList.add(CategoryModel(string, false, index))
        }
        _categoryListLiveData.postValue(tempList)
    }

    fun updateCategoriesRowItem(categoryModel: CategoryModel) {
        val list = _categoryListLiveData.value
        list.let {
            if (list != null) {
                for (item in list) {
                    if (item.category_name == categoryModel.category_name) {
                        it!![item.item_position] = CategoryModel(
                            categoryModel.category_name,
                            true,
                            categoryModel.item_position
                        )
                    } else {
                        it!![item.item_position] = CategoryModel(
                            item.category_name,
                            false,
                            item.item_position
                        )
                    }
                }
                _categoryListLiveData.value = it
            }
        }
    }

    fun getSpecificCategoryProducts(categoryName: String): Flow<ApiStatus<ProductsList?>> {
        return appRepository.getSpecificCategoryProducts(categoryName)
    }

    private fun updateCartItemInDatabase(cartItemModel: CartItemModel) {
        viewModelScope.launch {
            appRepository.updateCartItemInDatabase(cartItemModel)
        }
    }

    private fun deleteCartItemFromDatabase(cartItemModel: CartItemModel) {
        viewModelScope.launch {
            appRepository.deleteCartItemFromDatabase(cartItemModel)
        }
    }

    fun updateCartValues() {
        viewModelScope.launch {
            val cartItemsDeferred = async {
                appRepository.getCartItems()
            }

            // Wait for the result
            val list = cartItemsDeferred.await()
            var totalItems = 0
            var totalPrice = 0.0
            for (item in list) {
                if (item.quantity > 0) {
                    totalItems += item.quantity
                    totalPrice += item.quantity * item.price
                }
            }
            _cartModelLiveData.postValue(CartModel(totalItems, totalPrice))
        }
    }

    fun updateItemQuantity(
        productsList: ArrayList<ProductsListItem>,
        position: Int,
        quantity: Int,
        cartItemModel: CartItemModel,
        notifyItemModel: NotifyItemModel,
        isRemoveThisItem: Boolean,
    ) {
        productsList[position].quantity = quantity
        if (isRemoveThisItem) {
            deleteCartItemFromDatabase(cartItemModel)
        } else {
            updateCartItemInDatabase(cartItemModel)
        }
        updateCartValues()
        _isNotifyItemChangedLiveData.value = notifyItemModel
    }

    fun updateSortAction(sort: String?) {
        _isSortActionLiveData.value = sort
    }

    fun setRetry(boolean: Boolean) {
        _isRetryLiveData.value = boolean
    }

    fun logoutUser() {
        viewModelScope.launch {
            appRepository.logoutUser()
        }
    }

    fun deleteDatabase() {
        GlobalScope.launch {
            appRepository.deleteDatabase()
        }
    }

}