package com.filipblazekovic.totpy.fragment.panel;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.filipblazekovic.totpy.R;
import com.filipblazekovic.totpy.activity.TokensActivity;
import com.google.android.material.textfield.TextInputLayout;

public class SearchPanel extends Fragment {

  public SearchPanel() {
    super(R.layout.panel_search);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    final TextInputLayout searchInputLayout = view.findViewById(R.id.panel_search_text_field);
    searchInputLayout.getEditText().setOnEditorActionListener((v, actionId, event) -> {
      if (actionId == EditorInfo.IME_ACTION_SEARCH) {
        ((TokensActivity) getActivity()).search(
            searchInputLayout.getEditText().getText().toString()
        );
        return true;
      }
      return false;
    });
  }

}
