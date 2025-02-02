package com.example.todo.entities
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * This class is marked as an entity to create a table in the database for storing tags
 * The ID is autogenerated, ensuring a unique identifier for each tag
**/
@Entity(tableName = TagEntity.TABLE_NAME)
data class TagEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val title: String
){
    companion object {
        const val TABLE_NAME = "tags"
    }
}
