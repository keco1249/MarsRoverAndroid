package com.kc.marsrovers.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.kc.marsrovers.ui.theme.IconButtonContainer
import com.kc.marsrovers.ui.theme.OutlineNeutral
import com.kc.marsrovers.ui.theme.TextSecondary
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateSelectorField(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null,
) {
    var showPicker by remember { mutableStateOf(false) }

    val interactionSource = remember { MutableInteractionSource() }
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { if (it is PressInteraction.Release) showPicker = true }
    }

    Box(modifier) {
        OutlinedTextField(
            value = selectedDate.format(RoverDateFormatter),
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            label = { Text("Date", style = RoverTextStyles.Caption) },
            textStyle = RoverTextStyles.Input,
            trailingIcon = {
                IconButton(
                    onClick = { showPicker = true },
                    modifier = Modifier.size(40.dp).background(IconButtonContainer, CircleShape),
                ) {
                    Icon(Icons.Filled.Today, contentDescription = "Select date", tint = Color.Black)
                }
            },
            shape = RoundedCornerShape(4.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = OutlineNeutral,
                unfocusedBorderColor = OutlineNeutral,
                focusedLabelColor = TextSecondary,
                unfocusedLabelColor = TextSecondary,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
            ),
            interactionSource = interactionSource,
            modifier = Modifier.fillMaxWidth(),
        )

        if (showPicker) {
            val pickerState = rememberDatePickerState(
                initialSelectedDateMillis = selectedDate.toUtcMillis(),
                selectableDates = object : SelectableDates {
                    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                        val d = utcTimeMillis.toLocalDateUtc()
                        return (minDate == null || !d.isBefore(minDate)) &&
                            (maxDate == null || !d.isAfter(maxDate))
                    }
                },
            )
            val density = LocalDensity.current
            Popup(
                alignment = Alignment.TopStart,
                offset = with(density) { IntOffset(0, 64.dp.roundToPx()) },
                onDismissRequest = { showPicker = false },
                properties = PopupProperties(focusable = true),
            ) {
                Column(
                    modifier = Modifier
                        .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp), ambientColor = Color(0x1A000000), spotColor = Color(0x1A000000))
                        .border(1.dp, OutlineNeutral, RoundedCornerShape(16.dp))
                        .background(Color.White, RoundedCornerShape(16.dp)),
                ) {
                    DatePicker(
                        state = pickerState,
                        showModeToggle = false,
                        title = null,
                        headline = null,
                        colors = DatePickerDefaults.colors(
                            containerColor = Color.White,
                            weekdayContentColor = Color.Black,
                            dayContentColor = Color.Black,
                            disabledDayContentColor = OutlineNeutral,
                            selectedDayContainerColor = TextSecondary,
                            selectedDayContentColor = Color.White,
                            todayContentColor = Color.Black,
                            todayDateBorderColor = TextSecondary,
                            navigationContentColor = TextSecondary,
                            yearContentColor = TextSecondary,
                            currentYearContentColor = TextSecondary,
                            selectedYearContainerColor = TextSecondary,
                            selectedYearContentColor = Color.White,
                        ),
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                    ) {
                        TextButton(onClick = { showPicker = false }) {
                            Text("Cancel", style = RoverTextStyles.Action, color = TextSecondary)
                        }
                        TextButton(onClick = {
                            pickerState.selectedDateMillis?.let { onDateSelected(it.toLocalDateUtc()) }
                            showPicker = false
                        }) {
                            Text("OK", style = RoverTextStyles.Action, color = TextSecondary)
                        }
                    }
                }
            }
        }
    }
}

private fun LocalDate.toUtcMillis(): Long = atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
private fun Long.toLocalDateUtc(): LocalDate = Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDate()
