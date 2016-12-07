package com.example.witold.wicioguitartuner;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.pavlospt.roundedletterview.RoundedLetterView;

public class TunerFragment extends Fragment {
    static RoundedLetterView noteView;
    public TunerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tuner, container, false);
        noteView = (RoundedLetterView) view.findViewById(R.id.noteView);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public static TunerFragment newInstance(String text) {
        TunerFragment f = new TunerFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);
        f.setArguments(b);
        return f;
    }

    public void setNoteTextView(SingleFrequency frequency)
    {
        noteView.setTitleText(frequency.note);
    }
}
