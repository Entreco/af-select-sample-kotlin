package com.alphaflash.select

import af_select_sample_kotlin.composeApp.BuildConfig
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alphaflash.select.auth.AuthResponse
import com.alphaflash.select.auth.AuthService
import com.alphaflash.select.auth.AuthServiceImpl
import com.alphaflash.select.data.SelectDataService
import com.alphaflash.select.data.SelectDataServiceImpl
import com.alphaflash.select.realtime.RealtimeService
import com.alphaflash.select.realtime.RealtimeServiceImpl
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json

private val ktorClient = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
    install(WebSockets) {
        pingIntervalMillis = 20_000
    }
}

class AppViewModel(
    private val client: HttpClient = ktorClient,
    private val authService: AuthService = AuthServiceImpl(client),
    private val selectService: SelectDataService = SelectDataServiceImpl(client),
    private val realtimeService: RealtimeService = RealtimeServiceImpl(client)
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthResponse?>(null)
    val authState: StateFlow<AuthResponse?> = _authState.asStateFlow()
    private val _logs = MutableStateFlow<List<Log>>(emptyList())
    val logs: StateFlow<List<Log>> = _logs.asStateFlow()
    val beats: Flow<Instant?> = realtimeService.heartBeats

    fun toggle(response: AuthResponse?) {
        val isAuthenticated = selectService.auth(response?.access_token)
        if(!isAuthenticated) authenticate()
        else disconnect()
    }

    private fun authenticate() {
        viewModelScope.launch {
            _logs.value += Log(0, "authenticating")
            val auth = authService.authenticate(BuildConfig.USER, BuildConfig.PWD)
            _authState.value = auth
            _logs.value += Log(0, "connecting")
            realtimeService.connect(auth.access_token, "/topic/observations")
                .onStart { _logs.value += Log(0, "Realtime service connected") }
                .onEach { obs -> _logs.value += Log(0, obs.toString()) }
                .onCompletion { _logs.value += Log(0, "Realtime service disconnected") }
                .shareIn(viewModelScope, SharingStarted.Eagerly)
        }
    }

    private fun disconnect(){
        realtimeService.disconnect()
        _logs.value += Log(0, "disconnect")
        _authState.value = null
        _logs.value = emptyList()
    }
}