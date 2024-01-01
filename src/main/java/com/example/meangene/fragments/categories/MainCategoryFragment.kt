package com.example.meangene.fragments.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.get
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meangene.R
import com.example.meangene.adapters.BaseProductAdapter
import com.example.meangene.adapters.BestDealsAdapter
import com.example.meangene.adapters.SpecialProductsAdapter
import com.example.meangene.databinding.FragmentMainCategoryBinding
import com.example.meangene.util.Resource
import com.example.meangene.util.showBottomNavigationView
import com.example.meangene.viewmodel.MainCategoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private val TAG = "MainCategoryFragment"
@AndroidEntryPoint
class MainCategoryFragment: Fragment(R.layout.fragment_main_category) {
    private lateinit var binding :  FragmentMainCategoryBinding
    private lateinit var specialProductsAdapter:SpecialProductsAdapter
    private lateinit var bestDealsAdapter:BestDealsAdapter
    private lateinit var bestProductsAdapter: BaseProductAdapter



    private val viewModel by viewModels<MainCategoryViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSpecialProductsRv()
        setupBestDealsRv()
        setupBestProductsRv()

        specialProductsAdapter.onClick = {
            val b = Bundle().apply { putParcelable("product",it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,b)
        }

        bestDealsAdapter.onClick = {
            val b = Bundle().apply { putParcelable("product",it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,b)
        }

        bestProductsAdapter.onClick = {
            val b = Bundle().apply { putParcelable("product",it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,b)
        }



        lifecycleScope.launch {
            viewModel.specialProducts.collectLatest {
                when (it){
                    is Resource.Loading ->{
                        showLoading()
                    }
                    is Resource.Success ->{
                        specialProductsAdapter.differ.submitList(it.data)
                        hideLoading()
                    }
                    is Resource.Error ->{
                        hideLoading()
                        Log.e(TAG, it.message.toString())
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }

            }

        }
        lifecycleScope.launch {
            viewModel.bestDealsProducts.collectLatest {
                when (it){
                    is Resource.Loading ->{
                        showLoading()
                    }
                    is Resource.Success ->{
                        bestDealsAdapter.differ.submitList(it.data)
                        hideLoading()
                    }
                    is Resource.Error ->{
                        hideLoading()
                        Log.e(TAG, it.message.toString())
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }

            }

        }
        lifecycleScope.launch {
            viewModel.baseProducts.collectLatest {
                when (it){
                    is Resource.Loading ->{
                        binding.bestProductsProgessbar.visibility = View.VISIBLE
                    }
                    is Resource.Success ->{
                        bestProductsAdapter.differ.submitList(it.data)
                        binding.bestProductsProgessbar.visibility = View.GONE
                    }
                    is Resource.Error ->{
                        binding.bestProductsProgessbar.visibility = View.GONE
                        Log.e(TAG, it.message.toString())
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }

            }

        }
        binding.nestedScrollMainCategory.setOnScrollChangeListener (
            NestedScrollView.OnScrollChangeListener{ v,_, scrollY,_,_, ->

                if (v.getChildAt(0).bottom <= v.height + scrollY){

                    viewModel.fetchBaseProducts()
                }
            })

    }

    private fun setupBestProductsRv() {
        bestProductsAdapter = BaseProductAdapter()
        binding.rvBestProducts.apply {
            layoutManager = GridLayoutManager(
                requireContext(), 2,GridLayoutManager.VERTICAL,
                false
            )
            adapter = bestProductsAdapter

        }    }

    private fun setupBestDealsRv() {
        bestDealsAdapter = BestDealsAdapter()
        binding.rvBestDealsProducts.apply {
            layoutManager = LinearLayoutManager(
                requireContext(), LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = bestDealsAdapter

        }
    }

    private fun hideLoading() {
        binding.mainCategoryProgessbar.visibility = View.GONE
    }

    private fun showLoading() {
        binding.mainCategoryProgessbar.visibility = View.VISIBLE
    }

    private fun setupSpecialProductsRv() {
        specialProductsAdapter = SpecialProductsAdapter()
        binding.rvSpecialProducts.apply{
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,
                false)
            adapter = specialProductsAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        showBottomNavigationView()
    }
}