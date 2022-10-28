package com.horizam.pro.elean.ui.main.view.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarEntry
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.SellerActionModel
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
import com.github.mikephil.charting.components.*
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.horizam.pro.elean.data.model.AnalyticModel
import com.horizam.pro.elean.data.model.Analytics
import com.horizam.pro.elean.data.model.response.Notification
import com.horizam.pro.elean.data.model.response.NotificationsResponse
import com.horizam.pro.elean.ui.main.adapter.NotificationsAdapter
import com.horizam.pro.elean.ui.main.callbacks.DrawerHandler
import com.horizam.pro.elean.ui.main.viewmodel.NotificationsViewModel
import com.horizam.pro.elean.utils.BaseUtils.Companion.hideKeyboard


class SellerActionsFragment : Fragment(), OnItemClickListener,
    SwipeRefreshLayout.OnRefreshListener {

    private lateinit var binding: FragmentSellerActionsBinding
    private lateinit var navController: NavController
    private lateinit var viewModel: SellerViewModel
    private lateinit var sellerActionAdapter: SellerActionAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var notificationsViewModel: NotificationsViewModel
    private lateinit var adapter: NotificationsAdapter
    private lateinit var genericHandler: GenericHandler
    private lateinit var prefManager: PrefManager
    private lateinit var sellerActionList: ArrayList<SellerActionModel>
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var drawerHandler: DrawerHandler
    private lateinit var notificationsResponse:NotificationsResponse
    private var sum:Int=0
    private var count=0
    private var n:Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSellerActionsBinding.inflate(layoutInflater, container, false)
        getIntentData()
        initViews()
        setToolbarData()
        setupViewModel()
        setupObservers()
        setClickListeners()
        setAdapter()
        executeApi()
        return binding.root
    }

    override fun onRefresh() {
        if (swipeRefreshLayout.isRefreshing) {
            swipeRefreshLayout.isRefreshing = false
        }
        executeApi()
    }
    private fun executeApi() {
        genericHandler.showProgressBar(true)
        viewModel.SellerDataCall()
        notificationsViewModel.getNotificationsCall()
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
                } else if ((requireActivity().intent.getStringExtra(Constants.TYPE)) == Constants.TYPE_OFFER) {
                    val bundle = requireActivity().intent.extras
                    val id = bundle!!.getString(Constants.CONTENT_ID)
                    findNavController().navigate(R.id.postedJobsFragment)
                    requireActivity().intent.removeExtra(Constants.TYPE)
                }
            }
            if (requireActivity().intent.hasExtra("order")) {
                if (requireActivity().intent.getIntExtra("order", 0) == 0) {
                    this.findNavController().navigate(R.id.salesFragment)
                }
                else {
                    if (requireActivity().intent.getIntExtra("order", 0) == 1) {
                        this.findNavController().navigate(R.id.salesFragment)
                    }
                }
                requireActivity().intent.removeExtra("order")
            }
        }
    }
//            else if (requireActivity().intent.hasExtra(Constants.ORDER)) {
//                if (requireActivity().intent.getStringExtra(Constants.TYPE) == Constants.ORDER) {
//                    val bundle = requireActivity().intent.extras
//                    val contentId = bundle!!.getString(Constants.CONTENT_ID)
//
//                }
//            }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        genericHandler = context as GenericHandler
    }


    private fun setupObservers() {
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
        notificationsViewModel.notifications.observe(viewLifecycleOwner) {
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
    private fun setUIData(list: List<Notification>) {
        adapter.submitList(list)
    }
    private fun handleResponse(response: NotificationsResponse) {
        try {
            setUIData(response.data)
            notificationsRes(response.data)
        } catch (e: java.lang.Exception) {
            genericHandler.showErrorMessage(e.message.toString())
        }
    }
    private fun <T> handleResponse(item: T) {
        when (item) {
            is AnalyticModel -> {
                binding.apply {
                    item.apply {
                        tvPersonalBalanceValue.text =
                            "${getString(R.string.str_currency_sign)}$currentBalance"
                        tvAvgSellingPriceValue.text = "${getString(R.string.str_currency_sign)}$yearEarning"
                        tvEarningInDecemberValue.text =
                            "${getString(R.string.str_currency_sign)}$monthlyEarning"
//                        tvPendingClearanceValue.text =
//                            "${getString(R.string.str_currency_sign)}$pending_balance"
                        tvEarningInDecemberValue.text =
                            "${getString(R.string.str_currency_sign)}$monthlyEarning"
                        tvActiveOrdersrValue.apply {
                            text = ""
                            append("$activeOrders")
                     //       val spannable =
//                                SpannableStringBuilder(" (${getString(R.string.str_currency_sign)}$active_orders_balance)")
                          //  spannable.setSpan(
                            //    ForegroundColorSpan(
                              //      ContextCompat.getColor(
                                //        context,
                                  //      white_grey_color
                                   // )
                                //),
                                //0, spannable.length,
                                //Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            //)
                            //append(spannable)
                        }
//                        tvCancelledOrdersValue.apply {
//                            text = ""
//                            append("$cancelled_orders")
//                            val spannable =
//                                SpannableStringBuilder(" (-${getString(R.string.str_currency_sign)}$cancelled_orders_balance)")
//                            spannable.setSpan(
//                                ForegroundColorSpan(
//                                    ContextCompat.getColor(
//                                        context,
//                                        white_grey_color
//                                    )
//                                ),
//                                0, spannable.length,
//                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//                            )
//                            append(spannable)
       //                 }
                    }
                }
                item.totalImpressions?.let {
                    item.totalClicks?.let { it1 ->
                        populateGraphData(item.analytics,
                            it, it1)
                    }
                }
            }
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(SellerViewModel::class.java)
        notificationsViewModel=ViewModelProviders.of(
            requireActivity(),ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(NotificationsViewModel::class.java)
    }

    private fun setAdapter() {
        setDetailSellerActionList()
        adapter= NotificationsAdapter(this)
        sellerActionAdapter = SellerActionAdapter(sellerActionList, this)
        linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvSellerAction.layoutManager = linearLayoutManager
        binding.rvSellerAction.adapter = sellerActionAdapter
    }

    private fun setDetailSellerActionList() {
        sellerActionList.add(
            SellerActionModel(
                title = getString(R.string.str_create_service),
                image = R.drawable.img_create_service
            )
        )
        sellerActionList.add(
            SellerActionModel(
                title = getString(R.string.str_buyer_request),
                image = R.drawable.img_buyer_request
            )
        )
        sellerActionList.add(
            SellerActionModel(
                title = getString(R.string.str_manage_service),
                image = R.drawable.ic_list
            )
        )
        sellerActionList.add(SellerActionModel(title = getString(R.string.str_earnings), image = R.drawable.ic_budget))
    }

    private fun initViews() {
        binding.toolbar.ivToolbar.visibility = View.VISIBLE
        navController = this.findNavController()
        sellerActionList = ArrayList()
        swipeRefreshLayout = binding.swipeRefresh
        swipeRefreshLayout.setOnRefreshListener(this)
        drawerHandler = context as DrawerHandler

    }
    private fun setClickListeners() {
        binding.apply {
            binding.toolbar.ivToolbar.setOnClickListener {
                hideKeyboard()
                drawerHandler.openDrawer()
            }
            binding.toolbar.ivSecond.setOnClickListener {
                this@SellerActionsFragment.findNavController().navigate(R.id.notificationsFragment)
            }
        }
    }

    private fun setToolbarData() {
        binding.toolbar.ivToolbar.isVisible=true
        binding.toolbar.tvToolbar.text = getString(R.string.str_seller_actions)
        binding.toolbar.ivSecond.setImageResource(R.drawable.ic_notifications)
        binding.toolbar.ivSecond.visibility = View.VISIBLE
//        counter(notificationsResponse)
    }
    private fun notificationsRes(list: List<Notification>) {
        val notify = list
        count = notify.count()
        for (n in 0 until count) {
            if (notify[n].viewed == 0) {
                sum = sum + 1

            } else {
                println("is 1")

            }

        }
        println(sum)
        binding.toolbar.tvNoOfNotification.setText(sum.toString())
        binding.toolbar.rlNoOfNotification.isVisible = true
        binding.toolbar.tvNoOfNotification.isVisible = true
        if (sum == 0) {
            binding.toolbar.rlNoOfNotification.isVisible = false
            binding.toolbar.tvNoOfNotification.isVisible = false
        }
        sum = 0
    }
//    private fun counter(response: NotificationsResponse){
//        var notify = response.data.apply {
//            count = count()
//        }
//        for (n in 0 until count) {
//                    if (notify[n].viewed == 0) {
//                        sum = sum + 1
//                        binding.toolbar.rlNoOfNotification.visibility.plus(sum)
//                        binding.toolbar.rlNoOfNotification.isVisible = true
//                    } else {
//                        binding.toolbar.rlNoOfNotification.isVisible = false
//                        println("is 1")
//                    }
//                }
//                println(sum)
////                binding.toolbar.rlNoOfNotification.visibility.plus(sum)
////                binding.toolbar.rlNoOfNotification.isVisible = true
////            }
//        binding.toolbar.rlNoOfNotification.isVisible=true
//    }

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
                buildString {
        append(getString(R.string.str_impression))
        append(weeklyImpression)
        append(", ")
    },
                Legend.LegendForm.SQUARE,
                10f,
                10f,
                null,
                Color.BLUE
            )
        )
        legenedEntries.add(
            LegendEntry(
                buildString {
        append(getString(R.string.str_clicks))
        append(weeklyClicks)
    },
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
            analytics[i].impressions?.let { BarEntry((i + 1).toFloat(), it.toFloat()) }
                ?.let { yValueGroup1.add(it) }
            analytics[i].clicks?.let { BarEntry((i + 1).toFloat(), it.toFloat()) }
                ?.let { yValueGroup2.add(it) }
        }

        barDataSet1 = BarDataSet(yValueGroup1, "")
        barDataSet1.setColors(Color.BLUE)
        barDataSet1.label = getString(R.string.str_impression)
        barDataSet1.setDrawIcons(false)
        barDataSet1.setDrawValues(true)

        barDataSet2 = BarDataSet(yValueGroup2, "")
        barDataSet2.label = getString(R.string.str_clicks)
        barDataSet2.setColors(Color.GREEN)
        barDataSet2.setDrawIcons(false)
        barDataSet2.setDrawValues(true)

        return Pair(barDataSet1, barDataSet2)
    }

    private fun setXAxisValue(analytics: ArrayList<Analytics>): ArrayList<String> {
        var xAxisValues = ArrayList<String>()
        for (i in 6 downTo 0) {
            analytics[i].day?.let { xAxisValues.add(it) }
            analytics[i].day?.let { xAxisValues.add(it) }
        }
        return xAxisValues
    }
}