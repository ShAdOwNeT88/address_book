package com.iovineantonio.address_book.features.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.iovineantonio.address_book.BuildConfig
import com.iovineantonio.address_book.R
import com.iovineantonio.address_book.databinding.ScreenAboutBinding
import com.iovineantonio.address_book.features.Navigator
import net.nightwhistler.htmlspanner.HtmlSpanner
import org.koin.android.ext.android.inject

class AboutScreen : Fragment() {
    private lateinit var binding: ScreenAboutBinding
    private val navigator: Navigator by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = ScreenAboutBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.aboutDescription.text = ""
        binding.aboutAppVersion.text = resources.getString(R.string.app_version, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)

        return root
    }
}
