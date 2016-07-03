package ar.com.p39.localshare.receiver.fragments

import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.Switch
import ar.com.p39.localshare.MyApplication
import ar.com.p39.localshare.R
import ar.com.p39.localshare.Settings
import ar.com.p39.localshare.receiver.activities.ScanActivity
import butterknife.bindView
import javax.inject.Inject

/**
 * Created by gazer on 5/25/16.
 */
class ScanDeveloperSettingsFragment: Fragment() {
    val simulateQR: Button by bindView(R.id.b_qr_scan)
    val fakeSSID: Switch by bindView(R.id.developer_settings_device_fake_ssid)
    val fakedSSID: EditText by bindView(R.id.developer_settings_device_ssid)
    val serverUrl: EditText by bindView(R.id.developer_settings_server_url)

    @Inject
    lateinit var settings: Settings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        MyApplication.graph.plusDeveloperSettingsComponent().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_scan_developer_settings, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        simulateQR.setOnClickListener {
            Log.d(TAG, "Simulating QR : ${serverUrl.text.toString()}")
            val activity: ScanActivity = activity as ScanActivity
            activity.onSharerDetected(serverUrl.text.toString())
        }

        fakeSSID.setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->
            settings.fakeSSID = b
        }
        fakeSSID.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                settings.fakedSSID = s.toString()
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })
        super.onActivityCreated(savedInstanceState)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fakeSSID.isChecked = settings.fakeSSID
        fakedSSID.text = SpannableStringBuilder(settings.fakedSSID)
    }

    companion object {
        var TAG = "ScanDeveloperSettings"
    }
}