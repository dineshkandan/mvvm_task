package com.task.vpm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.task.vpm.db.FeedsRepository
import java.lang.IllegalArgumentException

class FeedsViewModelFactory(
        private val repository: FeedsRepository
        ):ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
     if(modelClass.isAssignableFrom(FeedsViewModel::class.java)){
         return FeedsViewModel(repository) as T
     }
        throw IllegalArgumentException("Unknown View Model class")
    }

}