package com.example.mibibliotecav2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_bibliotecas.*

class BibliotecasActivity : AppCompatActivity() {

    private val TAB_TITLES = arrayOf(
        R.drawable.ic_biblio,
        R.drawable.ic_place
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bibliotecas)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val sectionsPagerAdapter =
            SectionsPagerAdapter(
                this,
                supportFragmentManager
            )
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
        setupTabIcons()
    }

    private fun setupTabIcons() {
        tabs.getTabAt(0)?.setIcon(TAB_TITLES[0])
        tabs.getTabAt(1)?.setIcon(TAB_TITLES[1])
    }

}