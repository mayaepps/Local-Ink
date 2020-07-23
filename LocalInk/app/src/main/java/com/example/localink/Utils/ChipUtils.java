package com.example.localink.Utils;

import android.content.Context;

import com.example.localink.RegisterReaderActivity;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

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

        chipGroup.setSingleSelection(singleSelection);
        for (String choice : choices) {
            Chip chip = new Chip(context);
            chip.setText(choice);
            chip.setCheckable(true);
            chipGroup.addView(chip);
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
