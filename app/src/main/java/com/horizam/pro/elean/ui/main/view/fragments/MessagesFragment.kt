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
import androidx.lifecycle.Observer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
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
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.databinding.DialogChooseAttachmentBinding
import com.horizam.pro.elean.databinding.DialogFileUploadingBinding
import com.horizam.pro.elean.databinding.FragmentMessagesBinding
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.adapter.MessageAdapter
import com.horizam.pro.elean.ui.main.adapter.MyLoadStateAdapter
import com.horizam.pro.elean.ui.main.viewmodel.MessagesViewModel
import com.horizam.pro.elean.utils.BaseUtils.Companion.hideKeyboard
import java.io.File
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import android.view.WindowManager
import com.horizam.pro.elean.Constants.Companion.FIREBASE_DATABASE_ROOT
import com.stfalcon.imageviewer.StfalconImageViewer
import com.horizam.pro.elean.data.model.*
import com.horizam.pro.elean.data.model.requests.ChatOfferRequest
import com.horizam.pro.elean.data.model.response.GeneralResponse
import com.horizam.pro.elean.data.model.response.ServiceDetail
import com.horizam.pro.elean.ui.main.callbacks.*
import com.horizam.pro.elean.ui.main.viewmodel.FirebaseNotificationRequest
import com.horizam.pro.elean.ui.main.viewmodel.NotificationMessage
import com.horizam.pro.elean.utils.*

class MessagesFragment : Fragment(), MessagesHandler, CreateOfferHandler, CheckoutHandler {

    private lateinit var binding: FragmentMessagesBinding
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
    private var userId: String = "" // othere user id (may be seller or may be buyer)
    private var userName: String = ""
    private var myId: String = ""
    private var myName = ""
    private var count = 0
    private var name=""
    private var inboxCombinedId = ""
    private lateinit var intent:Intent
    private var myInfo: MessageUser? = null
    private var userInfo: MessageUser? = null
    private var offerMessage: Message? = null
    private lateinit var viewModel: MessagesViewModel
    private var chatNotExist: Boolean = true
    private var referGig: Boolean = false
    private var gig: ServiceDetail? = null
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
        savedInstanceState: Bundle?)
    : View {
        binding = FragmentMessagesBinding.inflate(layoutInflater, container, false)
        initViews()
        setupViewModel()
//        setupObservers()

        setRecyclerView(
            MessageUser("", "", "", ""),
            MessageUser("", "", "", ""),
        )
        getData()
        setToolbar()
        setClickListeners()
        return binding.root
    }
    private fun setToolbar()
    {
        binding.toolbar.ivToolbar.setImageResource(R.drawable.ic_back)
        binding.toolbar.ivToolbar.isVisible = true
        binding.toolbar.tvToolbar.text = userName
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

    private val notificationObserver = Observer<Resource<GeneralResponse>> {
        it?.let { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    genericHandler.showProgressBar(false)
                    genericHandler.showSuccessMessage(it.message!!)
                }
                Status.ERROR -> {
                    genericHandler.showProgressBar(false)
                    genericHandler.showErrorMessage(it.message.toString())
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
                    genericHandler.showErrorMessage(it.message.toString())
                }
                Status.LOADING -> {
                    genericHandler.showProgressBar(true)
                }
            }
        }
    }

    private fun handleResponse(response: GeneralResponse) {
        genericHandler.showSuccessMessage(getString(R.string.str_offer_accepted))
        if (offerMessage != null) {
            db.collection(Constants.FIREBASE_DATABASE_ROOT).document(inbox!!.id)
                .collection("Messages").document(offerMessage!!.id).update(
                    mapOf(
                        "messageOffer.status" to Constants.OFFER_ACCEPTED
                    )
                ).addOnSuccessListener {
//                        startActivity(Intent(requireActivity(), ManageOrdersActivity::class.java))
                }
        }
    }

    private fun getData() {
        genericHandler.showProgressBar(true)
        try {
            userId = args.id
            userName= args.userName
            referGig = args.refersGig
            myId = prefManager.userId
            myName = prefManager.username!!

            if (userId != "" && myId != "") {
                try {
                    checkIfChatExists()
                } catch (ex: Exception) {
                    genericHandler.showErrorMessage(ex.message.toString())
                }
            }
        } catch (ex: Exception) {
            genericHandler.showProgressBar(false)
            genericHandler.showErrorMessage(ex.message.toString())
        }
    }

    private fun checkIfChatExists() {

        val query: Query = inboxReference.whereArrayContains("members", myId)
            .orderBy("sentAt", Query.Direction.DESCENDING)
        query.get().addOnSuccessListener { queryDocumentSnapshots ->
//            chatNotExist = queryDocumentSnapshots.size() == 0
            count = 0
            genericHandler.showProgressBar(false)
            for (documentSnapshot in queryDocumentSnapshots) {
                val inbox1 = documentSnapshot.toObject(Inbox::class.java)
                try {
                    if (inbox1!!.membersInfo[0].id == userId && inbox1!!.membersInfo[1].id == myId) {
                        inbox = inbox1
                        count++
                        myInfo = MessageUser(
                            inbox1!!.membersInfo[1].id,
                            inbox1!!.membersInfo[1].name,
                            inbox1!!.membersInfo[1].photo
                        )
                        userInfo = MessageUser(
                            inbox1!!.membersInfo[0].id,
                            inbox1!!.membersInfo[0].name,
                            inbox1!!.membersInfo[0].photo
                        )
                        adapter.setMyInfo(myInfo!!)
                        adapter.setUserInfo(userInfo!!)
                        updateUsersInfo(true)
                    } else if (inbox1!!.membersInfo[0].id == myId && inbox1!!.membersInfo[1].id == userId) {
                        inbox = inbox1
                        count++
                        myInfo = MessageUser(
                            inbox1!!.membersInfo[0].id,
                            inbox1!!.membersInfo[0].name,
                            inbox1!!.membersInfo[0].photo
                        )
                        userInfo = MessageUser(
                            inbox1!!.membersInfo[1].id,
                            inbox1!!.membersInfo[1].name,
                            inbox1!!.membersInfo[1].photo
                        )
                        adapter.setMyInfo(myInfo!!)
                        adapter.setUserInfo(userInfo!!)
                        updateUsersInfo(true)
                    }
                } catch (ex: Exception) {
                    genericHandler.showProgressBar(false)
                    genericHandler.showErrorMessage(ex.message.toString())
                }
            }

        }.addOnFailureListener {
            Log.i(MessagesFragment::class.java.simpleName, it.message.toString())
            genericHandler.showErrorMessage(it.message.toString())
        }
    }

    private fun updateUsersInfo(updateMessages: Boolean) {
//        val hashMap = HashMap<String, Any>()
//        val membersInfo: List<MembersInfo> = inbox!!.membersInfo
//        for (i in membersInfo.indices) {
//            membersInfo[i].type = "available"
//            if (membersInfo[i].id == myId) {
//                membersInfo[i].name = myInfo!!.name
//                membersInfo[i].photo = myInfo!!.photo
//                membersInfo[i].hasReadLastMessage = true
//            } else {
//                membersInfo[i].name = userInfo!!.name
//                membersInfo[i].photo = userInfo!!.photo
//            }
//        }
//        hashMap["membersInfo"] = membersInfo
//        db.collection(Constants.FIREBASE_DATABASE_ROOT).document(inbox!!.id).update(hashMap)
//            .addOnSuccessListener {
        if (chatNotExist || updateMessages) {
            chatNotExist = false
            fetchMessages()
            setMessageRead()
        }
//                disableMessageSend(true)
//            }.addOnFailureListener {
//                disableMessageSend(true)
//                genericHandler.showErrorMessage(it.message.toString())
//            }
    }

    private fun fetchMessages() {
        messagesReference = db.collection(Constants.FIREBASE_DATABASE_ROOT).document(inbox!!.id)
            .collection("Messages")
        val query = messagesReference.orderBy("sentAt", Query.Direction.DESCENDING).limit(100)
        genericHandler.showProgressBar(true)
        viewModel.getMessagesCall(query)
        observeMessages(query)
    }

    private fun observeMessages(query: Query) {
        query.addSnapshotListener { snapshots, e ->
            if (e != null) {
                genericHandler.showProgressBar(false)
                genericHandler.showErrorMessage(e.message.toString())
                return@addSnapshotListener
            }
            var messageCount = 0
            var size = 0
            for (item in snapshots!!.documentChanges) {
                if (item.type == DocumentChange.Type.ADDED) {
                    size++
                }
            }
            for (dc in snapshots!!.documentChanges) {
                when (dc.type) {
                    DocumentChange.Type.ADDED -> {
                    }
                    DocumentChange.Type.MODIFIED -> {
                    }
                    DocumentChange.Type.REMOVED -> {
                    }
                }
            }
            adapter.refresh()
            genericHandler.showProgressBar(false)
        }
    }
    private fun setMessageRead() {
        var membersInfo = MembersInfo()
        for (item in inbox!!.membersInfo) {
            if (item.id == myId) {
                membersInfo = item
            }
        }
        membersInfo.hasReadLastMessage = false
        db.collection(FIREBASE_DATABASE_ROOT).document(inbox!!.id)
            .update(
                "membersInfo",
                FieldValue.arrayRemove(membersInfo),
                "members",
                FieldValue.arrayRemove(membersInfo.id)
            )
            .addOnCompleteListener { removeTask ->
                if (removeTask.isSuccessful) {
                    membersInfo.hasReadLastMessage = true
                    db.collection(FIREBASE_DATABASE_ROOT).document(inbox!!.id)
                        .update(
                            "membersInfo",
                            FieldValue.arrayUnion(membersInfo),
                            "members",
                            FieldValue.arrayUnion(membersInfo.id)
                        )
                        .addOnCompleteListener { addTask ->
                            if (addTask.isSuccessful) {
                                Log.wtf("mytag", "Update complete.")
                            } else {
                                addTask.exception!!.message.let {
                                    Log.wtf("mytag", "update failed + $it")
                                }
                            }
                        }
                } else {
                    removeTask.exception!!.message.let {
                        Log.wtf("mytag", "delete failed + $it")
                    }
                }
            }
    }

    private fun hasReadLastMessage() {
//        val hashMap = HashMap<String, Any>()
//        val membersInfo: List<MembersInfo> = inbox!!.membersInfo
//        for (i in membersInfo.indices) {
//            membersInfo[i].type = "available"
//            if (membersInfo[i].id == myId) {
//                membersInfo[i].name = myInfo!!.name
//                membersInfo[i].photo = myInfo!!.photo
//                membersInfo[i].hasReadLastMessage = true
//            } else {
//                membersInfo[i].name = userInfo!!.name
//                membersInfo[i].photo = userInfo!!.photo
//            }
//        }
//        hashMap["membersInfo"] = membersInfo
//        db.collection(Constants.FIREBASE_DATABASE_ROOT).document(inbox!!.id).update(hashMap)
    }

    private fun setRecyclerView(myInfo: MessageUser, userInfo: MessageUser) {
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
        setupObservers()
    }

    private fun initViews() {
        //adapter = MessageAdapter(this)

        recyclerView = binding.rvMessages
        db = Firebase.firestore
        firebaseStorage = FirebaseStorage.getInstance()
        inboxReference = db.collection(Constants.FIREBASE_DATABASE_ROOT)
        messagesArrayList = ArrayList()
        prefManager = PrefManager(requireContext())
        initChooseAttachmentDialog()
        initFileUploadDialog()
        BaseUtils.CurrentScreen = Constants.MESSAGESCREEN
        if (prefManager.sellerMode == 0) {
            binding.tvCreateOffer.visibility = View.GONE
        } else {
            binding.tvCreateOffer.visibility = View.VISIBLE
        }
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
                genericHandler.showProgressBar(loadState.source.refresh is LoadState.Loading)
                recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
                btnRetry.isVisible = loadState.source.refresh is LoadState.Error
                textViewError.isVisible = loadState.source.refresh is LoadState.Error
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
                    genericHandler.showProgressBar(true)
                    chatNotExist = count == 0
                    disableMessageSend(false)
                    validateMessage()
                } catch (ex: Exception) {
                    genericHandler.showProgressBar(false)
                    disableMessageSend(true)
                    genericHandler.showErrorMessage(ex.message.toString())
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
                bindingChooseAttachmentDialog.btnNo.setOnClickListener {
                    dialogChooseAttachment.dismiss() }
            }
            tvCreateOffer.setOnClickListener {
                val createOfferBottomSheet = CreateOfferBottomSheet(this@MessagesFragment)
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
                    genericHandler.showErrorMessage(getString(R.string.str_choose_valid_file))
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
                genericHandler.showErrorMessage(getString(R.string.str_choose_valid_file))
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
                genericHandler.showErrorMessage(
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
                genericHandler.showErrorMessage(
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
                        genericHandler.showErrorMessage(e.message.toString())
                    }
                } else {
                    genericHandler.showErrorMessage(getString(R.string.str_invalid_data))
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
                genericHandler.showErrorMessage(getString(R.string.str_valid_image))
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
            genericHandler.showErrorMessage(getString(R.string.str_something_went_wrong))
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
                        sendMessageToFirebase(
                            uri.toString(), "", myId,
                            utcMilliseconds, messageType
                        )
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
            genericHandler.showErrorMessage(e.message.toString())
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
    }

    private fun validateMessage() {
        val msg = binding.etSendMessage.text.toString().trim()
        if (msg.isEmpty()) {
            binding.etSendMessage.error = getString(R.string.str_enter_valid_message)
            disableMessageSend(true)
            genericHandler.showProgressBar(false)
        } else {
            try {
                genericHandler.showProgressBar(false)
                // use local to gmt method for utc if nothing works (also tried Date().time)
                val utcMilliseconds = Calendar.getInstance().timeInMillis
                sendMessageToFirebase(
                    "", binding.etSendMessage.text.toString().trimStart(),
                    myId, utcMilliseconds, Constants.MESSAGE_TYPE_TEXT
                )
            } catch (ex: Exception) {
                disableMessageSend(true)
                genericHandler.showErrorMessage(ex.message.toString())
            }
        }
    }

    private fun senderOfferToFirestore(offer: MessageOffer) {
        try {
            // use local to gmt method for utc if nothing works (also tried Date().time)
            val utcMilliseconds = Calendar.getInstance().timeInMillis
            sendMessageToFirebase(
                "", "",
                myId, utcMilliseconds, Constants.MESSAGE_TYPE_OFFER, offer = offer
            )
        } catch (ex: Exception) {
            disableMessageSend(true)
            genericHandler.showErrorMessage(ex.message.toString())
        }
    }

    private fun sendMessageToFirebase(
        attachment: String,
        message: String,
        senderId: String,
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
        senderId: String, attachmentType: Int, offer: MessageOffer?
    ) {
        val inboxReference = db.collection(Constants.FIREBASE_DATABASE_ROOT).document()
        val members: MutableList<String> = ArrayList()
        members.add(myId)
        members.add(userId)
        userInfo = MessageUser(id = userId, args.userName)
        myInfo = MessageUser(id = myId, name = prefManager.username!!)
        val membersInfo: MutableList<MembersInfo> = ArrayList()
        membersInfo.add(
            MembersInfo(
                myId,
                true,
                "available",
                prefManager.userImage,
                prefManager.username!!
            )
        )
        membersInfo.add(MembersInfo(userId, true, "available", args.photo, args.userName))
        // use local to gmt method for utc if nothing works (also tried Date().time)
        val utcMilliseconds = Calendar.getInstance().timeInMillis
        val inboxModel = Inbox(
            createdAt = sentAt, sentAt = utcMilliseconds, createdBy = myId,
            id = inboxReference.id, senderId = myId, members = members, membersInfo = membersInfo,
            title = "", combinedId = inboxCombinedId
        )
        inbox = inboxModel
        if (inbox!!.membersInfo[0].id == userId && inbox!!.membersInfo[1].id == myId) {
            val myInfo = MessageUser(
                inbox!!.membersInfo[1].id,
                inbox!!.membersInfo[1].name,
                inbox!!.membersInfo[1].photo
            )
            val userInfo = MessageUser(
                inbox!!.membersInfo[0].id,
                inbox!!.membersInfo[0].name,
                inbox!!.membersInfo[0].photo
            )
            adapter.setMyInfo(myInfo)
            adapter.setUserInfo(userInfo)
            updateUsersInfo(true)
        }
        inboxReference.set(inboxModel).addOnSuccessListener {
            val reference =
                db.collection(Constants.FIREBASE_DATABASE_ROOT).document(inboxReference.id)
                    .collection("Messages").document()
            val deleteMessage: List<Int> = ArrayList()
            val messageModel = Message(
                attachment = attachment, message = message, senderId = senderId,
                sentAt = sentAt, attachmentType = attachmentType, id = reference.id,
                deleteMessage = deleteMessage, messageOffer = offer, refersGig = referGig,
                messageGig = args.messageGig
            )
            referGig = false
            messageSavedOrNot(
                reference, messageModel, attachmentType, message, senderId,
                sentAt, inboxReference
            )
        }.addOnFailureListener {
            disableMessageSend(true)
        }
    }

    private fun messageSavedOrNot(
        reference: DocumentReference, messageModel: Message,
        attachmentType: Int, message: String, senderId: String, sentAt: Long,
        inboxReference: DocumentReference
    ) {
        disableMessageSend(true)
        reference.set(messageModel).addOnSuccessListener {
            binding.etSendMessage.setText("")
            handleDifferentMessages(
                attachmentType, message, senderId, sentAt, inboxReference,
                reference
            )
            sendFirebaseNotification(messageModel)
        }.addOnFailureListener { e: Exception ->
            disableMessageSend(true)
            genericHandler.showErrorMessage(e.message.toString())
        }
    }

    private fun sendFirebaseNotification(messageModel: Message) {
        val notificationMessage = NotificationMessage(
            message = messageModel.message,
            type = Constants.MESSAGE,
            sender_id = myId,
            sender_name = userInfo!!.name
        )
        val firebaseNotification = FirebaseNotificationRequest(
            subject = myInfo!!.name,
            reciever_id = userId,
            body = "send you a message",
            data = notificationMessage
        )
        viewModel.sendNotificationCall(firebaseNotification)
    }

    private fun addMessageToExistingChat(
        attachment: String,
        message: String,
        senderId: String,
        sentAt: Long,
        attachmentType: Int,
        offer: MessageOffer?
    ) {
        val reference = db.collection(Constants.FIREBASE_DATABASE_ROOT).document(inbox!!.id)
            .collection("Messages").document()
        val inboxReference = db.collection(Constants.FIREBASE_DATABASE_ROOT).document(inbox!!.id)
        val deleteMessage: List<Int> = ArrayList()
        val messageModel = Message(
            attachment = attachment, message = message, senderId = senderId,
            sentAt = sentAt, attachmentType = attachmentType, id = reference.id,
            deleteMessage = deleteMessage, messageOffer = offer, refersGig = referGig,
            messageGig = args.messageGig
        )
        referGig = false
        reference.set(messageModel).addOnSuccessListener {
            binding.etSendMessage.setText("")
            handleDifferentMessages(
                attachmentType, message, senderId, sentAt, inboxReference,
                reference
            )
            disableMessageSend(true)
            sendFirebaseNotification(messageModel)
        }.addOnFailureListener { e: Exception ->
            disableMessageSend(true)
            genericHandler.showErrorMessage(e.message.toString())
        }
    }

    private fun handleDifferentMessages(
        attachmentType: Int, message: String, senderId: String,
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
        lastMessage: String, senderId: String, senderName: String,
        sentAt: Long, id: String, lastMessageId: String
    ) {
        try {
            val hashMap = createHashMap(lastMessage, senderId, senderName, sentAt, lastMessageId)
            db.collection(Constants.FIREBASE_DATABASE_ROOT).document(id).update(hashMap)
                .addOnSuccessListener {
                    // genericHandler.showMessage("updated last message")
                    // FIXME: 15/10/2021 if no message then sender cant see sent messages until comes back
//                    fetchMessages()
                    updateUsersInfo(false)
                }.addOnFailureListener {
                    disableMessageSend(true)
                    genericHandler.showErrorMessage(it.message.toString())
                }
        } catch (ex: Exception) {
            disableMessageSend(true)
            genericHandler.showErrorMessage(ex.message.toString())
        }
    }

    private fun createHashMap(
        lastMessage: String, senderId: String, senderName: String,
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
            if (membersInfo[i].id == myId) {
                membersInfo[i].hasReadLastMessage = true
            } else {
                membersInfo[i].hasReadLastMessage = false
            }
        }
        hashMap["membersInfo"] = membersInfo
        return hashMap
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
            genericHandler.showErrorMessage(getString(R.string.str_file_downloaded).plus(" at $rootPath"))
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
            genericHandler.showErrorMessage(exception.message.toString())
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
                        db.collection(Constants.FIREBASE_DATABASE_ROOT).document(inbox!!.id)
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
                genericHandler.showErrorMessage(ex.message.toString())
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
            genericHandler.showErrorMessage(getString(R.string.str_something_went_wrong))
        }
    }

    override fun onStart() {
        super.onStart()
        (activity as HideBottomNavigation).hideNavigation()
    }

    override fun onPause() {
        super.onPause()
        if (inbox != null) {
            setMessageRead()
        }
    }

    override fun onStop() {
        super.onStop()
        (activity as HideBottomNavigation).showNavigation()
    }

}