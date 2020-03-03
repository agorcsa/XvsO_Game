package com.example.xvso.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xvso.Objects.GameItem;
import com.example.xvso.R;

import java.util.ArrayList;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder> {

    private ArrayList<GameItem>  mGameItemsList;

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_item, parent, false);
        GameViewHolder gameViewHolder = new GameViewHolder(view);
        return gameViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        GameItem currentItem = mGameItemsList.get(position);
        holder.profilePicture.setImageResource(currentItem.getProfilePicture());
        holder.gameNumber.setText(currentItem.getGameNumber());
        holder.opponentUserName.setText(currentItem.getLoggedUserName());
    }

    @Override
    public int getItemCount() {
        return  mGameItemsList == null ? 0 : mGameItemsList.size();
    }

    public GameAdapter(ArrayList<GameItem> gameItemsList) {
           mGameItemsList = gameItemsList;
    }

    public static class GameViewHolder extends RecyclerView.ViewHolder {

        public ImageView profilePicture;
        public TextView gameNumber;
        public TextView opponentUserName;

        public GameViewHolder(@NonNull View itemView) {
            super(itemView);

            profilePicture = itemView.findViewById(R.id.profile_image_view);
            gameNumber = itemView.findViewById(R.id.first_line_text_view);
            opponentUserName = itemView.findViewById(R.id.second_line_text_view);
        }
    }
}
