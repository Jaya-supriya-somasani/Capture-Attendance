package com.example.userattendence.presentation.user_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.userattendence.data.repository.AttendanceRepository
import com.example.userattendence.data.local.entity.AttendanceRecord
import com.example.userattendence.domain.LocationData
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class AttendanceViewModel @Inject constructor(
    private val repository: AttendanceRepository
) : ViewModel() {

    val records: StateFlow<List<AttendanceRecord>> = repository
        .getAllRecords()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun saveAttendance(imageUri: String, locationData: LocationData) {
        viewModelScope.launch {
            repository.saveAttendance(imageUri, locationData)
        }
    }

    fun clearAllRecords() {
        viewModelScope.launch {
            repository.clearAllRecords()
        }
    }

    class Factory @Inject constructor(
        private val repository: AttendanceRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return AttendanceViewModel(repository) as T
        }
    }
}