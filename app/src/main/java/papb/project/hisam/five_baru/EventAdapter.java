package papb.project.hisam.five_baru;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> implements Filterable {

    private Context mContext;
    private List<Event> mEventList;
    private List<Event> mEventFiltered;
    private EventAdapterListener listener;

    public class EventViewHolder extends RecyclerView.ViewHolder{
        public TextView eventName, eventDesc, eventCity, eventDate, eventLoct;
        public ImageView imageView;


        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.text_view_event_name);
            eventDesc = itemView.findViewById(R.id.text_view_desc);
            imageView = itemView.findViewById(R.id.image_view_event);
            eventCity = itemView.findViewById(R.id.text_view_event_city);
            eventDate = itemView.findViewById(R.id.text_view_event_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onEventSelected(mEventFiltered.get(getAdapterPosition()));
                }
            });
        }

    }

    public EventAdapter(Context context, List<Event> eventList, EventAdapterListener listener) {
        this.mContext = context;
        this.mEventList = eventList;
        this.listener = listener;
        this.mEventFiltered = eventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.itemview, parent, false);
        return new EventViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event eventCurrent = mEventFiltered.get(position);
        holder.eventName.setText(eventCurrent.getName());
        holder.eventDesc.setText(eventCurrent.getDesc());
        holder.eventCity.setText(eventCurrent.getKota());
        holder.eventDate.setText(eventCurrent.getTime());
        Picasso.with(mContext)//nampilin gambar
                .load(eventCurrent.getFotoUrl())
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mEventFiltered.size();
    }

    //filter search view (Berdasarkan kota dan event)
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mEventFiltered = mEventList;
                } else {
                    List<Event> filteredList = new ArrayList<>();
                    for (Event row : mEventList) {
                        if (row.getName().toLowerCase().contains(charString.toLowerCase()) || row.getKota().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    mEventFiltered = filteredList;

                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mEventFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mEventFiltered = (ArrayList<Event>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface EventAdapterListener {
        void onEventSelected(Event event);
    }

}
