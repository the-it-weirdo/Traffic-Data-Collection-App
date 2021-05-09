package dev.debaleen.project20050120

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import dev.debaleen.project20050120.databinding.ActivityStopInformationFormBinding
import dev.debaleen.project20050120.repositories.StopInformationRepository
import dev.debaleen.project20050120.util.*
import dev.debaleen.project20050120.viewmodels.StopInformationFormViewModel
import dev.debaleen.project20050120.viewmodels.StopInformationFormViewModelFactory
import java.util.concurrent.CancellationException

class StopInformationFormActivity : AppCompatActivity() {

    companion object {
        private val TAG = StopInformationFormActivity::class.java.simpleName
    }

    private lateinit var binding: ActivityStopInformationFormBinding
    private lateinit var viewModel: StopInformationFormViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_stop_information_form)
        val stopInformationRepository = StopInformationRepository(this)
        val vmFactory = StopInformationFormViewModelFactory(stopInformationRepository)
        viewModel = ViewModelProvider(this, vmFactory).get(StopInformationFormViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.showProgress.observe(this, {
            enableOrDisableInputs(!it)
            if (it) binding.progressBar.show() else binding.progressBar.hide()
        })

        viewModel.toastMessage.observe(this, {
            if (!it.hasBeenHandled)
                Toast.makeText(this, it.getContentIfNotHandled(), Toast.LENGTH_SHORT).show()
        })

        binding.btnSubmit.setOnClickListener {
            binding.disembarkTitle.error = null
            binding.vehicleTypeTitle.error = null
            binding.typeOfStopTitle.error = null

            when (viewModel.validateForm()) {
                StopInformationFormViewModel.FormState.SUCCESS -> {
                    val job = viewModel.submitForm()
                    job.invokeOnCompletion {
                        when (it) {
                            null -> finish()
                            is CancellationException -> Log.i(
                                TAG,
                                "Submit form job was cancelled. ${it.cause?.message}"
                            )
                            else -> {
                                Log.e(TAG, "Submit form job was unsuccessful.", it)
                            }
                        }
                    }
                }
                StopInformationFormViewModel.FormState.VEHICLE_TYPE_EMPTY -> {
                    binding.vehicleTypeTitle.error = "Please select a vehicle type."
                    binding.vehicleTypeRadio.requestFocus()
                }
                StopInformationFormViewModel.FormState.DISEMBARKED_EMPTY -> {
                    binding.disembarkTitle.error =
                        "Please select whether you have got off the vehicle."
                    binding.disembarkRadio.requestFocus()
                }
                StopInformationFormViewModel.FormState.STOP_TYPE_EMPTY -> {
                    binding.typeOfStopTitle.error = "Please select the type of stop."
                    binding.typeOfStopRadio.requestFocus()
                }
            }
        }
    }

    private fun enableOrDisableInputs(value: Boolean) {
        if (value) {
            binding.vehicleTypeBus.enable()
            binding.vehicleTypeCab.enable()
            binding.vehicleTypeTwoWheeler.enable()
            binding.vehicleTypeThreeWheeler.enable()
            binding.vehicleTypeFourWheeler.enable()
            binding.vehicleTypeTwoWheelerWithEngine.enable()
            binding.vehicleTypeThreeWheelerWithEngine.enable()
            binding.disembarkTrue.enable()
            binding.disembarkFalse.enable()
            binding.typeOfStopSignal.enable()
            binding.typeOfStopAdhoc.enable()
            binding.typeOfStopCongestion.enable()
            binding.typeOfStopRegular.enable()
            binding.btnSubmit.enable()
        } else {
            binding.vehicleTypeBus.disable()
            binding.vehicleTypeCab.disable()
            binding.vehicleTypeTwoWheeler.disable()
            binding.vehicleTypeThreeWheeler.disable()
            binding.vehicleTypeFourWheeler.disable()
            binding.vehicleTypeTwoWheelerWithEngine.disable()
            binding.vehicleTypeThreeWheelerWithEngine.disable()
            binding.disembarkTrue.disable()
            binding.disembarkFalse.disable()
            binding.typeOfStopSignal.disable()
            binding.typeOfStopAdhoc.disable()
            binding.typeOfStopCongestion.disable()
            binding.typeOfStopRegular.disable()
            binding.btnSubmit.disable()
        }
    }
}