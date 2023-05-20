package ch.hslu.mobpro.timeismoney.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "entries")
class Entry {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    var startTime: Long = 0
    var endTime: Long = 0
    var taskId: Long = 0

    constructor(startTime: Long, endTime: Long, taskId: Long) {
        this.startTime = startTime
        this.endTime = endTime
        this.taskId = taskId
    }
}