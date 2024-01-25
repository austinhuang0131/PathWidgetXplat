package com.sixbynine.transit.path.app.ui.filter

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sixbynine.transit.path.MR.strings
import com.sixbynine.transit.path.api.StationFilter
import com.sixbynine.transit.path.api.StationFilter.All
import com.sixbynine.transit.path.api.StationFilter.Interstate
import com.sixbynine.transit.path.app.filter.StationFilterManager
import com.sixbynine.transit.path.app.ui.AppUiScope
import com.sixbynine.transit.path.app.ui.PathBottomSheet
import com.sixbynine.transit.path.app.ui.gutter
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun AppUiScope.FilterBottomSheet(onDismiss: () -> Unit) {
    var filter by remember {
        mutableStateOf(StationFilterManager.filter.value)
    }
    FilterBottomSheet(
        filter = filter,
        onFilterClick = { filter = it },
        onDismiss = {
            StationFilterManager.update(filter)
            onDismiss()
        }
    )
}

@Composable
fun AppUiScope.FilterBottomSheet(
    filter: StationFilter,
    onFilterClick: (StationFilter) -> Unit,
    onDismiss: () -> Unit
) {
    PathBottomSheet(
        onDismissRequest = onDismiss,
    ) {
        StationFilter.entries.forEach {
            FilterRow(it, selected = it == filter, onClick = { onFilterClick(it) })
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun AppUiScope.FilterRow(
    filter: StationFilter,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.heightIn(min = 48.dp)
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onClick)
            .padding(vertical = 8.dp, horizontal = gutter()),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = null,
        )
        Spacer(Modifier.width(8.dp))

        val text = when (filter) {
            All -> stringResource(strings.show_all_trains)
            Interstate -> stringResource(strings.show_interstate_trains)
        }

        Text(
            modifier = Modifier.weight(1f),
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}