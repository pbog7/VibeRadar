package com.pbogdev.homescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pbogdev.domain.usecase.GetExamplesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    val getExamplesUseCase: GetExamplesUseCase
) : ViewModel() {
    private val _viewState = MutableStateFlow(HomeViewState())
    val viewState: StateFlow<HomeViewState> = _viewState

    init {
        viewModelScope.launch {
            getExamplesUseCase()
        }
    }

}