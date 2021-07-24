package dev.debaleen.project20050120.repositories

import android.content.Context
import android.util.Log
import dev.debaleen.project20050120.util.BusStopType
import dev.debaleen.project20050120.util.Constants
import dev.debaleen.project20050120.util.FileLogger
import dev.debaleen.project20050120.util.VehicleType
import kotlinx.coroutines.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class StopInformationRepository(val context: Context) {

    companion object {
        private val TAG = StopInformationRepository::class.java.simpleName
    }

    suspend fun writeToFile(
        vehicleType: VehicleType,
        disembarked: Boolean,
        stopTypes: Collection<BusStopType>
    ): Boolean {
        var success: Deferred<Boolean>
        coroutineScope {
            success = async(Dispatchers.IO) {
                try {
                    val stringBuffer = StringBuffer()
                    stringBuffer.append(
                        "${Constants.getCurrentDateTime()},${vehicleType.name},$disembarked,\""
                    )
                    stopTypes.forEach {
                        stringBuffer.append("${it.name},")
                    }
                    stringBuffer.replace(stringBuffer.length - 1, stringBuffer.length, "\"\n")
                    val logger = FileLogger(Constants.STOP_INFORMATION_FORM_INFORMATION)
                    logger.writeToFile(stringBuffer.toString())
                    return@async true
                } catch (e: Exception) {
                    Log.e(TAG, "Exception occurred while writing to StopInformationLog", e)
                    return@async false
                }
            }
        }
        return success.await()
    }
}