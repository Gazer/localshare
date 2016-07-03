package ar.com.p39.localshare.receiver.ui

import android.support.v4.widget.DrawerLayout
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import ar.com.p39.localshare.R
import ar.com.p39.localshare.common.ui.ViewModifier

/**
 * Created by gazer on 5/25/16.
 */
class ScanViewModifier: ViewModifier {
    override fun <T : View> modify(view: T): T {
        // Basically, what we do here is adding a Developer Setting Fragment to a DrawerLayout!
        val drawerLayout = view.findViewById(R.id.scan_drawer_layout) as DrawerLayout

        val layoutParams = DrawerLayout.LayoutParams(DrawerLayout.LayoutParams.MATCH_PARENT, DrawerLayout.LayoutParams.MATCH_PARENT)
        layoutParams.gravity = Gravity.END;

        drawerLayout.addView(LayoutInflater.from(view.getContext()).inflate(R.layout.developer_scan_settings_view, drawerLayout, false), layoutParams);
        return view;
    }
}