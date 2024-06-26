package com.example.new_clientes_sqlite

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    fun insertarClientesFirebase(listadoClientes: List<Cliente>) {
        val database : FirebaseDatabase = FirebaseDatabase.getInstance()
        val clientesRef = database.getReference("clientes")
        listadoClientes.forEach {cliente ->
            val key = clientesRef.push().key
            key?.let {
                clientesRef.child(it).setValue(cliente)
            }
        }
    }

    fun recuperarClientesFirebase() {
        val database : FirebaseDatabase = FirebaseDatabase.getInstance()
        val clientesRef = database.getReference("clientes")
        clientesRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(setClientes : DataSnapshot){
                for (cliente in setClientes.getChildren()) {
                    val nombre = cliente.child("nombre").getValue(String::class.java)
                    val dni = cliente.child("dni").getValue(String::class.java)

                    Log.i("MyFirebase", "Nombre: ${nombre} DNI: ${dni} ")
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                println("Error al leer datos: ${databaseError.toException()}")
            }
        })
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val c1 = Cliente ("Mary James", "11111111A")
        val c2 = Cliente ("John Smith", "22222222B")
        val c3 = Cliente ("Anne Miller", "33333333M")

        val bbddClientes = ClientesSQLite(this)
        bbddClientes.insert(c1)
        bbddClientes.insert(c2)
        bbddClientes.insert(c3)

        //Mostramos los clientes si es el ej sqllite
        //esto nos nos vale si estamos haciendo el ej de Firebase
        val listaClientes = bbddClientes.getListadoClientes()
        for (cliente in listaClientes) {
            Log.i("MyActivity", "Nombre: ${cliente.nombre} DNI: ${cliente.dni}")
        }

        //creamos el listado clientes con firebase
        val listadoClientesFirebase = listOf(c1,c2,c3)
        //insertarClientesFirebase(listadoClientesFirebase)
            recuperarClientesFirebase()


    }
}