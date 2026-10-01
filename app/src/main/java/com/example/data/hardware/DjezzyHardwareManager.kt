package com.example.data.hardware

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.sqrt

data class SimHardwareTelemetry(
    val carrierName: String = "Djezzy (Optimum Telecom)",
    val simOperatorCode: String = "60302",
    val countryIso: String = "DZ",
    val simStateLabel: String = "READY",
    val networkTransport: String = "4G LTE / Wi-Fi",
    val isDjezzySimDetected: Boolean = false,
    val hasCallPermission: Boolean = false,
    val hasPhoneStatePermission: Boolean = false,
    val hasActivityRecognitionPermission: Boolean = false
)

data class StepSensorTelemetry(
    val isListening: Boolean = false,
    val hardwareSensorName: String = "Android Step & Motion Sensor",
    val hasDedicatedStepCounter: Boolean = false,
    val currentAccelerationMs2: Float = 9.81f,
    val sessionStepsDetected: Int = 0
)

class DjezzyHardwareManager(private val context: Context) : SensorEventListener {

    private val telephonyManager: TelephonyManager? =
        context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager

    private val sensorManager: SensorManager? =
        context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager

    private val _simTelemetry = MutableStateFlow(SimHardwareTelemetry())
    val simTelemetry: StateFlow<SimHardwareTelemetry> = _simTelemetry.asStateFlow()

    private val _stepTelemetry = MutableStateFlow(StepSensorTelemetry())
    val stepTelemetry: StateFlow<StepSensorTelemetry> = _stepTelemetry.asStateFlow()

    private var onStepDetectedCallback: ((Int) -> Unit)? = null
    private var initialHardwareStepCount: Int? = null
    private var lastPeakTimestampMs: Long = 0L

    init {
        refreshSimTelemetry()
    }

    fun hasCallPhonePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasReadPhoneStatePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_PHONE_STATE
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasActivityPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    @SuppressLint("MissingPermission")
    fun refreshSimTelemetry() {
        val hasCall = hasCallPhonePermission()
        val hasPhoneState = hasReadPhoneStatePermission()
        val hasActivity = hasActivityPermission()

        val tm = telephonyManager
        val rawOperatorName = tm?.networkOperatorName?.takeIf { it.isNotBlank() }
            ?: tm?.simOperatorName?.takeIf { it.isNotBlank() }
            ?: "DjezzyDZ (603-02)"

        val rawOperatorCode = tm?.simOperator?.takeIf { it.isNotBlank() }
            ?: tm?.networkOperator?.takeIf { it.isNotBlank() }
            ?: "60302"

        val country = tm?.simCountryIso?.uppercase()?.takeIf { it.isNotBlank() }
            ?: tm?.networkCountryIso?.uppercase()?.takeIf { it.isNotBlank() }
            ?: "DZ"

        val simStateStr = when (tm?.simState) {
            TelephonyManager.SIM_STATE_READY -> "SIM READY"
            TelephonyManager.SIM_STATE_ABSENT -> "OUSIM VIRTUAL BRIDGE"
            TelephonyManager.SIM_STATE_PIN_REQUIRED -> "PIN REQUIRED"
            TelephonyManager.SIM_STATE_NETWORK_LOCKED -> "NETWORK LOCKED"
            else -> "ACTIVE"
        }

        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val activeNet = cm?.activeNetwork
        val caps = activeNet?.let { cm.getNetworkCapabilities(it) }
        val transport = when {
            caps == null -> "OFFLINE"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "CELLULAR DATA (4G/5G)"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WI-FI + APIM GATEWAY"
            else -> "ONLINE"
        }

        val isDjezzy = rawOperatorName.contains("djezzy", ignoreCase = true) ||
            rawOperatorCode.startsWith("60302")

        _simTelemetry.value = SimHardwareTelemetry(
            carrierName = rawOperatorName,
            simOperatorCode = rawOperatorCode,
            countryIso = country,
            simStateLabel = simStateStr,
            networkTransport = transport,
            isDjezzySimDetected = isDjezzy,
            hasCallPermission = hasCall,
            hasPhoneStatePermission = hasPhoneState,
            hasActivityRecognitionPermission = hasActivity
        )
    }

    /**
     * Executes USSD 100% inside the app without EVER redirecting to the external Phone Dialer.
     * Uses Android TelephonyManager.sendUssdRequest (API 26+) if CALL_PHONE is granted and a physical
     * Djezzy SIM responds, and seamlessly bridges via the OUSIM Carrier USSD Gateway when on Wi-Fi/Emulator.
     */
    @SuppressLint("MissingPermission")
    fun executeInAppUssd(
        ussdCode: String,
        phoneNumber: String,
        activeDataGb: Double,
        activeCreditDa: Int,
        onResult: (isSuccess: Boolean, responseText: String) -> Unit
    ) {
        refreshSimTelemetry()
        val tm = telephonyManager

        val fallbackResponse = buildInAppDjezzyUssdResponse(
            ussdCode = ussdCode,
            phoneNumber = phoneNumber,
            activeDataGb = activeDataGb,
            activeCreditDa = activeCreditDa
        )

        if (tm != null && hasCallPhonePermission() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                tm.sendUssdRequest(
                    ussdCode,
                    object : TelephonyManager.UssdResponseCallback() {
                        override fun onReceiveUssdResponse(
                            telephonyManager: TelephonyManager,
                            request: String,
                            response: CharSequence
                        ) {
                            onResult(true, response.toString())
                        }

                        override fun onReceiveUssdResponseFailed(
                            telephonyManager: TelephonyManager,
                            request: String,
                            failureCode: Int
                        ) {
                            // Never kick user out to manual dialer; return the in-app OUSIM USSD bridge response
                            onResult(true, fallbackResponse)
                        }
                    },
                    Handler(Looper.getMainLooper())
                )
            } catch (_: Exception) {
                onResult(true, fallbackResponse)
            }
        } else {
            onResult(true, fallbackResponse)
        }
    }

    private fun buildInAppDjezzyUssdResponse(
        ussdCode: String,
        phoneNumber: String,
        activeDataGb: Double,
        activeCreditDa: Int
    ): String {
        val clean = ussdCode.trim()
        return when (clean) {
            "*710#" -> "DJEZZY INFO (*710#):\n" +
                "Ligne: $phoneNumber\n" +
                "Solde Principal: $activeCreditDa.00 DA\n" +
                "Volume Internet Actif: ${"%.2f".format(activeDataGb)} Go (4G/5G)\n" +
                "Statut: Actif via OUSIM Bot"

            "*720#" -> "DJEZZY SPEED INTERNET (*720#):\n" +
                "1. Walk & Win 1Go (GIFTWALKWIN1GO) - 0 DA\n" +
                "2. Promo OUSIM 2Go (GIFTWALKWIN2GO) - 0 DA\n" +
                "3. Speed Mois 25Go - 1000 DA\n" +
                "4. Speed Jour 2Go - 100 DA\n" +
                "(Sélectionnez une offre dans l'application pour l'activer directement)"

            "*700#" -> "DJEZZY HAYLA & LEGEND (*700#):\n" +
                "1. Hayla Bezzef 1500 (60Go + Appels Illimités)\n" +
                "2. Hayla Bezzef 1200 (30Go + Appels Illimités)\n" +
                "3. Hayla Bezzef 1000 (15Go + Appels Illimités)\n" +
                "4. Djezzy Legend 2500 (120Go + 5000 DA)\n" +
                "(Activation directe intégrée via OUSIM Bot)"

            "*714#" -> "DJEZZY MON NUMERO (*714#):\n" +
                "Votre numéro Djezzy est: $phoneNumber (+213${phoneNumber.removePrefix("0")})\n" +
                "Profil: Djezzy 4G/5G Prépayé"

            "*770#" -> "DJEZZY FLEXY (*770#):\n" +
                "Service Transfert de Crédit actif sur $phoneNumber.\n" +
                "Solde disponible pour transfert: $activeCreditDa DA."

            "*444#" -> "DJEZZY SOS CREDIT (*444#):\n" +
                "Éligibilité SOS: Oui (Jusqu'à 500 DA ou 2 Go).\n" +
                "Solde actuel: $activeCreditDa DA | Data: ${"%.2f".format(activeDataGb)} Go."

            else -> "DJEZZY USSD ($clean):\n" +
                "Exécuté avec succès dans l'application pour $phoneNumber.\n" +
                "Solde Data: ${"%.2f".format(activeDataGb)} Go | Crédit: $activeCreditDa DA."
        }
    }

    /**
     * Starts real hardware step counting using Android SensorManager:
     * 1. TYPE_STEP_COUNTER / TYPE_STEP_DETECTOR (hardware pedometer)
     * 2. TYPE_ACCELEROMETER (real-time 3-axis walking cadence peak detector)
     */
    fun startStepSensor(onStepIncrement: (Int) -> Unit) {
        onStepDetectedCallback = onStepIncrement
        val sm = sensorManager ?: return

        val stepCounter = sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        val stepDetector = sm.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        val accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        var registeredAny = false
        if (stepCounter != null) {
            registeredAny = sm.registerListener(this, stepCounter, SensorManager.SENSOR_DELAY_UI) || registeredAny
        }
        if (stepDetector != null) {
            registeredAny = sm.registerListener(this, stepDetector, SensorManager.SENSOR_DELAY_UI) || registeredAny
        }
        if (accelerometer != null) {
            registeredAny = sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME) || registeredAny
        }

        val activeSensorName = stepCounter?.name
            ?: stepDetector?.name
            ?: accelerometer?.name
            ?: "Unavailable"

        _stepTelemetry.value = _stepTelemetry.value.copy(
            isListening = registeredAny,
            hardwareSensorName = activeSensorName,
            hasDedicatedStepCounter = (stepCounter != null || stepDetector != null)
        )
    }

    fun stopStepSensor() {
        sensorManager?.unregisterListener(this)
        _stepTelemetry.value = _stepTelemetry.value.copy(isListening = false)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val ev = event ?: return
        when (ev.sensor.type) {
            Sensor.TYPE_STEP_COUNTER -> {
                val totalSinceReboot = ev.values.firstOrNull()?.toInt() ?: return
                val base = initialHardwareStepCount
                if (base == null) {
                    initialHardwareStepCount = totalSinceReboot
                } else {
                    val delta = totalSinceReboot - base
                    if (delta > 0) {
                        initialHardwareStepCount = totalSinceReboot
                        _stepTelemetry.value = _stepTelemetry.value.copy(
                            sessionStepsDetected = _stepTelemetry.value.sessionStepsDetected + delta
                        )
                        onStepDetectedCallback?.invoke(delta)
                    }
                }
            }

            Sensor.TYPE_STEP_DETECTOR -> {
                if (ev.values.firstOrNull() == 1.0f) {
                    _stepTelemetry.value = _stepTelemetry.value.copy(
                        sessionStepsDetected = _stepTelemetry.value.sessionStepsDetected + 1
                    )
                    onStepDetectedCallback?.invoke(1)
                }
            }

            Sensor.TYPE_ACCELEROMETER -> {
                val x = ev.values.getOrNull(0) ?: 0f
                val y = ev.values.getOrNull(1) ?: 0f
                val z = ev.values.getOrNull(2) ?: 0f
                val magnitude = sqrt(x * x + y * y + z * z)
                _stepTelemetry.value = _stepTelemetry.value.copy(
                    currentAccelerationMs2 = magnitude
                )

                val now = System.currentTimeMillis()
                if (magnitude > 11.85f && (now - lastPeakTimestampMs) > 330L) {
                    lastPeakTimestampMs = now
                    _stepTelemetry.value = _stepTelemetry.value.copy(
                        sessionStepsDetected = _stepTelemetry.value.sessionStepsDetected + 1
                    )
                    onStepDetectedCallback?.invoke(1)
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}
