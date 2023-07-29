package edu.skillbox.skillcinema.presentation.allStaff

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.skillbox.skillcinema.data.RepositoryFilmAndSerial
import edu.skillbox.skillcinema.models.filmAndSerial.staff.StaffTable
import edu.skillbox.skillcinema.presentation.ViewModelState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "AllStaff.VM"

class AllStaffViewModel(
    private val repositoryFilmAndSerial: RepositoryFilmAndSerial
) : ViewModel() {

    private val _state = MutableStateFlow<ViewModelState>(
        ViewModelState.Loading
    )
    val state = _state.asStateFlow()

    var filmId: Int = 0
    var filmName: String = ""
    var staffType: Boolean = false

    var actorsList: List<StaffTable> = emptyList()
    var staffList: List<StaffTable> = emptyList()

    fun loadStaff() {
        Log.d(TAG, "loadAllStaff($filmId)")
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = ViewModelState.Loading
            var error = false

            val allStaffTableListLoadResult: Pair<List<StaffTable>?, Boolean> = repositoryFilmAndSerial.getStaffTableList(filmId)
            val allStaffTableList: List<StaffTable>? = allStaffTableListLoadResult.first
            if (allStaffTableListLoadResult.second || allStaffTableList == null) {
                Log.d(TAG, "Ошибка загрузки")
                error = true
            } else {
                val actorsAndStaff = repositoryFilmAndSerial.divideStaffByType(allStaffTableList)
                actorsList = actorsAndStaff.actorsList
                staffList = actorsAndStaff.staffList
            }

            if (error) {
                _state.value = ViewModelState.Error
                Log.d(TAG, "Состояние ошибки")
            } else {
                _state.value = ViewModelState.Loaded
                Log.d(TAG, "Состояние успешного завершения загрузки")
            }
        }
    }
}