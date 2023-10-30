package com.example.shakirtestproject.ui.checkout

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.shakirtestproject.Application
import com.example.shakirtestproject.OnItemClickListener
import com.example.shakirtestproject.R
import com.example.shakirtestproject.adapter.CartItemAdapter
import com.example.shakirtestproject.databinding.FragmentCheckoutBinding
import com.example.shakirtestproject.models.QuantityClickActionModel
import com.example.shakirtestproject.utils.Utils

private const val TAG = "CheckoutFragment"
class CheckoutFragment : Fragment(), OnItemClickListener {

    private lateinit var cartItemAdapter: CartItemAdapter
    private var _binding: FragmentCheckoutBinding? = null

    private val binding get() = _binding!!

    private lateinit var checkoutViewModel: CheckoutViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCheckoutBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = (requireActivity().application as Application).appRepository

        checkoutViewModel = ViewModelProvider(
            this,
            CheckoutViewModelFactory(repository)
        )[CheckoutViewModel::class.java]

        checkoutViewModel.getCartItems()

        attachObservers()

        binding.btnBookOrder.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Enter your address.")
            val edtLocation = EditText(requireContext())
            edtLocation.inputType = InputType.TYPE_CLASS_TEXT
            builder.setView(edtLocation)
            builder.setCancelable(false)
            builder.setPositiveButton("Book Order"
            ) { _, _ ->
                val location = edtLocation.text.toString()
                if (location.isNotBlank()) {
                    Utils.showLongToast(requireContext(), "Your order has been booked successfully!")
                } else {
                    Utils.showLongToast(requireContext(), "Enter your address first.")
                }
            }
            builder.setNegativeButton("Cancel"
            ) { dialog, _ -> dialog.cancel() }

            builder.show()
        }

    }

    @SuppressLint("SetTextI18n")
    private fun attachObservers() {
        checkoutViewModel.cartItemList.observe(viewLifecycleOwner) {
            if (it.size>0) {
                var itemCount = 0
                for (item in it) {
                    itemCount += item.quantity
                }
                binding.tvTotalItems.text = "Total Items: $itemCount"
                cartItemAdapter = CartItemAdapter(this@CheckoutFragment, it)
                binding.rvCartItems.adapter = cartItemAdapter
                checkoutViewModel.calculateCartTotal()
            } else {
                val navController = findNavController()
                navController.popBackStack()
                Utils.showLongToast(requireContext(),"Add items to your cart first")
            }
        }

        checkoutViewModel.cartTotalLiveData.observe(viewLifecycleOwner) {
            binding.tvCartTotal.text = "${getString(R.string.rupee_symbol)} $it"
            binding.tvTotalBill.text = "${getString(R.string.rupee_symbol)} ${Utils.roundOffNumber(it)}"
        }
    }

    override fun onItemClick(data: Any) {
        if (data is QuantityClickActionModel) {
            Log.d(TAG, "onItemClick: ${data.totalItemPrice}")
            when (data.action) {
                "increaseItemQuantity" -> {
                    checkoutViewModel.updateCartList(data.position, data)
                }
                "decreaseItemQuantity" -> {
                    checkoutViewModel.updateCartList(data.position, data)
                }
                "removeThisItem" -> {
                    Log.d(TAG, "onItemClick: removeThisItem")
                    checkoutViewModel.deleteCartItemFromDatabase(data)
                }
                else -> {
                    Log.d(TAG, "onItemClick: in else condition")
                }
            }
        }
    }
}