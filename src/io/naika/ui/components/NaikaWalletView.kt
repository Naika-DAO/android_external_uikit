package io.naika.ui.components

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import io.naika.naikapay.Connection
import io.naika.naikapay.NetworkType
import io.naika.naikapay.Payment
import io.naika.naikapay.entity.AccountInfo
import io.naika.naikapay.toSummarisedAddress
import io.naika.ui.R
import io.naika.ui.utils.ConnectionLiveData
import io.naika.ui.utils.ObjectSerializer
import io.naika.ui.utils.enc.encryptedPreferences
import io.naika.ui.utils.enc.readEncryptedFile
import io.naika.ui.utils.enc.writeEncryptedFile
import io.naika.ui.utils.web3.Web3ViewModel
import kotlinx.coroutines.launch
import java.io.File

class NaikaWalletView : FrameLayout, Observer<Boolean> {

    private val statusDot: TextView
        get() = findViewById(R.id.web3_wallet_status)
    private val identityProfileView: ImageView
        get() = findViewById(R.id.web3_profile)
    private val addressView: TextView
        get() = findViewById(R.id.web3_wallet_address)
    private val identityView: TextView
        get() = findViewById(R.id.web3_identity)
    private val addressBalanceView: TextView
        get() = findViewById(R.id.web3_wallet_balance)
    private val walletLaunch: Button
        get() = findViewById(R.id.web3_wallet_launch)
    private val identityContainer: LinearLayout
        get() = findViewById(R.id.web3_identity_container)
    private val arrowContainer: LinearLayout
        get() = findViewById(R.id.web3_content_arrow)

    private val LOG_TAG = javaClass.name.toString()

    private val networkConnection: ConnectionLiveData
        get() = ConnectionLiveData(activity.application)

    private val payment by lazy(LazyThreadSafetyMode.NONE) {
        Payment(context)
    }

    private val mainViewModel: Web3ViewModel
        get() = ViewModelProvider(activity)[Web3ViewModel::class.java]

    private lateinit var paymentConnection: Connection

    private var storedState: State = State.NO_WALLET
    private val validStates = arrayOf(State.NOT_CONNECTED.code, State.CONNECTED.code)

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        LayoutInflater.from(context).inflate(R.layout.naika_wallet_view, this, true)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startPaymentConnection()
        post {
            loadWallet()
            findViewTreeLifecycleOwner()?.let { networkConnection.observe(it, this) }
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(state)
        loadWallet()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        loadWallet()
    }

    private fun updateByState(state: State) {
        isClickable = true
        when (state) {
            State.CONNECTED -> {
                statusDot.tint(R.color.web3_wallet_status_ok)
                //identityContainer.isVisible = true
                walletLaunch.isVisible = true
                if (storedState.code == 404 || storedState.code == -1) {
                    startPaymentConnection()
                    loadWallet()
                }
                setOnClickListener {
                    // Launch Show Activity
                }
            }
            State.NOT_CONNECTED -> {
                statusDot.tint(R.color.web3_wallet_status_warning)
                addressBalanceView.setText(R.string.web3_wallet_no_internet)
                //identityContainer.isVisible = true
                walletLaunch.isVisible = false
                setOnClickListener(null)
            }
            State.ERROR -> {
                statusDot.tint(R.color.web3_wallet_status_error)
                identityContainer.isVisible = false
                walletLaunch.isVisible = false
                addressBalanceView.setText(R.string.web3_wallet_error)
                setOnClickListener {
                    connectWalletWithNewMethod()
                }
            }
            State.NO_WALLET -> {
                statusDot.tint(R.color.web3_wallet_status_error)
                identityContainer.isVisible = false
                walletLaunch.isVisible = false
                addressBalanceView.setText(R.string.web3_wallet_connect)
                setOnClickListener {
                    connectWalletWithNewMethod()
                }
            }
        }
        storedState = state
    }

    private fun connectWalletWithNewMethod() {
        payment.connectWallet(
            registry = activity.activityResultRegistry
        ) {
            connectWalletSucceed { accountInfo ->
                Log.d(LOG_TAG, "connectWalletSucceed")
                loadWallet(accountInfo)

                payment.getGasPrice {
                    gasPriceSucceed {
                        Log.d(LOG_TAG, it.gasPrice.toString())
                    }
                    gasPriceFailed {
                        Log.d(LOG_TAG, it.message!!)
                    }
                }

            }
            connectWalletCanceled {
                Log.d(LOG_TAG, "connectWalletCanceled")
            }
            connectWalletFailed {
                Log.e(LOG_TAG, "Error", it)
                updateByState(State.ERROR)
            }
        }
    }

    private fun loadWallet(accountInfo: AccountInfo) {
        updateByState(State.CONNECTED)
        mainViewModel.isAccountConnected = true
        mainViewModel.selectedAddressHash = accountInfo.address
        val serializedAccountInfo = ObjectSerializer.serialize(accountInfo)
        context.encryptedPreferences.edit()
            .putString("wallet_data", serializedAccountInfo)
            .apply()
        addressBalanceView.text = String.format("%.6f ETH", accountInfo.balance)
        addressView.text = toSummarisedAddress(accountInfo.address)
    }

    private fun loadWallet() {
        val walletData = context.encryptedPreferences.getString("wallet_data", null)
        val hasWallet = walletData != null
        if (hasWallet) {
            val accountInfo = ObjectSerializer.deserialize(walletData) as AccountInfo
            mainViewModel.isAccountConnected = true
            mainViewModel.selectedAddressHash = accountInfo.address
            addressBalanceView.text = String.format("%.6f ETH", accountInfo.balance)
            addressView.text = toSummarisedAddress(accountInfo.address)
            updateByState(State.CONNECTED)
        }
    }

    private fun startPaymentConnection() {
        if (!mainViewModel.connectToNetwork()) {
            post {
                updateByState(State.ERROR)
            }
        }
        paymentConnection = payment.initialize(NetworkType.ETH_MAIN) {
            connectionSucceed {
                Log.d(LOG_TAG, "connectionSucceed")
                updateByState(State.NO_WALLET)
            }
            connectionFailed {
                Log.e(LOG_TAG, "Error", it)
                updateByState(State.ERROR)
            }
            disconnected {
                Log.d(LOG_TAG, "disconnected")
            }
        }
    }

    override fun onDetachedFromWindow() {
        paymentConnection.disconnect()
        super.onDetachedFromWindow()
    }

    private val activity: FragmentActivity
        get() = context as FragmentActivity

    private fun TextView.tint(resID: Int) {
        setTextColor(context.getColor(resID))
    }

    sealed class State(val code: Int = 0) {
        object CONNECTED: State()
        object NOT_CONNECTED: State(404)
        object ERROR: State(-1)
        object NO_WALLET: State(100)
    }

    override fun onChanged(isConnected : Boolean) {
        if (validStates.contains(storedState.code)) {
            val newState = if (isConnected) State.CONNECTED else State.NOT_CONNECTED
            updateByState(newState)
        }
    }
}