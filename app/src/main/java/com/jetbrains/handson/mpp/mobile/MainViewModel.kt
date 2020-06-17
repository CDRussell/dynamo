package com.jetbrains.handson.mpp.mobile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cdrussell.dynamo.PasswordGenerator
import com.cdrussell.dynamo.PasswordGenerator.PasswordResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainViewModel : ViewModel() {

    val viewState: MutableLiveData<ViewState> = MutableLiveData()

    fun generateNewPassword(passwordConfiguration: PasswordGenerator.PasswordConfiguration) {
        viewModelScope.launch {
            val result = PasswordGenerator().generatePassword(passwordConfiguration)

            withContext(Dispatchers.Main) {
                viewState.value = ViewState(result = result)
            }
        }
    }
}

data class ViewState(val result: PasswordResult)