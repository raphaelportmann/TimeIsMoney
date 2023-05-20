package ch.hslu.mobpro.timeismoney.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
class Task {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    @ColumnInfo(index = true)
    var title: String = ""
    var userId: String = ""

    constructor(title: String) {
        this.title = title
    }
}