package org.example.collrecord.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WorkingPaper(
    @SerialName("task_id") val taskId: String,
    @SerialName("no_contract") val noContract: String,
    @SerialName("customer_id") val customerId: String,
    @SerialName("debitur_name") val debiturName: String,
    @SerialName("business_unit") val businessUnit: String,
    @SerialName("due_date") val dueDate: String,
    @SerialName("overdue") val overdue: Int,
    @SerialName("tenor") val tenor: Int,
    @SerialName("unit") val unit: String,
    @SerialName("denda") val denda: Long,
    @SerialName("billing") val billing: Long,
    @SerialName("total") val total: Long
)
