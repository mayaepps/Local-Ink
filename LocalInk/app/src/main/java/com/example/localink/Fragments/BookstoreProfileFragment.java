package com.example.localink.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.localink.EditBookstoreProfileActivity;
import com.example.localink.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookstoreProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookstoreProfileFragment extends Fragment {

    Button btnSave;

    public BookstoreProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bookstore_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnSave = view.findViewById(R.id.btnEdit);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), EditBookstoreProfileActivity.class);
                startActivity(i);
            }
        });
    }
}