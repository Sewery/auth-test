package com.example.firebase_test.presentation.sign_in;


import static com.example.firebase_test.FirebaseTestRoutes.getNavDirections;
import static com.example.firebase_test.SnackbarUI.showSnackbar;

import android.app.PendingIntent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;


import com.example.firebase_test.R;
import com.example.firebase_test.UIText;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;

public class SplashFragment extends Fragment {
    private static final Integer SPLASH_DELAY=1000;
    public static final String TAG="SplashFragment";
    private SplashViewModel viewModel;
    private SignInClient signInClient;
    public SplashFragment(){
        super(R.layout.fragment_splash);
    }
    private NavController navController;
    private ActivityResultLauncher<IntentSenderRequest> signInLauncher;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this,
                ViewModelProvider.Factory.from(SplashViewModel.initializer))
                .get(SplashViewModel.class);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        signInClient = Identity.getSignInClient(requireContext());
        navController=Navigation.findNavController(requireActivity(),R.id.nav_host_fragment);
        requireView().findViewById(R.id.sign_in1).setOnClickListener(v->{
            navController.navigate(R.id.action_splashFragment_to_signInFragment,null);
        });
        requireView().findViewById(R.id.sing_up1).setOnClickListener(v->{
            navController.navigate(R.id.action_splashFragment_to_signUpFragment,null);
        });
        //Sign in launcher calls firebase api from viewmodel
        signInLauncher= registerForActivityResult(
                new ActivityResultContracts.StartIntentSenderForResult(),
                result -> viewModel.handleSignInResult(
                        result.getData(), signInClient,
                        (from, to)-> Navigation.findNavController(requireActivity(),R.id.nav_host_fragment).navigate(getNavDirections(from,to)),
                        (snackbarType, uiText,logMes) -> showSnackbar(view,snackbarType,(UIText.ResourceString)uiText,logMes))
        );
        requireView().findViewById(R.id.sign_in_google).setOnClickListener(v->{
            signInWithGoogle();
        });
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            viewModel.onAppStart((from, to)-> Navigation.findNavController(view)
                    .navigate(getNavDirections(from,to)));
        }, SPLASH_DELAY);

    }
    private void signInWithGoogle() {

        GetSignInIntentRequest signInRequest = GetSignInIntentRequest.builder()
                .setServerClientId(getString(R.string.default_web_client_id))
                .build();
        signInClient.getSignInIntent(signInRequest)
                .addOnSuccessListener(this::launchSignIn)
                .addOnFailureListener(e -> Log.e(TAG, "Google Sign-in failed", e));
    }
    private void launchSignIn(PendingIntent pendingIntent) {
        try {
            IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(pendingIntent)
                    .build();
            signInLauncher.launch(intentSenderRequest);
        } catch (Exception e) {
            Log.e(TAG, "Couldn't start Sign In: " + e.getLocalizedMessage());
        }
    }

}
