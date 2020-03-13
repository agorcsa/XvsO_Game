package com.example.xvso.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xvso.Objects.Game;
import com.example.xvso.Objects.User;
import com.example.xvso.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder> {

    private ArrayList<Game>  mGameItemsList;

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_item, parent, false);
        GameViewHolder gameViewHolder = new GameViewHolder(view);
        return gameViewHolder;
    }

    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        Game currentItem = mGameItemsList.get(position);
        User user = currentItem.getHost();
        if (user != null) {
            if (!TextUtils.isEmpty(user.getImageUrl())) {
                Picasso.get().load(user.getImageUrl()).into(holder.profilePicture);
            }
            holder.gameNumber.setText(String.valueOf(position + 1));
            holder.opponentUserName.setText(user.getFirstName());
        }
    }

    @Override
    public int getItemCount() {
        return  mGameItemsList == null ? 0 : mGameItemsList.size();
    }

    public GameAdapter(ArrayList<Game> gameItemsList) {
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
