/*
 * (C) Copyright IBM Deutschland GmbH 2021
 * (C) Copyright IBM Corp. 2021
 */

package de.rki.covpass.commonapp

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import androidx.annotation.LayoutRes
import com.ibm.health.common.android.utils.BaseHookedActivity
import com.ibm.health.common.android.utils.isDebuggable
import com.ibm.health.common.android.utils.setupForAccessibility
import com.ibm.health.common.annotations.Continue
import com.ibm.health.common.navigation.android.Navigator
import com.ibm.health.common.navigation.android.NavigatorOwner
import de.rki.covpass.commonapp.dependencies.commonDeps
import de.rki.covpass.commonapp.dialog.DialogAction
import de.rki.covpass.commonapp.dialog.DialogListener

/** Common base activity with some common functionality like error handling or loading behaviour. */
public abstract class BaseActivity(@LayoutRes contentLayoutId: Int = 0) :
    BaseHookedActivity(contentLayoutId = contentLayoutId),
    NavigatorOwner,
    DialogListener {

    override val navigator: Navigator = Navigator()

    override fun onCreate(savedInstanceState: Bundle?) {
        if (!isDebuggable) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE,
            )
        }
        super.onCreate(savedInstanceState)
        supportFragmentManager.setupForAccessibility()
    }

    override fun onBackPressed() {
        if (navigator.onBackPressed() == Continue) {
            super.onBackPressed()
        }
    }

    override fun onError(error: Throwable) {
        commonDeps.errorHandler.handleError(error, supportFragmentManager)
    }

    override fun setLoading(isLoading: Boolean) {}

    override fun onDialogAction(tag: String, action: DialogAction) {
        if (tag == UPDATE_AVAILABLE_TAG && action == DialogAction.POSITIVE) {
            try {
                this.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$packageName"),
                    ),
                )
            } catch (e: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$packageName"),
                    ),
                )
            }
        }
    }

    public companion object {
        public const val UPDATE_AVAILABLE_TAG: String = "update_available_tag"
    }
}
