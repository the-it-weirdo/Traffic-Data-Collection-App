package dev.debaleen.project20050120

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class StopInformationFormViewModel : ViewModel() {

    val vehicleRadioChecked = MutableLiveData<Int>()

    val stopTypeChecked = MutableLiveData<Int>()

    fun onVehicleTypeChange(vehicleType: VehicleType) {
        when (vehicleType) {
            VehicleType.BUS -> vehicleRadioChecked.value = R.id.vehicle_type_bus
            VehicleType.CAB -> vehicleRadioChecked.value = R.id.vehicle_type_cab
            VehicleType.TWO_WHEELER -> vehicleRadioChecked.value = R.id.vehicle_type_two_wheeler
            VehicleType.THREE_WHEELER -> vehicleRadioChecked.value = R.id.vehicle_type_three_wheeler
            VehicleType.FOUR_WHEELER -> vehicleRadioChecked.value = R.id.vehicle_type_four_wheeler
            VehicleType.TWO_WHEELER_WITH_ENGINE -> vehicleRadioChecked.value =
                R.id.vehicle_type_two_wheeler_with_engine
            VehicleType.THREE_WHEELER_WITH_ENGINE -> vehicleRadioChecked.value =
                R.id.vehicle_type_three_wheeler_with_engine
        }
    }

    fun onStopTypeChange(stopType: BusStopType) {
        when (stopType) {
            BusStopType.ADHOC -> stopTypeChecked.value = R.id.type_of_stop_adhoc
            BusStopType.CONGESTION -> stopTypeChecked.value = R.id.type_of_stop_congestion
            BusStopType.SIGNAL -> stopTypeChecked.value = R.id.type_of_stop_signal
            BusStopType.BUS_STOP -> stopTypeChecked.value = R.id.type_of_stop_regular
        }
    }

}