package ch.hslu.mobpro.timeismoney.room

data class TaskEntry (
    val id: Long,
    val startTime: Long,
    val endTime: Long,
    val taskId: Long,
    val title: String
)