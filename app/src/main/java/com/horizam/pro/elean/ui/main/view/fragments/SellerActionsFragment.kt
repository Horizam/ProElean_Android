package com.horizam.pro.elean.ui.main.view.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarEntry
import com.horizam.pro.elean.App
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.R.color.white_grey_color
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.SellerActionModel
import com.horizam.pro.elean.data.model.response.SellerDataModel
import com.horizam.pro.elean.databinding.FragmentSellerActionsBinding
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.adapter.SellerActionAdapter
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.callbacks.OnItemClickListener
import com.horizam.pro.elean.ui.main.viewmodel.SellerViewModel
import com.horizam.pro.elean.utils.BaseUtils
import com.horizam.pro.elean.utils.PrefManager
import com.horizam.pro.elean.utils.Status
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.horizam.pro.elean.data.model.Score
import com.github.mikephil.charting.components.*
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.horizam.pro.elean.data.model.response.Analytics


class SellerActionsFragment : Fragment(), OnItemClickListener , SwipeRefreshLayout.OnRefreshListener{

    private lateinit var binding: FragmentSellerActionsBinding
    private lateinit var navController: NavController
    private lateinit var viewModel: SellerViewModel
    private lateinit var sellerActionAdapter: SellerActionAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var genericHandler: GenericHandler
    private lateinit var prefManager: PrefManager
    private lateinit var sellerActionList: ArrayList<SellerActionModel>
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSellerActionsBinding.inflate(layoutInflater, container, false)
        getIntentData()
        setToolbarData()
        setupViewModel()
        initViews()
        setClickListeners()
        setAdapter()
        exeApi()
        return binding.root
    }

    override fun onRefresh() {
        if (swipeRefreshLayout.isRefreshing) {
            swipeRefreshLayout.isRefreshing = false
        }
        exeApi()
    }

    private fun getIntentData() {
        prefManager = PrefManager(requireActivity())
        if (prefManager.sellerMode == 1) {
            if (requireActivity().intent.hasExtra(Constants.TYPE)) {
                if (requireActivity().intent.getStringExtra(Constants.TYPE) == Constants.MESSAGE) {
                    val bundle = requireActivity().intent.extras
                    val id = bundle!!.getString(Constants.SENDER_ID)
                    SellerActionsFragmentDirections.actionSellerFragmentToMessageFragment(id!!)
                        .also {
                            findNavController().navigate(it)
                            requireActivity().intent.removeExtra(Constants.TYPE)
                        }
                }
            }

//        if (requireActivity().intent.hasExtra("order")) {
//            if (requireActivity().intent.getIntExtra("order", 0) == 1) {
//                this.findNavController().navigate(R.id.orderFragment)
//            }
//            requireActivity().intent.removeExtra("order")
//        }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        genericHandler = context as GenericHandler
    }

    private fun exeApi() {
        viewModel.sellerData.observe(viewLifecycleOwner, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        genericHandler.showProgressBar(false)
                        handleResponse(resource.data)
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

    private fun <T> handleResponse(item: T) {
        when (item) {
            is SellerDataModel -> {
                binding.apply {
                    item.apply {
                        tvPersonalBalanceValue.text = "${getString(R.string.str_currency_sign)}$availabe_balance"
                        tvAvgSellingPriceValue.text = "${getString(R.string.str_currency_sign)}$average_selling"
                        tvPendingClearanceValue.text = "${getString(R.string.str_currency_sign)}$pending_balance"
                        tvEarningInDecemberValue.text = "${getString(R.string.str_currency_sign)}$monthly_selling"
                        tvActiveOrdersrValue.apply {
                            text = ""
                            append("$active_orders")
                            val spannable = SpannableStringBuilder(" (${getString(R.string.str_currency_sign)}$active_orders_balance)")
                            spannable.setSpan(
                                ForegroundColorSpan(
                                    ContextCompat.getColor(
                                        context,
                                        white_grey_color
                                    )
                                ),
                                0, spannable.length,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                            append(spannable)
                        }
                        tvCancelledOrdersValue.apply {
                            text = ""
                            append("$cancelled_orders")
                            val spannable = SpannableStringBuilder(" (-${getString(R.string.str_currency_sign)}$cancelled_orders_balance)")
                            spannable.setSpan(
                                ForegroundColorSpan(
                                    ContextCompat.getColor(
                                        context,
                                        white_grey_color
                                    )
                                ),
                                0, spannable.length,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                            append(spannable)
                        }
                    }
                }
                populateGraphData(item.analytics, item.weekly_clicks, item.weekly_impression)
            }
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(SellerViewModel::class.java)
    }

    private fun setAdapter() {
        setDetailSellerActionList()
        sellerActionAdapter = SellerActionAdapter(sellerActionList, this)
        linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvSellerAction.layoutManager = linearLayoutManager
        binding.rvSellerAction.adapter = sellerActionAdapter
    }

    private fun setDetailSellerActionList() {
        sellerActionList.add(
            SellerActionModel(
                title = "Create Service",
                image = R.drawable.ic_create_service
            )
        )
        sellerActionList.add(
            SellerActionModel(
                title = "Buyer Requests",
                image = R.drawable.ic_buyer_request
            )
        )
        sellerActionList.add(
            SellerActionModel(
                title = "Manage Services",
                image = R.drawable.ic_list
            )
        )
        sellerActionList.add(
            SellerActionModel(
                title = "Analytics",
                image = R.drawable.ic_analytics
            )
        )
        sellerActionList.add(SellerActionModel(title = "Earnings", image = R.drawable.ic_budget))
    }

    private fun initViews() {
        binding.toolbar.ivToolbar.visibility = View.INVISIBLE
        navController = this.findNavController()
        sellerActionList = ArrayList()
        swipeRefreshLayout = binding.swipeRefresh
        swipeRefreshLayout.setOnRefreshListener(this)
    }

    private fun setClickListeners() {
        binding.apply {
            toolbar.ivToolbar.setOnClickListener {
                navController.popBackStack()
            }
        }
    }

    private fun setToolbarData() {
        binding.toolbar.ivToolbar.setImageResource(R.drawable.ic_back)
        binding.toolbar.tvToolbar.text =
            App.getAppContext()!!.getString(R.string.str_seller_actions)
    }

    override fun <T> onItemClick(item: T) {
        if (item is Int) {
            when (item) {
                0 -> {
                    navController.navigate(
                        R.id.createServiceFragment,
                        null,
                        BaseUtils.animationOpenScreen()
                    )
                }
                1 -> {
                    navController.navigate(
                        R.id.buyerRequestsFragment,
                        null,
                        BaseUtils.animationOpenScreen()
                    )
                }
                2 -> {
                    navController.navigate(
                        R.id.manageServicesFragment,
                        null,
                        BaseUtils.animationOpenScreen()
                    )
                }
                3 -> {
//                    navController.navigate(
//                        R.id.analyticsFragment,
//                        null,
//                        BaseUtils.animationOpenScreen()
//                    )
                }
                4 -> {
                    navController.navigate(
                        R.id.earningsFragment,
                        null,
                        BaseUtils.animationOpenScreen()
                    )
                }
            }
        }
    }


    fun populateGraphData(
        analytics: ArrayList<Analytics>,
        weeklyClicks: Int,
        weeklyImpression: Int
    ) {

        var barChartView = binding.chart
        barChartView.setExtraOffsets(5f, 5f, 5f, 15f)
        barChartView.isDragEnabled = true
        val barWidth: Float
        val barSpace: Float
        val groupSpace: Float
        barWidth = 0.45f
        barSpace = 0.15f
        groupSpace = 0.80f

        //get graph value
        var (barDataSet1: BarDataSet, barDataSet2: BarDataSet) = getBarGraphValue(analytics)
        var barData = BarData(barDataSet1, barDataSet2)

        barData.barWidth = 1.5f
        barChartView.description.isEnabled = false
        barChartView.description.textSize = 0f
        barData.setValueFormatter(LargeValueFormatter())
        barChartView.data = barData
        barChartView.barData.barWidth = barWidth
        barChartView.xAxis.axisMinimum = 0f
        barChartView.xAxis.axisMaximum = 2f
        barChartView.groupBars(0f, groupSpace, barSpace)
        barChartView.setFitBars(true)
        barChartView.data.isHighlightEnabled = true
        barChartView.invalidate()
        barChartView.animateXY(2000, 2000);
        barChartView.isDoubleTapToZoomEnabled = false


        // set bar label
        var legend = barChartView.legend
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.setDrawInside(false)

        var legenedEntries = arrayListOf<LegendEntry>()

        legenedEntries.add(
            LegendEntry(
                "Impressions=${weeklyImpression}, ",
                Legend.LegendForm.SQUARE,
                10f,
                10f,
                null,
                Color.BLUE
            )
        )
        legenedEntries.add(
            LegendEntry(
                "Clicks=${weeklyClicks}",
                Legend.LegendForm.SQUARE,
                10f,
                10f,
                null,
                Color.GREEN
            )
        )
        legend.setCustom(legenedEntries)
        legend.yOffset = 2f
        legend.xOffset = 2f
        legend.yEntrySpace = 10f
        legend.textSize = 13f
        legend.textColor = ActivityCompat.getColor(requireContext(), R.color.colorWhite)

        val xAxis = barChartView.xAxis
        xAxis.textColor = ActivityCompat.getColor(requireContext(), R.color.colorWhite)
        xAxis.granularity = 0f
        xAxis.isGranularityEnabled = true
        xAxis.setCenterAxisLabels(true)
        xAxis.setDrawGridLines(true)
        xAxis.textSize = 10f
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = IndexAxisValueFormatter(setXAxisValue(analytics))
        xAxis.labelCount = 7
        xAxis.mAxisMaximum = 14f
        xAxis.setCenterAxisLabels(true)
        xAxis.setAvoidFirstLastClipping(true)
        xAxis.spaceMin = 0f
        xAxis.spaceMax = 2f

        barChartView.setVisibleXRangeMaximum(14f)
        barChartView.setVisibleXRangeMinimum(1f)
        barChartView.isDragEnabled = true

        //Y-axis
        barChartView.axisRight.isEnabled = false
        barChartView.setScaleEnabled(true)

        val leftAxis = barChartView.axisLeft
        leftAxis.textColor = ActivityCompat.getColor(requireContext(), R.color.colorWhite)
        leftAxis.valueFormatter = LargeValueFormatter()
        leftAxis.setDrawGridLines(false)
        leftAxis.spaceTop = 1f
        leftAxis.axisMinimum = 0f

        barChartView.data = barData
        barChartView.setVisibleXRange(1f, 14f)
    }

    private fun getBarGraphValue(analytics: ArrayList<Analytics>): Pair<BarDataSet, BarDataSet> {
        var yValueGroup1 = ArrayList<BarEntry>()
        var yValueGroup2 = ArrayList<BarEntry>()
        // draw the graph
        var barDataSet1: BarDataSet
        var barDataSet2: BarDataSet

        for (i in 6 downTo 0) {
            yValueGroup1.add(BarEntry((i + 1).toFloat(), analytics[i].impressions.toFloat()))
            yValueGroup2.add(BarEntry((i + 1).toFloat(), analytics[i].clicks.toFloat()))
        }

        barDataSet1 = BarDataSet(yValueGroup1, "")
        barDataSet1.setColors(Color.BLUE)
        barDataSet1.label = "Impressions"
        barDataSet1.setDrawIcons(false)
        barDataSet1.setDrawValues(true)

        barDataSet2 = BarDataSet(yValueGroup2, "")
        barDataSet2.label = "Clicks"
        barDataSet2.setColors(Color.GREEN)
        barDataSet2.setDrawIcons(false)
        barDataSet2.setDrawValues(true)

        return Pair(barDataSet1, barDataSet2)
    }

    private fun setXAxisValue(analytics: ArrayList<Analytics>): ArrayList<String> {
        var xAxisValues = ArrayList<String>()
        for (i in 6 downTo 0) {
            xAxisValues.add(analytics[i].day)
            xAxisValues.add(analytics[i].day)
        }
        return xAxisValues
    }
}