package com.example.meangene.fragments.shopping

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.meangene.R
import com.example.meangene.adapters.SearchAdapter
import com.example.meangene.databinding.FragmentSearchBinding
import com.example.meangene.util.Resource
import com.example.meangene.util.showBottomNavigationView
import com.example.meangene.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment: Fragment(R.layout.fragment_search) {
    private lateinit var binding: FragmentSearchBinding
    protected val  searchAdapter: SearchAdapter by lazy { SearchAdapter() }
    private val viewModel:SearchViewModel by viewModels<SearchViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSearchProductsRv()

        var searchString = arguments?.getString("searchString")

        binding.apply {
            if (searchString != "Search Box" ) {
                searchSearchBarTextView.setText(searchString)
                if (searchString != null) {
                    viewModel.getSearch(searchString)
                }

            }

                searchSearchButton.setOnClickListener {
                    //search bar val

                    var homeSearchBarVar = searchSearchBarTextView.text.toString()
                    Toast.makeText(requireContext(), homeSearchBarVar, Toast.LENGTH_SHORT).show()

                    viewModel.getSearch(homeSearchBarVar)
                }
            }

        searchAdapter.onClick = {
            val b = Bundle().apply { putParcelable("product",it) }
            findNavController().navigate(R.id.action_searchFragment_to_productDetailsFragment,b)
        }

        lifecycleScope.launch {
            viewModel.search.collectLatest {
                when (it){
                    is Resource.Loading ->{
                        binding.bestProductsProgressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success ->{
                        binding.bestProductsProgressBar.visibility = View.GONE

                        searchAdapter.differ.submitList(it.data)
                    }
                    is Resource.Error ->{
                        binding.bestProductsProgressBar.visibility = View.GONE

                        Log.e(TAG, it.message.toString())
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }


            }

        }
    }
    private fun setupSearchProductsRv() {
        binding.rvSearchProducts.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = searchAdapter
        }
    }
    override fun onResume() {
        super.onResume()
        showBottomNavigationView()
    }
}
