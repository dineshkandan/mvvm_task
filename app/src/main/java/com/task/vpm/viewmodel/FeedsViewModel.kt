package com.task.vpm.viewmodel

import android.widget.CompoundButton
import androidx.databinding.BindingAdapter
import androidx.lifecycle.*
import com.task.vpm.db.Feeds
import com.task.vpm.db.FeedsRepository
import com.task.vpm.utils.Event
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.datetime.*


class FeedsViewModel(private val repository: FeedsRepository) : ViewModel() {
    private var isUpdateOrDelete = false
    private lateinit var feedToUpdateOrDelete: Feeds
    val inputName = MutableLiveData<String>()
    val inputLive = MutableLiveData<Boolean>()
    val saveOrUpdateButtonText = MutableLiveData<String>()
    val clearAllOrDeleteButtonText = MutableLiveData<String>()
    val isCreatedUpdated = MutableLiveData<Boolean>()

    private val statusMessage = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>>
        get() = statusMessage

    init {
        saveOrUpdateButtonText.value = "Create"
        clearAllOrDeleteButtonText.value = "Clear All"
        inputLive.value = false
    }

    fun initCreate() {
        inputName.value = ""
        inputLive.value = false
        isUpdateOrDelete = false
        saveOrUpdateButtonText.value = "Create"
        clearAllOrDeleteButtonText.value = "Clear All"
    }

    fun initUpdateAndDelete(feed: Feeds) {
        inputName.value = feed.name
        inputLive.value = feed.isLive
        isUpdateOrDelete = true
        feedToUpdateOrDelete = feed
        saveOrUpdateButtonText.value = "Update"
        clearAllOrDeleteButtonText.value = "Delete"
    }

    fun saveOrUpdate() {
        if (inputName.value == null || inputName.value!!.isEmpty()) {
            statusMessage.value = Event("Please enter room name")
        }  else {
            if (isUpdateOrDelete) {
                feedToUpdateOrDelete.name = inputName.value!!
                feedToUpdateOrDelete.isLive = inputLive.value!!
                updateSubscriber(feedToUpdateOrDelete)
            } else {
                val name = inputName.value!!
                val isLive = inputLive.value!!
                val currentMoment: Instant = Clock.System.now()
                val datetimeInUtc: LocalDateTime = currentMoment.toLocalDateTime(TimeZone.UTC)
                insertSubscriber(Feeds(0, name,isLive, datetimeInUtc.toString()))
                inputName.value = ""
                inputLive.value = false
            }
        }
    }

    private fun insertSubscriber(subscriber: Feeds) = viewModelScope.launch {
        val newRowId = repository.insert(subscriber)
        if (newRowId > -1) {
            statusMessage.value = Event("Feed Added Successfully $newRowId")
            isCreatedUpdated.value = true
        } else {
            statusMessage.value = Event("Error Occurred")
        }
    }


    private fun updateSubscriber(subscriber: Feeds) = viewModelScope.launch {
        val noOfRows = repository.update(subscriber)
        if (noOfRows > 0) {
            inputName.value = ""
            inputLive.value = false
            isUpdateOrDelete = false
            saveOrUpdateButtonText.value = "Create"
            clearAllOrDeleteButtonText.value = "Clear All"
            isCreatedUpdated.value = true
            statusMessage.value = Event("Updated Successfully")
        } else {
            statusMessage.value = Event("Error Occurred")
        }
    }

    fun getSavedSubscribers() = liveData {
        repository.subscribers.collect {
            emit(it)
        }
    }

    fun clearAllOrDelete() {
        if (isUpdateOrDelete) {
            deleteSubscriber(feedToUpdateOrDelete)
        } else {
            clearAll()
        }
    }

    private fun deleteSubscriber(subscriber: Feeds) = viewModelScope.launch {
        val noOfRowsDeleted = repository.delete(subscriber)
        if (noOfRowsDeleted > 0) {
            inputName.value = ""
            inputLive.value = false
            isUpdateOrDelete = false
            isCreatedUpdated.value = true
            saveOrUpdateButtonText.value = "Create"
            clearAllOrDeleteButtonText.value = "Clear All"
            statusMessage.value = Event("Deleted Successfully")
        } else {
            statusMessage.value = Event("Error Occurred")
        }
    }

    private fun clearAll() = viewModelScope.launch {
        val noOfRowsDeleted = repository.deleteAll()
        if (noOfRowsDeleted > 0) {
            statusMessage.value = Event("$noOfRowsDeleted Subscribers Deleted Successfully")
        } else {
            statusMessage.value = Event("Error Occurred")
        }
    }

    @BindingAdapter("onUserCheckedChange")
    fun onCheckedChange(button: CompoundButton, check: Boolean) {
        inputLive.value = check
    }

}