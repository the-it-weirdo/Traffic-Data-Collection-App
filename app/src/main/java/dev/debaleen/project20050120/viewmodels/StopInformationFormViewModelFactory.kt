package dev.debaleen.project20050120.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.debaleen.project20050120.repositories.StopInformationRepository

class StopInformationFormViewModelFactory(private val repository: StopInformationRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StopInformationFormViewModel::class.java)) {
            return StopInformationFormViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class.")
    }
}