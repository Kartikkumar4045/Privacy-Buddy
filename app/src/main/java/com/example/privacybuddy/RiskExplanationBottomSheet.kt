package com.example.privacybuddy.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.privacybuddy.R
import com.example.privacybuddy.utils.RiskAnalyzer

class RiskExplanationBottomSheet(
    private val permissions: List<String>
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.layout_risk_explanation_sheet, container, false)

        val explanationText = view.findViewById<TextView>(R.id.riskExplanationText)
        val aiSuggestionText = view.findViewById<TextView>(R.id.aiSuggestionText)
        val closeButton = view.findViewById<Button>(R.id.closeSheetButton)

        // Static explanation
        val explanation = RiskAnalyzer.getRiskExplanation(permissions)
        explanationText.text = explanation

        // Simulated AI Suggestion
        aiSuggestionText.text = generateAISuggestion(permissions)

        closeButton.setOnClickListener {
            dismiss()
        }
        return view
    }

    private fun generateAISuggestion(permissions: List<String>): String {
        return when {
            permissions.any { it.contains("READ_SMS") } ->
                "Consider avoiding apps that access your SMS unless absolutely necessary."
            permissions.any { it.contains("LOCATION") } ->
                "Restrict location access unless needed for core functionality."
            permissions.any { it.contains("CAMERA") || it.contains("RECORD_AUDIO") } ->
                "Apps with camera or microphone access may record you — proceed with caution."
            else ->
                "No serious threats found, but always review permission usage in app settings."
        }
    }
}
