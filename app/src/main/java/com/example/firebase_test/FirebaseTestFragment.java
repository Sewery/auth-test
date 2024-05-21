package com.example.firebase_test;

import static com.example.firebase_test.FirebaseTestRoutes.getNavDirections;
import static com.example.firebase_test.SnackbarUI.showSnackbar;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class FirebaseTestFragment extends Fragment {
    private FirebaseTestViewModel viewModel;
    private NavController navController;
    public FirebaseTestFragment(){
        super(R.layout.fragment_firebase_test);
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this,
                ViewModelProvider.Factory.from(FirebaseTestViewModel.initializer))
                .get(FirebaseTestViewModel.class);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController=Navigation.findNavController(requireActivity(),R.id.nav_host_fragment);
        requireView().findViewById(R.id.signOutButton).setOnClickListener(v-> {
            viewModel.onSignOutClick(
                    (from,to)-> navController.navigate(getNavDirections(from,to)),
                    (snackbarType, uiText,logMes) -> showSnackbar(view,snackbarType,(UIText.ResourceString)uiText,logMes));
        });
        viewModel.getUserData().observe(getViewLifecycleOwner(),
                (userData)->((TextView) requireView().findViewById(R.id.username)).setText(userData.getUsername()));

    }
}
