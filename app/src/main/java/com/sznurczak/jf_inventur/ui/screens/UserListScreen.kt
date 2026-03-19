package com.sznurczak.jf_inventur.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sznurczak.jf_inventur.R
import com.sznurczak.jf_inventur.ui.viewmodels.InventoryViewModel
import com.sznurczak.jf_inventur.ui.viewmodels.UserViewModel
import com.sznurczak.jf_inventur.data.database.Shoes


@Composable
fun UserListScreen(modifier: Modifier = Modifier, InventoryViewModel: InventoryViewModel, UserViewModel: UserViewModel)
{

    val allShoes by InventoryViewModel.allShoes.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {

            LazyColumn {
                items(allShoes) { shoe ->
                    Text("Schuh: ${shoe.brand} - ID: ${shoe.id}")
                    UserListCard(shoe)

                }


            }
        }
    }

}

@Composable
fun UserListCard(shoe: Shoes,modifier: Modifier = Modifier )
{
    Row()
    {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = null,
        )
        Column()
        {
            //Text("Name", style = MaterialTheme.typography.bodySmall)
            Text("Schuh: ${shoe.brand} - ID: ${shoe.id}")

        }

    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmallTopAppBar(content: (PaddingValues) -> Unit)
{
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("Small Top App Bar") }
            )
        },
    ) { innerPadding ->
        content(innerPadding)
    }
}














