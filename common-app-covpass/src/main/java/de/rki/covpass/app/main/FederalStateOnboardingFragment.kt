/*
 * (C) Copyright IBM Deutschland GmbH 2021
 * (C) Copyright IBM Corp. 2021
 */

package de.rki.covpass.app.main

import android.os.Bundle
import android.view.View
import com.ensody.reactivestate.android.autoRun
import com.ensody.reactivestate.get
import com.ibm.health.common.android.utils.viewBinding
import com.ibm.health.common.navigation.android.FragmentNav
import com.ibm.health.common.navigation.android.findNavigator
import de.rki.covpass.app.R
import de.rki.covpass.app.databinding.FederalStateOnboardingPopupContentBinding
import de.rki.covpass.commonapp.BaseBottomSheet
import de.rki.covpass.commonapp.dependencies.commonDeps
import de.rki.covpass.commonapp.federalstate.ChangeFederalStateCallBack
import de.rki.covpass.commonapp.federalstate.ChangeFederalStateFragmentNav
import de.rki.covpass.commonapp.utils.FederalStateResolver
import kotlinx.parcelize.Parcelize

internal interface FederalStateOnboardingCallback {
    fun onFederalStateOnboardingFinish()
}

@Parcelize
internal class FederalStateOnboardingFragmentNav : FragmentNav(FederalStateOnboardingFragment::class)

internal class FederalStateOnboardingFragment : BaseBottomSheet(), ChangeFederalStateCallBack {

    private val binding by viewBinding(FederalStateOnboardingPopupContentBinding::inflate)
    override val buttonTextRes: Int = R.string.ok

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomSheetBinding.bottomSheetTitle.setText(R.string.infschg_popup_choose_federal_state_title)
        binding.federalStateOnboardingValue.setOnClickListener {
            findNavigator().push(
                ChangeFederalStateFragmentNav(commonDeps.federalStateRepository.federalState.value),
            )
        }
        autoRun {
            FederalStateResolver.getFederalStateByCode(
                get(commonDeps.federalStateRepository.federalState),
            )?.let {
                binding.federalStateOnboardingValue.updateText(
                    getString(it.nameRes),
                )
            }
        }
    }

    override fun onActionButtonClicked() {
        launchWhenStarted {
            commonDeps.federalStateRepository.federalStateOnboardingShown.set(true)
            findNavigator().popUntil<FederalStateOnboardingCallback>()?.onFederalStateOnboardingFinish()
        }
    }

    override fun onClickOutside() {
        launchWhenStarted {
            commonDeps.federalStateRepository.federalStateOnboardingShown.set(true)
            findNavigator().popUntil<FederalStateOnboardingCallback>()?.onFederalStateOnboardingFinish()
        }
    }

    override fun onChangeDone() {}
}