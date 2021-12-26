package com.rahul.notetaking.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EncryptFileDao {
    @Query("SELECT * FROM enc_file_table")
    fun getAll(): Flow<List<EncryptFileEntity>>

    @Query("SELECT * FROM enc_file_table")
    fun getAllNonFlow(): List<EncryptFileEntity>

    @Query("SELECT * FROM enc_file_table where file_name =:fileName")
    fun get(fileName:String): EncryptFileEntity

    @Query("SELECT * FROM enc_file_table where uid =:id")
    fun get(id:Int): EncryptFileEntity

    @Insert
    fun insert(entity:EncryptFileEntity)

    @Query("UPDATE enc_file_table SET enc_status =:encStatus where file_name =:fileName")
    fun update(fileName:String, encStatus:Int)

    @Query("UPDATE enc_file_table SET title =:title where uid =:id")
    fun update(id:Int, title:String)

    @Query("UPDATE enc_file_table SET enc_status =:encStatus where uid =:id")
    fun update(id:Int, encStatus:Int)

    @Query("DELETE from enc_file_table where file_name =:fileName")
    fun delete(fileName:String): Int

}