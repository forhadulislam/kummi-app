package fi.oulu.mobisocial.kummi_application;

import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.List;

/**
 * Created by opoku on 19-Apr-17.
 */

public class AskRecyclerViewAdaptor extends RecyclerView.Adapter<AskRecyclerViewAdaptor.AskViewHolder>{
    private List<HashMap<String,String>> dataSet;

    public AskRecyclerViewAdaptor(List<HashMap<String,String>> dataSet){
        this.dataSet=dataSet;
    }

    @Override
    public AskViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ask_card_view, parent, false);
        AskViewHolder viewHolder = new AskViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AskViewHolder holder,int position){

        HashMap<String,String> entry=dataSet.get(position);
        String messageType=entry.get("messageType");

        switch(messageType){
            case AskFragment.MESSAGE_TYPE_INCOMING:
                holder.getOutGoingLayout().setVisibility(View.GONE);
                holder.askIncommingMessage.setText(entry.get("message"));
                break;
            case AskFragment.MESSAGE_TYPE_OUTGOING:
                holder.getIncomingLayout().setVisibility(View.GONE);
                holder.askOutGoingMessage.setText(entry.get("message"));
                break;
        }
    }
    public void insertItemAt(HashMap<String,String> entry, int index) {
        dataSet.add(index, entry);

        notifyDataSetChanged();
    }
    public  void insertItem(HashMap<String,String> entry){
        dataSet.add(entry);
        notifyDataSetChanged();
    }
    public void deleteItem(int index) {

        dataSet.remove(index);
        notifyItemRemoved(index);
    }

    public void deleteItem(HashMap<String,String> entry) {
        dataSet.remove(entry);
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount(){
        return dataSet.size();
    }

    public class AskViewHolder extends RecyclerView.ViewHolder{
        public View holderView;
        public TextView askIncommingMessage, askOutGoingMessage;
        public ImageView askOutGoingMessgeSenderImage, askIncomingMessageSenderImage;


        public LinearLayout incomingLayout, outGoingLayout;

        public AskViewHolder(View itemView){
            super(itemView);
            holderView=itemView;
            incomingLayout=(LinearLayout)itemView.findViewById(R.id.ask_incoming_message_view);
            outGoingLayout=(LinearLayout)itemView.findViewById(R.id.ask_outgoing_message_view);
            askIncommingMessage=(TextView)itemView.findViewById(R.id.ask_incoming_message);
            askOutGoingMessage=(TextView)itemView.findViewById(R.id.ask_outgoing_message);
            askIncomingMessageSenderImage=(ImageView)itemView.findViewById(R.id.ask_incoming_message_sender_image);
            askOutGoingMessgeSenderImage=(ImageView)itemView.findViewById(R.id.ask_outgoing_message_sender_image);
        }

        public View getHolderView(){
            return holderView;
        }

        public LinearLayout getIncomingLayout(){
            return incomingLayout;
        }

        public LinearLayout getOutGoingLayout(){
            return outGoingLayout;
        }

        public TextView getAskIncommingMessage(){
            return askIncommingMessage;
        }

        public TextView getAskOutGoingMessage(){
            return askOutGoingMessage;
        }

        public ImageView getAskOutGoingMessgeSenderImage(){
            return askOutGoingMessgeSenderImage;
        }

        public ImageView getAskIncomingMessageSenderImage(){
            return askIncomingMessageSenderImage;
        }

    }
}
