package com.example.feature.family

import com.example.core.di.AppContainer
import com.example.core.viewmodel.BaseViewModel
import com.example.data.database.entity.ChildEntity
import com.example.data.database.entity.FamilyEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class FamilyUiState(
    val family: FamilyEntity? = null,
    val children: List<ChildEntity> = emptyList(),
    val showAddChildDialog: Boolean = false,
    val newChildName: String = "",
    val newChildAvatar: String = "🌱",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class FamilyViewModel(
    private val appContainer: AppContainer
) : BaseViewModel(appContainer.dispatchers, appContainer.logger) {

    private val _uiState = MutableStateFlow(FamilyUiState(isLoading = true))
    val uiState: StateFlow<FamilyUiState> = _uiState.asStateFlow()

    init {
        loadFamilyData()
    }

    fun showAddChildDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showAddChildDialog = show, newChildName = "")
    }

    fun updateNewChildName(name: String) {
        _uiState.value = _uiState.value.copy(newChildName = name)
    }

    fun updateNewChildAvatar(avatar: String) {
        _uiState.value = _uiState.value.copy(newChildAvatar = avatar)
    }

    fun loadFamilyData() {
        launchOnIO {
            appContainer.familyRepository.getFamily().collect { family ->
                if (family != null) {
                    appContainer.familyRepository.getChildren(family.id).collect { children ->
                        _uiState.value = FamilyUiState(
                            family = family,
                            children = children,
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            }
        }
    }

    fun addChild() {
        launchOnIO {
            val familyId = _uiState.value.family?.id ?: return@launchOnIO
            val name = _uiState.value.newChildName.ifBlank { "Kid" }
            val avatar = _uiState.value.newChildAvatar

            appContainer.familyRepository.addChild(familyId, name, avatar)
            appContainer.historyRepository.logActivity(
                familyId = familyId,
                childId = null,
                type = "CHILD_ADDED",
                title = "New Child Added",
                description = "$name joined the family."
            )

            _uiState.value = _uiState.value.copy(
                showAddChildDialog = false,
                newChildName = ""
            )
        }
    }

    fun deleteChild(childId: Long) {
        launchOnIO {
            appContainer.familyRepository.deleteChild(childId)
        }
    }
}
