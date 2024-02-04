package com.sixbynine.transit.path.api

import com.sixbynine.transit.path.preferences.IntPersistable

enum class StationSort(override val number: Int) : IntPersistable {
    Alphabetical(1), NjAm(2), NyAm(3)
}
