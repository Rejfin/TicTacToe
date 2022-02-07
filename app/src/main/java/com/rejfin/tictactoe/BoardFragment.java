package com.rejfin.tictactoe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import static java.util.Arrays.*;
import java.util.List;
import java.util.Random;

public class BoardFragment extends Fragment implements OnClickListener {

    // tryb gry (nadpisywany w metodzie onViewCreated)
    private boolean isOnePlayer = true;
    // stan pól( 0 - nie użyte, 1 lub 2 - gracz zaznaczył to pole)
    private int[] fieldsStates = new int[9];
    //index gracza
    private int playerIndex = 1;
    // referencja do obrazka wskazującego który gracz ma wykonać ruch
    private View playerTurnImageView;
    //lista referencji do wszytkich pól
    private final View[] listOfFields = new View[9];
    // liczba zajętych już pól
    private int usedFieldsCounter = 0;
    // wszytkie wygrywające kombinacje
    private final List<List<Integer>> winningCombinations = asList(
            asList(0,1,2),
            asList(3,4,5),
            asList(6,7,8),
            asList(0,3,6),
            asList(1,4,7),
            asList(2,5,8),
            asList(2,4,6),
            asList(0,4,8)
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_board, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // odczytujemy przekazany tryb gry (jeden / dwóch graczy)
        if(getArguments() != null){
            isOnePlayer = getArguments().getBoolean("isOnePlayer", true);
        }

        // znajdujemy wszystkie przyciski i pola w którcyh musimy obsłużyć pewne akcje
        // tj. zmiana obrazka lub kliknięcie w element
        View field1 = view.findViewById(R.id.iv_field1);
        View field2 = view.findViewById(R.id.iv_field2);
        View field3 = view.findViewById(R.id.iv_field3);
        View field4 = view.findViewById(R.id.iv_field4);
        View field5 = view.findViewById(R.id.iv_field5);
        View field6 = view.findViewById(R.id.iv_field6);
        View field7 = view.findViewById(R.id.iv_field7);
        View field8 = view.findViewById(R.id.iv_field8);
        View field9 = view.findViewById(R.id.iv_field9);
        View buttonEndGame = view.findViewById(R.id.bt_end_game);
        playerTurnImageView = view.findViewById(R.id.iv_turn);

        // zapisujemy wszystkie referencje do pól aby można było w łatwy sposób zresetować stan gry
        listOfFields[0] = field1;
        listOfFields[1] = field2;
        listOfFields[2] = field3;
        listOfFields[3] = field4;
        listOfFields[4] = field5;
        listOfFields[5] = field6;
        listOfFields[6] = field7;
        listOfFields[7] = field8;
        listOfFields[8] = field9;

        // rejestrujemy ClickListener aby był obsługiwany przez ten fragment w funkcji onClick która jest poniżej
        field1.setOnClickListener(this);
        field2.setOnClickListener(this);
        field3.setOnClickListener(this);
        field4.setOnClickListener(this);
        field5.setOnClickListener(this);
        field6.setOnClickListener(this);
        field7.setOnClickListener(this);
        field8.setOnClickListener(this);
        field9.setOnClickListener(this);

        // używając navigation component wracamy do głównego menu
        buttonEndGame.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());

        // dzięki tej linijce to bot zaczyna pierwszy w grze jednoosobowej
        if(isOnePlayer){
            artificialPlayerMove();
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getTag() != null){
            // każde pole ma swój tag (0-8)
            // tutaj odczytujemy tag danego pola i zamieniamy go na int
            // który odpowiada indexowi w tablicy fieldsStates
            int index = Integer.parseInt((String) v.getTag());
            if(fieldsStates[index] == 0){
                usedFieldsCounter += 1;
                // nadpisuje wartośc pola aktualnym indexem gracza
                fieldsStates[index] = playerIndex;
                updateTurnImage(v);
                // warunki sprawdzające jaką wiadomość wyświetlić po zakończeniu gry
                if(isEndGame()){
                    if(!isOnePlayer){
                        createDialog(getString(R.string.game_won_player, playerIndex == 1 ? getString(R.string.cross) : getString(R.string.circle))).show();
                    }else if(playerIndex == 1){
                        createDialog(getString(R.string.end_game_message_win)).show();
                    }else{
                        createDialog(getString(R.string.end_game_message_lose)).show();
                    }
                }else if(usedFieldsCounter == 9){
                    createDialog(getString(R.string.end_game_message_draw)).show();
                }else if(isOnePlayer && playerIndex == 1){
                    artificialPlayerMove();
                }
            }
        }
    }

    // funkcja resetująca stan gry
    private void restartGame() {
        fieldsStates = new int[9];
        playerIndex = 1;
        ((ImageView)playerTurnImageView).setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_circle));
        for (View field : listOfFields) {
            ((ImageView)field).setImageDrawable(null);
        }
        usedFieldsCounter = 0;
        if(isOnePlayer){
            artificialPlayerMove();
        }
    }

    // funkcja tworząca Dialog, który jest wyświetlany na końcu gry
    private AlertDialog createDialog(String message){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(requireContext());
        alertBuilder.setMessage(message);
        alertBuilder.setCancelable(false);
        // klawisz w alercie pozwalający zresetować stan gry po jej zakończeniu
        alertBuilder.setPositiveButton(R.string.play_again, (dialog, which) ->
                restartGame()
        );
        // klawisz w alercie pozwalający wrócić do menu po zakończeniu gry
        alertBuilder.setNegativeButton(R.string.go_to_menu, (dialog, which) ->
                NavHostFragment.findNavController(requireParentFragment()).popBackStack()
        );
        return alertBuilder.create();
    }

    //funkcja aktualizuje informacje o przebiegu gry
    private void updateTurnImage(View fieldImage){
        if(playerIndex == 1){
            // aktualizuje obrazek wskazujący którego gracza jest kolej
            ((ImageView)playerTurnImageView).setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_cross));
            // zmienia obrazek na polu na którym nacisnął gracz
            ((ImageView)fieldImage).setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_circle));
            // zmienia index gracza
            playerIndex = 2;
        }else{
            // aktualizuje obrazek wskazujący którego gracza jest kolej
            ((ImageView)playerTurnImageView).setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_circle));
            // zmienia obrazek na polu na którym nacisnął gracz
            ((ImageView)fieldImage).setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_cross));
            // zmienia index gracza
            playerIndex = 1;
        }
    }

    // funkcja sprawdzająca czy gra się zakończyła
    private boolean isEndGame(){
        // sprawdzamy wszystkie wygrywające kombinacje pól
        for (List<Integer> winningCombination : winningCombinations) {
            // jeśli każdy element w liście wygrywających kombinacji się zgadza tj. jest taki sam
            // zwracamy true informując że gra została zakończona
            // w przeciwnym wypadku zwracamy false i gra trwa dalej
            if(fieldsStates[winningCombination.get(0)] != 0 &&
            fieldsStates[winningCombination.get(0)] == fieldsStates[winningCombination.get(1)] &&
                    fieldsStates[winningCombination.get(1)] == fieldsStates[winningCombination.get(2)]) {
                return true;
            }
        }
        return false;
    }

    //funkcja wykonujaca ruch sztucznego gracza
    private void artificialPlayerMove(){
        Random rand = new Random();
        int counter = 0;
        while(true){
            // losujemy liczbę z przedziału od 0 do 8 włącznie
            // jeśli trafimy na wolne pole zaznaczamy je
            int randomFieldIndex = rand.nextInt(9);
            if(fieldsStates[randomFieldIndex] == 0){
                listOfFields[randomFieldIndex].performClick();
                break;
            }

            // losuje maksymalnie 5 razy po czym wybieramy perwszy wolny index
            counter += 1;
            if(counter > 4){
                listOfFields[getFirstEmptyFieldIndex()].performClick();
                break;
            }
        }
    }

    // funckja znajduje index pierwszego nie uzytego pola
    // funkcja ta jest wywoływana gdy losowanie długo nie przynosi efektów
    private int getFirstEmptyFieldIndex(){
        for (int i = 0; i < 9; i++) {
            if(fieldsStates[i] == 0){
                return i;
            }
        }
        return 0;
    }
}
