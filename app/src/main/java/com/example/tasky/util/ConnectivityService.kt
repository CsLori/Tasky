package com.example.tasky.util

import kotlinx.coroutines.flow.Flow


interface ConnectivityService {
    val networkStatus: Flow<NetworkStatus>
}

sealed class NetworkStatus {
    data object Unknown: NetworkStatus()
    data object Connected: NetworkStatus()
    data object Disconnected: NetworkStatus()
}