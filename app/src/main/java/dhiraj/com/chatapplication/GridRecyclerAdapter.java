package dhiraj.com.chatapplication;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class GridRecyclerAdapter extends RecyclerView.Adapter<GridRecyclerAdapter.GridRecyclerViewHolder> {
    ArrayList<User> arrayListGrid=new ArrayList<>();
    Context mContext;
    private IGridListener gridListener;

    public GridRecyclerAdapter(Context mContext, ArrayList<User> arrayListGrid, IGridListener gridListener) {
        this.arrayListGrid = arrayListGrid;
        this.mContext = mContext;
        this.gridListener = gridListener;

    }

    @Override
    public GridRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(mContext);
        View view= layoutInflater.inflate(R.layout.search_layout,parent,false);
        GridRecyclerAdapter.GridRecyclerViewHolder gridRecyclerViewHolder=new GridRecyclerAdapter.GridRecyclerViewHolder(view);
        return gridRecyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(GridRecyclerViewHolder holder, int position) {
        final User user=arrayListGrid.get(position);
        Picasso.with(mContext).load(user.getImage()).into(holder.imageViewSearchUserImage);
        holder.textViewSearchUser.setText(user.getFirstName()+" "+user.getLastName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridListener.showDetail(user);
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayListGrid.size();
    }

    public static class GridRecyclerViewHolder extends RecyclerView.ViewHolder{
        ImageView imageViewSearchUserImage;
        TextView textViewSearchUser;
        public GridRecyclerViewHolder(View itemView) {
            super(itemView);
            imageViewSearchUserImage= (ImageView) itemView.findViewById(R.id.imageViewSearchImage);
            textViewSearchUser= (TextView) itemView.findViewById(R.id.textViewSearchName);
        }
    }

    interface IGridListener
    {
        void showDetail(User user);
    }
}
