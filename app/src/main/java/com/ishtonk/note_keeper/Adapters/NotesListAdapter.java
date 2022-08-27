package com.ishtonk.note_keeper.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ishtonk.note_keeper.Models.Notes;
import com.ishtonk.note_keeper.NotesClickListener;
import com.ishtonk.note_keeper.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NotesListAdapter extends RecyclerView.Adapter<NotesViewHolder> {
    Context context; // context of the app
    List<Notes> notes; // list of notes
    NotesClickListener Listener; // interface to be implemented by the activity

    public NotesListAdapter(Context context, List<Notes> notes, NotesClickListener listener) {
        this.context = context;
        this.notes = notes;
        Listener = listener;
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotesViewHolder(
                (LayoutInflater.from(context).inflate(R.layout.notes_list, parent, false)) // inflate the layout
        );
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {
        holder.notes_title.setText(notes.get(position).getTitle()); // set the title
        holder.notes_title.setSelected(true); // set the title to be selected (to make horizontal scrolling work)

        holder.notes_content.setText(notes.get(position).getNotes()); // set the content

        holder.notes_date.setText(notes.get(position).getDate()); // set the date
        holder.notes_date.setSelected(true); // set the date to be selected (to make horizontal scrolling work)

        if (notes.get(position).isPinned()) {
            // if the note is pinned
            holder.notes_image_pin.setImageResource(R.drawable.ic_note_pin_24); // set the pin icon to be pinned (add the pin icon)
        } else {
            // if the note is not pinned
            holder.notes_image_pin.setImageResource(0); // set the pin icon to be unpinned (remove the pin icon)
        }

        int color_code = getRandomColor(); // get a random color code
        holder.notes_container.setCardBackgroundColor(holder.itemView.getResources().getColor(color_code, null)); // set the card background color to the random color code

        holder.notes_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Listener.onClick(notes.get(holder.getAdapterPosition())); // call the onClick method of the interface
            }
        });

        holder.notes_container.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Listener.onLongClick(notes.get(holder.getAdapterPosition()), holder.notes_container); // call the onLongClick method of the interface
                return true;
            }
        });

    }

    private int getRandomColor() {
        List<Integer> colors = new ArrayList<>();

        colors.add(R.color.color1);
        colors.add(R.color.color2);
        colors.add(R.color.color3);
        colors.add(R.color.color4);
        colors.add(R.color.color5);

        Random random = new Random();
        return colors.get(random.nextInt(colors.size()));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void filterList(List<Notes> filteredList) {
        notes = filteredList;
        notifyDataSetChanged();
    }
}

class NotesViewHolder extends RecyclerView.ViewHolder {

    CardView notes_container;
    TextView notes_title, notes_content, notes_date;
    ImageView notes_image_pin;

    public NotesViewHolder(@NonNull View itemView) {
        super(itemView);
        notes_container = itemView.findViewById(R.id.notes_container);
        notes_title = itemView.findViewById(R.id.notes_title);
        notes_content = itemView.findViewById(R.id.notes_content);
        notes_date = itemView.findViewById(R.id.notes_date);
        notes_image_pin = itemView.findViewById(R.id.notes_image_pin);
    }
}
