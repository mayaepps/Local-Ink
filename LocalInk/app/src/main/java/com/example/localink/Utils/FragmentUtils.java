package com.example.localink.Utils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.localink.R;

public class FragmentUtils {

    // Show fragmentA and hide all the other fragments
    public static void displayFragment(FragmentManager fragmentManager, Fragment fA, Fragment fB, Fragment fC) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        if (fA.isAdded()) { // if the fragment is already in container
            ft.show(fA);
        } else { // fragment needs to be added to frame container
            ft.add(R.id.flContainer, fA, fA.getClass().getSimpleName());
        }

        if (fB.isAdded()) {
            ft.hide(fB);
        }

        if (fC.isAdded()) {
            ft.hide(fC);
        }

        ft.commit();
    }

    // Show fragmentA and hide all the other fragments
    public static void displayFragment(FragmentManager fragmentManager, Fragment fA, Fragment fB, Fragment fC, Fragment fD) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        if (fA.isAdded()) { // if the fragment is already in container
            ft.show(fA);
        } else { // fragment needs to be added to frame container
            ft.add(R.id.flContainer, fA, fA.getClass().getSimpleName());
        }

        if (fB.isAdded()) {
            ft.hide(fB);
        }

        if (fC.isAdded()) {
            ft.hide(fC);
        }

        if (fD.isAdded()) {
            ft.hide(fD);
        }

        ft.commit();
    }

    // Display the given fragment and hide the other fragment
    public static void displayFragment(FragmentManager fragmentManager, Fragment fragmentA, Fragment fragmentB) {

        FragmentTransaction ft = fragmentManager.beginTransaction();
        if (fragmentA.isAdded()) { // if the fragment is already in container
            ft.show(fragmentA);
        } else { // fragment needs to be added to frame container
            ft.add(R.id.flContainer, fragmentA);
        }
        // Hide fragment B
        if (fragmentB.isAdded()) { ft.hide(fragmentB); }

        ft.commit();
    }


}
