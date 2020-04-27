package com.thelarios.firebaseservices

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.thelarios.firebaseservices.Adapters.AdapterShow
import com.thelarios.firebaseservices.Data.DataItem
import kotlinx.android.synthetic.main.activity_show.*
import java.util.ArrayList

class ShowActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show)
        recyclerView.layoutManager = GridLayoutManager(this, 1)

        val database = FirebaseDatabase.getInstance().getReference("DB")

        val postListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val arrayItems = ArrayList<DataItem>()
                if(dataSnapshot.child("Object").exists())
                {
                    for(data in dataSnapshot.child("Object").children)
                    {
                        arrayItems.add(DataItem(data.child("Image").value.toString(),
                                                data.child("Object").value.toString(),
                                                data.child("Confidence").value.toString().toFloat()))
                    }
                    recyclerView.adapter = AdapterShow(arrayItems)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        database.addValueEventListener(postListener)
    }
}
