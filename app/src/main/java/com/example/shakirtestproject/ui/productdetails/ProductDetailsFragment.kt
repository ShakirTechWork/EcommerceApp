package com.example.shakirtestproject.ui.productdetails

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import coil.load
import coil.request.CachePolicy
import com.example.shakirtestproject.Application
import com.example.shakirtestproject.R
import com.example.shakirtestproject.api.ApiStatus
import com.example.shakirtestproject.databinding.FragmentProductDetailsBinding
import com.example.shakirtestproject.databinding.FragmentProductsListBinding
import com.example.shakirtestproject.ui.login.UserLoginViewModel
import com.example.shakirtestproject.ui.login.UserLoginViewModelFactory
import com.example.shakirtestproject.utils.Utils

private const val TAG = "ProductDetailsFragment"
class ProductDetailsFragment : Fragment() {
    private var productId: Int = 0
    private lateinit  var productDetailsViewModel: ProductDetailsViewModel

    private var _binding: FragmentProductDetailsBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            productId = it.getInt("product_id")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProductDetailsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val repository = (requireActivity().application as Application).appRepository

        productDetailsViewModel = ViewModelProvider(
            this,
            ProductDetailsViewModelFactory(repository)
        )[ProductDetailsViewModel::class.java]

        getProductDetails(productId)

        productDetailsViewModel.isRetryLiveData.observe(viewLifecycleOwner) {
            productDetailsViewModel.setRetry(false)
            if (it) {
                binding.tvRetry.visibility = View.VISIBLE
            } else {
                binding.tvRetry.visibility = View.GONE
            }
        }
        
    }

    @SuppressLint("SetTextI18n")
    private fun getProductDetails(productId: Int) {
        if (Utils.isInternetAvailable(requireContext())) {
            productDetailsViewModel.getProductsDetails(productId).asLiveData().observe(viewLifecycleOwner) {
                when (it) {
                    is ApiStatus.Success -> {
                        val data = it.data
                        Log.d(TAG, "Called_API_Status  getProductsDetails: Success: $data")
                        if (data!=null) {
                            binding.imgProduct.load(data.image) {
                                crossfade(true)
                                crossfade(800)
                                memoryCachePolicy(CachePolicy.ENABLED)
                            }
                            binding.tvRating.text = "${data.rating.count} customers rated ${data.rating.rate}"
                            binding.tvProductName.text = data.title
                            binding.tvProductDesciption.text = data.description
                            binding.tvProductCategory.text = data.category
                            binding.tvProductPrice.text = "${getString(R.string.rupee_symbol)} ${data.price}"
                        } else {
                            Utils.showLongToast(requireContext(), "Something went wrong!")
                        }
                    }
                    is ApiStatus.Failure -> {
                        Log.d(TAG,
                            "Called_API_Status  getProductsDetails: Failure:  message:${it.message}         code:${it.code}"
                        )
                        productDetailsViewModel.setRetry(true)
                    }
                    ApiStatus.Loading -> {
                        Log.d(TAG, "Called_API_Status  getProductsDetails: Loading")
                    }
                }
            }
        } else {
            Utils.showLongToast(requireContext(), "Please check your Internet Connection.")
            productDetailsViewModel.setRetry(true)
        }
    }

}