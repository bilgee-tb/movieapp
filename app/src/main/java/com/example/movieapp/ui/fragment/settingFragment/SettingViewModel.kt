package com.example.movieapp.ui.fragment.settingFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(private val auth: FirebaseAuth) : ViewModel(){

        private val _logoutStatus = MutableLiveData<Boolean>()
        val logoutStatus: LiveData<Boolean> = _logoutStatus

        fun logout() {
            auth.signOut()
            _logoutStatus.value = true
        }


}
