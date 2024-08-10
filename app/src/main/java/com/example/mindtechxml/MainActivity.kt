package com.example.mindtechxml

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.example.mindtechxml.DataProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var listAdapter: ListAdapter
    private lateinit var searchView: SearchView
    private lateinit var currentList: List<ListItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        val tabLayout: TabLayout = findViewById(R.id.tabLayout)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        searchView = findViewById(R.id.searchView)
        val fab: FloatingActionButton = findViewById(R.id.fab)

        recyclerView.isNestedScrollingEnabled = false

        val images = listOf(
            R.drawable.car,
            R.drawable.city,
            R.drawable.forest,
            R.drawable.river,
            R.drawable.fruit
        )

        val allLists = listOf(
            DataProvider.carList,
            DataProvider.cityList,
            DataProvider.forestList,
            DataProvider.riverList,
            DataProvider.fruitList
        )

        currentList = allLists[0]
        listAdapter = ListAdapter(currentList)
        recyclerView.adapter = listAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = ImageCarouselAdapter(images)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.icon = if (position == 0) {
                getDrawable(R.drawable.dot_selected)
            } else {
                getDrawable(R.drawable.dot_unselected)
            }
        }.attach()

        // Update the dot indicators based on page changes
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                for (i in 0 until tabLayout.tabCount) {
                    val tab = tabLayout.getTabAt(i)
                    tab?.icon = if (i == position) {
                        getDrawable(R.drawable.dot_selected)
                    } else {
                        getDrawable(R.drawable.dot_unselected)
                    }
                }
                currentList = allLists[position]
                listAdapter = ListAdapter(currentList)
                listAdapter.updateItems(currentList)
                listAdapter.filter.filter(searchView.query)
                recyclerView.adapter = listAdapter
            }
        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d("MainActivity", "Query Text Changed: $newText")
                listAdapter.filter.filter(newText)
                return true
            }
        })

        fab.setOnClickListener {
            showBottomSheetDialog()
        }
    }

    private fun showBottomSheetDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogView)

        val itemCountTextView: TextView = dialogView.findViewById(R.id.itemCount)
        val characterOccurrencesTextView: TextView = dialogView.findViewById(R.id.characterOccurrences)

        val itemCount = currentList.size
        val characterOccurrences = getTopCharacters(currentList)

        itemCountTextView.text = "Item count: $itemCount"
        characterOccurrencesTextView.text = "Top characters: $characterOccurrences"

        dialog.show()
    }

    private fun getTopCharacters(items: List<ListItem>): String {
        val frequencyMap = mutableMapOf<Char, Int>()
        items.flatMap { it.title.toLowerCase().toList() }.forEach { char ->
            frequencyMap[char] = frequencyMap.getOrDefault(char, 0) + 1
        }

        val topCharacters = frequencyMap.entries.sortedByDescending { it.value }
            .take(3)
            .joinToString(", ") { "${it.key} = ${it.value}" }

        return topCharacters
    }
}