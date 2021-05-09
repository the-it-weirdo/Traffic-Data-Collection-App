package dev.debaleen.project20050120.viewmodels

import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.debaleen.project20050120.util.BusStopType
import dev.debaleen.project20050120.R
import dev.debaleen.project20050120.util.VehicleType
import dev.debaleen.project20050120.repositories.StopInformationRepository
import dev.debaleen.project20050120.util.Event
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class StopInformationFormViewModel(private val repository: StopInformationRepository) : ViewModel(),
    Observable {

    enum class FormState {
        SUCCESS, VEHICLE_TYPE_EMPTY, DISEMBARKED_EMPTY, STOP_TYPE_EMPTY
    }

    private val _toastMessage = MutableLiveData<Event<String>>()
    val toastMessage: LiveData<Event<String>>
        get() = _toastMessage

    private val _showProgress = MutableLiveData<Boolean>()
    val showProgress: LiveData<Boolean>
        get() = _showProgress

    private val vehicleType = MutableLiveData<VehicleType>()

    private val disembarked = MutableLiveData<Boolean>()

    val stopTypes = MutableLiveData<HashSet<BusStopType>>(HashSet())

    @Bindable
    val vehicleRadioChecked: MutableLiveData<Int> = MutableLiveData()

    /*@Bindable
    val stopTypeAdhocChecked = MutableLiveData<Boolean>()*/

    @Bindable
    val disembarkRadioChecked = MutableLiveData<Int>()

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
        this.vehicleType.value = vehicleType
    }

    fun onStopTypeChange(stopType: BusStopType) {
        if (this.stopTypes.value == null)
            this.stopTypes.value = HashSet()

        this.stopTypes.value?.let {
            if (it.contains(stopType))
                it.remove(stopType)
            else
                it.add(stopType)
        }

        /*if (stopType == BusStopType.ADHOC) {
            stopTypeAdhocChecked.value = true
        }*/
    }

    fun onDisembarkChange(disembarked: Boolean) {
        disembarkRadioChecked.value = if (disembarked) R.id.disembark_true else R.id.disembark_false
        this.disembarked.value = disembarked
    }

    fun submitForm(): Job =
        viewModelScope.launch {
            _showProgress.value = true
            val status =
                repository.writeToFile(vehicleType.value!!, disembarked.value!!, stopTypes.value!!)
            _toastMessage.value =
                if (status) Event("Form submitted successfully.")
                else Event("Form could not be submitted.")
            _showProgress.value = false
        }

    fun validateForm(): FormState {
        if (vehicleType.value == null) {
            _toastMessage.value = Event("Please select a vehicle type.")
            return FormState.VEHICLE_TYPE_EMPTY
        }
        if (disembarked.value == null) {
            _toastMessage.value = Event("Please mention whether you got off the vehicle.")
            return FormState.DISEMBARKED_EMPTY
        }
        if (stopTypes.value == null || stopTypes.value?.isEmpty()!!) {
            _toastMessage.value = Event("Please select the type of stop.")
            return FormState.STOP_TYPE_EMPTY
        }
        _toastMessage.value = Event("Inputs validated successfully.")
        return FormState.SUCCESS
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

}