package com.sixbynine.transit.path.api.path

import com.sixbynine.transit.path.Logging
import com.sixbynine.transit.path.api.NetworkException
import com.sixbynine.transit.path.preferences.StringPreferencesKey
import com.sixbynine.transit.path.preferences.persisting
import com.sixbynine.transit.path.preferences.persistingInstant
import com.sixbynine.transit.path.time.now
import com.sixbynine.transit.path.util.JsonFormat
import com.sixbynine.transit.path.util.suspendRunCatching
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlin.time.Duration.Companion.seconds

object PathRepository {
    private val httpClient = HttpClient()
    private var lastPathResponse by persisting(StringPreferencesKey("last_success"))
    private var lastPathResponseTime by persistingInstant("last_success_time")
    private var fetchJob: Job? = null

    suspend fun getResults(
        force: Boolean = false
    ): Result<PathServiceResults> = withContext(Dispatchers.IO) {
        var joinedExistingFetch = false
        fetchJob?.takeIf { it.isActive }?.let {
            Logging.d("Join existing fetch")
            it.join()
            joinedExistingFetch = true
        }

        if ((!force || joinedExistingFetch) && hasRecentCachedResponse()) {
            getCachedResults()?.let {
                Logging.d("Returning cached results")
                return@withContext Result.success(it)
            }
        }


        async {
            Logging.d("Starting a new fetch")
            suspendRunCatching {
                withTimeout(5.seconds) {
                    val response =
                        httpClient.get("https://www.panynj.gov/bin/portauthority/ridepath.json")

                    if (!response.status.isSuccess()) {
                        throw NetworkException(response.status.toString())
                    }

                    val responseText = response.bodyAsText()

                    lastPathResponseTime = now()
                    lastPathResponse = responseText

                    JsonFormat.decodeFromString<PathServiceResults>(responseText)
                }
            }
        }
            .also { fetchJob = it }
            .await()
    }

    suspend fun getCachedResults(): PathServiceResults? = withContext(Dispatchers.IO) {
        val result = suspendRunCatching {
            val lastPathResponse = lastPathResponse ?: return@suspendRunCatching null
            JsonFormat.decodeFromString<PathServiceResults>(lastPathResponse)
        }

        result.getOrNull()
    }

    private fun hasRecentCachedResponse(): Boolean {
        val lastPathResponseTime = lastPathResponseTime ?: return false
        return lastPathResponseTime < now() - 30.seconds
    }

    @Serializable
    data class PathServiceResults(val results: List<PathServiceResult>)

    @Serializable
    data class PathServiceResult(
        val consideredStation: String,
        val destinations: List<PathDestination>
    )

    @Serializable
    data class PathDestination(
        val label: String,
        val messages: List<PathDestinationMessage>
    )

    @Serializable
    data class PathDestinationMessage(
        val target: String,
        val secondsToArrival: String,
        val arrivalTimeMessage: String?,
        val lineColor: String,
        val headSign: String,
        val lastUpdated: Instant
    ) {
        val durationToArrival get() = secondsToArrival.toInt().seconds
    }
}