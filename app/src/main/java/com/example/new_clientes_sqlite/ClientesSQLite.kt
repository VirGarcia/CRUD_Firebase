package com.example.new_clientes_sqlite

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ClientesSQLite (context: Context) : SQLiteOpenHelper (context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "NewClientes.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE clientes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                dni TEXT NOT NULL
                )
                """.trimIndent()
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        //Eliminar la tabla NewClientes si existe ya volver a crearla
        db.execSQL("DROP TABLE IF EXISTS clientes")
        onCreate(db)
    }



    fun insert(nuevoCliente: Cliente): Long {
        val db = getWritableDatabase()

        val values = ContentValues()
        values.put("nombre", nuevoCliente.nombre)
        values.put("dni", nuevoCliente.dni)

        val newId = db.insert("clientes", null, values)
        db.close()
        return newId
    }

    /**
     * Leer los datos de un cliente usando la clase Cliente
     * @param idCliente la ID del cliente en BBDD
     * @return el objeto Cliente con su información
     */


    @SuppressLint("Range")
    fun read(idCliente: Long) : Cliente {
        val db = getReadableDatabase()
        val selectQuery = "SELECT * FROM clientes WHERE id = ?"
        val cursor: Cursor = db.rawQuery (selectQuery, arrayOf (idCliente.toString()))
        var cliente = Cliente ("", "")
        if (cursor.moveToFirst()) {
            val nombre = cursor.getString(cursor.getColumnIndex("nombre"))
            val dni = cursor.getString(cursor.getColumnIndex("dni"))
            cliente = Cliente(nombre, dni)
        }
        cursor.close()
        db.close()
        return cliente
    }

    /**
     * Actualizar un cliente con los datos de forma individual
     * @param idCliente ID del cliente en BBDD
     * @param cliente objeto de clase Cliente con los datos a actualizar
     * @return el número de filas afectadas en un Int
     */

    fun update(idCliente: Long, cliente : Cliente):Int {
        val db = getWritableDatabase()
        val values = ContentValues()
        values.put ("nombre", cliente.nombre)
        values.put ("dni", cliente.dni)

        val affectedRows = db.update("clientes", values, "id = ?", arrayOf(idCliente.toString()))
        db.close()
        return affectedRows
    }

    /**
     * Método que elimina un cliente de la Base de Datos
     * @param idcliente ID del cliente en BBDD
     * @return el número de filas afectadas en un Int
     */
    fun delete (idCliente:Long): Int {
        val db = getWritableDatabase()
        val affectedRows = db.delete("clientes", "id = ?", arrayOf(idCliente.toString()))
        db.close()
        return affectedRows
    }

    /**
     * Método para obtener el número de clientes de nuestra BBDD
     *@return el número de clientes en BBDD, si da error devuelve -1
     */


    @SuppressLint("Range")
    fun getNumeroClientes(): Int {
        val db = getReadableDatabase()
        val selectQuery = "SELECT count (*) as numClientes FROM clientes"
        val cursor: Cursor = db.rawQuery(selectQuery, null)
        var num = -1
        if (cursor.moveToFirst()) {
            num = cursor.getInt(cursor.getColumnIndex("numClientes"))
        }
        cursor.close()
        db.close()
        return num
    }

    @SuppressLint("Range")
    fun getListadoClientes(): List<Cliente> {
        val clientesList = mutableListOf<Cliente>()
        val db = getReadableDatabase()
        val selectQuery = "SELECT * FROM clientes ORDER BY dni DESC"
        val cursor: Cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val nombre = cursor.getString(cursor.getColumnIndex("nombre"))
                val dni = cursor.getString(cursor.getColumnIndex("dni"))
                val cliente = Cliente (nombre, dni)
                clientesList.add(cliente)

            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return clientesList
    }

}