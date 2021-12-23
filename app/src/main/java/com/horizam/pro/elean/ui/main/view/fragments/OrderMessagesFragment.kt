package com.horizam.pro.elean.ui.main.view.fragments

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.horizam.pro.elean.App
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.*
import com.horizam.pro.elean.data.model.requests.ChatOfferRequest
import com.horizam.pro.elean.data.model.response.GeneralResponse
import com.horizam.pro.elean.data.model.response.Order
import com.horizam.pro.elean.databinding.DialogChooseAttachmentBinding
import com.horizam.pro.elean.databinding.DialogFileUploadingBinding
import com.horizam.pro.elean.databinding.FragmentOrderMessagesBinding
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.adapter.MessageAdapter
import com.horizam.pro.elean.ui.main.adapter.MyLoadStateAdapter
import com.horizam.pro.elean.ui.main.callbacks.CheckoutHandler
import com.horizam.pro.elean.ui.main.callbacks.CreateOfferHandler
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.callbacks.MessagesHandler
import com.horizam.pro.elean.ui.main.view.activities.ManageOrdersActivity
import com.horizam.pro.elean.ui.main.viewmodel.FirebaseNotification
import com.horizam.pro.elean.ui.main.viewmodel.MessagesViewModel
import com.horizam.pro.elean.ui.main.viewmodel.NotificationMessage
import com.horizam.pro.elean.utils.*
import com.horizam.pro.elean.utils.BaseUtils.Companion.hideKeyboard
import com.stfalcon.imageviewer.StfalconImageViewer
import java.io.File
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList


class OrderMessagesFragment(var order: Order) : Fragment() , MessagesHandler, CreateOfferHandler, CheckoutHandler {

    private lateinit var binding: FragmentOrderMessagesBinding
    private lateinit var adapter: MessageAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var messagesArrayList: ArrayList<Message>
    private lateinit var db: FirebaseFirestore
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var inboxReference: CollectionReference
    private lateinit var messagesReference: CollectionReference
    private lateinit var prefManager: PrefManager
    private val args: MessagesFragmentArgs by navArgs()
    private lateinit var genericHandler: GenericHandler
    private var inbox: Inbox? = null
    private var userId: String = ""
    private var myId: String = ""
    private var myName = ""
    private var inboxCombinedId = ""
    private var myInfo: MessageUser? = null
    private var userInfo: MessageUser? = null
    private var offerMessage: Message? = null
    private lateinit var viewModel: MessagesViewModel
    private var chatNotExist: Boolean = true
    private var referGig: Boolean = false
    private lateinit var dialogChooseAttachment: Dialog
    private lateinit var bindingChooseAttachmentDialog: DialogChooseAttachmentBinding
    private lateinit var dialogFileUpload: Dialog
    private lateinit var bindingFileUploadDialog: DialogFileUploadingBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        genericHandler = context as GenericHandler
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOrderMessagesBinding.inflate(layoutInflater, container, false)

        setToolbarData()
        initViews()
        //setupViewModel()
        //setupObservers()
        getData()
        //setRecyclerView()
        setClickListeners()

        return binding.root
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(MessagesViewModel::class.java)
    }

    private fun setupObservers() {
        viewModel.messages.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
        viewModel.chatOrder.observe(viewLifecycleOwner, chatOrderObserver)
        viewModel.sendNotification.observe(viewLifecycleOwner, notificationObserver)
    }

    override fun onPause() {
        super.onPause()
        BaseUtils.CurrentScreen = ""
    }

    private val notificationObserver = Observer<Resource<GeneralResponse>> {
        it?.let { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    genericHandler.showProgressBar(false)
                    resource.data?.let { response ->
//                        handleResponse(response)
                    }
                }
                Status.ERROR -> {
                    genericHandler.showProgressBar(false)
                    genericHandler.showMessage(it.message.toString())
                }
                Status.LOADING -> {
                    genericHandler.showProgressBar(false)
                }
            }
        }
    }

    private val chatOrderObserver = Observer<Resource<GeneralResponse>> {
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
                    genericHandler.showMessage(it.message.toString())
                }
                Status.LOADING -> {
                    genericHandler.showProgressBar(true)
                }
            }
        }
    }

    private fun handleResponse(response: GeneralResponse) {
        genericHandler.showMessage(response.message)
        if (response.status == Constants.STATUS_OK) {
            if (offerMessage != null) {
                db.collection(Constants.FIREBASE_DATABASE_ORDERS_CONVERSATION).document(inbox!!.id)
                    .collection("Messages").document(offerMessage!!.id).update(
                        mapOf(
                            "messageOffer.status" to Constants.OFFER_ACCEPTED
                        )
                    ).addOnSuccessListener {
                        startActivity(Intent(requireActivity(), ManageOrdersActivity::class.java))
                    }
            }
        }
    }

    private fun getData() {
        try {
            if(prefManager.userId == order.sellerId){
                userId = order.buyerId
            }
//            referGig = args.refersGig
            myId = prefManager.userId
            myName = prefManager.username!!
            inboxCombinedId = generateCombinedId()
            if (userId != "" && myId != "" && inboxCombinedId.isNotEmpty()) {
                try {
                    getChatUsers()
                } catch (ex: Exception) {
                    genericHandler.showMessage(ex.message.toString())
                }
            }
        } catch (ex: Exception) {
            genericHandler.showMessage(ex.message.toString())
        }
    }

    private fun getChatUsers() {
        val usersReference = db.collection(Constants.FIREBASE_DATABASE_USERS)
        usersReference.document(myId.toString()).get().addOnSuccessListener {
            myInfo = it.toObject(MessageUser::class.java)
            //adapter.setMyInfo(myInfo!!)
            getOtherUser()
        }.addOnFailureListener {
            genericHandler.showMessage(it.message.toString())
            findNavController().popBackStack()
        }
    }

    private fun getOtherUser() {
        val usersReference = db.collection(Constants.FIREBASE_DATABASE_USERS)
        usersReference.document(userId.toString()).get()
            .addOnSuccessListener {
                userInfo = it.toObject(MessageUser::class.java)
                userInfo?.let { user ->
                    if (user.name.isNotEmpty()) {
                        binding.toolbar.tvToolbar.text = user.name
                    }
                }
                //adapter.setUserInfo(userInfo!!)
                if (prefManager.isFreelancer == 1) {
                    binding.tvCreateOffer.isVisible = true
                }
                setRecyclerView()
                checkIfChatExists()
            }.addOnFailureListener {
                genericHandler.showMessage(it.message.toString())
                findNavController().popBackStack()
            }
    }

    private fun checkIfChatExists() {
        val query: Query = inboxReference.whereEqualTo("combinedId", inboxCombinedId)
            .orderBy("sentAt", Query.Direction.DESCENDING)
        query.get().addOnSuccessListener { queryDocumentSnapshots ->
            chatNotExist = queryDocumentSnapshots.size() == 0
            for (documentSnapshot in queryDocumentSnapshots) {
                inbox = documentSnapshot.toObject(Inbox::class.java)
                try {
                    updateUsersInfo(true)
                } catch (ex: Exception) {
                    genericHandler.showMessage(ex.message.toString())
                }
            }
        }.addOnFailureListener {
//            Log.i(MessagesFragment::class.java.simpleName, it.message.toString())
//            genericHandler.showMessage(it.message.toString())
        }
    }

    private fun updateUsersInfo(updateMessages: Boolean) {
        val hashMap = HashMap<String, Any>()
        val membersInfo: List<MembersInfo> = inbox!!.membersInfo
        for (i in membersInfo.indices) {
            membersInfo[i].type = "available"
            if (membersInfo[i].id == myId) {
                membersInfo[i].name = myInfo!!.name
                membersInfo[i].photo = myInfo!!.photo
                membersInfo[i].hasReadLastMessage = true
            } else {
                membersInfo[i].name = userInfo!!.name
                membersInfo[i].photo = userInfo!!.photo
            }
        }
        hashMap["membersInfo"] = membersInfo
        db.collection(Constants.FIREBASE_DATABASE_ORDERS_CONVERSATION).document(inbox!!.id).update(hashMap)
            .addOnSuccessListener {
                if (chatNotExist || updateMessages) {
                    chatNotExist = false
                    fetchMessages()
                }
                disableMessageSend(true)
            }.addOnFailureListener {
                disableMessageSend(true)
                genericHandler.showMessage(it.message.toString())
            }
    }

    private fun fetchMessages() {
        messagesReference = db.collection(Constants.FIREBASE_DATABASE_ORDERS_CONVERSATION).document(inbox!!.id)
            .collection("Messages")
        val query = messagesReference.orderBy("sentAt", Query.Direction.DESCENDING).limit(10)
        //genericHandler.showProgressBar(true)
        viewModel.getMessagesCall(query)
        observeMessages(query)
    }

    private fun observeMessages(query: Query) {
        query.addSnapshotListener { snapshots, e ->
            if (e != null) {
                genericHandler.showMessage(e.message.toString())
                return@addSnapshotListener
            }

            for (dc in snapshots!!.documentChanges) {
                when (dc.type) {
                    DocumentChange.Type.ADDED -> {
                        adapter.refresh()
                    }
                    DocumentChange.Type.MODIFIED -> {
                        adapter.refresh()
                    }
                    DocumentChange.Type.REMOVED -> {
                        adapter.refresh()
                    }
                }
            }
        }
    }

    private fun hasReadLastMessage() {
        val hashMap = HashMap<String, Any>()
        val membersInfo: List<MembersInfo> = inbox!!.membersInfo
        for (i in membersInfo.indices) {
            membersInfo[i].type = "available"
            if (membersInfo[i].id == myId) {
                membersInfo[i].name = myInfo!!.name
                membersInfo[i].photo = myInfo!!.photo
                membersInfo[i].hasReadLastMessage = true
            } else {
                membersInfo[i].name = userInfo!!.name
                membersInfo[i].photo = userInfo!!.photo
            }
        }
        hashMap["membersInfo"] = membersInfo
        db.collection(Constants.FIREBASE_DATABASE_ORDERS_CONVERSATION).document(inbox!!.id).update(hashMap)
    }

    private fun setRecyclerView() {
        adapter = MessageAdapter(this, myInfo, userInfo)
        val linearLayoutManager = LinearLayoutManager(requireContext()).also { layoutManager ->
            layoutManager.reverseLayout = true
            layoutManager.stackFromEnd = false
        }
        recyclerView.let {
            it.setHasFixedSize(true)
            it.layoutManager = linearLayoutManager
            it.adapter = adapter.withLoadStateFooter(
                footer = MyLoadStateAdapter {
                    adapter.retry()
                }
            )
        }
        setAdapterLoadState(adapter)
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0) {
                    linearLayoutManager.scrollToPosition(0)
                }
            }
        })
        setupViewModel()
        setupObservers()
    }

    private fun generateCombinedId(): String {
        val inboxCombinedId: String = if (userId < myId) {
            userId.toString().plus("_").plus(myId).plus(order.orderNo)
        } else {
            myId.toString().plus("_").plus(userId).plus(order.orderNo)
        }
        return inboxCombinedId
    }

    private fun initViews() {
        //adapter = MessageAdapter(this)
        recyclerView = binding.rvMessages
        db = Firebase.firestore
        firebaseStorage = FirebaseStorage.getInstance()
        inboxReference = db.collection(Constants.FIREBASE_DATABASE_ORDERS_CONVERSATION)
        messagesArrayList = ArrayList()
        prefManager = PrefManager(requireContext())
        initChooseAttachmentDialog()
        initFileUploadDialog()
        BaseUtils.CurrentScreen = Constants.MESSAGESCREEN
    }

    private fun initFileUploadDialog() {
        dialogFileUpload = Dialog(requireContext())
        bindingFileUploadDialog = DialogFileUploadingBinding.inflate(layoutInflater)
        dialogFileUpload.setContentView(bindingFileUploadDialog.root)
        setDialogWidth()
    }

    private fun setDialogWidth() {
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialogFileUpload.window!!.attributes)
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        dialogFileUpload.window!!.attributes = layoutParams
    }

    private fun initChooseAttachmentDialog() {
        dialogChooseAttachment = Dialog(requireContext())
        bindingChooseAttachmentDialog = DialogChooseAttachmentBinding.inflate(layoutInflater)
        dialogChooseAttachment.setContentView(bindingChooseAttachmentDialog.root)
    }

    private fun setAdapterLoadState(adapter: MessageAdapter) {
        adapter.addLoadStateListener { loadState ->
            binding.apply {
//                genericHandler.showProgressBar(loadState.source.refresh is LoadState.Loading)
//                recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
                btnRetry.isVisible = loadState.source.refresh is LoadState.Error
//                textViewError.isVisible = loadState.source.refresh is LoadState.Error
                // no results
                if (loadState.source.refresh is LoadState.NotLoading &&
                    loadState.append.endOfPaginationReached &&
                    adapter.itemCount < 1
                ) {
                    recyclerView.isVisible = false
                    tvPlaceholder.isVisible = true
                } else {
                    tvPlaceholder.isVisible = false
                }
            }
        }
    }

    private fun setClickListeners() {
        binding.apply {
            toolbar.ivToolbar.setOnClickListener {
                findNavController().popBackStack()
            }
            ivSend.setOnClickListener {
                try {
                    hideKeyboard()
                    disableMessageSend(false)
                    validateMessage()
                } catch (ex: Exception) {
                    disableMessageSend(true)
                    genericHandler.showMessage(ex.message.toString())
                }
            }
            btnRetry.setOnClickListener {
                adapter.retry()
            }
            ivAttachment.setOnClickListener {
                dialogChooseAttachment.show()
                bindingChooseAttachmentDialog.btnGallery.setOnClickListener {
                    dialogChooseAttachment.dismiss()
                    onClickRequestPermission()
                }
                bindingChooseAttachmentDialog.btnVideo.setOnClickListener {
                    dialogChooseAttachment.dismiss()
                    onClickRequestFilePermission()
                }
                bindingChooseAttachmentDialog.btnNo.setOnClickListener { dialogChooseAttachment.dismiss() }
            }
            tvCreateOffer.setOnClickListener {
                val createOfferBottomSheet = CreateOfferBottomSheet(this@OrderMessagesFragment)
                createOfferBottomSheet.show(
                    requireActivity().supportFragmentManager,
                    CreateOfferBottomSheet.TAG
                )
            }
        }
    }

    private fun onClickRequestFilePermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                getContent.launch("*/*")
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) -> {
                showSnackbar(
                    requireView(),
                    getString(R.string.permission_required),
                    Snackbar.LENGTH_INDEFINITE,
                    getString(R.string.str_ok)
                ) {
                    requestFilePermissionLauncher.launch(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                }
            }
            else -> {
                requestFilePermissionLauncher.launch(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }
        }
    }

    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            // Handle the returned Uri
            uri?.let {
                val uriPathHelper = URIPathHelper()
                val filePath = uriPathHelper.getPath(requireContext(), uri)
                if (!filePath.isNullOrEmpty()) {
                    val file = File(filePath)
                    handleFiles(file)
                } else {
                    genericHandler.showMessage(getString(R.string.str_choose_valid_file))
                }

            }
        }

    private fun handleFiles(file: File) {
        when (file.extension.lowercase()) {
            "mp4", "mkv" -> {
                //genericHandler.showMessage("Video File")
                uploadVideoToStorage(file)
            }
            "pdf" -> {
                // genericHandler.showMessage("Pdf File or other document")
                uploadDocumentToStorage(file)
            }
            else -> {
                genericHandler.showMessage(getString(R.string.str_choose_valid_file))
            }
        }
    }

    private val requestFilePermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.i("Permission: ", "Granted")
                getContent.launch("*/*")
            } else {
                Log.i("Permission: ", "Denied")
                genericHandler.showMessage(
                    getString(R.string.permission_required)
                        .plus(". ").plus(R.string.str_please_enable)
                )
            }
        }

    private fun onClickRequestPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                val imageIntent = Intent().apply {
                    type = "image/*"
                    putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
                    action = Intent.ACTION_GET_CONTENT
                }
                resultLauncher.launch(imageIntent)
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) -> {
                showSnackbar(
                    requireView(),
                    getString(R.string.permission_required),
                    Snackbar.LENGTH_INDEFINITE,
                    getString(R.string.str_ok)
                ) {
                    requestPermissionLauncher.launch(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                }
            }
            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.i("Permission: ", "Granted")
                val imageIntent = Intent().apply {
                    type = "image/*"
                    putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
                    action = Intent.ACTION_GET_CONTENT
                }
                resultLauncher.launch(imageIntent)
            } else {
                Log.i("Permission: ", "Denied")
                genericHandler.showMessage(
                    getString(R.string.permission_required)
                        .plus(". ").plus(getString(R.string.str_please_enable))
                )
            }
        }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data != null) {
                    try {
                        handlePickerResult(result.data!!)
                    } catch (e: Exception) {
                        genericHandler.showMessage(e.message.toString())
                    }
                } else {
                    genericHandler.showMessage("Invalid data")
                }
            }
        }

    private fun handlePickerResult(data: Intent) {
        if (data.data != null) {
            // if single image is selected
            val imageUri: Uri = data.data!!
            findAbsolutePath(imageUri)
        }
    }

    private fun findAbsolutePath(imageUri: Uri) {
        imageUri.let {
            val uriPathHelper = URIPathHelper()
            val imagePath = uriPathHelper.getPath(requireContext(), it)
            if (!imagePath.isNullOrEmpty()) {
                uploadImageToStorage(imagePath)
            } else {
                genericHandler.showMessage("Choose valid image")
            }
        }
    }

    private fun uploadVideoToStorage(file: File) {
        val uniqueId = UUID.randomUUID().toString()
        val storagePath = "uploads/videos/".plus(myId).plus("/").plus(uniqueId)
        uploadFile(file, storagePath, Constants.MESSAGE_TYPE_VIDEO)
    }

    private fun uploadDocumentToStorage(file: File) {
        val uniqueId = UUID.randomUUID().toString()
        val storagePath = "uploads/docs/".plus(myId).plus("/").plus(uniqueId)
        uploadFile(file, storagePath, Constants.MESSAGE_TYPE_DOCUMENT)
    }

    private fun uploadImageToStorage(imagePath: String) {
        val file = File(imagePath)
        val uniqueId = UUID.randomUUID().toString()
        val storagePath = "uploads/images/".plus(myId).plus("/").plus(uniqueId)
        uploadFile(file, storagePath, Constants.MESSAGE_TYPE_IMAGE)
    }

    private fun uploadFile(file: File, storagePath: String, messageType: Int) {
        val ext: String = file.extension
        if (ext.isEmpty()) {
            genericHandler.showMessage(getString(R.string.str_something_went_wrong))
            return
        }
        val storageReference = firebaseStorage.reference.child("$storagePath.$ext")
        val uriFile = Uri.fromFile(file)
        val uploadTask: UploadTask = storageReference.putFile(uriFile)
        uploadTask.addOnSuccessListener { taskSnapshot ->
            if (taskSnapshot.metadata != null && taskSnapshot.metadata!!.reference != null) {
                val result = taskSnapshot.storage.downloadUrl
                result.addOnSuccessListener { uri ->
                    if (uri != null) {
                        if (dialogFileUpload.isShowing) {
                            dialogFileUpload.dismiss()
                        }
                        val utcMilliseconds = Calendar.getInstance().timeInMillis
//                        sendMessageToFirebase(
//                            uri.toString(), "", myId,
//                            utcMilliseconds, messageType
//                        )
                    }
                }
            }
        }.addOnProgressListener { taskSnapshot ->
            val progress: Double =
                100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
            if (!dialogFileUpload.isShowing) {
                dialogFileUpload.show()
            }
            bindingFileUploadDialog.progressBar.progress = progress.toInt()
            bindingFileUploadDialog.tvFileProgress.text = progress.toInt().toString().plus("%")
        }.addOnFailureListener { e ->
            genericHandler.showMessage(e.message.toString())
            if (dialogFileUpload.isShowing) {
                dialogFileUpload.dismiss()
            }
        }
    }

    fun showSnackbar(
        view: View, msg: String, length: Int, actionMessage: CharSequence?,
        action: (View) -> Unit
    ) {
        val snackbar = Snackbar.make(view, msg, length)
        if (actionMessage != null) {
            snackbar.setAction(actionMessage) {
                action(requireActivity().findViewById(android.R.id.content))
            }.show()
        } else {
            snackbar.show()
        }
    }

    private fun disableMessageSend(disable: Boolean) {
        binding.etSendMessage.clearFocus()
        binding.ivSend.isEnabled = disable
        binding.etSendMessage.isEnabled = disable
    }

    private fun validateMessage() {
        if (binding.etSendMessage.text.toString().isEmpty()) {
            binding.etSendMessage.error = getString(R.string.str_enter_valid_message)
            binding.etSendMessage.requestFocus()
            disableMessageSend(true)
        } else {
            try {
                // use local to gmt method for utc if nothing works (also tried Date().time)
                val utcMilliseconds = Calendar.getInstance().timeInMillis
//                sendMessageToFirebase(
//                    "", binding.etSendMessage.text.toString(),
//                    myId, utcMilliseconds, Constants.MESSAGE_TYPE_TEXT
//                )
            } catch (ex: Exception) {
                disableMessageSend(true)
                genericHandler.showMessage(ex.message.toString())
            }
        }
    }

    private fun senderOfferToFirestore(offer: MessageOffer) {
        try {
            // use local to gmt method for utc if nothing works (also tried Date().time)
            val utcMilliseconds = Calendar.getInstance().timeInMillis
//            sendMessageToFirebase(
//                "", "",
//                myId, utcMilliseconds, Constants.MESSAGE_TYPE_OFFER, offer = offer
//            )
        } catch (ex: Exception) {
            disableMessageSend(true)
            genericHandler.showMessage(ex.message.toString())
        }
    }

    private fun sendMessageToFirebase(
        attachment: String,
        message: String,
        senderId: Int,
        sentAt: Long,
        attachmentType: Int,
        offer: MessageOffer? = null
    ) {
        if (inbox != null) {
            addMessageToExistingChat(attachment, message, senderId, sentAt, attachmentType, offer)
            hasReadLastMessage()
        } else {
            createChatAndAddMessage(sentAt, attachment, message, senderId, attachmentType, offer)
        }
    }

    private fun createChatAndAddMessage(
        sentAt: Long, attachment: String, message: String,
        senderId: Int, attachmentType: Int, offer: MessageOffer?
    ) {
        val inboxReference = db.collection(Constants.FIREBASE_DATABASE_ORDERS_CONVERSATION).document()
        val members: MutableList<Int> = ArrayList()
//        members.add(myId)
//        members.add(userId)
        val membersInfo: MutableList<MembersInfo> = ArrayList()
        membersInfo.add(MembersInfo(myId, true, "available"))
        membersInfo.add(MembersInfo(userId, true, "available"))
        // use local to gmt method for utc if nothing works (also tried Date().time)
        val utcMilliseconds = Calendar.getInstance().timeInMillis
//        val inboxModel = Inbox(
//            createdAt = sentAt, sentAt = utcMilliseconds, createdBy = myId,
//            id = inboxReference.id, senderId = myId, members = members, membersInfo = membersInfo,
//            title = "", combinedId = inboxCombinedId
//        )
//        inbox = inboxModel
//        inboxReference.set(inboxModel).addOnSuccessListener {
//            val reference =
//                db.collection(Constants.FIREBASE_DATABASE_ORDERS_CONVERSATION).document(inboxReference.id)
//                    .collection("Messages").document()
//            val deleteMessage: List<Int> = ArrayList()
//            val messageModel = Message(
//                attachment = attachment, message = message, senderId = senderId,
//                sentAt = sentAt, attachmentType = attachmentType, id = reference.id,
//                deleteMessage = deleteMessage, messageOffer = offer, refersGig = referGig,
////                messageGig = args.messageGig
//            )
//            referGig = false
//            messageSavedOrNot(
//                reference, messageModel, attachmentType, message, senderId,
//                sentAt, inboxReference
//            )
//        }.addOnFailureListener {
//            disableMessageSend(true)
//        }
    }

    private fun messageSavedOrNot(
        reference: DocumentReference, messageModel: Message,
        attachmentType: Int, message: String, senderId: Int, sentAt: Long,
        inboxReference: DocumentReference
    ) {
        reference.set(messageModel).addOnSuccessListener {
            binding.etSendMessage.setText("")
            handleDifferentMessages(
                attachmentType, message, senderId, sentAt, inboxReference,
                reference
            )
            sendFirebaseNotification(messageModel)
        }.addOnFailureListener { e: Exception ->
            disableMessageSend(true)
            genericHandler.showMessage(e.message.toString())
        }
    }

    private fun sendFirebaseNotification(messageModel: Message) {
//        val firebaseNotification = FirebaseNotification(
//            userInfo!!.fcmToken,
//            NotificationMessage(
//                messageModel.senderId,
//                myInfo!!.name,
//                messageModel.message,
//                Constants.TYPE_MESSAGE
//            )
//        )
//        viewModel.sendFirebaseNotificationCall(firebaseNotification)
    }

    private fun addMessageToExistingChat(
        attachment: String,
        message: String,
        senderId: Int,
        sentAt: Long,
        attachmentType: Int,
        offer: MessageOffer?
    ) {
        val reference = db.collection(Constants.FIREBASE_DATABASE_ORDERS_CONVERSATION).document(inbox!!.id)
            .collection("Messages").document()
        val inboxReference = db.collection(Constants.FIREBASE_DATABASE_ORDERS_CONVERSATION).document(inbox!!.id)
        val deleteMessage: List<Int> = ArrayList()
//        val messageModel = Message(
//            attachment = attachment, message = message, senderId = senderId,
//            sentAt = sentAt, attachmentType = attachmentType, id = reference.id,
//            deleteMessage = deleteMessage, messageOffer = offer, refersGig = referGig,
////            messageGig = args.messageGig
//        )
//        referGig = false
//        reference.set(messageModel).addOnSuccessListener {
//            binding.etSendMessage.setText("")
//            handleDifferentMessages(
//                attachmentType, message, senderId, sentAt, inboxReference,
//                reference
//            )
//            sendFirebaseNotification(messageModel)
//        }.addOnFailureListener { e: Exception ->
//            disableMessageSend(true)
//            genericHandler.showMessage(e.message.toString())
//        }
    }

    private fun handleDifferentMessages(
        attachmentType: Int, message: String, senderId: Int,
        sentAt: Long, inboxReference: DocumentReference, reference: DocumentReference
    ) {
        when (attachmentType) {
            Constants.MESSAGE_TYPE_TEXT -> updateLastMessage(
                message, senderId, myName,
                sentAt, inboxReference.id, reference.id
            )
            Constants.MESSAGE_TYPE_IMAGE -> updateLastMessage(
                Constants.MESSAGE_TYPE_IMAGE_STRING, senderId,
                myName, sentAt, inboxReference.id, reference.id
            )
            Constants.MESSAGE_TYPE_VIDEO -> updateLastMessage(
                Constants.MESSAGE_TYPE_VIDEO_STRING, senderId, myName, sentAt, inboxReference.id,
                reference.id
            )
            Constants.MESSAGE_TYPE_DOCUMENT -> updateLastMessage(
                Constants.MESSAGE_TYPE_DOCUMENT_STRING, senderId, myName, sentAt, inboxReference.id,
                reference.id
            )
            Constants.MESSAGE_TYPE_OFFER -> updateLastMessage(
                Constants.MESSAGE_TYPE_OFFER_STRING, senderId, myName, sentAt, inboxReference.id,
                reference.id
            )
            else -> updateLastMessage(
                Constants.MESSAGE_TYPE_AUDIO_STRING, senderId, myName, sentAt, inboxReference.id,
                reference.id
            )
        }
    }

    private fun updateLastMessage(
        lastMessage: String, senderId: Int, senderName: String,
        sentAt: Long, id: String, lastMessageId: String
    ) {
        try {
            val hashMap = createHashMap(lastMessage, senderId, senderName, sentAt, lastMessageId)
            db.collection(Constants.FIREBASE_DATABASE_ORDERS_CONVERSATION).document(id).update(hashMap)
                .addOnSuccessListener {
                    // genericHandler.showMessage("updated last message")
                    // FIXME: 15/10/2021 if no message then sender cant see sent messages until comes back
//                    fetchMessages()
                    updateUsersInfo(false)
                }.addOnFailureListener {
                    disableMessageSend(true)
                    genericHandler.showMessage(it.message.toString())
                }
        } catch (ex: Exception) {
            disableMessageSend(true)
            genericHandler.showMessage(ex.message.toString())
        }
    }

    private fun createHashMap(
        lastMessage: String, senderId: Int, senderName: String,
        sentAt: Long, lastMessageId: String
    ): HashMap<String, Any> {
        val hashMap = HashMap<String, Any>()
        hashMap["lastMessage"] = lastMessage
        hashMap["senderId"] = senderId
        hashMap["senderName"] = senderName
        hashMap["sentAt"] = sentAt
        hashMap["lastMessageId"] = lastMessageId
        val membersInfo: List<MembersInfo> = inbox!!.membersInfo
        for (i in membersInfo.indices) {
            membersInfo[i].type = "available"
            if (membersInfo[i].id != myId) {
                membersInfo[i].hasReadLastMessage = false
            } else {
                membersInfo[i].hasReadLastMessage = true
            }
        }
        hashMap["membersInfo"] = membersInfo
        return hashMap
    }

    private fun setToolbarData() {
        binding.toolbar.ivToolbar.setImageResource(R.drawable.ic_back)
        binding.toolbar.tvToolbar.text = App.getAppContext()!!.getString(R.string.str_messages)
    }

    private fun downloadFile(fileUrl: String, fileType: String) {
        val downloadFileReference = firebaseStorage.getReferenceFromUrl(fileUrl)
        val root = requireContext().getExternalFilesDir(null)!!.absolutePath
        if (root.isNullOrEmpty()) {
            return
        }
        val rootPath = File(root)
        if (!rootPath.exists()) {
            rootPath.mkdirs()
        }
        val uniqueId = UUID.randomUUID().toString()
        val localFile = File(rootPath, "$uniqueId.$fileType")
        downloadFileReference.getFile(localFile).addOnSuccessListener {
            BaseUtils.scanFile(requireContext(), localFile, fileType)
            NotificationUtils.createNotification(requireContext(), "$uniqueId.$fileType")
            genericHandler.showMessage(getString(R.string.str_file_downloaded).plus(" at $rootPath"))
            if (dialogFileUpload.isShowing) {
                dialogFileUpload.dismiss()
            }
        }.addOnProgressListener { taskSnapshot ->
            val progress: Double =
                100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
            if (!dialogFileUpload.isShowing) {
                bindingFileUploadDialog.tvFileUploading.text =
                    getString(R.string.str_file_downloading)
                dialogFileUpload.show()
            }
            bindingFileUploadDialog.progressBar.progress = progress.toInt()
            bindingFileUploadDialog.tvFileProgress.text = progress.toInt().toString().plus("%")
        }.addOnFailureListener { exception ->
            genericHandler.showMessage(exception.message.toString())
            if (dialogFileUpload.isShowing) {
                dialogFileUpload.dismiss()
            }
        }
    }

    override fun <T> onMessageClick(item: T) {
        if (item is Message) {

        }
    }

    override fun <T> onImageClick(item: T) {
        if (item is Message) {
            val images = arrayListOf<Message>(item)
            StfalconImageViewer.Builder(context, images) { view, image ->
                Glide.with(requireContext()).load(image.attachment).into(view)
            }.show()
        }
    }

    override fun <T> onPlayVideo(item: T) {
        if (item is Message) {
            val videoUrl = item.attachment
            MessagesFragmentDirections.actionMessagesFragmentToExoPlayerFragment(videoUrl).also {
                findNavController().navigate(it)
            }
        }
    }

    override fun <T> onDownloadDocument(item: T) {
        if (item is Message) {
            if (item.attachmentType == Constants.MESSAGE_TYPE_DOCUMENT) {
                if (item.attachment.lowercase().contains(Constants.DOCUMENT_PDF)) {
                    downloadFile(item.attachment, Constants.DOCUMENT_PDF)
                }
            }
        }
    }

    override fun <T> onOfferButtonClick(item: T) {
        if (item is Message) {
            offerMessage = item
            updateOfferMessage(offerMessage)
        }
    }

    private fun updateOfferMessage(item: Message?) {
        if (item != null) {
            try {
                item.messageOffer?.let { offer ->
                    if (offer.offerSenderId == prefManager.userId) {
                        db.collection(Constants.FIREBASE_DATABASE_ORDERS_CONVERSATION).document(inbox!!.id)
                            .collection("Messages").document(item.id).update(
                                mapOf(
                                    "messageOffer.status" to Constants.OFFER_WITHDRAWN
                                )
                            )
                    } else {
                        val checkoutBottomSheet = CheckoutBottomSheet(this)
                        checkoutBottomSheet.show(
                            requireActivity().supportFragmentManager,
                            CheckoutBottomSheet.TAG
                        )
                    }
                }
            } catch (ex: Exception) {
                genericHandler.showMessage(ex.message.toString())
            }
        }
    }

    override fun <T> sendOffer(item: T) {
        if (item is MessageOffer) {
            disableMessageSend(false)
            senderOfferToFirestore(item)
        }
    }

    override fun sendToken(token: String) {
        if (offerMessage != null) {
            offerMessage!!.messageOffer?.let { offer ->
                val chatOfferRequest = ChatOfferRequest(
                    service_id = offer.serviceId,
                    description = offer.description,
                    price = offer.totalOffer,
                    revision = offer.revisions,
                    delivery_time = offer.deliveryDays,
                    token = token
                )
                viewModel.chatOrderCall(chatOfferRequest)
            }
        } else {
            genericHandler.showMessage(getString(R.string.str_something_went_wrong))
        }
    }
}