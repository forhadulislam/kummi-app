package fi.oulu.mobisocial.kummi_application;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DoFragment extends Fragment{



    public DoFragment(){
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
        return inflater.inflate(R.layout.fragment_do,container,false);
    }



    @Override
    public void onAttach(Context context){
        super.onAttach(context);

    }

    @Override
    public void onDetach(){
        super.onDetach();

    }


}
