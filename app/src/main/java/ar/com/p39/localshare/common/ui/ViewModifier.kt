package ar.com.p39.localshare.common.ui

import android.view.View

/**
 * Created by gazer on 5/25/16.
 */
interface ViewModifier {
    fun <T : View> modify(view: T): T
}
