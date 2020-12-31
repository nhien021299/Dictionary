package com.example.dictionary.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dictionary.R;
import com.example.dictionary.ui.WordMeaningAcitvity;

public class FragmentDef extends Fragment {
    public FragmentDef() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_def,container,false);

        Context context = getActivity();
        TextView textViewDef = (TextView) view.findViewById(R.id.txtDef);

        String vie_definition = ((WordMeaningAcitvity)context).vieDefinition;

        textViewDef.setText(vie_definition);
        if(vie_definition==null){
            textViewDef.setText("No definition found :(((");
        }

        return view;
    }
}
