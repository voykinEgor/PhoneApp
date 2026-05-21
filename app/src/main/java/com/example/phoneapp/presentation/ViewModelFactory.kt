package com.example.phoneapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider


class ViewModelFactory @Inject constructor(
    private val viewModelProviders: @JvmSuppressWildcards Map<Class<out ViewModel>, Provider<ViewModel>>
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val provider = viewModelProviders[modelClass]
            ?: viewModelProviders.entries.firstOrNull { (viewModelClass, _) ->
                modelClass.isAssignableFrom(viewModelClass)
            }?.value
            ?: throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")

        @Suppress("UNCHECKED_CAST")
        return provider.get() as T
    }
}