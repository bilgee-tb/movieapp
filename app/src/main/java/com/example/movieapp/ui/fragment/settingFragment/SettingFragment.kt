package com.example.movieapp.ui.fragment.settingFragment

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.movieapp.databinding.FragmentSettingBinding
import com.example.movieapp.ui.activity.authentication.loginActivity.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class SettingFragment : Fragment() {
    private lateinit var binding: FragmentSettingBinding
    private val viewModel: SettingViewModel by viewModels()

    @Inject
    lateinit var auth: FirebaseAuth
    private val PICK_IMAGE_REQUEST = 1 // Constant to identify the image selection request


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSettingBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the logged in user's email
        val currentUser = auth.currentUser
        currentUser?.let {
            binding.tvUserEmailId.text = it.email
        }

        binding.buttonLogOut.setOnClickListener {
            viewModel.logout() // Call the logout function in ViewModel
        }


// Set click listener on the ImageView
        binding.imgUser.setOnClickListener {
            openImagePicker()
        }
        // Load saved image if exists
        loadSavedImage()

        viewModel.logoutStatus.observe(viewLifecycleOwner) { isLoggedOut ->
            if (isLoggedOut) {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                findNavController().popBackStack() // Pop back stack to remove SettingFragment
            }
        }
    }


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted, open image picker
            openImagePicker()
        } else {
            // Permission denied, handle accordingly
        }
    }

    private fun requestStoragePermission() {
        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }


    companion object {
        private const val REQUEST_CODE_STORAGE_PERMISSIONS = 123
    }

    private fun openImagePicker() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*" // Allow only image files
            resultLauncher.launch(intent)
        } else {
            requestStoragePermission()
        }
    }


    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.data?.let { uri ->
                // Set the selected image to ImageView
                binding.imgUser.setImageURI(uri)
                // Save the selected image URI
                saveImageUri(uri)
            }
        }
    }

    private fun saveImageUri(uri: Uri) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString("user_image_uri", uri.toString())
                    apply()
                }
            }
        }
    }

    private fun loadSavedImage() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
                val imageUriString = sharedPref.getString("user_image_uri", null)
                imageUriString?.let { uriString ->
                    val uri = Uri.parse(uriString)
                    try {
                        val bitmap = Glide.with(requireContext())
                            .asBitmap()
                            .load(uri)
                            .submit()
                            .get()
                        withContext(Dispatchers.Main) {
                            binding.imgUser.setImageBitmap(bitmap)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}


