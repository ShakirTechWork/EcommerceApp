package com.example.shakirtestproject.ui.productslist

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.shakirtestproject.Application
import com.example.shakirtestproject.OnItemClickListener
import com.example.shakirtestproject.R
import com.example.shakirtestproject.adapter.CategoriesListAdapter
import com.example.shakirtestproject.adapter.ProductsListAdapter
import com.example.shakirtestproject.api.ApiStatus
import com.example.shakirtestproject.databinding.FragmentProductsListBinding
import com.example.shakirtestproject.models.*
import com.example.shakirtestproject.ui.login.UserLoginActivity
import com.example.shakirtestproject.utils.Utils
import kotlinx.coroutines.launch

private const val TAG = "ProductsListFragment"

class ProductsListFragment : Fragment(), OnItemClickListener {

    private var sort: String? = null
    private lateinit var navController: NavController
    private var _binding: FragmentProductsListBinding? = null

    private val binding get() = _binding!!

    private lateinit var productsListViewModel: ProductsListViewModel

    private lateinit var productsListAdapter: ProductsListAdapter
    private lateinit var categoriesListAdapter: CategoriesListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProductsListBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = NavHostFragment.findNavController(this@ProductsListFragment)

        val repository = (requireActivity().application as Application).appRepository

        productsListViewModel = ViewModelProvider(
            this,
            ProductsListViewModelFactory(repository)
        )[ProductsListViewModel::class.java]

        attachClickListeners()
        attachObservers()

        lifecycleScope.launch {
            getProducts(sort)
        }

        getAllCategories()

    }

    private fun attachClickListeners() {
        binding.clCartLayout.setOnClickListener {
            navController.navigate(R.id.action_products_list_to_checkout_fragment)
        }

        binding.tvApplyFilter.setOnClickListener {
            Utils.twoOptionAlertDialog(requireContext(), "Apply Filter",
                "Apply a filter by product's ID. It will sort the list in ascending and descending order.",
                "Ascending", "Descending", true, {
                    Log.d(TAG, "attachClickListeners: Ascending")
                    productsListViewModel.updateSortAction("asc")
                }, {
                    Log.d(TAG, "attachClickListeners: Descending")
                    productsListViewModel.updateSortAction("desc")
                })
        }

        binding.tvLogout.setOnClickListener {
            Utils.twoOptionAlertDialog(requireContext(), "Confirmation",
                "Are you sure you want to logout?",
                "Yes", "Cancel", true, {
                    Log.d(TAG, "attachClickListeners: Yes")
                    Utils.showLongToast(requireContext(), "Logging you out!")
                    productsListViewModel.deleteDatabase()
                    productsListViewModel.logoutUser()
                    val intent = Intent(requireContext(), UserLoginActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }, {
                    Log.d(TAG, "attachClickListeners: Cancel")
                })
        }

        binding.tvRetry.setOnClickListener {
            productsListViewModel.setRetry(false)
            lifecycleScope.launch {
                getProducts(sort)
            }

            getAllCategories()
        }

    }

    @SuppressLint("SetTextI18n")
    private fun attachObservers() {

        productsListViewModel.productsLiveData.observe(viewLifecycleOwner) {
            binding.rvProducts.layoutManager = GridLayoutManager(requireContext(), 2)
            productsListAdapter = ProductsListAdapter(this, ArrayList(it))
            binding.rvProducts.adapter = productsListAdapter
        }

        productsListViewModel.categoryListLiveData.observe(viewLifecycleOwner) {
            categoriesListAdapter = CategoriesListAdapter(this, it)
            binding.rvCategories.adapter = categoriesListAdapter
        }

        productsListViewModel.cartModelLiveData.observe(viewLifecycleOwner) {
            if (it.totalItems > 0 && it.totalPrice > 0.0) {
                binding.tvTotalItems.text = "${it.totalItems} Item | "
                binding.tvTotalPrice.text = "${getString(R.string.rupee_symbol)} ${Utils.formatDoubleValue(it.totalPrice)}"
                binding.clCartLayout.visibility = View.VISIBLE
            } else {
                binding.clCartLayout.visibility = View.GONE
            }
        }

        productsListViewModel.isNotifyItemChangedLiveData.observe(viewLifecycleOwner) {
            productsListAdapter.notifyItemChanged(it.position)
        }

        productsListViewModel.isSortActionLiveData.observe(viewLifecycleOwner) {
            getProducts(it)
        }

        productsListViewModel.isRetryLiveData.observe(viewLifecycleOwner) {
            if (it) {
                binding.tvRetry.visibility = View.VISIBLE
            } else {
                binding.tvRetry.visibility = View.GONE
            }
        }
    }

    private fun getAllCategories() {
        if (Utils.isInternetAvailable(requireContext())) {
            productsListViewModel.getAllCategories().asLiveData().observe(viewLifecycleOwner) {
                when (it) {
                    is ApiStatus.Success -> {
                        val data = it.data
                        Log.d(TAG, "Called_API_Status  getAllCategories: Success: $data")
                        if (!data.isNullOrEmpty()) {
                            val categoriesList = data as ArrayList<String>
                            productsListViewModel.storeCustomCategoryList(categoriesList)
                        } else {
                            Utils.showLongToast(
                                requireContext(),
                                "Something went wrong in the categories"
                            )
                        }
                    }
                    is ApiStatus.Failure -> {
                        Log.d(
                            TAG,
                            "Called_API_Status  getAllCategories: Failure:  message:${it.message}         code:${it.code}"
                        )
                    }
                    ApiStatus.Loading -> {
                        Log.d(TAG, "Called_API_Status  getAllCategories: Loading")
                    }
                }
            }
        } else {
            Utils.showLongToast(requireContext(), "Please check your Internet Connection.")
            productsListViewModel.setRetry(true)
        }
    }

    private fun getProducts(sort: String?) {
        if (Utils.isInternetAvailable(requireContext())) {
            productsListViewModel.getProducts(50, sort).asLiveData()
                .observe(viewLifecycleOwner) {
                    when (it) {
                        is ApiStatus.Success -> {
                            val data = it.data
                            Log.d(TAG, "Called_API_Status  getProducts: Success: $data")
                            if (!data.isNullOrEmpty()) {
                                productsListViewModel.saveProductsToLiveData(data)
                            } else {
                                Utils.showLongToast(
                                    requireContext(),
                                    "Something went wrong in the products"
                                )
                            }
                        }
                        is ApiStatus.Failure -> {
                            Log.d(
                                TAG,
                                "Called_API_Status  getProducts: Failure:  message:${it.message}         code:${it.code}"
                            )
                            productsListViewModel.setRetry(true)
                        }
                        ApiStatus.Loading -> {
                            Log.d(TAG, "Called_API_Status  getProducts: Loading")
                        }
                    }
                }
        } else {
            Utils.showLongToast(requireContext(), "Please check your Internet Connection.")
        }
    }

    private fun getSpecificCategoryProducts(categoryModel: CategoryModel) {
        if (Utils.isInternetAvailable(requireContext())) {
            productsListViewModel.updateCategoriesRowItem(categoryModel)
            productsListViewModel.getSpecificCategoryProducts(categoryModel.category_name).asLiveData()
                .observe(viewLifecycleOwner) {
                    when (it) {
                        is ApiStatus.Success -> {
                            val data = it.data
                            Log.d(
                                TAG,
                                "Called_API_Status  getSpecificCategoryProducts: Success: $data"
                            )
                            if (!data.isNullOrEmpty()) {
                                productsListViewModel.saveProductsToLiveData(data)
                                productsListViewModel.updateCartValues()
                            } else {
                                Utils.showLongToast(
                                    requireContext(),
                                    "Something went wrong in the products"
                                )
                            }
                        }
                        is ApiStatus.Failure -> {
                            Log.d(
                                TAG,
                                "Called_API_Status  getSpecificCategoryProducts: Failure:  message:${it.message}         code:${it.code}"
                            )
                            productsListViewModel.setRetry(true)
                        }
                        ApiStatus.Loading -> {
                            Log.d(TAG, "Called_API_Status  getSpecificCategoryProducts: Loading")
                        }
                    }
                }
        } else {
            Utils.showLongToast(requireContext(), "Please check your Internet Connection.")
        }
    }

    override fun onItemClick(data: Any) {
        if (data is CategoryModel) {
            getSpecificCategoryProducts(data)
        } else if (data is Int) {
            val bundle = Bundle()
            bundle.putInt("product_id", data)
            navController.navigate(R.id.action_products_list_to_product_details_fragment, bundle)
        } else if (data is QuantityClickActionModel) {
            when (data.action) {
                "addThisItem" -> {
                    Log.d(TAG, "onItemClick: addThisItem")
                    val cartItemModel = CartItemModel(
                        data.productId,
                        data.productName,
                        data.productImage,
                        data.quantity,
                        data.price,
                        data.totalItemPrice,
                        data.position
                    )
                    productsListViewModel.updateItemQuantity(
                        ArrayList(productsListViewModel.productsLiveData.value), data.position,
                        data.quantity, cartItemModel, NotifyItemModel(true, data.position), false
                    )
                }
                "increaseItemQuantity" -> {
                    Log.d(TAG, "onItemClick: increaseItemQuantity")
                    val cartItemModel = CartItemModel(
                        data.productId,
                        data.productName,
                        data.productImage,
                        data.quantity,
                        data.price,
                        data.totalItemPrice,
                        data.position
                    )
                    productsListViewModel.updateItemQuantity(
                        ArrayList(productsListViewModel.productsLiveData.value), data.position,
                        data.quantity, cartItemModel, NotifyItemModel(true, data.position), false
                    )
                }
                "decreaseItemQuantity" -> {
                    Log.d(TAG, "onItemClick: decreaseItemQuantity")
                    val cartItemModel = CartItemModel(
                        data.productId,
                        data.productName,
                        data.productImage,
                        data.quantity,
                        data.price,
                        data.totalItemPrice,
                        data.position
                    )
                    productsListViewModel.updateItemQuantity(
                        ArrayList(productsListViewModel.productsLiveData.value), data.position,
                        data.quantity, cartItemModel, NotifyItemModel(true, data.position), false
                    )
                }
                "removeThisItem" -> {
                    Log.d(TAG, "onItemClick: removeThisItem")
                    val cartItemModel = CartItemModel(
                        data.productId,
                        data.productName,
                        data.productImage,
                        data.quantity,
                        data.price,
                        data.totalItemPrice,
                        data.position
                    )
                    productsListViewModel.updateItemQuantity(
                        ArrayList(productsListViewModel.productsLiveData.value), data.position,
                        data.quantity, cartItemModel, NotifyItemModel(true, data.position), true
                    )
                }
                else -> {
                    Log.d(TAG, "onItemClick: in else condition")
                }
            }

        }
    }


}