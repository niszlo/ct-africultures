package com.pigeoff.africultures.fragments

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.pigeoff.africultures.R
import de.psdev.licensesdialog.LicensesDialog
import de.psdev.licensesdialog.licenses.ApacheSoftwareLicense20
import de.psdev.licensesdialog.model.Notice
import de.psdev.licensesdialog.model.Notices

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val developer: Preference = findPreference("libraries")!!
        developer.setOnPreferenceClickListener {
            val notices = Notices()
            notices.addNotice(Notice("Android Jetpack Library", "https://developer.android.com/jetpack", "", ApacheSoftwareLicense20()))
            notices.addNotice(Notice("Google Material Components", "https://material.io/develop/android", "", ApacheSoftwareLicense20()))
            notices.addNotice(Notice("Retrofit", "https://square.github.io/retrofit/", "Copyright 2013 Square, Inc.", ApacheSoftwareLicense20()))
            notices.addNotice(Notice("Picasso", "https://square.github.io/picasso/", "Copyright 2013 Square, Inc.", ApacheSoftwareLicense20()))

            LicensesDialog.Builder(requireActivity())
                    .setTitle(R.string.settings_item_libraries)
                    .setNotices(notices)
                    .setIncludeOwnLicense(true)
                    .build()
                    .show()
            true
        }
    }
}