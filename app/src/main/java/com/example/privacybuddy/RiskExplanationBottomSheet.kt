package com.example.privacybuddy.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
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
        val toggleExplanation = view.findViewById<TextView>(R.id.toggleRiskExplanation)
        val toggleAiSuggestion = view.findViewById<TextView>(R.id.toggleAiSuggestion)

        val explanationContainer = view.findViewById<ScrollView>(R.id.riskExplanationContainer)
        val aiSuggestionContainer = view.findViewById<ScrollView>(R.id.aiSuggestionContainer)

        val copyButton = view.findViewById<Button>(R.id.copyButton)
        val shareButton = view.findViewById<Button>(R.id.shareButton)
        val closeButton = view.findViewById<Button>(R.id.closeSheetButton)

        // Get full data
        val fullExplanation = RiskAnalyzer.getRiskExplanation(permissions)
        val fullSuggestion = RiskAnalyzer.getPermissionSuggestion(permissions)

        // Utility: convert dp to px
        fun Int.dpToPx(): Int =
            (this * resources.displayMetrics.density).toInt()

        // Trimming logic (first 5 lines)
        fun trimText(text: String, maxLines: Int = 5): String {
            val lines = text.split("\n")
            return if (lines.size <= maxLines) text
            else lines.take(maxLines).joinToString("\n") + "\n..."
        }

        val shortExplanation = trimText(fullExplanation)
        val shortSuggestion = trimText(fullSuggestion)

        var isExplanationExpanded = false
        var isSuggestionExpanded = false

        // Initially show trimmed versions with fixed height
        explanationText.text = shortExplanation
        explanationContainer.layoutParams.height = 100.dpToPx()
        explanationContainer.requestLayout()

        aiSuggestionText.text = shortSuggestion
        aiSuggestionContainer.layoutParams.height = 100.dpToPx()
        aiSuggestionContainer.requestLayout()

        toggleExplanation.setOnClickListener {
            isExplanationExpanded = !isExplanationExpanded
            explanationText.text = if (isExplanationExpanded) fullExplanation else shortExplanation
            explanationContainer.layoutParams.height =
                if (isExplanationExpanded) ViewGroup.LayoutParams.WRAP_CONTENT else 100.dpToPx()
            explanationContainer.requestLayout()
            toggleExplanation.text = if (isExplanationExpanded) "Show Less" else "Show More"
        }

        toggleAiSuggestion.setOnClickListener {
            isSuggestionExpanded = !isSuggestionExpanded
            aiSuggestionText.text = if (isSuggestionExpanded) fullSuggestion else shortSuggestion
            aiSuggestionContainer.layoutParams.height =
                if (isSuggestionExpanded) ViewGroup.LayoutParams.WRAP_CONTENT else 100.dpToPx()
            aiSuggestionContainer.requestLayout()
            toggleAiSuggestion.text = if (isSuggestionExpanded) "Show Less" else "Show More"
        }

        copyButton.setOnClickListener {
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("AI Suggestion", fullSuggestion)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(requireContext(), "Copied to clipboard!", Toast.LENGTH_SHORT).show()
        }

        shareButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, fullSuggestion)
            }
            startActivity(Intent.createChooser(shareIntent, "Share AI Suggestion via"))
        }

        closeButton.setOnClickListener {
            dismiss()
        }

        return view
    }
}

