package com.example.movieapp.ui.fragment.settingFragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.movieapp.databinding.FragmentSettingBinding
import com.example.movieapp.ui.activity.authentication.loginActivity.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
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
        viewModel.logoutStatus.observe(viewLifecycleOwner) { isLoggedOut ->
            if (isLoggedOut) {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                findNavController().popBackStack() // Pop back stack to remove SettingFragment
            }
        }
    }
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*" // Allow only image files
        resultLauncher.launch(intent)
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.data?.let { uri ->
                binding.imgUser.setImageURI(uri) // Set the selected image to ImageView
            }
        }
    }

}


