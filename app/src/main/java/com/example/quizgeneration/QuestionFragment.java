package com.example.quizgeneration;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.Map;

public class QuestionFragment extends Fragment {

    private static final String ARG_QUESTION = "question";

    private QuizQuestion quizQuestion;

    public QuestionFragment() {
        // Required empty public constructor
    }

    public static QuestionFragment newInstance(QuizQuestion question) {
        QuestionFragment fragment = new QuestionFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_QUESTION, question);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            quizQuestion = getArguments().getParcelable(ARG_QUESTION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_question, container, false);

        if (quizQuestion != null) {
            RadioGroup radioGroup = view.findViewById(R.id.questionRadioGroup);
            radioGroup.clearCheck(); // Clear any previous selection

            Map<String, String> options = quizQuestion.getOptions();

            for (Map.Entry<String, String> entry : options.entrySet()) {
                RadioButton radioButton = new RadioButton(requireContext());
                radioButton.setText(entry.getValue());
                radioButton.setTag(entry.getKey());
                radioGroup.addView(radioButton);
            }

            // Set the question text
            TextView questionTextView = view.findViewById(R.id.questionTextView);
            questionTextView.setText(quizQuestion.getQuestion());
        }

        return view;
    }


    public String getSelectedAnswer() {
        // Get the selected answer from the RadioGroup
        RadioGroup radioGroup = requireView().findViewById(R.id.questionRadioGroup);
        int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();

        if (selectedRadioButtonId != -1) {
            RadioButton selectedRadioButton = requireView().findViewById(selectedRadioButtonId);
            return selectedRadioButton.getTag().toString();
        }

        return "";
    }
}
