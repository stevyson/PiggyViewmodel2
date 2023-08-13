package com.example.piggy.feature_expense.presentation.add_expense

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import com.example.piggy.feature_expense.domain.model.Expense
import com.example.piggy.feature_expense.domain.repository.ExpenseRepository
import com.example.piggy.feature_expense.domain.use_case.expense_usecase.ExpenseUseCases
import com.example.piggy.feature_expense.domain.util.Routes
import com.example.piggy.feature_expense.domain.util.UiEvent
import com.example.piggy.feature_expense.presentation.add_expense.component.ExpenseTag
import com.example.piggy.feature_expense.presentation.add_expense.component.expenseTags
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

//i am trying to write the viewmodel for a selection screen in  an expense tracker app,
// remember i am using dependency injections,
// the selection screen is a flowrow of list of expense tag items
// and clicking on your expense tag of your choice,
// and clicking okay should take you to another screen,the budget expense screen.
// You should also be able to create a custom expense tag of your choice
// and it will be added to the selection screen.


@HiltViewModel
class AddExpenseViewModel @Inject constructor(
    private val repository: ExpenseRepository,
    savedStateHandle: SavedStateHandle,
    private val useCases: ExpenseUseCases,
): ViewModel() {




    private val _selectedExpenseTag = MutableLiveData<ExpenseTag>()
    private val selectedExpenseTag: LiveData<ExpenseTag>
        get() = _selectedExpenseTag

    // MutableLiveData for the title of the selection screen
    private val _title = MutableLiveData<String>()
    val title: LiveData<String>
        get() = _title

    // LiveData for available expense tags
    private val _availableExpenseTags = MutableLiveData<List<ExpenseTag>>(emptyList())
    val availableExpenseTags: LiveData<List<ExpenseTag>>
        get() = _availableExpenseTags

    private val _state = MutableStateFlow(AddExpenseState())
    val state: MutableStateFlow<AddExpenseState> = _state

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()


    fun onEvent(event: AddExpenseEvent){
        when(event){
            is AddExpenseEvent.OnExpenseTagSelected -> {
                viewModelScope.launch {
                    if (_state.value.isSelected){
                        _state.value = AddExpenseState(
                            isSelected = false
                        )
                    }
                    _state.value = AddExpenseState(
                        isSelected = true
                    )
                    _selectedExpenseTag.value = event.expenseTag

                }
            }

            AddExpenseEvent.OnCreateExpenseTag -> {
                _state.update { it.copy(
                    isCreatingExpense = true
                )}
                sendUiEvent(UiEvent.Navigate(Routes.CREATE_EXPENSE_DIALOG))
            }

            AddExpenseEvent.OnDoneSelected -> {
                fun DoneSelected(navigation: Navigation) {
                    // Get the selected expense tag
                    val selectedTag = selectedExpenseTag.value

                    // Check if a tag is selected before navigating
                    if (selectedTag != null) {
                        // Create the NavDirections object to navigate to the budgetExpenseScreen
                        sendUiEvent(UiEvent.Navigate(Routes.BUDGET_SCREEN))

                        val action =
                            BudgetExpenseScreenDirections.actionBudgetExpenseScreen(selectedTag)

                        // Navigate to the new screen using the Navigation component
                        navigation.navigate(action)
                    }
                }
            }

            is AddExpenseEvent.SetTitle -> {
                _state.update { it.copy(
                    title = event.title
                )}
            }

            AddExpenseEvent.OnDoneCreateExpenseTag -> {
                fun CreateExpenseTag(tagName: String) {

                    // Create a new expense tag
                    val newExpenseTag = ExpenseTag(
                        title = tagName,
                        price = 0.00
                    )

                    // Get the current list of expense tags
                    val currentTags = _availableExpenseTags.value.orEmpty().toMutableList()

                    // Add the new expense tag to the list
                    currentTags.add(newExpenseTag)

                    // Update the LiveData with the modified list of tags
                    _availableExpenseTags.value = currentTags
                }
                CreateExpenseTag(_state.value.title)
            }


        }


    }


    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}





