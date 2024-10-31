package com.example.tasky.agenda.agenda_data.dto_mappers

import com.example.tasky.agenda.agenda_data.remote.dto.ReminderTypeSerialized
import com.example.tasky.agenda.agenda_domain.model.ReminderType

fun ReminderType.toSerializedReminderType(): ReminderTypeSerialized {
    return when (this) {
        ReminderType.TASK -> ReminderTypeSerialized.TASK
        ReminderType.REMINDER -> ReminderTypeSerialized.REMINDER
        ReminderType.EVENT -> ReminderTypeSerialized.EVENT
        ReminderType.NONE -> ReminderTypeSerialized.NONE
    }
}

fun ReminderTypeSerialized.toReminderType(): ReminderType {
    return when (this) {
        ReminderTypeSerialized.TASK -> ReminderType.TASK
        ReminderTypeSerialized.REMINDER -> ReminderType.REMINDER
        ReminderTypeSerialized.EVENT -> ReminderType.EVENT
        ReminderTypeSerialized.NONE -> ReminderType.NONE
    }
}