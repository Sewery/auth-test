package com.example.firebase_test.presentation.sign_in;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import static com.example.firebase_test.FirebaseTestRoutes.getNavDirections;
import static com.example.firebase_test.SnackbarUI.showSnackbar;

import com.example.firebase_test.R;
import com.example.firebase_test.UIText;

import lombok.Getter;

public class SignUpFragment  extends Fragment {
    public static final String TAG="SignUpFragment";
    private SignUpViewModel viewModel;
    private NavController navController;
    public SignUpFragment(){
        super(R.layout.fragment_sign_up);
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this,
                ViewModelProvider.Factory.from(SignUpViewModel.initializer))
                .get(SignUpViewModel.class);

    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController=Navigation.findNavController(requireActivity(),R.id.nav_host_fragment);
        requireView().findViewById(R.id.emailCreateAccountButton).setOnClickListener(v->{
            EditText email=requireView().findViewById(R.id.fieldEmail2);
            viewModel.updateEmail(email.getText().toString());
            EditText password=requireView().findViewById(R.id.fieldPassword2);
            viewModel.updatePassword(password.getText().toString());
            EditText confirmPassword=requireView().findViewById(R.id.fieldConfirmPassword2);
            viewModel.updateConfirmPassword(confirmPassword.getText().toString());
            viewModel.onSignUpClick(
                    (from,to)-> navController.navigate(getNavDirections(from,to)),
                    (snackbarType, uiText,logMes) -> showSnackbar(view,snackbarType,(UIText.ResourceString)uiText,logMes));
        });
    }
}
