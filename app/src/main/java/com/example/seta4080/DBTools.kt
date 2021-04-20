package com.example.seta4080

import android.app.Instrumentation
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.database.getStringOrNull
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream

class DBTools(context: Context) : SQLiteOpenHelper(context, "seta4080.db", null, 1){
    val DBAssetContext = context
    var setaDB : SQLiteDatabase? = null
    val APP_DATA_LOCATION = context.applicationInfo.dataDir
    val dbPath = context.getDatabasePath("$APP_DATA_LOCATION/databases/seta4080.db").absolutePath
    val tablesDirectory = mapOf<String, String>(
        "textInfo" to "text_modules",
        "quiz" to "quizzes",
        "video" to "videos"
    )

    override fun onCreate(db: SQLiteDatabase?) {
        copyDbToDevice(DBAssetContext)
        setaDB = db!!
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        copyDbToDevice(DBAssetContext)
        setaDB = db!!
    }

    fun readDB(type: String, entryNumber: Int): Map<String, String?> {
        setaDB = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY)
        val tableName = tablesDirectory[type]
        val result = setaDB?.rawQuery("SELECT * FROM '$tableName' WHERE id='$entryNumber'", null)
        var returned_fields = emptyMap<String, String?>().toMutableMap()
        result?.moveToFirst()
        when (tableName) {
            "text_modules" -> {
                returned_fields["title"] = result?.getString(result.getColumnIndex("title"))
                returned_fields["textInfo"] = result?.getString(result.getColumnIndex("information"))
            }
            "quizzes" -> {
                returned_fields["title"] = result?.getString(result.getColumnIndex("title"))
                returned_fields["question"] = result?.getString(result.getColumnIndex("question"))
                returned_fields["options"] = result?.getString(result.getColumnIndex("options"))
                returned_fields["solution"] = result?.getString(result.getColumnIndex("solution"))
            }
            "videos" -> {
                returned_fields["title"] = result?.getString(result.getColumnIndex("title"))
                returned_fields["embed"] = result?.getString(result.getColumnIndex("embed"))
            }
        }
        return returned_fields.toMap()
    }

    fun copyDbToDevice(context: Context){

        // Ensure DB folder exists
        val deviceDbFolder = File(APP_DATA_LOCATION+"/databases")
        deviceDbFolder.mkdir()

        //
        val deviceDbPath = File(APP_DATA_LOCATION+"/databases/seta4080.db")
        val deviceDatabaseAsset = FileOutputStream(deviceDbPath)
        try {
            val databaseAsset = context.assets.open("seta4080.db").use {
                it.copyTo(deviceDatabaseAsset, 1024)
            }
        } catch (e: FileNotFoundException){
            throw e
        }


    }

}