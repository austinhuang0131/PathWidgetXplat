package com.sixbynine.transit.path.app.ui.settings

import androidx.compose.runtime.Composable
import com.sixbynine.transit.path.MR.strings
import com.sixbynine.transit.path.api.TrainFilter
import com.sixbynine.transit.path.app.ui.PathBottomSheet
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun TrainFilterBottomSheet(
    isShown: Boolean,
    filter: TrainFilter,
    onFilterClicked: (TrainFilter) -> Unit,
    onDismiss: () -> Unit,
) {
    PathBottomSheet(
        isShown = isShown,
        onDismissRequest = onDismiss,
        title = stringResource(strings.filter)
    ) {
        RadioSection {
            TrainFilter.entries.forEach {
                item(
                    text = stringResource(it.title),
                    subtext = it.subtext?.let { stringResource(it) },
                    selected = it == filter,
                    onClick = { onFilterClicked(it) }
                )
            }
        }
    }
}