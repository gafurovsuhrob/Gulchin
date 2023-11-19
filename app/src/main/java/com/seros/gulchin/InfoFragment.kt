package com.seros.gulchin

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import com.seros.gulchin.databinding.FragmentInfoBinding


class InfoFragment : Fragment() {

    private var _binding: FragmentInfoBinding? = null
    private val binding get() = _binding!!
    private lateinit var toolbar: MaterialToolbar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("IntentReset")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar = requireActivity().findViewById(R.id.toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_back)
        binding.apply {
            intentMail.setOnClickListener {
                val mIntent = Intent(Intent.ACTION_SENDTO)

                mIntent.data = Uri.parse("mailto:tajlingvo@gmail.com")

                val uriText = "mailto:tajlingvo@gmail.com" +
                        "?subject=" + Uri.encode("Дархост оид ба замимаи мобилии «Гулчини Ашъор»") +
                        "&body=" + Uri.encode("Салом, дархости ман - ")

                mIntent.data = Uri.parse(uriText)

                try {
                    startActivity(mIntent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(requireContext(), "Нет подходящего почтового клиента.", Toast.LENGTH_LONG).show()
                }
            }
            intentWebsite.setOnClickListener {
                val intent = Intent(ACTION_VIEW, Uri.parse("https://www.tajlingvo.tj/"))
                try {
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(requireContext(), "Нет подходящего приложения для открытия ссылок.", Toast.LENGTH_LONG).show()
                }

            }



        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}