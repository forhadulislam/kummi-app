package fi.oulu.mobisocial.kummi_application;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.GetRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import model.Ask;





public class AskFragment extends Fragment{

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private List<HashMap<String,String>> messageDataSet;
    private AskRecyclerViewAdaptor askRecyclerViewAdaptor;
    private ImageView sendMessageButton;
    private TextView textMessage;
    public static final String MESSAGE_TYPE_INCOMING="incommingMessage";
    public static final String MESSAGE_TYPE_OUTGOING="outgoingMessage";

    Ask askMessage;
    public AskFragment(){
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState){
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ask,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        recyclerView=(RecyclerView)getView().findViewById(R.id.ask_recycler_view);
        recyclerViewLayoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        ArrayList<HashMap<String,String>> dataset=new ArrayList<>();
        messageDataSet=dataset;
        askRecyclerViewAdaptor=new AskRecyclerViewAdaptor(dummyData());
        recyclerView.setAdapter(askRecyclerViewAdaptor);

        final int[] count={0};
        sendMessageButton=(ImageView)getView().findViewById(R.id.type_message_send);
        textMessage=(TextView)getView().findViewById(R.id.type_message_text);
        sendMessageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                List<HashMap<String,String>> dataset=dummyData();
                for(int i=0;i<dataset.size();i++){
                    HashMap<String,String> entry=dataset.get(i);
                    askRecyclerViewAdaptor.insertItemAt(entry,i);

                    /**
                     HashMap<String,String> message=new HashMap<>();
                     message.put("message",textMessage.getText().toString().trim());
                     message.put("messageType",MESSAGE_TYPE_OUTGOING);
                     askRecyclerViewAdaptor.insertItem(message);
                     count[0]++;

                     demola(count[0]);*/

                }

            }

        });

        askMessage = new Ask("123123", "nothing much");

    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
    }


    private void demola(int entry){
        List<HashMap<String,String>> dataset=dummyData();
        HashMap<String,String> message=dataset.get(entry%dataset.size());
        askRecyclerViewAdaptor.insertItem(message);

        /*
        for(int i=0;i<dataset.size();i++){
            HashMap<String,String> entry=dataset.get(i);
            askRecyclerViewAdaptor.insertItem(entry,i);
            try{
                Thread.sleep(4000);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
         */
    }

    private List<HashMap<String,String>> dummyData(){

        //HttpClient httpclient = new DefaultHttpClient();
        GetRequest response = Unirest.get("https://kummi-ad21.restdb.io/rest/ask");
        response.header("x-apikey", "aae81c5685e95a5cc268116b0a6bb0353033f");
        //HttpResponse<String> stringHttpResponse = response.header("cache-control", "no-cache").asString();

        //Log.d("RestDB", "dummyData: " + stringHttpResponse);

        ArrayList<HashMap<String,String>> dataset=new ArrayList<>();
        HashMap<String,String> message=new HashMap<>();
        message.put("message","Hi Kummi");
        message.put("messageType",MESSAGE_TYPE_OUTGOING);
        dataset.add(message);

        message=new HashMap<>();
        message.put("message","Howdy");
        message.put("messageType",MESSAGE_TYPE_INCOMING);
        dataset.add(message);

        message=new HashMap<>();
        message.put("message","I have heard about one Distributed Systems course where there is random sampling of answers from  each group member. The sum of the marks becomes the groups mark woow, this is the most unsual grading system I have heard of in my entire life. Guys.. for this am not coming to University of Oulu. Am going to University of Eastern Finland");
        message.put("messageType",MESSAGE_TYPE_OUTGOING);
        dataset.add(message);


        message=new HashMap<>();
        message.put("message","Oh Naaa its not that bad, I have done this course. You can score 11 out of 30 for exams and battle for a grade 2 :)");
        message.put("messageType",MESSAGE_TYPE_INCOMING);
        dataset.add(message);

        message=new HashMap<>();
        message.put("message","Really?? And I heard there is one called Intro. to Social Network Analyse... you will have to give data");
        message.put("messageType",MESSAGE_TYPE_OUTGOING);
        dataset.add(message);

        message=new HashMap<>();
        message.put("message","Yes.. Where is the data .. give me data :)");
        message.put("messageType",MESSAGE_TYPE_INCOMING);
        dataset.add(message);

        message=new HashMap<>();
        message.put("message","Sounds fun..  ok I will still come to University of Oulu");
        message.put("messageType",MESSAGE_TYPE_OUTGOING);
        dataset.add(message);

        message=new HashMap<>();
        message.put("message","Great!!");
        message.put("messageType",MESSAGE_TYPE_INCOMING);
        dataset.add(message);

        return dataset;
    }

    @Override
    public void onDetach(){
        super.onDetach();
    }


}
