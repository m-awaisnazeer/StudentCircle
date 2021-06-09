package com.communisolve.studentcircle.ui.chat;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.communisolve.studentcircle.databinding.FragmentNotificationsBinding;

public class ChatsFragment extends Fragment {

    private ChatsViewModel chatsViewModel;
    private FragmentNotificationsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        chatsViewModel =
                new ViewModelProvider(this).get(ChatsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.shimmerNotification.startShimmer();

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isAdded()) {
                    showNotifications();
                }
            }
        }, 1500);
        return root;
    }

    private void showNotifications() {
        binding.shimmerNotification.stopShimmer();
        binding.shimmerNotification.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}