package com.rejfin.tictactoe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

public class HomeFragment extends Fragment{
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View startOneButton = view.findViewById(R.id.bt_start_one);
        View startTwoButton = view.findViewById(R.id.bt_start_two);
        View exitButton = view.findViewById(R.id.bt_exit);

        // klawisz wyjścia z aplikacji
        exitButton.setOnClickListener(v ->
                requireActivity().finish()
        );

        // klawisz uruchamiający gre dla jednego gracza
        startOneButton.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putBoolean("isOnePlayer", true);
            // do poruszania się po oknach aplikacji wykorzystujemy Navigation Component
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_homeFragment_to_boardFragment, args);
            }
        );

        // klawisz uruchamiający gre dla dwóch graczy
        startTwoButton.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putBoolean("isOnePlayer", false);
            // do poruszania się po oknach aplikacji wykorzystujemy Navigation Component
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_homeFragment_to_boardFragment, args);
            }
        );

    }
}
