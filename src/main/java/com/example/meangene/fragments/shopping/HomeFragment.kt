package com.example.meangene.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.meangene.R
import com.example.meangene.adapters.HomeViewpagerAdapter
import com.example.meangene.databinding.FragmentHomeBinding
import com.example.meangene.fragments.categories.AccessoryFragment
import com.example.meangene.fragments.categories.ChairFragment
import com.example.meangene.fragments.categories.CupboardFragment
import com.example.meangene.fragments.categories.FurnitureFragment
import com.example.meangene.fragments.categories.MainCategoryFragment
import com.example.meangene.fragments.categories.TableFragment
import com.example.meangene.util.showBottomNavigationView
import com.example.meangene.viewmodel.SearchViewModel
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment: Fragment(R.layout.fragment_home) {
    private lateinit var binding:FragmentHomeBinding
    private val viewModel: SearchViewModel by viewModels<SearchViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoriesFragment = arrayListOf<Fragment>(
            MainCategoryFragment(),
            ChairFragment(),
            CupboardFragment(),
            TableFragment(),
            AccessoryFragment(),
            FurnitureFragment()
        )


        binding.apply {
            homeSearchButton.setOnClickListener {
                //search bar val
                var homeSearchBarVar = homeSearchBarTextView.text.toString()
                Toast.makeText(requireContext(), homeSearchBarVar, Toast.LENGTH_SHORT).show()

                var b = Bundle().apply { putString("searchString", homeSearchBarVar) }
                findNavController().navigate(R.id.searchFragment, b)

            }
        }

        binding.homeViewPager.isUserInputEnabled = false

        val viewpager2Adapter = HomeViewpagerAdapter(
            categoriesFragment,
            childFragmentManager,
            lifecycle)

        binding.homeViewPager.adapter = viewpager2Adapter
        TabLayoutMediator(binding.homeTableLayout, binding.homeViewPager){ tab, position->
            when(position){
                0-> tab.text = "Main"
                1-> tab.text = "Chair"
                2-> tab.text = "Cupboard"
                3-> tab.text = "Table"
                4-> tab.text = "Accessories"
                5-> tab.text = "Furniture"

            }
        }.attach()
    }
    override fun onResume() {
        super.onResume()
        showBottomNavigationView()
    }
}