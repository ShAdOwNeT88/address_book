package com.iovineantonio.address_book.features.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.iovineantonio.address_book.BuildConfig
import com.iovineantonio.address_book.R
import com.iovineantonio.address_book.databinding.ScreenAboutBinding
import com.iovineantonio.address_book.utils.exhaustive
import com.iovineantonio.address_book.utils.visible
import net.nightwhistler.htmlspanner.HtmlSpanner
import org.koin.android.ext.android.inject
import timber.log.Timber

class AboutScreen : Fragment() {
    private lateinit var binding: ScreenAboutBinding
    private val aboutViewModel: AboutViewModel by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = ScreenAboutBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupAboutViewModel()
        binding.aboutTitle.text = HtmlSpanner().fromHtml(resources.getString(R.string.about_app_title))
        binding.aboutDescription.text = HtmlSpanner().fromHtml(resources.getString(R.string.about_description))
        binding.aboutAppVersion.text = resources.getString(R.string.app_version, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)

        binding.resetContactsAction.setOnClickListener {
            aboutViewModel.send(AboutEvent.RequestToDeleteAllContacts)
        }

        return root
    }

    private fun setupAboutViewModel() {
        aboutViewModel.observe(lifecycleScope) { state ->
            when (state) {
                is AboutState.DeletedAllContacts -> {
                    hideProgress()
                    Toast.makeText(requireContext(), getString(R.string.about_deleted_all_contacts), Toast.LENGTH_LONG).show()
                }

                is AboutState.Error -> showError(state.error)
                is AboutState.InProgress -> showProgress()
            }.exhaustive
        }
    }

    private fun showError(error: Throwable) {
        hideProgress()
        Timber.e("[AboutScreen] Error: $error")
        Toast.makeText(requireContext(), getString(R.string.generic_error), Toast.LENGTH_LONG).show()
    }

    private fun showProgress() {
        binding.progressBar.visible(true)
    }

    private fun hideProgress() {
        binding.progressBar.visible(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        aboutViewModel.send(AboutEvent.UnsubscribeFromEvents)
    }
}
