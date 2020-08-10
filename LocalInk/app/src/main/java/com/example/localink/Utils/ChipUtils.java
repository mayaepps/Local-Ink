package com.example.localink.Utils;

import android.content.Context;

import com.example.localink.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class ChipUtils {

    // Get a string of the selected chips from the given chipGroup
    public static List<String> getChipSelections(ChipGroup chipGroup) {

        List<String> selections = new ArrayList<>();

        for (int i=0; i < chipGroup.getChildCount(); i++){
            Chip chip = (Chip) chipGroup.getChildAt(i);
            if (chip.isChecked()){
                selections.add((String) chip.getText());
            }
        }
        return selections;
    }

    // Put a chip in the chipGroup for each element in the choices string
    public static void setUpChips(Context context, ChipGroup chipGroup, String[] choices, boolean singleSelection) {

        chipGroup.removeAllViews();
        chipGroup.setSingleSelection(singleSelection);
        for (String choice : choices) {
            Chip chip = new Chip(context);
            chip.setText(choice);
            chip.setCheckable(true);
            chip.setChipBackgroundColorResource(R.color.paleColorAccent);
            chipGroup.addView(chip);
        }
    }

    // Put a chip in the chipGroup for each element in the choices string, cannot be checked
    public static void setUpChips(Context context, ChipGroup chipGroup, List<String> choices, boolean doThinStyle) {

        chipGroup.removeAllViews();
        for (String choice : choices) {
            Chip chip = new Chip(context);
            chip.setText(choice);
            chip.setChipBackgroundColorResource(R.color.paleColorAccent);
            chipGroup.addView(chip);

            if (doThinStyle) {
                chip.setChipMinHeight(64F);
            }
        }
    }

    // Select the chips that have text that matches the given list of strings
    public static void selectChips(List<String> selections, ChipGroup chipGroup) {
        Chip chip;
        for (int i = 0; i < chipGroup.getChildCount(); i++){
            chip = (Chip) chipGroup.getChildAt(i);
            if (selections.contains(chip.getText())){
                chip.setChecked(true);
            } else {
                chip.setChecked(false);
            }

        }

    }
}
