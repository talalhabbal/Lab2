package com.example.lab2

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lab2.ui.theme.Lab2Theme
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var companyList by remember { mutableStateOf(emptyList<Company>()) }
            fetchCompanyFromFirebase { companiesFromDb -> companyList = companiesFromDb }
            CompanyList(companyList)
        }


    }
}



//private fun fetchCompanyFromFirebase(onDataReceived: (List<Company>) -> Unit) {
//    val databaseReference = FirebaseDatabase.getInstance().getReference("companies")
//
//    databaseReference.addValueEventListener(object : ValueEventListener {
//        override fun onDataChange(snapshot: DataSnapshot) {
//            val jsonString = snapshot.value.toString()
//            val gson = Gson()
//            val listType = object : TypeToken<Map<String, List<Company>>>() {}.type
//            val companiesMap: Map<String, List<Company>> = gson.fromJson(jsonString, listType)
//            val companies = companiesMap["companies"] ?: emptyList()
//            onDataReceived(companies)
//        }
//
//        override fun onCancelled(error: DatabaseError) {
//            Log.e("FirebaseError", "Error fetching data: ${error.message}")
//        }
//    })
//}
private fun fetchCompanyFromFirebase(onDataReceived: (List<Company>) -> Unit) {
    val databaseReference = FirebaseDatabase.getInstance().getReference("companies")

    databaseReference.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val companies = mutableListOf<Company>()
            for (companySnapshot in snapshot.children) {
                val company = companySnapshot.getValue(Company::class.java)
                company?.let { companies.add(it) }
            }
            onDataReceived(companies) // Pass the list back
        }

        override fun onCancelled(error: DatabaseError) {
            // Handle possible errors
            Log.e("FirebaseError", "Error fetching data: ${error.message}")
        }
    })
}

@Composable
fun CompanyList(companies: List<Company>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(companies) { company ->
            CompanyCard(company)
        }
    }
}

@Composable
fun CompanyCard(company: Company) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Title: ${company.title}")
            Text(text = "City: ${company.city}")
            Text(text = "Webpage: ${company.webpage}")
        }
    }
}
