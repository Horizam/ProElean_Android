package com.horizam.pro.elean.ui.main.view.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.mikephil.charting.animation.Easing
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.databinding.*
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.viewmodel.EarningsViewModel
import com.horizam.pro.elean.utils.Status
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.data.PieData

import com.github.mikephil.charting.data.PieDataSet
import com.horizam.pro.elean.data.model.AnalyticModel


class EarningsFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var binding: FragmentEarningsBinding
    private lateinit var viewModel: EarningsViewModel
    private lateinit var genericHandler: GenericHandler
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var accountVerified: Int = 0
    private lateinit var dialogWithdrawalAmount: Dialog
    private lateinit var layoutWithdrawlAmountBinding: LayoutWithdrawlAmountBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        genericHandler = context as GenericHandler
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEarningsBinding.inflate(layoutInflater, container, false)
        setToolbarData()
        initViews()
        setupViewModel()
        setupObservers()
        setupWithDrawlAmountObserver()
        setOnClickListeners()
        initChooseImageDialog()
        return binding.root
    }

    private fun setupWithDrawlAmountObserver() {
        viewModel.withdrawalAmount.observe(viewLifecycleOwner, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        genericHandler.showProgressBar(false)
                        genericHandler.showSuccessMessage(it.data!!.message.toString())
                    }
                    Status.ERROR -> {
                        genericHandler.showProgressBar(false)
                        genericHandler.showErrorMessage(it.message.toString())
                    }
                    Status.LOADING -> {
                        genericHandler.showProgressBar(true)
                    }
                }
            }
        })
    }

    override fun onRefresh() {
        if (swipeRefreshLayout.isRefreshing) {
            swipeRefreshLayout.isRefreshing = false
        }
        setupObservers()
    }
    private fun setPieChartData(
        weekly: Int,
        yearly: Int,
        monthly:Int)
    {
        val listPie = ArrayList<PieEntry>()
        val listColors = ArrayList<Int>()
        listPie.add(PieEntry(35F, buildString {
        append(getString(R.string.str_yearly))
        append(yearly)
    }))
        listColors.add(ContextCompat.getColor(requireContext(), R.color.colorThree))
        listPie += PieEntry(35F, buildString {
        append(getString(R.string.str_weekly))
        append(weekly)
    })
        listColors.add(ContextCompat.getColor(requireContext(), R.color.color_green))
        listPie.add(PieEntry(30F, buildString {
        append(getString(R.string.str_monthly))
        append(monthly)
    }))
        listColors.add(ContextCompat.getColor(requireContext(), R.color.colorGolden))
        val pieDataSet = PieDataSet(listPie, "")
        pieDataSet.colors = listColors
        val pieData = PieData(pieDataSet)
        pieDataSet.valueTextSize = 12f
        binding.chart.data=pieData
        binding.chart.setUsePercentValues(true)
        binding.chart.isDrawHoleEnabled=false
        binding.chart.description.isEnabled=false
        binding.chart.setEntryLabelColor(R.color.colorAccent)
        binding.chart.animateY(1400,Easing.EaseInOutQuad)
    }

    fun setPieChartGraph( weekly: Int,
                        yearly: Int) {
        val analytics =AnalyticModel()

        binding.chart.isDrawHoleEnabled = true
        binding.chart.setDrawEntryLabels(false)
        binding.chart.setHoleColor(ContextCompat.getColor(requireContext(), R.color.colorAccent))
        val pieEntries: ArrayList<PieEntry> = ArrayList()
        val label = ""
        //initializing data
        val amountMap: HashMap<String, Int> = HashMap()
        val colors: ArrayList<Int> = ArrayList()
        colors.add(ContextCompat.getColor(requireContext(), R.color.colorThree))
        amountMap["Total Earning${weekly}"]= analytics.totalEarning!!.toInt()
        colors.add(ContextCompat.getColor(requireContext(), R.color.colorThree))
        amountMap["This Year Earning${yearly}"] = analytics.yearEarning!!.toInt()
        colors.add(ContextCompat.getColor(requireContext(), R.color.colorGolden))
        amountMap["This Month Earning"] = analytics.monthlyEarning!!.toInt()


        for (type in amountMap.keys) {
            pieEntries.add(PieEntry(amountMap[type]!!.toFloat(), type))
        }

        //collecting the entries with label name
        val pieDataSet = PieDataSet(pieEntries, label)
        //setting text size of the value
        pieDataSet.valueTextSize = 12f
        //providing color list for coloring different entries
        pieDataSet.colors = colors
        //grouping the data set from entry to chart
        val pieData = PieData(pieDataSet)
        //showing the value of the entries, default true if not set
        pieData.setDrawValues(true)

        binding.chart.data = pieData
        binding.chart.description.setEnabled(false)
        binding.chart.setUsePercentValues(true)
        binding.chart.isDrawHoleEnabled=false
        binding.chart.description.isEnabled=false
        binding.chart.setEntryLabelColor(R.color.colorAccent)
        binding.chart.animateY(1400,Easing.EaseInOutQuad)

    }

    private fun initViews() {
        // init views here
        swipeRefreshLayout = binding.swipeRefresh
        swipeRefreshLayout.setOnRefreshListener(this)
    }

    private fun setOnClickListeners() {
        binding.apply {
            toolbar.ivToolbar.setOnClickListener {
                findNavController().popBackStack()
            }
        }
        binding.btnWithdraw.setOnClickListener {

            Toast.makeText(requireContext(),getString(R.string.str_withdrawal),Toast.LENGTH_LONG).show()

        }
//            if (accountVerified == 0) {
//                this.findNavController().navigate(R.id.bankDetailsFragment)
//            } else {
//                dialogWithdrawalAmount.show()
//                val window: Window = dialogWithdrawalAmount.window!!
//                window.setLayout(
//                    ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT
//                )
//            }
//        }
    }

    private fun initChooseImageDialog() {
        dialogWithdrawalAmount = Dialog(requireContext())
        layoutWithdrawlAmountBinding = LayoutWithdrawlAmountBinding.inflate(layoutInflater)
        dialogWithdrawalAmount.setContentView(layoutWithdrawlAmountBinding.root)
        layoutWithdrawlAmountBinding.btnWithdraw.setOnClickListener {
            dialogWithdrawalAmount.dismiss()
            exeWithdrawalRequest(layoutWithdrawlAmountBinding.etAmount.text.toString())
        }
    }

    private fun exeWithdrawalRequest(amount: String) {
        viewModel.withdrawalAmount(amount.toDouble())
    }

    private fun setToolbarData() {
        binding.toolbar.ivToolbar.setImageResource(R.drawable.ic_back)
        binding.toolbar.tvToolbar.text =getString(R.string.str_earnings)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(EarningsViewModel::class.java)
    }

    private fun setupObservers() {
        viewModel.earnings.observe(viewLifecycleOwner) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        genericHandler.showProgressBar(false)
                        resource.data?.let { response ->
                            handleResponse(response)
                        }
                    }
                    Status.ERROR -> {
                        genericHandler.showProgressBar(false)
                        genericHandler.showErrorMessage(it.message.toString())
                    }
                    Status.LOADING -> {
                        genericHandler.showProgressBar(true)
                    }
                }
            }
        }
    }

    private fun <T> handleResponse(item: T) {
        when (item) {
            is AnalyticModel -> {
                binding.apply {
                    item.apply {
                        tvCurrentBalanceValue.text =
                            "${getString(R.string.str_currency_sign)}$currentBalance"
                        tvTotalEarningValue.text =
                            "${getString(R.string.str_currency_sign)}$totalEarning"
                        tvEarningThisYearValue.text =
                            "${getString(R.string.str_currency_sign)}$yearEarning"
//                        tvPendingClearanceValue.text =
//                            "${getString(R.string.str_currency_sign)}$pending_balance"
                        tvEarningThisMonthValue.text =
                            "${getString(R.string.str_currency_sign)}$monthlyEarning"
                        tvEarningThisWeekValue.text =
                            "${getString(R.string.str_currency_sign)}$weeklyEarning"
                    }
                 //    setPieChartGraph(item.yearEarning!!.toInt(),item.weeklyEarning!!.toInt())
                   setPieChartData(item.yearEarning!!.toInt(),item.weeklyEarning!!.toInt(),item.monthlyEarning!!.toInt())
                   // item.weeklyEarning?.let { setPieChartGraph(it) }
                            }
                        }
                    }
                }
}



//    private fun handleResponse() {
//        try {
////            accountVerified = response.data.bankaccount_verified
//             setPieChartGraph()
//            setUIData()
//        } catch (e: Exception) {
//            genericHandler.showErrorMessage(e.message.toString())
//        }
//    }
//
//    private fun setUIData() {
//        var analytics=AnalyticModel()
//        binding.apply {
//            tvCurrentBalanceValue.text =
//                "${getString(R.string.str_currency_sign)}${analytics.currentBalance}"
//            tvTotalEarningValue.text =
//                "${getString(R.string.str_currency_sign)}${analytics.totalEarning}"
//            tvEarningThisYearValue.text =
//                "${getString(R.string.str_currency_sign)}${analytics.yearEarning}"
//            tvEarningThisMonthValue.text =
//                "${getString(R.string.str_currency_sign)}${analytics.monthlyEarning}"
//            tvEarningThisWeekValue.text =
//                "${getString(R.string.str_currency_sign)}${analytics.weeklyEarning}"
//        }
//    }
