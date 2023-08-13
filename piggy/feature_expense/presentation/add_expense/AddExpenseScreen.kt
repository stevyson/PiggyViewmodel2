package com.example.piggy.feature_expense.presentation.add_expense

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.piggy.feature_expense.presentation.add_expense.component.CreateExpenseDialog
import com.example.piggy.feature_expense.presentation.add_expense.component.ExpenseTag
import com.google.accompanist.flowlayout.FlowRow


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    expenseTags: List<ExpenseTag>,
    selectedExpenseTag: ExpenseTag?,
    viewModel: AddExpenseViewModel = hiltViewModel(),
    onExpenseTagSelected: (ExpenseTag) -> Unit,
    onEvent: (AddExpenseEvent) -> Unit,
    state: AddExpenseState

    ){

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Expense Tags") },
                actions = {
                    // Show a "Done" button to confirm the selection
                    IconButton(onClick = { AddExpenseEvent.OnDoneSelected }) {
                        Icon(Icons.Default.Done, contentDescription = "done")
                    }
                }
            )
        },
    content = { padding ->
        if(state.isCreatingExpense){
            CreateExpenseDialog(state = AddExpenseState(), onEvent = onEvent )
        }

        Spacer(modifier = Modifier
            .height(15.dp)
            .padding(1.dp))

               FlowRow(
                   mainAxisSpacing = 10.dp,
                   crossAxisSpacing = 10.dp,
                   modifier = Modifier.fillMaxWidth()
               ){
                   expenseTags.forEach { expenseTag ->
                       // Pass the current expenseTag and handle the selection
                       ExpenseTag(
                           expenseTag = expenseTag,
                           isSelected = expenseTag == selectedExpenseTag,
                           onItemClick = { selectedExpenseTag ->
                               onExpenseTagSelected(selectedExpenseTag)
                           }
                       )
                   }
                   // Create custom expense Tags
                   Box(
                       modifier = Modifier
                           .border(
                               width = 1.dp,
                               color = MaterialTheme.colorScheme.primary,
                               shape = RoundedCornerShape(100.dp)
                           )
                           .padding(10.dp)
                           .clickable { viewModel.onEvent(AddExpenseEvent.OnCreateExpenseTag) }

                   ) {
                       Text(
                           text = "+",
                           color = MaterialTheme.colorScheme.primary,
                           textAlign = TextAlign.Center,
                           style = MaterialTheme.typography.bodyMedium
                       )
                   }
               }
    })


}

