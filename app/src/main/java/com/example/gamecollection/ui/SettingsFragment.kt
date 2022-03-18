package com.example.gamecollection.ui

import android.os.Bundle
import androidx.preference.MultiSelectListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.gamecollection.R

class SettingsFragment : PreferenceFragmentCompat() {
   override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey:String?) {
       setPreferencesFromResource(R.xml.settings, rootKey)
   }
}